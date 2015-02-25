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
import java.util.List;

import rest.Job;
import rest.JobSearchResult;
import rest.MonthlyTrend;
import services.rest.RestService;


public class SkillTrendActivity extends ActionBarActivity {

    private List<MonthlyTrend> monthlyTrendsList = new ArrayList<MonthlyTrend>();
    private LinearLayout chartLyt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill_trend);

        new HttpRequestTask().execute();


        chartLyt = (LinearLayout) findViewById(R.id.chart);


        XYSeries series = new XYSeries("London Temperature hourly");

        for (int i = 0; i<10; i++) {
            series.add(i, i+13);
        }
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


        XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
        mRenderer.addSeriesRenderer(renderer);



        // We want to avoid black border
        mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00)); // transparent margins
        // Disable Pan on two axis
        mRenderer.setPanEnabled(false, false);
        mRenderer.setYAxisMax(35);
        mRenderer.setYAxisMin(0);
        mRenderer.setShowGrid(true); // we show the grid

        GraphicalView chartView = ChartFactory.getLineChartView(SkillTrendActivity.this, dataset, mRenderer);

        chartLyt.addView(chartView,0);
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

        private List<JobSearchResult> tempList;

        // u pozadini prikuplja podatke s web servisa ()
        @Override
        protected Void doInBackground(Integer... params) {
            try {

                Intent itnt = getIntent();
                String jobId = itnt.getStringExtra("job_id");
//                jobId="251905";
                String url = getResources().getString(R.string.jobServiceUrl) + "statistics/getTehnologyTrend?tehnologyId=10&startDate=2015-01-23&endDate=2015-02-07";

//		http://localhost:8080/webService/statistics/getTehnologyTrend?tehnologyId=10&startDate=2015-01-23&endDate=2015-02-07

                RestService rs = new RestService();

                monthlyTrendsList = new ArrayList<MonthlyTrend>();
                tempList = rs.getList(url, MonthlyTrend[].class);

                tempList.get(0);


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

        }
    }
}
