package com.moje.jobclient.app;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.util.List;

import rest.GenericType;
import rest.Job;
import rest.JobRating;
import services.rest.RestService;


public class JobActivity extends ActionBarActivity {

    private Job job;
    private String strCounties;
    private String strCategories;
    private String strQualifications;
    private String strJobTypes;

    TextView poslodavacAdresa;

    MultiValueMap<String, String> paramsMap;


    // prihvaća se id posla od JobListActivity intenta.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job);

        new HttpRequestTask().execute();

        Button ocijeniButton = (Button) findViewById(R.id.ocijeniButton);

        // dobiva parametre sa ekrana i šalje ih na JobListActivity
        ocijeniButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Spinner radNaRadomSpinner = (Spinner) findViewById(R.id.radNaRadomSpinner);
                Spinner razumljivSpinner = (Spinner) findViewById(R.id.razumljivSpinner);
                Spinner preopsezanSpinner = (Spinner) findViewById(R.id.preopsezanSpinner);

                String radNaRadomString = radNaRadomSpinner.getSelectedItem().toString();
                String razumljivString = razumljivSpinner.getSelectedItem().toString();
                String preopsezanString = preopsezanSpinner.getSelectedItem().toString();

                paramsMap = new LinkedMultiValueMap<>();
                if(!radNaRadomString.equals("Izaberi ocjenu")) {
                    paramsMap.add("descRating", radNaRadomString.substring(0,1));
                }
                if(!razumljivString.equals("Izaberi ocjenu")) {
                    paramsMap.add("jobRating", razumljivString.substring(0,1));
                }
                if(!preopsezanString.equals("Izaberi ocjenu")) {
                    paramsMap.add("empRequest", preopsezanString.substring(0,1));
                }

                if (paramsMap.size() == 0) {
                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(JobActivity.this);
                    dlgAlert.setMessage("Ocijenite barem jedno pitanje!");
                    dlgAlert.setTitle("Ocjenjivanje");
                    dlgAlert.setPositiveButton("OK", null);
                    dlgAlert.setCancelable(true);
                    dlgAlert.create().show();
                } else {
                    paramsMap.add("jobId", String.valueOf(job.getId()));
                    new HttpSendRatingsTask().execute();
                }


            }
        });

        poslodavacAdresa = (TextView) findViewById(R.id.poslodavacAdresa);
        poslodavacAdresa.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent jobIntent = new Intent(JobActivity.this, LocationActivity.class);
                jobIntent.putExtra("address", poslodavacAdresa.getText());
                startActivity(jobIntent);
            }
        });
    }

    /*
     * šalje ocjene na server
     */
    private class HttpSendRatingsTask extends AsyncTask<Void, Void, Void> {

        String uspjeh = "Ocijenjivanje uspješno!";

        @Override
        protected Void doInBackground(Void... params) {
            try {

                String url = getResources().getString(R.string.jobServiceUrl) + "job/rateJob";

                RestService rs = new RestService();
                String status = rs.postData(url, paramsMap);

                new HttpRequestTask().execute();

                paramsMap.clear();
                paramsMap = null;

            } catch (Exception e) {
                uspjeh = "Vjerojatno EOF Exception!";
                Log.e("JobListActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(JobActivity.this);
            dlgAlert.setMessage(uspjeh);
            dlgAlert.setTitle("Ocjenjivanje");
            dlgAlert.setPositiveButton("OK", null);
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
        }
    }


    private class HttpRequestTask extends AsyncTask<Void, Void, Void> {

        // u pozadini prikuplja podatke s web servisa ()
        @Override
        protected Void doInBackground(Void... params) {
            try {

                Intent itnt = getIntent();
                String jobId = itnt.getStringExtra("job_id");
//                jobId="251905";
                String url = getResources().getString(R.string.jobServiceUrl) + "job/get/" + jobId;

                RestService rs = new RestService();
                job = rs.getItem(url, Job.class);

            } catch (Exception e) {
                Log.e("JobListActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            strCounties = getStringFromList(job.getCounties());
            strCategories = getStringFromList(job.getCategories());
            strQualifications = getStringFromList(job.getQualifications());
            strJobTypes = getStringFromList(job.getJobTypes());

            // naziv posla
            TextView naslovPosla = (TextView) findViewById(R.id.naslovPosla);
            naslovPosla.setText(job.getTitle());

            if (job.getEmployer().getName() != null) {
                TextView poslodavacLabel = (TextView) findViewById(R.id.poslodavacLabel);
                poslodavacLabel.setTypeface(poslodavacLabel.getTypeface(), Typeface.BOLD);
                TextView poslodavac = (TextView) findViewById(R.id.poslodavac);
                poslodavac.setText(job.getEmployer().getName());
                poslodavacLabel.setVisibility(View.VISIBLE);
                poslodavac.setVisibility(View.VISIBLE);
            }

            // adresa
            TextView poslodavacAdresaLabel = (TextView) findViewById(R.id.poslodavacAdresaLabel);
            poslodavacAdresaLabel.setTypeface(poslodavacAdresaLabel.getTypeface(), Typeface.BOLD);
            poslodavacAdresa = (TextView) findViewById(R.id.poslodavacAdresa);

            if (job.getEmployer().getAddress() != null) {
                poslodavacAdresa.setText(job.getEmployer().getAddress());
                poslodavacAdresa.setTextColor(Color.rgb(42, 82, 239));
                poslodavacAdresa.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            } else {
                poslodavacAdresa.setText("Nije dostupna");
                poslodavacAdresa.setClickable(false);
            }

            poslodavacAdresaLabel.setVisibility(View.VISIBLE);
            poslodavacAdresa.setVisibility(View.VISIBLE);

            // opis posla
            if (job.getDescription() != null) {
                TextView opisPoslaLabel = (TextView) findViewById(R.id.opisPoslaLabel);
                opisPoslaLabel.setTypeface(opisPoslaLabel.getTypeface(), Typeface.BOLD);
                TextView opisPosla = (TextView) findViewById(R.id.opisPosla);
                opisPosla.setText(job.getDescription());
                opisPoslaLabel.setVisibility(View.VISIBLE);
                opisPosla.setVisibility(View.VISIBLE);
            }

            if (job.getConditions() != null && !job.getConditions().equals("")) {
                TextView uvjetiLabel = (TextView) findViewById(R.id.uvjetiLabel);
                uvjetiLabel.setTypeface(uvjetiLabel.getTypeface(), Typeface.BOLD);
                TextView uvjeti = (TextView) findViewById(R.id.uvjeti);
                uvjeti.setText(job.getConditions());
                uvjetiLabel.setVisibility(View.VISIBLE);
                uvjeti.setVisibility(View.VISIBLE);
            }

            if (strQualifications != null) {
                TextView strucnaSpremaLabel = (TextView) findViewById(R.id.strucnaSpremaLabel);
                strucnaSpremaLabel.setTypeface(strucnaSpremaLabel.getTypeface(), Typeface.BOLD);
                TextView strucnaSprema = (TextView) findViewById(R.id.strucnaSprema);
                strucnaSprema.setText(strQualifications);
                strucnaSpremaLabel.setVisibility(View.VISIBLE);
                strucnaSprema.setVisibility(View.VISIBLE);
            }

            if (job.getYearsOfExperience() != null) {
                TextView godineIskustvaLabel = (TextView) findViewById(R.id.godineIskustvaLabel);
                godineIskustvaLabel.setTypeface(godineIskustvaLabel.getTypeface(), Typeface.BOLD);
                TextView godineIskustva = (TextView) findViewById(R.id.godineIskustva);
                godineIskustva.setText(job.getYearsOfExperience());
                godineIskustvaLabel.setVisibility(View.VISIBLE);
                godineIskustva.setVisibility(View.VISIBLE);
            }

            if (job.getLanguages() != null) {
                TextView jeziciLabel = (TextView) findViewById(R.id.jeziciLabel);
                jeziciLabel.setTypeface(jeziciLabel.getTypeface(), Typeface.BOLD);
                TextView jezici = (TextView) findViewById(R.id.jezici);
                jezici.setText(job.getLanguages());
                jeziciLabel.setVisibility(View.VISIBLE);
                jezici.setVisibility(View.VISIBLE);
            }

            if (job.getSkills() != null) {
                TextView vjestineLabel = (TextView) findViewById(R.id.vjestineLabel);
                vjestineLabel.setTypeface(vjestineLabel.getTypeface(), Typeface.BOLD);
                TextView vjestine = (TextView) findViewById(R.id.vjestine);
                vjestine.setText(job.getSkills());
                vjestineLabel.setVisibility(View.VISIBLE);
                vjestine.setVisibility(View.VISIBLE);
            }

            if (job.getDrivingLicence() != null) {
                TextView vozackaLabel = (TextView) findViewById(R.id.vozackaLabel);
                vozackaLabel.setTypeface(vozackaLabel.getTypeface(), Typeface.BOLD);
                TextView vozacka = (TextView) findViewById(R.id.vozacka);
                vozacka.setText(job.getDrivingLicence());
                vozackaLabel.setVisibility(View.VISIBLE);
                vozacka.setVisibility(View.VISIBLE);
            }

            if (job.getEmployerOffer() != null) {
                TextView stoNudimoLabel = (TextView) findViewById(R.id.stoNudimoLabel);
                stoNudimoLabel.setTypeface(stoNudimoLabel.getTypeface(), Typeface.BOLD);
                TextView stoNudimo = (TextView) findViewById(R.id.stoNudimo);
                stoNudimo.setText(job.getEmployerOffer());
                stoNudimoLabel.setVisibility(View.VISIBLE);
                stoNudimo.setVisibility(View.VISIBLE);
            }

            if (strJobTypes != null) {
                TextView kategorijeLabel = (TextView) findViewById(R.id.kategorijeLabel);
                kategorijeLabel.setTypeface(kategorijeLabel.getTypeface(), Typeface.BOLD);
                TextView kategorije = (TextView) findViewById(R.id.kategorije);
                kategorije.setText(strJobTypes);
                kategorijeLabel.setVisibility(View.VISIBLE);
                kategorije.setVisibility(View.VISIBLE);
            }

            if (strCategories != null) {
                TextView vrstaZaposlenjaLabel = (TextView) findViewById(R.id.vrstaZaposlenjaLabel);
                vrstaZaposlenjaLabel.setTypeface(vrstaZaposlenjaLabel.getTypeface(), Typeface.BOLD);
                TextView vrstaZaposlenja = (TextView) findViewById(R.id.vrstaZaposlenja);
                vrstaZaposlenja.setText(strCategories);
                vrstaZaposlenjaLabel.setVisibility(View.VISIBLE);
                vrstaZaposlenja.setVisibility(View.VISIBLE);
            }

            if (strCounties != null) {
                TextView zupanijaLabel = (TextView) findViewById(R.id.zupanijaLabel);
                zupanijaLabel.setTypeface(zupanijaLabel.getTypeface(), Typeface.BOLD);
                TextView zupanija = (TextView) findViewById(R.id.zupanija);
                zupanija.setText(strCounties);
                zupanijaLabel.setVisibility(View.VISIBLE);
                zupanija.setVisibility(View.VISIBLE);
            }

            ScrollView posaoScroll = (ScrollView) findViewById(R.id.posaoScroll);
            TextView ocijeniOglasLabel = (TextView) findViewById(R.id.ocijeniOglasLabel);

            // ocjene
            TextView radNaRadnomOcjena = (TextView) findViewById(R.id.radNaRadomOcjena);
            TextView razumljivOcjena = (TextView) findViewById(R.id.razumljivOcjena);
            TextView preopsezanOcjena = (TextView) findViewById(R.id.preopsezanOcjena);
            TextView radNaRadomBrojOcjena = (TextView) findViewById(R.id.radNaRadomBrojOcjena);
            TextView razumljivBrojOcjena = (TextView) findViewById(R.id.razumljivBrojOcjena);
            TextView preopsezanBrojOcjena = (TextView) findViewById(R.id.preopsezanBrojOcjena);

            ocijeniOglasLabel.setTypeface(ocijeniOglasLabel.getTypeface(), Typeface.BOLD);

//            if (job.getJobRatings().size() != 0) {

            boolean radNaRadnomIsNull = true;
            boolean razumljivIsNull = true;
            boolean preopsezanIsNull = true;
            for (JobRating jobRating : job.getJobRatings()) {

                // Biste li željeli raditi na ovom radnom mjestu
                if (jobRating.getRatingType().getId() == 1) {
                    radNaRadnomOcjena.setText("Ocjena: " + String.valueOf(round((float) jobRating.getRatingSum() / jobRating.getRatingNum(), 2)));
                    radNaRadomBrojOcjena.setText(jobRating.getRatingNum() + " ljudi");
                    radNaRadnomIsNull = false;
                }
                // je li oglas dovoljno razumljiv
                else if (jobRating.getRatingType().getId() == 2) {
                    razumljivOcjena.setText("Ocjena: " + String.valueOf(round((float) jobRating.getRatingSum() / jobRating.getRatingNum(), 2)));
                    razumljivBrojOcjena.setText(jobRating.getRatingNum() + " ljudi");
                    razumljivIsNull = false;
                }
                // jesu li zahtjevi koje poslodavac traži od posloprimca preopsežni
                else if (jobRating.getRatingType().getId() == 3) {
                    preopsezanOcjena.setText("Ocjena: " + String.valueOf(round((float) jobRating.getRatingSum() / jobRating.getRatingNum(), 2)));
                    preopsezanBrojOcjena.setText(jobRating.getRatingNum() + " ljudi");
                    preopsezanIsNull = false;
                }
            }

            if(radNaRadnomIsNull) {
                radNaRadnomOcjena.setText("Ocjena: 0.00");
                radNaRadomBrojOcjena.setText("0 ljudi");
            }
            if(razumljivIsNull) {
                razumljivOcjena.setText("Ocjena: 0.00");
                razumljivBrojOcjena.setText("0 ljudi");
            }
            if(preopsezanIsNull) {
                preopsezanOcjena.setText("Ocjena: 0.00");
                preopsezanBrojOcjena.setText("0 ljudi");
            }

            posaoScroll.setVisibility(View.VISIBLE);
        }
    }

    // zaokružuje float na dvije decimale
    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    /*
     * - iz liste vuče podatke i pretvara ih u string tako da budu jedan ispod drugoga
     *   da bi se ispravno prikazali na ekranu
     * - koristi se za category, county, jobtype i qualification
     */
    private <T> String getStringFromList(List<T> items) {
        String itemNames = "";
        for (T item : items) {
            itemNames += (((GenericType) item).getName()) + "\n";
        }

        if (itemNames.equals("")) {
            return null;
        }
        return itemNames.substring(0, itemNames.length() - 1);
    }

//    public class JobListAdapter  extends ArrayAdapter<JobSearchResult> {
//
//        private final Context context;
//
//        public JobListAdapter(Context context, int textViewResourceId, List<JobSearchResult> jobList) {
//            super(context, textViewResourceId, jobList);
//            this.context = context;
//        }
//
//        @Override
//        public View getView(final int position, View convertView, ViewGroup parent) {
//
//            JobSearchResult item = (JobSearchResult)getItem(position);
//
//            LayoutInflater inflater = (LayoutInflater) context
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            View rowView = inflater.inflate(R.layout.tv_job_in_list, parent, false);
//            TextView text1 = (TextView) rowView.findViewById(R.id.text1);
//            TextView text2 = (TextView) rowView.findViewById(R.id.text2);
//
//            text1.setText(item.getJobTitle());
//            text2.setText(item.getEmployerName());
//
//            rowView.setTag(item.getId());
//
//            return rowView;
//        }
//    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.job, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id != R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

}
