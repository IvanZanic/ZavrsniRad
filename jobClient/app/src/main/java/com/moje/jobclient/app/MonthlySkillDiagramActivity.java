package com.moje.jobclient.app;

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

import enums.SkillEnum;
import graphics.ColorArray;
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

    // radi se s async da ekran ne izgleda kao da se smrznuo
    private class HttpRequestTask extends AsyncTask<Integer, Void, Void> {


        // u pozadini prikuplja podatke s web servisa ()
        @Override
        protected Void doInBackground(Integer... params) {
            try {

                Intent itnt = getIntent();
                String url = getResources().getString(R.string.jobServiceUrl) + "statistics/getTehnologyTrend?" + itnt.getStringExtra("searchParams");

//                String url = "http://192.168.1.1:8080/webService/statistics/getTehnologyTrend?tehnologyIds=1,3,6,4,7,10,13&startDate=2014-08-03&endDate=2015-03-03";

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

                XYSeries series = new XYSeries(SkillEnum.getName(monthlyTrendsList.get(0).getId().intValue()) + "     ");
                XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
                XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

                int i = 1;
                int allCounter = 0;
                Long prevId = 0L;
                // raščlanjuje set dobiven sa servisa u setove potrebne za kreiranje pojedine linije na grafu
                for (MonthlyTrend mt : monthlyTrendsList) {

                    if (prevId != mt.getId()) {
                        if (allCounter != 0) {
                            dataset.addSeries(series);
                            series = new XYSeries(SkillEnum.getName(mt.getId().intValue()) + "     ");
                            i=1;
                        }
                    }
                    series.add(i, mt.getCounter());
                    mRenderer.addXTextLabel(i, mt.getMonth());
                    i++;
                    prevId = mt.getId();
                    allCounter++;
                    if(monthlyTrendsList.size() == allCounter) {
                        dataset.addSeries(series);
                    }
                }

                // pronalazi max count za tehnologije
                MonthlyTrend maxSkillNum = Collections.max(monthlyTrendsList, new Comparator<MonthlyTrend>() {
                    public int compare(MonthlyTrend o1, MonthlyTrend o2) {
                        return Long.compare(o1.getCounter(), o2.getCounter());
                    }
                });

                // kreira sve grafičke linije za sve setove podataka
                for (int count = 0; count < dataset.getSeriesCount(); count++) {
                    // Now we create the renderer
                    XYSeriesRenderer renderer = new XYSeriesRenderer();
                    renderer.setLineWidth(3);
                    renderer.setColor(ColorArray.getColorArray()[count]);
                    // Include low and max value
                    renderer.setDisplayBoundingPoints(true);
                    // we add point markers
                    renderer.setPointStyle(PointStyle.CIRCLE);
                    renderer.setPointStrokeWidth(4);
                    mRenderer.addSeriesRenderer(renderer);
                }

                // izgled grafa
                mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00)); // transparent margins
                mRenderer.setPanEnabled(false, false);
                mRenderer.setYAxisMax(maxSkillNum.getCounter() + 5);
                mRenderer.setYAxisMin(0);
                mRenderer.setShowGrid(true); // we show the grid
                mRenderer.setLabelsTextSize(25);
                mRenderer.setYLabelsPadding(20);
                mRenderer.setLegendTextSize(30);
                mRenderer.setMargins(new int[]{30,45,60,40});
                mRenderer.setXLabels(0);
                mRenderer.setAxesColor(Color.rgb(0, 0, 0));
                mRenderer.setXLabelsColor(Color.rgb(0, 0, 0));
                mRenderer.setYLabelsColor(0, Color.rgb(0, 0, 0));

                GraphicalView chartView = ChartFactory.getLineChartView(MonthlySkillDiagramActivity.this, dataset, mRenderer);

                chartLyt.addView(chartView, 0);

//            }
        }
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
}
