package com.moje.jobclient.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import enums.CategoryEnum;
import enums.CountyEnum;
import enums.JobTypeEnum;
import enums.QualificationEnum;
import enums.SkillEnum;
import rest.DataPercentages;
import rest.JobSearchResult;
import rest.MonthlyTrend;
import services.rest.RestService;


public class DataPercentagesDiagramActivity extends ActionBarActivity {

    private List<DataPercentages> dataPercentagesList = new ArrayList<DataPercentages>();
    private LinearLayout chartLyt;
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_percentages_diagram);

        new HttpRequestTask().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_data_percentages_diagram, menu);
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
                String url = getResources().getString(R.string.jobServiceUrl) + "statistics/getPercentages?" + itnt.getStringExtra("searchParams");

                RestService rs = new RestService();

                dataPercentagesList = new ArrayList<DataPercentages>();
                dataPercentagesList = rs.getList(url, DataPercentages[].class);


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

                chartLyt = (LinearLayout) findViewById(R.id.chart);

                Intent itnt = getIntent();
                XYSeries series = new XYSeries(itnt.getStringExtra("criteria"));
                XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

                series.add(0, 0);
                mRenderer.addXTextLabel(0, "0");

                Collections.sort(dataPercentagesList, new Comparator<DataPercentages>() {
                    public int compare(DataPercentages o1, DataPercentages o2) {
                        return o1.getId().compareTo(o2.getId());
                    }
                });

                for (DataPercentages dp : dataPercentagesList) {
                    series.add(dp.getId(), dp.getPercentage().doubleValue());
                    mRenderer.addXTextLabel(dp.getId(), dp.getId().toString());
                }

                String[] strLegendList = new String[dataPercentagesList.size()];

                String title = "";
                if (itnt.getStringExtra("criteria").equals("category")) {
                    title = "Kategorije posla";
                    for (int i = 0; i < dataPercentagesList.size(); i++)
                        strLegendList[i] = dataPercentagesList.get(i).getId() + " - " + CategoryEnum.getName(dataPercentagesList.get(i).getId().intValue());
                } else if (itnt.getStringExtra("criteria").equals("county")) {
                    title = "Županije rada";
                    for (int i = 0; i < dataPercentagesList.size(); i++)
                        strLegendList[i] = dataPercentagesList.get(i).getId() + " - " + CountyEnum.getName(dataPercentagesList.get(i).getId().intValue());
                } else if (itnt.getStringExtra("criteria").equals("jobtype")) {
                    title = "Tipovi posla";
                    for (int i = 0; i < dataPercentagesList.size(); i++)
                        strLegendList[i] = dataPercentagesList.get(i).getId() + " - " + JobTypeEnum.getName(dataPercentagesList.get(i).getId().intValue());
                } else if (itnt.getStringExtra("criteria").equals("qualification")) {
                    title = "Stručne spreme";
                    for (int i = 0; i < dataPercentagesList.size(); i++)
                        strLegendList[i] = dataPercentagesList.get(i).getId() + " - " + QualificationEnum.getName(dataPercentagesList.get(i).getId().intValue());
                } else if (itnt.getStringExtra("criteria").equals("skill")) {
                    title = "Tehnologije";
                    for (int i = 0; i < dataPercentagesList.size(); i++)
                        strLegendList[i] = dataPercentagesList.get(i).getId() + " - " + SkillEnum.getName(dataPercentagesList.get(i).getId().intValue());
                }

                DataPercentages dataPerMax = Collections.max(dataPercentagesList, new Comparator<DataPercentages>() {
                    public int compare(DataPercentages o1, DataPercentages o2) {
                        return o1.getPercentage().compareTo(o2.getPercentage());
                    }
                });


                XYSeriesRenderer renderer = new XYSeriesRenderer();
                renderer.setLineWidth(2);
                renderer.setColor(Color.RED);
                renderer.setDisplayBoundingPoints(true);
                renderer.setPointStyle(PointStyle.CIRCLE);
                renderer.setPointStrokeWidth(3);
                renderer.setDisplayChartValues(true);
                renderer.setDisplayChartValuesDistance(10);
                renderer.setChartValuesTextSize(13);

                XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
                dataset.addSeries(series);

                mRenderer.addSeriesRenderer(renderer);
                mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));
                mRenderer.setPanEnabled(false, false);
                mRenderer.setYAxisMax(dataPerMax.getPercentage().longValue() + 2);
                mRenderer.setYAxisMin(0);
                mRenderer.setShowGrid(true);
                mRenderer.setLabelsTextSize(20);
                mRenderer.setYLabelsPadding(20);
                mRenderer.setXLabels(0);
                mRenderer.setAxesColor(Color.rgb(0, 0, 0));
                mRenderer.setXLabelsColor(Color.rgb(0, 0, 0));
                mRenderer.setYLabelsColor(0, Color.rgb(0, 0, 0));
                mRenderer.setBarSpacing(0.5);
                mRenderer.setBarWidth(25);
                mRenderer.setChartTitle(title);
                mRenderer.setChartTitleTextSize(30);
                mRenderer.setShowLegend(false);

                GraphicalView chartView = ChartFactory.getBarChartView(DataPercentagesDiagramActivity.this, dataset, mRenderer, BarChart.Type.DEFAULT);

                chartLyt.addView(chartView, 0);

                gridView = (GridView) findViewById(R.id.legendGrid);
                gridView.setAdapter(new TextViewAdapter(DataPercentagesDiagramActivity.this, strLegendList));
//            }
        }
    }

    public class TextViewAdapter extends BaseAdapter {
        private Context context;
        private final String[] textViewValues;

        public TextViewAdapter(Context context, String[] textViewValues) {
            this.context = context;
            this.textViewValues = textViewValues;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View gridView;

            if (convertView == null) {

                gridView = new View(context);

                // get layout from mobile.xml
                gridView = inflater.inflate(R.layout.legend_text, null);

                // set value into textview
                TextView textView = (TextView) gridView
                        .findViewById(R.id.legendText);
                textView.setText(textViewValues[position]);
            } else {
                gridView = (View) convertView;
            }

            return gridView;
        }

        @Override
        public int getCount() {
            return textViewValues.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

    }
}
