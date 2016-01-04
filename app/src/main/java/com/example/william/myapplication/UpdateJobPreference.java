package com.example.william.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;

public class UpdateJobPreference extends ActionBarActivity {

    private int INSERT_SALARY = 1;
    private int SELECT_JOB_TYPE = 2;
    private int MALAYSIA_STATE = 3;
    private int OTHER_COUNTRY = 4;

    private TextView insertedSalary;
    private TextView selectedCurrency;
    private TextView selectedJobType;
    private TextView selectedMalaysiaState;
    private TextView selectedOtherCountry;

    TableJobPreference tableJobPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_job_preference);

        LinearLayout insertSalary = (LinearLayout)findViewById(R.id.insertSalary);
        LinearLayout selectJobType = (LinearLayout)findViewById(R.id.selectJobType);
        LinearLayout selectMalaysiaState = (LinearLayout)findViewById(R.id.selectMalaysiaState);
        LinearLayout selectOtherCountry = (LinearLayout)findViewById(R.id.selectOtherCountry);

        insertedSalary = (TextView)findViewById(R.id.insertedSalary);
        selectedCurrency = (TextView)findViewById(R.id.selectedCurrency);
        selectedJobType = (TextView)findViewById(R.id.selectedJobType);
        selectedMalaysiaState = (TextView)findViewById(R.id.selectedMalaysiaState);
        selectedOtherCountry = (TextView)findViewById(R.id.selectedOtherCountry);

        HashMap listOfJobTypes = Jenjobs.getJobType();
        HashMap listOfStates = Jenjobs.getState();
        HashMap listOfCountries = Jenjobs.getCountry();
        HashMap listOfCurrencies = Jenjobs.getCurrency();

        /*
        * fetch saved data
        * */
        tableJobPreference = new TableJobPreference(this);
        Cursor c = tableJobPreference.getJobPreference();
        if( c.moveToFirst() ){
            String savedSalary = c.getString(0);

            insertedSalary.setText(savedSalary);
            selectedCurrency.setText( (String)listOfCurrencies.get( c.getString(1) ) );
            selectedJobType.setText( (String)listOfJobTypes.get( c.getString(4) ));
            selectedMalaysiaState.setText( (String)listOfStates.get( c.getString(2) ) );
            selectedOtherCountry.setText( (String)listOfCountries.get( c.getString(3) ) );
        }

        /*
        * clicked on salary
        * */
        insertSalary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                // TODO: pass already inserted salary and currency record from db
                intent.setClass(getApplicationContext(), UpdateSalary.class);
                startActivityForResult(intent, INSERT_SALARY);
            }
        });

        /*
        * clicked on job type
        * */
        selectJobType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                // TODO: pass already inserted job type from db
                intent.setClass(getApplicationContext(), SelectJobType.class);
                intent.putExtra("single", true);
                startActivityForResult(intent, SELECT_JOB_TYPE);
            }
        });

        /*
        * clicked on malaysia states
        * */
        selectMalaysiaState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), SelectState.class);
                // TODO: pass already inserted value from db
                startActivityForResult(intent, MALAYSIA_STATE);
            }
        });

        /*
        * clicked on other country
        * */
        selectOtherCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), SelectCountry.class);
                // TODO: pass already inserted value from db
                startActivityForResult(intent, OTHER_COUNTRY);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INSERT_SALARY) {
            if (resultCode == RESULT_OK) {
                Bundle extra = data.getExtras();

                String salary = (String)extra.get("the_text");
                MyCurrency c = (MyCurrency)extra.get("the_currency");

                insertedSalary.setText(salary);
            }
        }else if( requestCode == SELECT_JOB_TYPE ){
            if (resultCode == RESULT_OK) {
                Bundle extra = data.getExtras();

                JobType jobType = (JobType)extra.get("jobType");
                selectedJobType.setText(jobType.name);
            }
        }else if( requestCode == MALAYSIA_STATE ){
            if (resultCode == RESULT_OK) {
                Bundle extra = data.getExtras();

                State state = (State)extra.get("state");
                selectedMalaysiaState.setText(state.name);
            }
        }else if( requestCode == OTHER_COUNTRY ){
            if (resultCode == RESULT_OK) {
                Bundle extra = data.getExtras();

                Country country = (Country)extra.get("country");
                selectedOtherCountry.setText(country.name);
            }
        }
    }
}
