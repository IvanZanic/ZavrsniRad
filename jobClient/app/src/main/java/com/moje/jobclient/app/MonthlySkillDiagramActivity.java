package com.moje.jobclient.app;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rest.DataPercentages;
import rest.MonthlyTrend;
import services.rest.RestService;


public class MonthlySkillDiagramActivity extends ActionBarActivity {

    private List<MonthlyTrend> monthlyTrendsList = new ArrayList<MonthlyTrend>();
    private LinearLayout chartLyt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill_trend);

        new HttpRequestTask().execute();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_skill_trend, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // radi se s async da ekran ne izgleda kao da se smrznuo
    private class HttpRequestTask extends AsyncTask<Integer, Void, Void> {


        // u pozadini prikuplja podatke s web servisa ()
        @Override
        protected Void doInBackground(Integer... params) {
            try {

                Intent itnt = getIntent();
                String url = getResources().getString(R.string.jobServiceUrl) + "statistics/getTehnologyTrend?" + itnt.getStringExtra("searchParams");

                RestService rs = new RestService();

                monthlyTrendsList = new ArrayList<MonthlyTrend>();
                monthlyTrendsList = rs.getList(url, MonthlyTrend[].class);


            } catch (Exception e) {
                Log.e("JobListActivity", e.getMessage(), e);
            }

            return null;
        }

        /**
         * - Nakon što doInBackground prikupi podatke, u ovoj metodi se ti podaci šalju u
         *   custom adapter da bi se pravilno prikazali na ekranu.
         */
        @Override
        protected void onPostExecute(Void result) {

//            if (monthlyTrendsList.size() == 0) {
//                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(MonthlySkillDiagramActivity.this);
//                dlgAlert.setMessage("Nije se moguće spojiti");
//                dlgAlert.setTitle("Spajanje na servis");
//                dlgAlert.setPositiveButton("OK", null);
//                dlgAlert.setCancelable(true);
//                dlgAlert.create().show();
//            } else {

                chartLyt = (LinearLayout) findViewById(R.id.chart);

                Intent itnt = getIntent();

                XYSeries series = new XYSeries(itnt.getStringExtra("skill"));
                XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

                int i = 1;
                for (MonthlyTrend mt : monthlyTrendsList) {
                    series.add(i, mt.getCounter());
                    mRenderer.addXTextLabel(i, mt.getMonth());
                    i++;
                }

                MonthlyTrend maxSkillNum = Collections.max(monthlyTrendsList, new Comparator<MonthlyTrend>() {
                    public int compare(MonthlyTrend o1, MonthlyTrend o2) {
                        return Long.compare(o1.getCounter(), o2.getCounter());
                    }
                });

                // Now we create the renderer
                XYSeriesRenderer renderer = new XYSeriesRenderer();
                renderer.setLineWidth(2);
                renderer.setColor(Color.RED);
                // Include low and max value
                renderer.setDisplayBoundingPoints(true);
                // we add point markers
                renderer.setPointStyle(PointStyle.CIRCLE);
                renderer.setPointStrokeWidth(3);


                // Now we add our series
                XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
                dataset.addSeries(series);

                mRenderer.addSeriesRenderer(renderer);
                // We want to avoid black border
                mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00)); // transparent margins
                // Disable Pan on two axis
                mRenderer.setPanEnabled(false, false);
                mRenderer.setYAxisMax(maxSkillNum.getCounter() + 5);
                mRenderer.setYAxisMin(0);
                mRenderer.setShowGrid(true); // we show the grid
                mRenderer.setLabelsTextSize(20);
                mRenderer.setYLabelsPadding(20);
                mRenderer.setLegendTextSize(25);
                mRenderer.setXLabels(0);
                mRenderer.setAxesColor(Color.rgb(0, 0, 0));
                mRenderer.setXLabelsColor(Color.rgb(0, 0, 0));
                mRenderer.setYLabelsColor(0, Color.rgb(0, 0, 0));

                //            mRenderer.setMargins(new int[]{1,2,3});

                GraphicalView chartView = ChartFactory.getLineChartView(MonthlySkillDiagramActivity.this, dataset, mRenderer);

                chartLyt.addView(chartView, 0);

//            }
        }
    }
}
