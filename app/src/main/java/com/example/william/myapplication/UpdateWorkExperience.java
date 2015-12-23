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

public class UpdateWorkExperience extends ActionBarActivity {

    private static final int SELECT_JOB_SPEC = 1;
    private static final int SELECT_JOB_ROLE = 2;

    TableWorkExperience tableWorkExperience;
    TableJobSpec tableJobSpec;
    TableJobRole tableJobRole;

    SharedPreferences sharedPreferences;
    String accessToken;

    LinearLayout jobSpec;
    TextView selectedJobSpec;
    LinearLayout jobRole;
    TextView selectedJobRole;

    int theJobSpec = 0;
    int theJobRole = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_work_experience);

        tableWorkExperience = new TableWorkExperience(this);
        tableJobSpec = new TableJobSpec(this);
        tableJobRole = new TableJobRole(this);
        int currentId = 0;
        int savedId = 0;
        int selectedWork = 0;

        sharedPreferences = getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        accessToken = sharedPreferences.getString("access_token", null);

        final EditText positionTitle = (EditText)findViewById(R.id.position_title);
        final EditText companyName = (EditText)findViewById(R.id.company_name);

        // job type
        final Spinner employmentType = (Spinner)findViewById(R.id.employment_type);
        final JobTypeAdapter jta = new JobTypeAdapter(this);
        jta.setSingle(true);
        employmentType.setAdapter(jta);

        // job level
        final Spinner jobLevel = (Spinner)findViewById(R.id.job_level);
        JobLevelAdapter jla = new JobLevelAdapter(this);
        jla.setSingle(true);
        jobLevel.setAdapter(jla);

        // currency
        final Spinner currency = (Spinner)findViewById(R.id.currency);
        CurrencyAdapter ca = new CurrencyAdapter(this);
        currency.setAdapter(ca);
        final EditText salary = (EditText)findViewById(R.id.salary_amount);

        jobSpec = (LinearLayout)findViewById(R.id.selectJobSpec);
        selectedJobSpec = (TextView)findViewById(R.id.selectedJobSpec);

        jobRole = (LinearLayout)findViewById(R.id.selectJobRole);
        selectedJobRole = (TextView)findViewById(R.id.selectedJobRole);

        // industry
        final Spinner industry = (Spinner)findViewById(R.id.company_industry);
        IndustryAdapter ia = new IndustryAdapter(this);
        industry.setAdapter(ia);

        final Spinner monthStart = (Spinner)findViewById(R.id.month_start);
        final Spinner monthEnd = (Spinner)findViewById(R.id.month_end);
        final Spinner yearStart = (Spinner)findViewById(R.id.year_start);
        final Spinner yearEnd = (Spinner)findViewById(R.id.year_end);

        String[] listOfMonth = Jenjobs.listOfMonth();
        String[] listOfYear = Jenjobs.listOfYear();

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listOfMonth);
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listOfYear);
        monthStart.setAdapter(monthAdapter);
        monthEnd.setAdapter(monthAdapter);
        yearStart.setAdapter(yearAdapter);
        yearEnd.setAdapter(yearAdapter);

        final EditText exp = (EditText)findViewById(R.id.experience_text);

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
                employmentType.setSelection(jta.findPosition(w.getInt(6)));
                jobLevel.setSelection(jla.findPosition(w.getInt(7)));
                industry.setSelection(ia.findPosition(w.getInt(8)));

                theJobSpec = w.getInt( 4 );
                JobSpec _jobSpec = tableJobSpec.findById(theJobSpec);
                selectedJobSpec.setText(_jobSpec.name);

                theJobRole = w.getInt(5);
                JobRole _jobRole = tableJobRole.findById( theJobRole );
                selectedJobRole.setText(_jobRole.name);

                String startDate = w.getString( 12 );
                String[] startDatePart = startDate.split("\\-");
                int a = Integer.valueOf(startDatePart[1]);
                if( a < 10 ){ startDatePart[1] = ""+a; }
                monthStart.setSelection( monthAdapter.getPosition( startDatePart[1] ) );
                yearStart.setSelection( yearAdapter.getPosition( startDatePart[0] ) );

                String endDate = w.getString( 13 );
                Log.e("endDate", ""+endDate);
                if( endDate != null && !endDate.equals("null") ){
                    String[] endDatePart = endDate.split( "\\-" );
                    Log.e("endDatePart", ""+endDatePart.length);
                    monthEnd.setSelection( monthAdapter.getPosition( endDatePart[1] ) );
                    yearEnd.setSelection( yearAdapter.getPosition( endDatePart[0] ) );
                }

                if( !w.getString(9).equals("null") ){
                    // TODO: write html
                    exp.setText(Html.fromHtml(w.getString(9)) );
                }

            }
            w.close();
        }

        // event binding
        jobSpec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), SelectSingleJobSpec.class);
                startActivityForResult(intent, SELECT_JOB_SPEC);
            }
        });

        jobRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !selectedJobSpec.getText().equals(getResources().getString(R.string.no_value)) ){
                    Intent intent = new Intent();
                    intent.putExtra("jobspecid", theJobSpec);
                    intent.setClass(getApplicationContext(), SelectSingleJobRole.class);
                    startActivityForResult(intent, SELECT_JOB_ROLE);
                }
            }
        });

        Button okButton = (Button)findViewById(R.id.okButton);
        Button cancelButton = (Button)findViewById(R.id.cancelButton);

        final int finalCurrentId = currentId;
        final int finalSavedId = savedId;
        final int finalSelectedWork = selectedWork;
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> errors = new ArrayList<>();

                if( positionTitle.getText().length() == 0 ){
                    errors.add("Please enter the position title.");
                }

                if( companyName.getText().length() == 0 ){
                    errors.add("Please enter the company name.");
                }

                if( employmentType.getSelectedItemPosition() == Spinner.INVALID_POSITION ){
                    errors.add("Please select the employment type.");
                }

                if( jobLevel.getSelectedItemPosition() == Spinner.INVALID_POSITION ){
                    errors.add("Please select the job level.");
                }

                if( monthStart.getSelectedItemPosition() == Spinner.INVALID_POSITION || yearStart.getSelectedItemPosition() == Spinner.INVALID_POSITION ){
                    errors.add("Please specify the month and year you start this work.");
                }

                Log.e("invalid", ""+Spinner.INVALID_POSITION);
                Log.e("monthEnd", ""+monthEnd.getSelectedItemPosition());
                Log.e("yearEnd", ""+yearEnd.getSelectedItemPosition());
                if( monthEnd.getSelectedItemPosition() != 0 || yearEnd.getSelectedItemPosition() != 0 ){
                    errors.add("Please specify both resignation month and year, or left both blank.");
                }

                if( industry.getSelectedItemPosition() == Spinner.INVALID_POSITION ){
                    errors.add("Please select the company industry.");
                }

                if( selectedJobSpec.getText().equals(getResources().getString(R.string.no_value)) ){
                    errors.add("Please select your work specialisation.");
                }

                if( selectedJobRole.getText().equals(getResources().getString(R.string.no_value)) ){
                    errors.add("Please select the sub specialisation.");
                }

                if( errors.size() == 0 ){
                    /*
                    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT //0
                    _id INTEGER //1
                    position TEXT //2
                    company TEXT //3
                    job_spec_id INTEGER(4) //4
                    job_role_id INTEGER(4) //5
                    job_type_id INTEGER(4) //6
                    job_level_id INTEGER(4) //7
                    industry_id INTEGER(4) //8
                    experience TEXT //9
                    salary INTEGER //10
                    currency_id INTEGER //11
                    started_on NUMERIC //12
                    resigned_on NUMERIC //13
                    update_at //14
                    */

                    // TODO: save to database
                    ContentValues cv = new ContentValues();

                    String thePositionTitle = positionTitle.getText().toString();
                    cv.put("position", thePositionTitle);

                    String theCompanyName = companyName.getText().toString();
                    cv.put("company", theCompanyName);

                    cv.put("job_spec_id", theJobSpec);
                    cv.put("job_role_id", theJobRole);

                    JobType jt = (JobType)employmentType.getSelectedItem();
                    cv.put("job_type_id", jt.id);

                    JobLevel jl = (JobLevel)jobLevel.getSelectedItem();
                    cv.put("job_level_id", jl.id);

                    Industry ind = (Industry)industry.getSelectedItem();
                    cv.put("industry_id", ind.id);

                    String experience = exp.getText().toString();
                    cv.put("experience", experience);

                    cv.put("salary", salary.getText().toString());
                    MyCurrency _currency = (MyCurrency)currency.getSelectedItem();
                    if( _currency.id <= 0 ){ _currency.id = 6; }
                    cv.put("currency_id", _currency.id);

                    String _monthStart = monthStart.getSelectedItem().toString();
                    String _yearStart = yearStart.getSelectedItem().toString();
                    String startedOn = _yearStart+"-"+_monthStart+"-01";
                    cv.put("started_on", startedOn);

                    String endOn = null;
                    if( monthEnd.getSelectedItemPosition() != 0 || yearEnd.getSelectedItemPosition() != 0 ){
                        String _monthEnd = monthEnd.getSelectedItem().toString();
                        String _yearEnd = yearEnd.getSelectedItem().toString();
                        endOn = _yearEnd+"-"+_monthEnd+"-01";
                    }
                    cv.put("resigned_on", ""+endOn);
                    cv.put("update_at", Jenjobs.date(null, "yyyy-MM-dd", null));

                    int localId = finalCurrentId;
                    if( finalCurrentId > 0 ){
                        tableWorkExperience.updateWorkExperience(cv, finalCurrentId);
                    }else{
                        Long _savedId = tableWorkExperience.addWorkExperience(cv);
                        localId = _savedId.intValue();
                    }

                    // TODO: send POST request
                    JSONObject obj = new JSONObject();
                    String url = Jenjobs.WORK_EXPERIENCE_URL;
                    if( finalSavedId > 0 ){ url += "/"+ finalSavedId; }
                    url += "?access-token=" + accessToken;

                    try {
                        obj.put("job_spec_id", theJobSpec);
                        obj.put("job_role_id", theJobRole);
                        obj.put("job_type_id", jt.id);
                        obj.put("job_level_id", jl.id);
                        obj.put("industry_id", ind.id);
                        obj.put("currency_id", _currency.id);
                        obj.put("position", thePositionTitle);
                        obj.put("company", theCompanyName);
                        obj.put("experience", experience);
                        obj.put("salary", salary.getText().toString());
                        obj.put("started_on", startedOn);
                        obj.put("resigned_on", ""+endOn);

                    } catch (JSONException e) {
                        Log.e("jsonExcpt", e.getMessage());
                    }
                    Log.e("obj", obj.toString());
                    String[] s = {url, obj.toString()};
                    new PostRequest().execute(s);

                    Intent intent = new Intent();
                    intent.putExtra("id", localId);
                    if( finalSelectedWork > 0 ){
                        intent.putExtra("selectedWork", finalSelectedWork);
                    }
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), TextUtils.join(". ", errors), Toast.LENGTH_SHORT).show();
                }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_JOB_SPEC) {
            if (resultCode == RESULT_OK) {
                Bundle extra = data.getExtras();
                JobSpec jobSpec = (JobSpec)extra.get("spec");
                selectedJobSpec.setText(jobSpec.name);
                theJobSpec = jobSpec.id;
            }
        }else if( requestCode == SELECT_JOB_ROLE ){
            if (resultCode == RESULT_OK) {
                Bundle extra = data.getExtras();
                JobRole jobrole = (JobRole)extra.get("role");
                selectedJobRole.setText(jobrole.name);
                theJobRole = jobrole.id;
            }
        }
    }

}
