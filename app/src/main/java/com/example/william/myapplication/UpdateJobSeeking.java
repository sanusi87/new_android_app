package com.example.william.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class UpdateJobSeeking extends ActionBarActivity {

    public static final int SELECT_JOB_SEEKING_STATUS = 1;
    public static final int SELECT_COUNTRY = 2;
    public static final int SELECT_STATE = 3;
    public static final int SELECT_JOB_NOTICE = 4;

    private TextView selectedMalaysiaState;
    private ArrayList<State> selectedMalaysiaStateValues = new ArrayList<>();
    private TextView selectedCountry;
    private ArrayList<Country> selectedCountryValues = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_job_seeking);

        selectedMalaysiaState = (TextView)findViewById(R.id.selectedMalaysiaState);
        selectedCountry = (TextView)findViewById(R.id.selectedCountry);

        LinearLayout selectJobSeekingStatus = (LinearLayout)findViewById(R.id.selectJobSeekingStatus);
        LinearLayout selectCountry = (LinearLayout)findViewById(R.id.selectCountry);
        LinearLayout selectMalaysiaState = (LinearLayout)findViewById(R.id.selectMalaysiaState);
        LinearLayout selectJobNotice = (LinearLayout)findViewById(R.id.selectJobNotice);

        selectJobSeekingStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        selectCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectCountry.class);
                intent.putExtra("single", true);
                startActivityForResult(intent, SELECT_COUNTRY);
            }
        });

        selectMalaysiaState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectState.class);
                intent.putExtra("single", true);
                startActivityForResult(intent, SELECT_STATE);
            }
        });

        selectJobNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == SELECT_JOB_SEEKING_STATUS) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Bundle filters = data.getExtras();
                ArrayList<State> selectedValues = (ArrayList<State>) filters.get("state");
                ArrayList<String> selectedLabels = new ArrayList<>();

                if (selectedValues != null) {
                    for (int i = 0; i < selectedValues.size(); i++) {
                        State c = selectedValues.get(i);
                        selectedLabels.add(c.name);
                    }
                    //selectedMalaysiaStateValues = selectedValues;
                    //selectedMalaysiaState.setText(TextUtils.join(",", selectedLabels));
                }
            }
        }else if( requestCode == SELECT_COUNTRY ){
            if (resultCode == RESULT_OK) {
                Bundle filters = data.getExtras();
                ArrayList<Country> selectedValues = (ArrayList<Country>) filters.get("country");
                ArrayList<String> selectedLabels = new ArrayList<>();

                if( selectedValues != null ){
                    for( int i=0;i<selectedValues.size();i++ ){
                        Country c = selectedValues.get(i);
                        selectedLabels.add(c.name);
                    }
                    selectedCountryValues = selectedValues;
                    selectedCountry.setText(TextUtils.join(",", selectedLabels));
                }
            }
        }else if( requestCode == SELECT_STATE ){
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
                    selectedMalaysiaState.setText(TextUtils.join(",", selectedLabels));
                }
            }
        }else if( requestCode == SELECT_JOB_NOTICE ){
            if (resultCode == RESULT_OK) {
                Bundle filters = data.getExtras();

            }
        }
    }
}
