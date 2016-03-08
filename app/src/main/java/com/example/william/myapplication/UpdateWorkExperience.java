package com.example.william.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class UpdateWorkExperience extends ActionBarActivity {

    private static final int SELECT_JOB_SPEC = 1;
    private static final int SELECT_JOB_ROLE = 2;
    private static final int SELECT_INDUSTRY = 3;
    private static final int SELECT_JOB_TYPE = 4;
    private static final int SELECT_POSITION_LEVEL = 5;

    TableWorkExperience tableWorkExperience;
    TableJobSpec tableJobSpec;
    TableJobRole tableJobRole;
    TableProfile tableProfile;
    //TableSettings tableSettings;

    SharedPreferences sharedPreferences;
    String accessToken;
    int profileId;

    TextView selectedJobSpec;
    TextView selectedJobRole;
    TextView selectedIndustry;
    TextView selectedJobType;
    TextView selectedPositionLevel;

    //int theJobSpec = 0;
    //int theJobRole = 0;
    int currentId = 0; // the local db index
    //int theIndustry = 0;

    JobSpec _jobSpec;
    JobRole _jobRole;
    JobType _jobType;
    Industry _industry;
    PositionLevel _positionLevel;

    EditText positionTitle;
    EditText companyName;
    Spinner monthStart;
    Spinner monthEnd;
    Spinner yearStart;
    Spinner yearEnd;
    int savedId = 0;
    int selectedWork = -1;
    EditText salary;
    Spinner currency;
    EditText exp;

    boolean isOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_work_experience);

        isOnline = Jenjobs.isOnline(getApplicationContext());

        tableWorkExperience = new TableWorkExperience(this);
        tableJobSpec = new TableJobSpec(this);
        tableJobRole = new TableJobRole(this);
        tableProfile = new TableProfile(this);

        sharedPreferences = getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        accessToken = sharedPreferences.getString("access_token", null);
        profileId = sharedPreferences.getInt("js_profile_id", 0);

        positionTitle = (EditText)findViewById(R.id.position_title);
        companyName = (EditText)findViewById(R.id.company_name);

        LinearLayout selectJobType = (LinearLayout)findViewById(R.id.selectJobType);
        selectedJobType = (TextView)findViewById(R.id.selectedJobType);
        HashMap listOfJobType = Jenjobs.getJobType();

        LinearLayout selectPositionLevel = (LinearLayout)findViewById(R.id.selectPositionLevel);
        selectedPositionLevel = (TextView)findViewById(R.id.selectedPositionLevel);
        HashMap listOfPositionLevel = Jenjobs.getPositionLevel();

        // currency
        currency = (Spinner)findViewById(R.id.currency);
        CurrencyAdapter ca = new CurrencyAdapter(this);
        currency.setAdapter(ca);
        salary = (EditText)findViewById(R.id.salary_amount);

        LinearLayout selectJobSpec = (LinearLayout)findViewById(R.id.selectJobSpec);
        selectedJobSpec = (TextView)findViewById(R.id.selectedJobSpec);

        LinearLayout selectJobRole = (LinearLayout)findViewById(R.id.selectJobRole);
        selectedJobRole = (TextView)findViewById(R.id.selectedJobRole);

        // industry
        //final Spinner industry = (Spinner)findViewById(R.id.company_industry);
        //IndustryAdapter ia = new IndustryAdapter(this);
        //industry.setAdapter(ia);

        LinearLayout selectIndustry = (LinearLayout)findViewById(R.id.selectIndustry);
        selectedIndustry = (TextView)findViewById(R.id.selectedIndustry);
        HashMap listOfIndustry = Jenjobs.getIndustry();

        monthStart = (Spinner)findViewById(R.id.month_start);
        monthEnd = (Spinner)findViewById(R.id.month_end);
        yearStart = (Spinner)findViewById(R.id.year_start);
        yearEnd = (Spinner)findViewById(R.id.year_end);

        String[] listOfMonth = Jenjobs.listOfMonth();
        String[] listOfYear = Jenjobs.listOfYear();

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, listOfMonth);
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, listOfYear);
        monthStart.setAdapter(monthAdapter);
        monthEnd.setAdapter(monthAdapter);
        yearStart.setAdapter(yearAdapter);
        yearEnd.setAdapter(yearAdapter);

        exp = (EditText)findViewById(R.id.experience_text);

        Bundle extra = getIntent().getExtras();
        if( extra != null ){
            // for updating existing job
            currentId = extra.getInt("id");
            selectedWork = extra.getInt("selectedWork");
            Cursor w = tableWorkExperience.getWorkExperienceById(currentId);
            if( w.moveToFirst() ) {
                if( w.getInt(1) > 0 ){
                    savedId = w.getInt(1);
                }

                positionTitle.setText(w.getString(2));
                companyName.setText(w.getString(3));
                salary.setText(w.getString(10));

                currency.setSelection( ca.findPosition( w.getInt(11) ) );
                //employmentType.setSelection(jta.findPosition(w.getInt(6)));
                //jobLevel.setSelection(jla.findPosition(w.getInt(7)));
                //industry.setSelection(ia.findPosition(w.getInt(8)));

                int __jobLevelId = w.getInt(7);
                String __jobLevelName = (String) listOfPositionLevel.get(__jobLevelId);
                _positionLevel = new PositionLevel(__jobLevelId, __jobLevelName);
                selectedPositionLevel.setText(__jobLevelName);

                int __jobSpecId = w.getInt(4);
                _jobSpec = tableJobSpec.findById(__jobSpecId);
                selectedJobSpec.setText(_jobSpec.name);

                int __jobRoleId = w.getInt(5);
                _jobRole = tableJobRole.findById( __jobRoleId );
                selectedJobRole.setText(_jobRole.name);

                int __industryId = w.getInt(8);
                String __industryName = (String)listOfIndustry.get(__industryId);
                _industry = new Industry(__industryId, __industryName);
                selectedIndustry.setText(__industryName);

                int __jobTypeId = w.getInt(6);
                if( __jobTypeId > 0 ){
                    String __jobTypeName = (String) listOfJobType.get(__jobTypeId);
                    _jobType = new JobType(__jobTypeId, __jobTypeName);
                    selectedJobType.setText(__jobTypeName);
                }


                String startDate = w.getString(12);
                String _selectedMonth = Jenjobs.date(startDate, "MMMM", "dd-MM-yyyy");
                String _selectedYear = startDate.substring(0,4);
                monthStart.setSelection(monthAdapter.getPosition(_selectedMonth));
                yearStart.setSelection( yearAdapter.getPosition( _selectedYear ) );

                String endDate = w.getString( 13 );
                if( endDate != null && !endDate.equals("null") && !endDate.matches("^0000\\-00\\-00") ){
                    String __selectedMonth = Jenjobs.date(endDate, "MMMM", "dd-MM-yyyy");
                    String __selectedYear = endDate.substring(0,4);
                    monthEnd.setSelection( monthAdapter.getPosition( __selectedMonth ) );
                    yearEnd.setSelection( yearAdapter.getPosition( __selectedYear ) );
                }

                if( !w.getString(9).equals("null") ){
                    // TODO: write html
                    exp.setText(Html.fromHtml(w.getString(9)) );
                }

            }
            w.close();
            setTitle(getText(R.string.update_work_exp));
        }else{
            setTitle(getText(R.string.add_work_exp));
        }

        // event binding
        selectJobSpec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), SelectSingleJobSpec.class);
                startActivityForResult(intent, SELECT_JOB_SPEC);
            }
        });

        selectJobRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !selectedJobSpec.getText().equals(getResources().getString(R.string.no_value)) ){
                    Intent intent = new Intent();
                    intent.putExtra("jobspecid", _jobSpec.id);
                    intent.setClass(getApplicationContext(), SelectSingleJobRole.class);
                    startActivityForResult(intent, SELECT_JOB_ROLE);
                }else{
                    Toast.makeText(getApplicationContext(), "Please select job specialisation first.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        selectIndustry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), SelectJobIndustry.class);
                startActivityForResult(intent, SELECT_INDUSTRY);
            }
        });

        selectJobType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("single", true);
                intent.setClass(getApplicationContext(), SelectJobType.class);
                startActivityForResult(intent, SELECT_JOB_TYPE);
            }
        });

        selectPositionLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("single", true);
                intent.setClass(getApplicationContext(), SelectPositionLevel.class);
                startActivityForResult(intent, SELECT_POSITION_LEVEL);
            }
        });

        Button okButton = (Button)findViewById(R.id.okButton);
        Button cancelButton = (Button)findViewById(R.id.cancelButton);

        //final int finalCurrentId = currentId;
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int clickedItem = item.getItemId();
        if (clickedItem == R.id.save) {
            if( isOnline ){
                ArrayList<String> errors = new ArrayList<>();
                if( positionTitle.getText().length() == 0 ){
                    errors.add("Please enter the position title.");
                }

                if( companyName.getText().length() == 0 ){
                    errors.add("Please enter the company name.");
                }

                if( _jobType == null ){
                    errors.add("Please select the employment type.");
                }

                if( _positionLevel == null ){
                    errors.add("Please select the job level.");
                }

                if( monthStart.getSelectedItemPosition() == Spinner.INVALID_POSITION || yearStart.getSelectedItemPosition() == Spinner.INVALID_POSITION ){
                    errors.add("Please specify the month and year you start this work.");
                }

                //if( ( monthEnd.getSelectedItemPosition() != 0 && yearEnd.getSelectedItemPosition() != 0 )
                // || ( monthEnd.getSelectedItemPosition() == 0 && yearEnd.getSelectedItemPosition() == 0 ) ){
                // (both selected || both not selected) -- ok
                //}else{
                //    errors.add("Please specify both resignation month and year, or left both blank.");
                // }

                if ((monthEnd.getSelectedItemPosition() == 0 || yearEnd.getSelectedItemPosition() == 0)
                        && (monthEnd.getSelectedItemPosition() != 0 || yearEnd.getSelectedItemPosition() != 0)) {
                    errors.add("Please specify both resignation month and year, or left both blank.");
                }

                //if( industry.getSelectedItemPosition() == Spinner.INVALID_POSITION ){
                if( _industry == null ){
                    errors.add("Please select the company industry.");
                }

                if( selectedJobSpec.getText().equals(getResources().getString(R.string.no_value)) ){
                    errors.add("Please select your work specialisation.");
                }

                if( selectedJobRole.getText().equals(getResources().getString(R.string.no_value)) ){
                    errors.add("Please select the sub specialisation.");
                }

                if( errors.size() == 0 ){
                    ContentValues cv = new ContentValues();

                    String thePositionTitle = positionTitle.getText().toString();
                    cv.put("position", thePositionTitle);

                    String theCompanyName = companyName.getText().toString();
                    cv.put("company", theCompanyName);

                    cv.put("job_spec_id", _jobSpec.id);
                    cv.put("job_role_id", _jobRole.id);
                    cv.put("job_type_id", _jobType.id);
                    cv.put("job_level_id", _positionLevel.id);
                    cv.put("industry_id", _industry.id);

                    String experience = exp.getText().toString();
                    cv.put("experience", experience);

                    cv.put("salary", salary.getText().toString());
                    MyCurrency _currency = (MyCurrency)currency.getSelectedItem();
                    if( _currency.id <= 0 ){ _currency.id = 6; }
                    cv.put("currency_id", _currency.id);

                    String _monthStart = monthStart.getSelectedItem().toString();
                    String _yearStart = yearStart.getSelectedItem().toString();
                    String startedOn = Jenjobs.date(_yearStart+" "+_monthStart+" 01", "yyyy-MM-dd", "yyyy MMMM dd");
                    cv.put("started_on", startedOn);

                    String endOn = null;
                    if( monthEnd.getSelectedItemPosition() != 0 || yearEnd.getSelectedItemPosition() != 0 ){
                        String _monthEnd = monthEnd.getSelectedItem().toString();
                        String _yearEnd = yearEnd.getSelectedItem().toString();
                        endOn = Jenjobs.date(_yearEnd+" "+_monthEnd+" 01", "yyyy-MM-dd", "yyyy MMMM dd");
                    }
                    cv.put("resigned_on", ""+endOn);
                    cv.put("update_at", Jenjobs.date(null, "yyyy-MM-dd", null));

                    if( currentId > 0 ){
                        tableWorkExperience.updateWorkExperience(cv, currentId);
                    }else{
                        Long _savedId = tableWorkExperience.addWorkExperience(cv);
                        currentId = _savedId.intValue();

                        TableProfile tableProfile = new TableProfile(getApplicationContext());
                        ContentValues cv3 = new ContentValues();
                        cv3.put("no_work_exp", 0); // set to got work exp
                        tableProfile.updateProfile(cv3, profileId);
                    }

                    // update got work exp
                    ContentValues _cv = new ContentValues();
                    _cv.put("no_work_exp", 0);
                    tableProfile.updateProfile(_cv,profileId);

                    JSONObject obj = new JSONObject();
                    String url = Jenjobs.WORK_EXPERIENCE_URL;
                    if( savedId > 0 ){ url += "/"+ savedId; }
                    url += "?access-token=" + accessToken;

                    try {
                        obj.put("job_spec_id", _jobSpec.id);
                        obj.put("job_role_id", _jobRole.id);
                        obj.put("job_type_id", _jobType.id);
                        obj.put("job_level_id", _positionLevel.id);
                        obj.put("industry_id", _industry.id);
                        obj.put("currency_id", _currency.id);
                        obj.put("position", thePositionTitle);
                        obj.put("company", theCompanyName);
                        obj.put("experience", experience);
                        obj.put("salary", salary.getText().toString());
                        obj.put("started_on", startedOn);
                        obj.put("resigned_on", ""+endOn);

                        String[] s = {url, obj.toString()};
                        PostRequest p = new PostRequest();
                        p.setResultListener(new PostRequest.ResultListener() {
                            @Override
                            public void processResult(JSONObject result) {
                                if (result != null) {
                                    if (result.optInt("id") > 0) {
                                        ContentValues cv = new ContentValues();
                                        cv.put("_id", result.optInt("id"));
                                        tableWorkExperience.updateWorkExperience(cv, currentId);
                                    }
                                }
                            }
                        });
                        p.execute(s);
                    } catch (JSONException e) {
                        Log.e("jsonExcpt", e.getMessage());
                    }

                    Intent intent = new Intent();
                    intent.putExtra("id", currentId);
                    intent.putExtra("selectedWork", selectedWork);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), TextUtils.join(". ", errors), Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_JOB_SPEC) {
            if (resultCode == RESULT_OK) {
                Bundle extra = data.getExtras();
                JobSpec jobSpec = (JobSpec)extra.get("jobspec");
                if( jobSpec != null ){
                    selectedJobSpec.setText(jobSpec.name);
                    //theJobSpec = jobSpec.id;
                    _jobSpec = jobSpec;
                }

                // reset job role
                selectedJobRole.setText(getResources().getString(R.string.no_value));
                _jobRole = null;
            }
        }else if( requestCode == SELECT_JOB_ROLE ){
            if (resultCode == RESULT_OK) {
                Bundle extra = data.getExtras();
                JobRole jobrole = (JobRole)extra.get("jobrole");
                if( jobrole != null ) {
                    selectedJobRole.setText(jobrole.name);
                    _jobRole = jobrole;
                }
            }
        }else if( requestCode == SELECT_INDUSTRY ){
            if (resultCode == RESULT_OK) {
                Bundle extra = data.getExtras();
                Industry industry = (Industry)extra.get("industry");
                if( industry != null ) {
                    selectedIndustry.setText(industry.name);
                    _industry = industry;
                }
            }
        }else if( requestCode == SELECT_JOB_TYPE ){
            if (resultCode == RESULT_OK) {
                Bundle extra = data.getExtras();
                JobType jobType = (JobType)extra.get("jobType");
                if( jobType != null ) {
                    _jobType = jobType;
                    selectedJobType.setText(_jobType.name);
                }
            }
        }else if( requestCode == SELECT_POSITION_LEVEL ){
            if (resultCode == RESULT_OK) {
                Bundle extra = data.getExtras();
                PositionLevel positionLevel = (PositionLevel)extra.get("positionlevel");
                if( positionLevel != null ) {
                    _positionLevel = positionLevel;
                    selectedPositionLevel.setText(_positionLevel.name);
                }
            }
        }
    }
}
