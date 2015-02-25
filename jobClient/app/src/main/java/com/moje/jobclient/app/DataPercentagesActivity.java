package com.moje.jobclient.app;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DataPercentagesActivity extends ActionBarActivity {

    private int mYear;
    private int mMonth;
    private int mDay;

    private TextView mDateDisplay1;
    private Button mPickDate1;

    private TextView mDateDisplay2;
    private Button mPickDate2;

    private Button prikaziButton;
    private Spinner criteriaSpinner;

    static final int DATE_DIALOG_ID = 0;
    private int type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_percentages);

        mDateDisplay1 = (TextView) findViewById(R.id.showMyDate1);
        mPickDate1 = (Button) findViewById(R.id.myDatePickerButton1);

        mDateDisplay2 = (TextView) findViewById(R.id.showMyDate2);
        mPickDate2 = (Button) findViewById(R.id.myDatePickerButton2);

        prikaziButton = (Button) findViewById(R.id.prikaziButton);

        mPickDate1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
                type = 1;
            }
        });

        mPickDate2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
                type = 2;
            }
        });

        prikaziButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                mDateDisplay1 = (TextView) findViewById(R.id.showMyDate1);
                mDateDisplay2 = (TextView) findViewById(R.id.showMyDate2);
                criteriaSpinner = (Spinner) findViewById(R.id.criteriaSpinner);

                String startDate = mDateDisplay1.getText().toString();
                String endDate = mDateDisplay2.getText().toString();

                DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);
                Date start = null;
                Date end = null;
                try {
                    start = format.parse(startDate);
                    end = format.parse(endDate);
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }

                if (end.before(start)) {
                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(DataPercentagesActivity.this);
                    dlgAlert.setMessage("Datumi nisu ispravni");
                    dlgAlert.setTitle("");
                    dlgAlert.setPositiveButton("OK", null);
                    dlgAlert.setCancelable(true);
                    dlgAlert.create().show();
                    return;
                }

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                startDate = df.format(start);
                endDate = df.format(end);

                String criteriaString = criteriaSpinner.getSelectedItem().toString();

                if (criteriaString.equals("Kategorija posla")) {
                    criteriaString = "category";
                }
                else if (criteriaString.equals("Zupanija rada")) {
                    criteriaString = "county";
                }
                else if (criteriaString.equals("Tip posla")) {
                    criteriaString = "jobtype";
                }
                else if (criteriaString.equals("Kvalifikacija")) {
                    criteriaString = "qualification";
                }
                else if (criteriaString.equals("Tehnologija")) {
                    criteriaString = "skill";
                }

                Intent jobIntent = new Intent(DataPercentagesActivity.this, DataPercentagesDiagramActivity.class);

                String path = "type=" + criteriaString + "&startDate=" + startDate + "&endDate=" + endDate;
                jobIntent.putExtra("searchParams", path);
                jobIntent.putExtra("criteria", criteriaString);
                startActivity(jobIntent);

//		http://localhost:8080/webService/statistics/getPercentages?type=category&startDate=2014-12-25&endDate=2015-01-15
            }
        });

           // get the current date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        // display the current date
        updateDisplay();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);

        }
        return null;
    }

    private void updateDisplay() {

        switch (type) {
            case 0:
                this.mDateDisplay1.setText(
                        new StringBuilder()
                                .append(mDay).append(".")
                                .append(mMonth + 1).append(".")
                                .append(mYear));
                this.mDateDisplay2.setText(
                        new StringBuilder()
                                .append(mDay).append(".")
                                .append(mMonth + 1).append(".")
                                .append(mYear));
                break;
            case 1:
                this.mDateDisplay1.setText(
                        new StringBuilder()
                                .append(mDay).append(".")
                                .append(mMonth + 1).append(".")
                                .append(mYear));
                break;
            case 2:
                this.mDateDisplay2.setText(
                        new StringBuilder()
                                .append(mDay).append(".")
                                .append(mMonth + 1).append(".")
                                .append(mYear));
                break;
        }
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear;
                    mDay = dayOfMonth;
                    updateDisplay();
                }
            };



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_data_percentages, menu);
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
