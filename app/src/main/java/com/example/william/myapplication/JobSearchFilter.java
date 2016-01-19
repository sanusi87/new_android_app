package com.example.william.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class JobSearchFilter extends ActionBarActivity {

    private static int INTENT_GET_STATE = 1;
    private static int INTENT_GET_LEVEL = 2;
    private static int INTENT_GET_COUNTRY = 3;
    private static int INTENT_GET_SPEC = 4;
    private static int INTENT_GET_ROLE = 5;
    private static int INTENT_GET_TYPE = 6;
    private static int INTENT_GET_KEYWORD_FILTER = 7;
    private static int INTENT_GET_KEYWORD = 8;

    private TextView selectedOtherCountry;
    private TextView selectedPositionLevel;
    private TextView selectedMalaysiaState;
    private TextView selectedJobSpec;
    private TextView selectedJobRole;
    private TextView selectedJobType;
    private TextView selectedKeywordFilter;
    private TextView enteredKeyword;

    private CheckBox directEmployerCb;
    private CheckBox recruitmentAgencyCb;

    private ArrayList<Country> selectedOtherCountryValues = new ArrayList<>();
    private ArrayList<PositionLevel> selectedPositionLevelValues = new ArrayList<>();
    private ArrayList<State> selectedMalaysiaStateValues = new ArrayList<>();
    private ArrayList<JobSpec> selectedJobSpecValues = new ArrayList<>();
    private ArrayList<JobRole> selectedJobRoleValues = new ArrayList<>();
    private ArrayList<JobType> selectedJobTypeValues = new ArrayList<>();
    private KeywordFilter keywordFilter;

    //Spinner keywordFilterInput;
    //EditText keywordInput;
    EditText salaryMinInput;
    EditText salaryMaxInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_search_filter);

        // keyword filter
        //keywordFilterInput = (Spinner)findViewById(R.id.keyword_filter_spinner);
        //KeywordFilterAdapter kwa = new KeywordFilterAdapter(getApplicationContext());
        //keywordFilterInput.setAdapter(kwa);
        // keyword
        //keywordInput = (EditText)findViewById(R.id.keyword);
        // min salary
        salaryMinInput = (EditText)findViewById(R.id.minimum_salary);
        // max salary
        salaryMaxInput = (EditText)findViewById(R.id.maximum_salary);
        // position level
        //ListView positionLevelInput = (ListView)findViewById(R.id.position_level);

        // selected labels
        selectedOtherCountry = (TextView)findViewById(R.id.selectedOtherCountry);
        selectedPositionLevel = (TextView)findViewById(R.id.selectedPositionLevel);
        selectedMalaysiaState = (TextView)findViewById(R.id.selectedMalaysiaState);
        selectedJobSpec = (TextView)findViewById(R.id.selectedSpec);
        selectedJobRole = (TextView)findViewById(R.id.selectedRole);
        selectedJobType = (TextView)findViewById(R.id.selectedJobType);
        enteredKeyword = (TextView)findViewById(R.id.enteredKeyword);
        selectedKeywordFilter = (TextView)findViewById(R.id.selectedKeywordFilter);

        TextView directEmployer = (TextView) findViewById(R.id.direct_employer);
        directEmployerCb = (CheckBox)findViewById(R.id.direct_employer_checkbox);

        TextView recruitmentAgency = (TextView) findViewById(R.id.recruitment_agency);
        recruitmentAgencyCb = (CheckBox)findViewById(R.id.recruitment_agency_checkBox);

        Bundle extra = getIntent().getExtras();
        keywordFilter = new KeywordFilter(1,"Position Title");
        if( extra != null ){
            keywordFilter = (KeywordFilter) extra.get("keyword_filter");
        }

        if( keywordFilter != null ){
            selectedKeywordFilter.setText(keywordFilter.name);
        }

        LinearLayout enterKeyword = (LinearLayout)findViewById(R.id.enterKeyword);
        enterKeyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UpdateName.class);
                intent.putExtra("the_title", getText(R.string.keyword));
                startActivityForResult(intent, INTENT_GET_KEYWORD);
            }
        });

        LinearLayout selectKeywordFilter = (LinearLayout)findViewById(R.id.selectKeywordFilter);
        selectKeywordFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectKeywordFilter.class);
                startActivityForResult(intent, INTENT_GET_KEYWORD_FILTER);
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

        // select job type
        LinearLayout selectJobType = (LinearLayout)findViewById(R.id.selectJobType);
        selectJobType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectJobType.class);
                intent.putExtra("single", false);
                // get a list of already selected states and update the Extra to be submitted to next activity
                startActivityForResult(intent, INTENT_GET_TYPE);
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
                intent.putExtra("single", false);
                // passed selected values if user want to reselect
                if( selectedPositionLevelValues.size() > 0 ){
                    intent.putExtra("positionlevel", selectedPositionLevelValues);
                }
                // get a list of already selected states and update the Extra to be submitted to next activity
                startActivityForResult(intent, INTENT_GET_LEVEL);
            }
        });

        directEmployer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                directEmployerCb.setChecked(!directEmployerCb.isChecked());
            }
        });

        recruitmentAgency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recruitmentAgencyCb.setChecked(!recruitmentAgencyCb.isChecked());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.job_search_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int clickedItem = item.getItemId();
        if( clickedItem == R.id.perform_filter ){
            /*
            * perform filter
            * */
            Intent intent = new Intent();

            //if( keywordFilterInput.getSelectedItem() != null ){
            //    intent.putExtra("keyword_filter", (KeywordFilter) keywordFilterInput.getSelectedItem());
            //}

            if( keywordFilter != null ){
                intent.putExtra("keyword_filter", keywordFilter);
            }

            if( enteredKeyword.getText().toString().length() > 0 ){
                intent.putExtra("keyword", enteredKeyword.getText().toString());
            }

            if( salaryMinInput.getText().toString().length() > 0 ){
                intent.putExtra("salary_min", salaryMinInput.getText().toString());
            }

            if( salaryMaxInput.getText().toString().length() > 0 ){
                intent.putExtra("salary_max", salaryMaxInput.getText().toString());
            }

            if( selectedJobSpecValues.size() > 0 ){
                intent.putExtra("job_spec", selectedJobSpecValues);
            }

            if( selectedJobRoleValues.size() > 0 ){
                intent.putExtra("job_role", selectedJobRoleValues);
            }

            if( selectedJobTypeValues.size() > 0 ){
                intent.putExtra("job_type", selectedJobTypeValues);
            }

            if( selectedMalaysiaStateValues.size() > 0 ){
                intent.putExtra("state", selectedMalaysiaStateValues);
            }

            if( selectedOtherCountryValues.size() > 0 ){
                intent.putExtra("country", selectedOtherCountryValues);
            }

            if( selectedPositionLevelValues.size() > 0 ){
                intent.putExtra("position_level", selectedPositionLevelValues);
            }

            boolean postedDirectEmployer = directEmployerCb.isChecked();
            intent.putExtra("direct_employer", postedDirectEmployer);

            boolean postedRecruitmentAgency = recruitmentAgencyCb.isChecked();
            intent.putExtra("recruitment_agency", postedRecruitmentAgency);

            setResult(Activity.RESULT_OK, intent);
            finish();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == INTENT_GET_STATE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Bundle filters = data.getExtras();
                ArrayList selectedValues = (ArrayList) filters.get("state");
                ArrayList<String> selectedLabels = new ArrayList<>();

                if( selectedValues != null ){
                    for( int i=0;i<selectedValues.size();i++ ){
                        State c = (State) selectedValues.get(i);
                        selectedLabels.add(c.name);
                    }
                    selectedMalaysiaStateValues = selectedValues;
                    selectedMalaysiaState.setText(TextUtils.join(",",selectedLabels));
                }
            }
        }else if( requestCode == INTENT_GET_COUNTRY ){
            if (resultCode == RESULT_OK) {
                Bundle filters = data.getExtras();
                ArrayList selectedValues = (ArrayList) filters.get("country");
                ArrayList<String> selectedLabels = new ArrayList<>();

                if( selectedValues != null ){
                    for( int i=0;i<selectedValues.size();i++ ){
                        Country c = (Country) selectedValues.get(i);
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
        }else if( requestCode == INTENT_GET_TYPE ){
            if (resultCode == RESULT_OK) {
                Bundle filters = data.getExtras();
                ArrayList<JobType> selectedValues = (ArrayList<JobType>) filters.get("jobType");
                ArrayList<String> selectedLabels = new ArrayList<>();

                if( selectedValues != null ){
                    for( int i=0;i<selectedValues.size();i++ ){
                        JobType c = selectedValues.get(i);
                        selectedLabels.add(c.name);
                    }
                    selectedJobTypeValues = selectedValues;
                    selectedJobType.setText(TextUtils.join(",",selectedLabels));
                }
            }
        }else if( requestCode == INTENT_GET_KEYWORD_FILTER ){
            if (resultCode == RESULT_OK) {
                Bundle filters = data.getExtras();
                keywordFilter = (KeywordFilter) filters.get("keyword_filter");

                if( keywordFilter != null ){
                    selectedKeywordFilter.setText(keywordFilter.name);
                }
            }
        }else if( requestCode == INTENT_GET_KEYWORD ){
            if (resultCode == RESULT_OK) {
                Bundle filters = data.getExtras();
                enteredKeyword.setText(filters.getString("the_text"));
            }
        }
    }

}
