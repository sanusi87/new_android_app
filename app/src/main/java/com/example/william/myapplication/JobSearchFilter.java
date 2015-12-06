package com.example.william.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

public class JobSearchFilter extends ActionBarActivity {

    private static int INTENT_GET_STATE = 1;
    private static int INTENT_GET_LEVEL = 2;
    private static int INTENT_GET_COUNTRY = 3;
    private static int INTENT_GET_SPECROLE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_search_filter);

        // keyword filter
        Spinner keywordFilterInput = (Spinner)findViewById(R.id.keyword_filter_spinner);
        KeywordFilterAdapter kwa = new KeywordFilterAdapter(getApplicationContext());
        keywordFilterInput.setAdapter(kwa);
        // keyword
        EditText keywordInput = (EditText)findViewById(R.id.keyword);
        // min salary
        EditText salaryMinInput = (EditText)findViewById(R.id.minimum_salary);
        // max salary
        EditText salaryMaxInput = (EditText)findViewById(R.id.maximum_salary);
        // position level
        //ListView positionLevelInput = (ListView)findViewById(R.id.position_level);

        Button searchButton = (Button)findViewById(R.id.startSearch);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent();
            intent.putExtra("result", "test ok");
            setResult(Activity.RESULT_OK, intent);
            finish();
            }
        });

        Button cancelButton = (Button)findViewById(R.id.closeJobSearchFilter);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            setResult(Activity.RESULT_CANCELED);
            finish();
            }
        });

        // select job spec and roles
        LinearLayout selectSpecAndRole = (LinearLayout)findViewById(R.id.selectSpecAndRole);
        selectSpecAndRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), SelectSpecAndRole.class);
            // get a list of already selected states and update the Extra to be submitted to next activity
            startActivityForResult(intent, INTENT_GET_SPECROLE);
            }
        });

        // select states
        LinearLayout selectMalaysiaState = (LinearLayout)findViewById(R.id.selectMalaysiaState);
        selectMalaysiaState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), SelectState.class);
            // get a list of already selected states and update the Extra to be submitted to next activity
            startActivityForResult(intent, INTENT_GET_STATE);
            }
        });

        // select countries
        LinearLayout selectOtherCountry = (LinearLayout)findViewById(R.id.selectOtherCountry);
        selectOtherCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), SelectCountry.class);
            // get a list of already selected states and update the Extra to be submitted to next activity
            startActivityForResult(intent, INTENT_GET_COUNTRY);
            }
        });

        // select position levels
        LinearLayout selectPositionLevel = (LinearLayout)findViewById(R.id.selectPositionLevel);
        selectPositionLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), SelectPositionLevel.class);
            // get a list of already selected states and update the Extra to be submitted to next activity
            startActivityForResult(intent, INTENT_GET_LEVEL);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == INTENT_GET_STATE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Bundle filters = data.getExtras();
                Log.e("filterdata", "1"+filters.getString("result"));
                Log.e("filterdata", filters.toString());
            }
        }else if( requestCode == INTENT_GET_COUNTRY ){
            if (resultCode == RESULT_OK) {
                Bundle filters = data.getExtras();
                Log.e("filterdata", "2"+filters.getString("result"));
                Log.e("filterdata", filters.toString());
            }
        }else if( requestCode == INTENT_GET_LEVEL ){
            if (resultCode == RESULT_OK) {
                Bundle filters = data.getExtras();
                Log.e("filterdata", "3"+filters.getString("result"));
                Log.e("filterdata", filters.toString());
            }
        }else if( requestCode == INTENT_GET_SPECROLE ){
            if (resultCode == RESULT_OK) {
                Bundle filters = data.getExtras();
                Log.e("filterdata", "4"+filters.getString("result"));
                Log.e("filterdata", filters.toString());
            }
        }
    }

}
