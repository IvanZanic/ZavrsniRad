package com.moje.jobclient.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rest.JobSearchResult;
import services.rest.RestService;


public class JobListActivity extends ActionBarActivity {

    private ListView lvJobList;
    private HttpRequestTask myTask;
    private List<JobSearchResult> jobList = new ArrayList<JobSearchResult>();
    private boolean loading = true;
    private JobListAdapter adapter;
    private String url;
    private String sortBy = "";

//    kod kreiranja menua za sortiranje
    private int group1Id = 1;
    private int objava = Menu.FIRST;
    private int rok = Menu.FIRST +1;
    private int ocjena = Menu.FIRST +2;

//    za onscroll listener
    private int visibleThreshold = 1;
    private int currentPage = -1;
    private int previousTotal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_list);

        Intent itnt = getIntent();
        url = getResources().getString(R.string.jobServiceUrl) + "job/getList" + itnt.getStringExtra("searchParams") + "offset=";

        lvJobList = (ListView) findViewById(R.id.lvJobList);
        adapter = new JobListAdapter(JobListActivity.this, R.id.lvJobList, jobList);// data is List<JobSearchResult> to be added in list view
        lvJobList.setAdapter(adapter);
        lvJobList.setOnScrollListener(new EndlessScrollListener());
        lvJobList.setOnItemClickListener(new OnJobClickListener());

        sortBy = "publishDate";
        myTask = new HttpRequestTask();
        myTask.execute(0);
    }

    /**
     * - Kreira se click event koji se okida kada se na listview pritisne neki posao i
     *   pokreće se novi activity koji prikazuje detalje jednog posla (šalje se id posla kroz intent)
     */
    private class OnJobClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            Intent jobIntent = new Intent(JobListActivity.this, JobActivity.class);

            jobIntent.putExtra("job_id", view.getTag().toString());
            startActivity(jobIntent);

        }
    }

    /**
     *  - poziva HttpRequestTask da bi loadao dodatne podatke kada se scroll-a do kraja trenutnih podataka
     */
    private class EndlessScrollListener implements AbsListView.OnScrollListener {

        public EndlessScrollListener() {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {

            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                    currentPage++;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                // I load the next page of gigs using a background task,
                // but you can call any function here.
                if(myTask != null && (myTask.getStatus() == AsyncTask.Status.FINISHED)) {
                    myTask = new HttpRequestTask();
                    myTask.execute((currentPage + 1) * 20);
                    loading = true;
                }
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
    }

    /**
     * Created by ivan on 27.12.14..
     *
     * - custom adapter: služi da bi se prilagodio pogled na listView
     * - Funkcionira tako da za svaki item iz liste napravi jedan red listView-a
     */
    public class JobListAdapter  extends ArrayAdapter<JobSearchResult> {

        private final Context context;

        public JobListAdapter(Context context, int textViewResourceId, List<JobSearchResult> jobList) {
            super(context, textViewResourceId, jobList);
            this.context = context;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            JobSearchResult item = (JobSearchResult)getItem(position);

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.tv_job_in_list, parent, false);
            TextView tvNaslovPosla = (TextView) rowView.findViewById(R.id.naslovPosla);
            TextView tvPoslodavac = (TextView) rowView.findViewById(R.id.poslodavac);
            TextView tvRokPrijave = (TextView) rowView.findViewById(R.id.rokPrijave);
            TextView ocjenaValue = (TextView) rowView.findViewById(R.id.ocjenaLabel);

            tvNaslovPosla.setText(item.getJobTitle());
            tvPoslodavac.setText(item.getEmployerName());

            if (item.getTotalRating() == null) {
                ocjenaValue.setText("Ocjena: 0.00");
            }
            else {
                ocjenaValue.setText("Ocjena: " + item.getTotalRating().toPlainString());
            }

            Date rokPrijaveDate = item.getDeadline();
            SimpleDateFormat dt1 = new SimpleDateFormat("dd.MM.yyyy");

            tvRokPrijave.setText("Rok prijave: " + dt1.format(rokPrijaveDate).toString());

            rowView.setTag(item.getId());

            return rowView;
        }
    }

    // radi se s async da ekran ne izgleda kao da se smrznuo
    private class HttpRequestTask extends AsyncTask<Integer, String, Void> {

        private List<JobSearchResult> tempList;

        // u pozadini prikuplja podatke s web servisa ()
        @Override
        protected Void doInBackground(Integer... params) {
            try {

                String localUrl = url + params[0] + "&sortBy=" + sortBy;

                RestService rs = new RestService();
                tempList = new ArrayList<JobSearchResult>();
                tempList = rs.getList(localUrl, JobSearchResult[].class);

                if(params[0] == 0) {
                    jobList.clear();
                    visibleThreshold = 1;
                    currentPage = -1;
                    previousTotal = 0;
                }
                for (JobSearchResult jsr : tempList) {
                    jobList.add(jsr);
                }

                loading = true;
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

//            if (jobList.size() == 0) {
//                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(JobListActivity.this);
//                dlgAlert.setMessage("Nije se moguće spojiti");
//                dlgAlert.setTitle("Spajanje na servis");
//                dlgAlert.setPositiveButton("OK", null);
//                dlgAlert.setCancelable(true);
//                dlgAlert.create().show();
//            } else {

//            loading = false;
                adapter.notifyDataSetChanged();
//            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add("Sortiraj po").setEnabled(false);
        menu.add(group1Id, objava, objava, "Datumu objave");
        menu.add(group1Id, rok, rok, "Roku za prijavu");
        menu.add(group1Id, ocjena, ocjena, "Ukupnoj ocjeni");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case 1:
                sortBy = "publishDate";
                myTask = new HttpRequestTask();
                myTask.execute(0);
                return true;

            case 2:
                sortBy = "deadline";
                myTask = new HttpRequestTask();
                myTask.execute(0);
                return true;

            case 3:
                sortBy = "totalRating";
                myTask = new HttpRequestTask();
                myTask.execute(0);
                return true;

            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
