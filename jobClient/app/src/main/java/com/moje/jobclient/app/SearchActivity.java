package com.moje.jobclient.app;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import enums.CategoryEnum;
import enums.CountyEnum;
import enums.JobTypeEnum;
import enums.QualificationEnum;
import enums.SkillEnum;
import rest.Category;


public class SearchActivity extends ActionBarActivity {

    private Spinner jobTypeSpinner;
    private Spinner countySpinner;
    private Spinner categorySpinner;
    private Spinner qualificationSpinner;
    private Spinner skillSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Button searchButton = (Button) findViewById(R.id.searchButton);
        Button kriterijButton = (Button) findViewById(R.id.kriterijButton);
        Button trazenostButton = (Button) findViewById(R.id.trazenostButton);

        jobTypeSpinner = (Spinner) findViewById(R.id.jobTypeSpinner);
        countySpinner = (Spinner) findViewById(R.id.countySpinner);
        categorySpinner = (Spinner) findViewById(R.id.categorySpinner);
        qualificationSpinner = (Spinner) findViewById(R.id.qualificationSpinner);
        skillSpinner = (Spinner) findViewById(R.id.skillSpinner);

        skillSpinner.setEnabled(false);

        // dobiva parametre sa ekrana i šalje ih na JobListActivity
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String jobTypeString = jobTypeSpinner.getSelectedItem().toString();
                String countyString = countySpinner.getSelectedItem().toString();
                String categoryString = categorySpinner.getSelectedItem().toString();
                String qualificationString = qualificationSpinner.getSelectedItem().toString();
                String skillString = skillSpinner.getSelectedItem().toString();

                StringBuffer params = new StringBuffer();
                params.append("?");

                if (!jobTypeString.equals("Izaberi tip posla")) {
                    params.append("jobtypeId=" + JobTypeEnum.getId(jobTypeString) + "&");
                }
                if (!countyString.equals("Izaberi županiju")) {
                    params.append("countyId=" + CountyEnum.getId(countyString) + "&");
                }
                if (!categoryString.equals("Izaberi kategoriju posla")) {
                    params.append("categoryId=" + CategoryEnum.getId(categoryString) + "&");
                }
                if (!qualificationString.equals("Izaberi stručnu spremu")) {
                    params.append("qualificationId=" + QualificationEnum.getId(qualificationString) + "&");
                }
                if (!skillString.equals("Izaberi tehnologiju")) {
                    params.append("skillId=" + SkillEnum.getId(skillString) + "&");
                }

                Intent jobIntent = new Intent(SearchActivity.this, JobListActivity.class);

                jobIntent.putExtra("searchParams", params.toString());
                startActivity(jobIntent);
            }
        });

        kriterijButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent dataPercentagesIntent = new Intent(SearchActivity.this, DataPercentagesActivity.class);
                startActivity(dataPercentagesIntent);
            }
        });

        trazenostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent monthlySkillIntent = new Intent(SearchActivity.this, MonthlySkillActivity.class);
                startActivity(monthlySkillIntent);
            }
        });

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    if (CategoryEnum.getName(position).equals("IT, telekomunikacije")) {
//                        Toast.makeText(parent.getContext(), CategoryEnum.getName(position), Toast.LENGTH_SHORT).show();
                        skillSpinner.setEnabled(true);
                    } else {
//                        Toast.makeText(parent.getContext(), "else", Toast.LENGTH_SHORT).show();
                        skillSpinner.setEnabled(false);
                        skillSpinner.setSelection(0);
                    }
                } else {
//                    Toast.makeText(parent.getContext(), "position = 0", Toast.LENGTH_SHORT).show();
                    skillSpinner.setEnabled(false);
                    skillSpinner.setSelection(0);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                Toast.makeText(parent.getContext(), "nothing selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
