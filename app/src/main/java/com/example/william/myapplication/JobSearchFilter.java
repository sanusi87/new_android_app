package com.example.william.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class JobSearchFilter extends Activity {

    private static int INTENT_GET_STATE = 1;
    private static int INTENT_GET_LEVEL = 2;
    private static int INTENT_GET_COUNTRY = 3;
    private static int INTENT_GET_SPEC = 4;
    private static int INTENT_GET_ROLE = 5;

    private TextView selectedOtherCountry;
    private TextView selectedPositionLevel;
    private TextView selectedMalaysiaState;
    private TextView selectedJobSpec;
    private TextView selectedJobRole;

    private ArrayList<Country> selectedOtherCountryValues = new ArrayList<>();
    private ArrayList<PositionLevel> selectedPositionLevelValues = new ArrayList<>();
    private ArrayList<State> selectedMalaysiaStateValues = new ArrayList<>();
    private ArrayList<JobSpec> selectedJobSpecValues = new ArrayList<>();
    private ArrayList<JobRole> selectedJobRoleValues = new ArrayList<>();

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

        // selected labels
        selectedOtherCountry = (TextView)findViewById(R.id.selectedOtherCountry);
        selectedPositionLevel = (TextView)findViewById(R.id.selectedPositionLevel);
        selectedMalaysiaState = (TextView)findViewById(R.id.selectedMalaysiaState);
        selectedJobSpec = (TextView)findViewById(R.id.selectedSpec);
        selectedJobRole = (TextView)findViewById(R.id.selectedRole);

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
        LinearLayout selectSpec = (LinearLayout)findViewById(R.id.selectSpec);
        selectSpec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectJobSpec.class);
                // get a list of already selected states and update the Extra to be submitted to next activity
                startActivityForResult(intent, INTENT_GET_SPEC);
            }
        });

        LinearLayout selectRole = (LinearLayout)findViewById(R.id.selectRole);
        selectRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectJobRole.class);
                // get a list of already selected states and update the Extra to be submitted to next activity
                startActivityForResult(intent, INTENT_GET_ROLE);
            }
        });

        // select states
        LinearLayout selectMalaysiaState = (LinearLayout)findViewById(R.id.selectMalaysiaState);
        selectMalaysiaState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectState.class);
                // passed selected values if user want to reselect
                if( selectedMalaysiaStateValues.size() > 0 ){
                    intent.putExtra("state", selectedMalaysiaStateValues);
                }
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
                // passed selected values if user want to reselect
                if( selectedOtherCountryValues.size() > 0 ){
                    intent.putExtra("country", selectedOtherCountryValues);
                }
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
                // passed selected values if user want to reselect
                if( selectedPositionLevelValues.size() > 0 ){
                    intent.putExtra("positionlevel", selectedPositionLevelValues);
                }
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
                ArrayList<State> selectedValues = (ArrayList<State>) filters.get("state");
                ArrayList<String> selectedLabels = new ArrayList<>();

                if( selectedValues != null ){
                    for( int i=0;i<selectedValues.size();i++ ){
                        State c = selectedValues.get(i);
                        selectedLabels.add(c.name);
                    }
                    selectedMalaysiaStateValues = selectedValues;
                    selectedMalaysiaState.setText(TextUtils.join(",",selectedLabels));
                }
            }
        }else if( requestCode == INTENT_GET_COUNTRY ){
            if (resultCode == RESULT_OK) {
                Bundle filters = data.getExtras();
                ArrayList<Country> selectedValues = (ArrayList<Country>) filters.get("country");
                ArrayList<String> selectedLabels = new ArrayList<>();

                if( selectedValues != null ){
                    for( int i=0;i<selectedValues.size();i++ ){
                        Country c = selectedValues.get(i);
                        selectedLabels.add(c.name);
                    }
                    selectedOtherCountryValues = selectedValues;
                    selectedOtherCountry.setText(TextUtils.join(",",selectedLabels));
                }
            }
        }else if( requestCode == INTENT_GET_LEVEL ){
            if (resultCode == RESULT_OK) {
                Bundle filters = data.getExtras();
                ArrayList<PositionLevel> selectedValues = (ArrayList<PositionLevel>) filters.get("positionlevel");
                ArrayList<String> selectedLabels = new ArrayList<>();

                if( selectedValues != null ){
                    for( int i=0;i<selectedValues.size();i++ ){
                        PositionLevel c = selectedValues.get(i);
                        selectedLabels.add(c.name);
                    }
                    selectedPositionLevelValues = selectedValues;
                    selectedPositionLevel.setText(TextUtils.join(",",selectedLabels));
                }
            }
        }else if( requestCode == INTENT_GET_SPEC ){
            if (resultCode == RESULT_OK) {
                Bundle filters = data.getExtras();
                ArrayList<JobSpec> selectedValues = (ArrayList<JobSpec>) filters.get("jobspec");
                ArrayList<String> selectedLabels = new ArrayList<>();

                if( selectedValues != null ){
                    for( int i=0;i<selectedValues.size();i++ ){
                        JobSpec c = selectedValues.get(i);
                        selectedLabels.add(c.name);
                    }
                    selectedJobSpecValues = selectedValues;
                    selectedJobSpec.setText(TextUtils.join(",",selectedLabels));
                }
            }
        }else if( requestCode == INTENT_GET_ROLE ){
            if (resultCode == RESULT_OK) {
                Bundle filters = data.getExtras();
                ArrayList<JobRole> selectedValues = (ArrayList<JobRole>) filters.get("jobrole");
                ArrayList<String> selectedLabels = new ArrayList<>();

                if( selectedValues != null ){
                    for( int i=0;i<selectedValues.size();i++ ){
                        JobRole c = selectedValues.get(i);
                        selectedLabels.add(c.name);
                    }
                    selectedJobRoleValues = selectedValues;
                    selectedJobRole.setText(TextUtils.join(",",selectedLabels));
                }
            }
        }
    }

}
