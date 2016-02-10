package com.example.william.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    TableJobPreferenceLocation tableJobPreferenceLocation;

    ArrayList<State> _state;
    ArrayList<Country> _country;
    ArrayList<JobType> _jobtype;
    MyCurrency savedCurrency;

    SharedPreferences sharedPref;
    boolean isOnline;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_job_preference);
        setTitle(getText(R.string.update_job_preference));

        sharedPref = this.getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        isOnline = Jenjobs.isOnline(getApplicationContext());

        final LinearLayout insertSalary = (LinearLayout)findViewById(R.id.insertSalary);
        LinearLayout selectJobType = (LinearLayout)findViewById(R.id.selectJobType);
        LinearLayout selectMalaysiaState = (LinearLayout)findViewById(R.id.selectMalaysiaState);
        LinearLayout selectOtherCountry = (LinearLayout)findViewById(R.id.selectOtherCountry);

        insertedSalary = (TextView)findViewById(R.id.insertedSalary);
        selectedCurrency = (TextView)findViewById(R.id.selectedCurrency);
        selectedJobType = (TextView)findViewById(R.id.selectedJobType);
        selectedMalaysiaState = (TextView)findViewById(R.id.selectedMalaysiaState);
        selectedOtherCountry = (TextView)findViewById(R.id.selectedOtherCountry);

        Button save = (Button)findViewById(R.id.save_job_preferred);
        Button cancel = (Button)findViewById(R.id.cancelButton);

        HashMap listOfJobTypes = Jenjobs.getJobType();
        HashMap listOfStates = Jenjobs.getState();
        HashMap listOfCountries = Jenjobs.getCountry();
        HashMap listOfCurrencies = Jenjobs.getCurrency();

        _state = new ArrayList<>();
        _country = new ArrayList<>();
        _jobtype = new ArrayList<>();
        savedCurrency = new MyCurrency(6, "MYR");

        /*
        * fetch saved data
        * */
        tableJobPreference = new TableJobPreference(this);
        tableJobPreferenceLocation = new TableJobPreferenceLocation(this);

        Cursor c = tableJobPreference.getJobPreference();

        if( c.moveToFirst() ){
            String savedSalary = c.getString(0);
            if( savedSalary == null || savedSalary.equals("null") ){
                savedSalary = "";
            }

            insertedSalary.setText(savedSalary);
            selectedCurrency.setText((String)listOfCurrencies.get( c.getInt(1) ) );

            String savedJobTypes = c.getString(2);
            try {
                JSONArray arrSavedJobTypes = new JSONArray(savedJobTypes);
                ArrayList<String> jobTypeName = new ArrayList<>();

                for(int i=0;i<arrSavedJobTypes.length();i++){
                    int theJobType = (int) arrSavedJobTypes.get(i);
                    jobTypeName.add( (String)listOfJobTypes.get( theJobType ) );

                    _jobtype.add(new JobType(theJobType, (String)listOfJobTypes.get( theJobType )));
                }

                selectedJobType.setText( TextUtils.join( ", ", jobTypeName ) );
                savedCurrency = new MyCurrency( c.getInt(1), (String)listOfCurrencies.get( c.getInt(1) ) );
            } catch (JSONException e) {
                Log.e("jsonErr", e.getMessage());
            }
        }
        c.close();

        Cursor d = tableJobPreferenceLocation.getJobPreference();
        if( d.moveToFirst() ){
            ArrayList<String> selectedStates = new ArrayList<>();
            ArrayList<String> selectedCountries = new ArrayList<>();

            while( !d.isAfterLast() ){
                int theState = d.getInt(2);
                int theCountry = d.getInt(4);

                if( theState > 0 ){
                    selectedStates.add( (String) listOfStates.get( theState ) );
                    _state.add(new State(theState, (String)listOfStates.get(theState)));
                    theCountry = 0;
                }

                if( theCountry > 0 ){
                    selectedCountries.add( (String) listOfCountries.get( theCountry ) );
                    _country.add(new Country(theCountry, (String) listOfCountries.get( theCountry )));
                }

                d.moveToNext();
            }

            selectedMalaysiaState.setText(TextUtils.join(", ", selectedStates));
            selectedOtherCountry.setText(TextUtils.join(", ", selectedCountries));
        }
        d.close();

        /*
        * clicked on salary
        * */
        insertSalary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), UpdateSalary.class);
                // TODO: pass already inserted salary and currency record from db
                String _insertedSalary = insertedSalary.getText().toString();
                if( _insertedSalary.length() > 0 ){
                    intent.putExtra("salary", _insertedSalary);
                }

                // TODO: add currency to intent
                if( savedCurrency != null ){
                    intent.putExtra("currency", savedCurrency);
                }
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
                intent.setClass(getApplicationContext(), SelectJobType.class);
                intent.putExtra("single", false);
                // TODO: pass already inserted job type from db
                if( _jobtype.size() > 0 ){
                    intent.putExtra("jobtype", _jobtype);
                }
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
                intent.putExtra("single", false);
                // TODO: pass already inserted value from db
                if( _state.size() > 0 ){
                    intent.putExtra("state", _state);
                }
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
                intent.putExtra("single", false);
                // TODO: pass already inserted value from db
                if( _country.size() > 0 ){
                    intent.putExtra("country", _country);
                }
                startActivityForResult(intent, OTHER_COUNTRY);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
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
                String[] params = new String[5];
                ContentValues cv = new ContentValues();
                String summary = "";

                if( insertedSalary.getText().toString().length() > 0 ){
                    cv.put("salary", insertedSalary.getText().toString());
                    params[0] = insertedSalary.getText().toString();
                }else{
                    errors.add("Please enter your expected salary.");
                }

                if( savedCurrency != null ){
                    cv.put("currency_id", savedCurrency.id);
                    params[1] = String.valueOf(savedCurrency.id);

                    summary = savedCurrency.name+" "+insertedSalary.getText().toString()+" per month";
                }

                ArrayList<Integer> savedJobTypeId = new ArrayList<>();
                if( _jobtype.size() > 0 ){
                    for(int i=0;i<_jobtype.size();i++){
                        JobType __jobtype = _jobtype.get(i);
                        savedJobTypeId.add(__jobtype.id);
                    }
                    cv.put("job_type_id", (new JSONArray( savedJobTypeId )).toString() );
                    params[2] = (new JSONArray( savedJobTypeId )).toString();
                }else{
                    errors.add("Please select your preferred job type(s).");
                }

                ArrayList<Integer> savedStateId = new ArrayList<>();
                if( _state.size() > 0 ){
                    tableJobPreferenceLocation.truncate();
                    for(int i=0;i<_state.size();i++){
                        State __state = _state.get(i);
                        savedStateId.add(__state.id);

                        ContentValues cv2 = new ContentValues();
                        cv2.put("country_id", 127);
                        cv2.put("state_id", __state.id);
                        tableJobPreferenceLocation.insertJobPreference(cv2);
                    }
                    params[3] = (new JSONArray( savedStateId )).toString();
                }else{
                    errors.add("Please select your preferred working location in Malaysia.");
                }

                if( errors.size() == 0 ){
                    tableJobPreference.updateJobPreference(cv);
                    ArrayList<Integer> savedCountryId = new ArrayList<>();
                    if( _country.size() > 0 ) {
                        for(int i=0;i<_country.size();i++){
                            Country __country = _country.get(i);
                            savedCountryId.add(__country.id);

                            ContentValues cv2 = new ContentValues();
                            cv2.put("country_id", __country.id);
                            cv2.put("state_id", 0);
                            tableJobPreferenceLocation.insertJobPreference(cv2);
                        }
                    }
                    params[4] = (new JSONArray( savedCountryId )).toString();

                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("salary", params[0]);
                        obj.put("currency_id", params[1]);
                        obj.put("job_type_id", new JSONArray(params[2]));
                        obj.put("state_id", new JSONArray(params[3]));
                        obj.put("country_id", new JSONArray(params[4]));

                        PostRequest p = new PostRequest();
                        p.setResultListener(new PostRequest.ResultListener() {
                            @Override
                            public void processResult(JSONObject success) {
                                if( success != null ){
                                    Log.e("success", success.toString());
                                }
                            }
                        });
                        String accessToken = sharedPref.getString("access_token", null);
                        p.execute(Jenjobs.JOB_PREFERENCE_URL+"?access-token="+accessToken);
                    } catch (JSONException e) {
                        Log.e("JSONException", e.getMessage());
                    }

                    Intent intent = new Intent();
                    intent.putExtra("summary", summary);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), TextUtils.join(", ", errors), Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == INSERT_SALARY) {
            if (resultCode == RESULT_OK) {
                Bundle extra = data.getExtras();

                String salary = (String)extra.get("the_text");
                MyCurrency c = (MyCurrency)extra.get("the_currency");

                insertedSalary.setText(salary);
                if( c != null ){
                    selectedCurrency.setText(c.name);
                    savedCurrency = c;
                }
            }
        }else if( requestCode == SELECT_JOB_TYPE ){
            if (resultCode == RESULT_OK) {
                Bundle extra = data.getExtras();

                ArrayList jobtypes = (ArrayList) extra.get("jobType");
                ArrayList<String> jobtype_names = new ArrayList<>();

                if( jobtypes != null && jobtypes.size() > 0 ){
                    _jobtype.clear();
                    for( int i=0;i< jobtypes.size(); i++ ){
                        JobType jobType = (JobType) jobtypes.get(i);
                        jobtype_names.add(jobType.name);
                        _jobtype.add(jobType);
                    }
                    selectedJobType.setText(TextUtils.join(", ", jobtype_names));
                }
            }
        }else if( requestCode == MALAYSIA_STATE ){
            if (resultCode == RESULT_OK) {
                Bundle extra = data.getExtras();
                ArrayList states = (ArrayList)extra.get("state");
                ArrayList<String> state_name = new ArrayList<>();

                if( states != null && states.size() > 0 ){
                    _state.clear();
                    for( int i=0;i< states.size(); i++ ){
                        State state = (State)states.get(i);
                        state_name.add(state.name);
                        _state.add(state);
                    }
                    selectedMalaysiaState.setText(TextUtils.join(", ", state_name));
                }
            }
        }else if( requestCode == OTHER_COUNTRY ){
            if (resultCode == RESULT_OK) {
                Bundle extra = data.getExtras();

                ArrayList countries = (ArrayList)extra.get("country");
                ArrayList<String> country_name = new ArrayList<>();

                if( countries != null && countries.size() > 0 ){
                    _country.clear();
                    for( int i=0;i< countries.size(); i++ ){
                        Country country = (Country)countries.get(i);
                        country_name.add(country.name);
                        _country.add(country);
                        Log.e("country_name", country.name);
                    }
                    selectedOtherCountry.setText(TextUtils.join(", ", country_name));
                }
            }
        }
    }
}
