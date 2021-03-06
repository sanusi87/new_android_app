package jen.jobs.application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

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
    //private TextView selectedJobRole;
    private TextView selectedJobType;
    private TextView selectedKeywordFilter;
    //private TextView enteredKeyword;
    private EditText enteredKeyword;

    private CheckBox directEmployerCb;
    private CheckBox recruitmentAgencyCb;

    //----
    private LinearLayout listOfSelectedJobSpec;
    private ListView listOfSelectedJobSpecInner;
    private SelectedJobSpecAdapter selectedJobSpecAdapter;
    //----

    private ArrayList<Country> selectedOtherCountryValues = new ArrayList<>();
    private ArrayList<PositionLevel> selectedPositionLevelValues = new ArrayList<>();
    private ArrayList<State> selectedMalaysiaStateValues = new ArrayList<>();
    private ArrayList<JobSpec> selectedJobSpecValues = new ArrayList<>();
    //private ArrayList<JobRole> selectedJobRoleValues = new ArrayList<>();
    private ArrayList<JobType> selectedJobTypeValues = new ArrayList<>();
    private KeywordFilter keywordFilter;

    private ArrayList<ArrayList<JobRole>> _jobRoleBasedOnView = new ArrayList<>();

    //Spinner keywordFilterInput;
    //EditText keywordInput;
    EditText salaryMinInput;
    EditText salaryMaxInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_search_filter);
        setTitle(R.string.job_search_filter);

        if( getSupportActionBar() != null ){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TableJobSpec tableJobSpec = new TableJobSpec(this);
        TableJobRole tableJobRole = new TableJobRole(this);

        selectedJobSpecAdapter = new SelectedJobSpecAdapter(getApplicationContext());
        listOfSelectedJobSpec = (LinearLayout)findViewById(R.id.listOfSelectedJobSpec);
        listOfSelectedJobSpecInner = new ListView(getApplicationContext());
        listOfSelectedJobSpecInner.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

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
        //selectedJobRole = (TextView)findViewById(R.id.selectedRole);
        selectedJobType = (TextView)findViewById(R.id.selectedJobType);
        //enteredKeyword = (TextView)findViewById(R.id.enteredKeyword);
        enteredKeyword = (EditText)findViewById(R.id.enteredKeyword);
        selectedKeywordFilter = (TextView)findViewById(R.id.selectedKeywordFilter);

        TextView directEmployer = (TextView) findViewById(R.id.direct_employer);
        directEmployerCb = (CheckBox)findViewById(R.id.direct_employer_checkbox);

        TextView recruitmentAgency = (TextView) findViewById(R.id.recruitment_agency);
        recruitmentAgencyCb = (CheckBox)findViewById(R.id.recruitment_agency_checkBox);

        Bundle extra = getIntent().getExtras();
        keywordFilter = new KeywordFilter(1,"Position Title");
        if( extra != null ){
            Bundle searchParameters = extra.getBundle("searchParameters");
            if( searchParameters != null ){
                if( searchParameters.getSerializable("keyword_filter") != null ){
                    keywordFilter = (KeywordFilter) searchParameters.getSerializable("keyword_filter");
                }
                enteredKeyword.setText(searchParameters.getString("keyword"));
                salaryMinInput.setText(searchParameters.getString("salary_min"));
                salaryMaxInput.setText(searchParameters.getString("salary_max"));

                ArrayList<Integer> _selectedJobSpec = searchParameters.getIntegerArrayList("job_spec");
                ArrayList<Integer> _selectedJobRole = searchParameters.getIntegerArrayList("job_role");
                //Log.e("_selectedJobRole", ""+_selectedJobRole);
                ArrayList<String> selectedJobSpecLabels = new ArrayList<>();
                if( _selectedJobSpec != null && _selectedJobSpec.size() > 0 ){
                    for( int i=0;i<_selectedJobSpec.size();i++ ){
                        Integer _js = _selectedJobSpec.get(i); // job_spec_id
                        JobSpec __js = tableJobSpec.findById(_js); // job_spec
                        selectedJobSpecValues.add(__js);
                        selectedJobSpecLabels.add(__js.name);

                        selectedJobSpecAdapter.addJobSpec(__js);

                        ArrayList<JobRole> myJobRoles = tableJobRole.findByJobSpec(String.valueOf(__js.id), _selectedJobRole);

                        Log.e("myJobRoles.size", i+"="+myJobRoles.size());
                        _jobRoleBasedOnView.add(i, myJobRoles);
                        selectedJobSpecAdapter.setJobRole(i, myJobRoles);
                    }
                    listOfSelectedJobSpec.setVisibility(View.VISIBLE);
                }
                selectedJobSpec.setText(TextUtils.join(",", selectedJobSpecLabels));
                //selectedJobSpecAdapter.setJobSpec(selectedJobSpecValues);
                recalculateHeight();

                //---
                ArrayList<Integer> _selectedJobType = searchParameters.getIntegerArrayList("job_type");
                if( _selectedJobType != null && _selectedJobType.size() > 0 ){
                    HashMap listOfJobTypes = Jenjobs.getJobType();
                    ArrayList<String> selectedLabels = new ArrayList<>();
                    for(Integer jobTypeId : _selectedJobType){
                        String jobTypeName = (String)listOfJobTypes.get(jobTypeId);
                        selectedJobTypeValues.add(new JobType(jobTypeId, jobTypeName));
                        selectedLabels.add(jobTypeName);
                    }
                    selectedJobType.setText(TextUtils.join(",",selectedLabels));
                }
                //---

                //---
                ArrayList<Integer> _selectedState = searchParameters.getIntegerArrayList("state");
                if( _selectedState != null && _selectedState.size() > 0 ){
                    HashMap listOfStates = Jenjobs.getState();
                    ArrayList<String> selectedLabels = new ArrayList<>();
                    for(Integer stateId : _selectedState){
                        String stateName = (String) listOfStates.get(stateId);
                        Log.e("stateId", ""+stateId);
                        Log.e("stateId", ""+stateName);
                        selectedMalaysiaStateValues.add(new State(stateId, stateName));
                        selectedLabels.add(stateName);
                    }
                    selectedMalaysiaState.setText(TextUtils.join(",",selectedLabels));
                }
                //---

                //---
                ArrayList<Integer> _selectedCountry = searchParameters.getIntegerArrayList("country");
                if( _selectedCountry != null && _selectedCountry.size() > 0 ){
                    HashMap listOfCountries = Jenjobs.getCountry();
                    ArrayList<String> selectedLabels = new ArrayList<>();
                    for(Integer countryId : _selectedCountry){
                        String countryName = (String) listOfCountries.get(countryId);
                        Log.e("countryId", ""+countryId);
                        Log.e("countryId", ""+countryName);
                        selectedOtherCountryValues.add(new Country(countryId, countryName));
                        selectedLabels.add(countryName);
                    }
                    selectedOtherCountry.setText(TextUtils.join(",",selectedLabels));
                }
                //---

                //---
                ArrayList<Integer> _selectedPositionLevel = searchParameters.getIntegerArrayList("position_level");
                if( _selectedPositionLevel != null && _selectedPositionLevel.size() > 0 ){
                    HashMap listOfPositionLevels = Jenjobs.getPositionLevel();
                    ArrayList<String> selectedLabels = new ArrayList<>();
                    for( Integer positionLevelId : _selectedPositionLevel ){
                        String positionLevelName = (String) listOfPositionLevels.get(positionLevelId);
                        Log.e("positionLevelId", ""+positionLevelId);
                        Log.e("positionLevelId", ""+positionLevelName);
                        selectedPositionLevelValues.add(new PositionLevel(positionLevelId, positionLevelName));
                        selectedLabels.add(positionLevelName);
                    }
                    selectedPositionLevel.setText(TextUtils.join(",",selectedLabels));
                }
                //---

                boolean _directEmployer = searchParameters.getBoolean("direct_employer");
                if( _directEmployer ){
                    directEmployerCb.setChecked(true);
                }

                boolean _recruitmentAgency = searchParameters.getBoolean("recruitment_agency");
                if( _recruitmentAgency ){
                    recruitmentAgencyCb.setChecked(true);
                }
            }
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
                if( enteredKeyword.getText().toString().length() > 0 ){
                    intent.putExtra("the_text", enteredKeyword.getText().toString());
                }
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

        //selectedJobSpecAdapter.setJobSpec(selectedJobSpecValues);
        listOfSelectedJobSpecInner.setAdapter(selectedJobSpecAdapter);
        listOfSelectedJobSpec.addView(listOfSelectedJobSpecInner);

        listOfSelectedJobSpecInner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), SelectJobRole.class);

                //Log.e("savedtag", ""+view.getTag(R.id.TAG_JOB_ROLE_ID));
                JobSpec jsInTag = (JobSpec) view.getTag(R.id.TAG_JOB_ROLE_ID);
                intent.putExtra("jobspecid", jsInTag.id);
                intent.putExtra("jsViewIndex", position);
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
                intent.putExtra("jobtype", selectedJobTypeValues);
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
                intent.putExtra("state", selectedMalaysiaStateValues);
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
                intent.putExtra("country", selectedOtherCountryValues);
                intent.putExtra("remove_malaysia", true);
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
                intent.putExtra("positionlevel", selectedPositionLevelValues);
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
        super.onCreateOptionsMenu(menu);
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

            //if( selectedJobRoleValues.size() > 0 ){
                //intent.putExtra("job_role", selectedJobRoleValues);
            //}

            if( _jobRoleBasedOnView.size() > 0 ){
                ArrayList<JobRole> selectedJobRoleValues = new ArrayList<>();
                for(int i=0;i<_jobRoleBasedOnView.size();i++){
                    if( _jobRoleBasedOnView.get(i) != null ){
                        selectedJobRoleValues.addAll(_jobRoleBasedOnView.get(i));
                    }
                }
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
            //intent.setClass(getApplicationContext(), JobSearchActivity.class);
            //startActivity(intent);
            finish();

        }else if( clickedItem == R.id.clear_filter ){
            CharSequence noValue = getText(R.string.no_value);
            // reset values
            keywordFilter = new KeywordFilter(1,"Position Title");
            enteredKeyword.setText(noValue);
            salaryMinInput.setText(noValue);
            salaryMaxInput.setText(noValue);
            selectedJobSpecValues.clear();
            _jobRoleBasedOnView.clear();
            selectedJobTypeValues.clear();
            selectedMalaysiaStateValues.clear();
            selectedOtherCountryValues.clear();
            selectedPositionLevelValues.clear();
            directEmployerCb.setChecked(false);
            recruitmentAgencyCb.setChecked(false);

            // reset views
            selectedJobSpecAdapter.resetJobSpec();
            listOfSelectedJobSpec.setVisibility(View.GONE);
            //selectedJobSpecAdapter.notifyDataSetChanged();
            selectedJobType.setText(noValue);
            selectedJobSpec.setText(noValue);
            selectedMalaysiaState.setText(noValue);
            selectedOtherCountry.setText(noValue);
            selectedPositionLevel.setText(noValue);

        }else if( clickedItem == android.R.id.home ){
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
                    // reset this array upon selecting new jobspec
                    _jobRoleBasedOnView = new ArrayList<>();

                    selectedJobSpecAdapter.resetJobSpec();
                    listOfSelectedJobSpec.setVisibility(View.VISIBLE);

                    // TODO - empty content
                    for( int i=0;i<selectedValues.size();i++ ){
                        JobSpec c = selectedValues.get(i);
                        selectedLabels.add(c.name);
                        //selectedJobSpecAdapter.addJobSpec(c);
                        _jobRoleBasedOnView.add(null);
                        selectedJobSpecAdapter.addJobSpec(c);
                    }
                    selectedJobSpecValues = selectedValues;
                    selectedJobSpec.setText(TextUtils.join(",", selectedLabels));
                    //selectedJobSpecAdapter.setJobSpec(selectedJobSpecValues);

                    // set list view height
                    int totalHeight = 0;
                    for (int i = 0; i < selectedJobSpecAdapter.getCount(); i++) {
                        View listItem = selectedJobSpecAdapter.getView(i, null, listOfSelectedJobSpecInner);
                        listItem.measure(0, 0);
                        totalHeight += listItem.getMeasuredHeight();
                    }

                    ViewGroup.LayoutParams params = listOfSelectedJobSpecInner.getLayoutParams();
                    params.height = totalHeight + (listOfSelectedJobSpecInner.getDividerHeight() * (selectedJobSpecAdapter.getCount() - 1));
                    listOfSelectedJobSpecInner.setLayoutParams(params);
                    listOfSelectedJobSpecInner.requestLayout();
                    // end hack
                }
            }
        }else if( requestCode == INTENT_GET_ROLE ){
            if (resultCode == RESULT_OK) {
                Bundle filters = data.getExtras();
                ArrayList<JobRole> selectedValues = (ArrayList<JobRole>) filters.get("jobrole");
                ArrayList<String> selectedLabels = new ArrayList<>();
                int viewIndex = filters.getInt("viewIndex");

                if( selectedValues != null ){
                    for( int i=0;i<selectedValues.size();i++ ){
                        JobRole c = selectedValues.get(i);
                        selectedLabels.add(c.name);
                    }
                    //selectedJobRoleValues = selectedValues;
                    //selectedJobRole.setText(TextUtils.join(",", selectedLabels));
                    // TODO
                    //Log.e("viewIndex", ""+viewIndex);
                    if( viewIndex != -1 ){
                        //Log.e("_jobRoleSize", ""+_jobRoleBasedOnView.size());
                        try{
                            _jobRoleBasedOnView.remove(viewIndex);
                        }catch(IndexOutOfBoundsException e) {
                            //Log.e("err_"+viewIndex,e.getMessage());
                        }
                        _jobRoleBasedOnView.add(viewIndex, selectedValues); // re-set
                        selectedJobSpecAdapter.setJobRole(viewIndex, selectedValues);
                        selectedJobSpecAdapter.notifyDataSetChanged();

                        View v = listOfSelectedJobSpecInner.getChildAt(viewIndex);
                        TextView tv = (TextView)v.findViewById(R.id.currentJobRole);
                        tv.setText(TextUtils.join(",", selectedLabels));
                    }
                    recalculateHeight();
                }
            }
        }else if( requestCode == INTENT_GET_TYPE ){
            if (resultCode == RESULT_OK) {
                Bundle filters = data.getExtras();
                ArrayList<JobType> selectedValues = (ArrayList<JobType>) filters.get("jobType");
                ArrayList<String> selectedLabels = new ArrayList<>();
                //Log.e("selectedValues", ""+selectedValues);
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

    private void recalculateHeight(){
        int totalHeight = 0;
        for (int i = 0; i < selectedJobSpecAdapter.getCount(); i++) {
            View listItem = selectedJobSpecAdapter.getView(i, null, listOfSelectedJobSpecInner);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listOfSelectedJobSpecInner.getLayoutParams();
        params.height = totalHeight + (listOfSelectedJobSpecInner.getDividerHeight() * (selectedJobSpecAdapter.getCount() - 1));
        listOfSelectedJobSpecInner.setLayoutParams(params);
        listOfSelectedJobSpecInner.requestLayout();
    }

}
