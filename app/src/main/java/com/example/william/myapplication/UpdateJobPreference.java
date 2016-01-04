package com.example.william.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class UpdateJobPreference extends Activity {

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

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_job_preference);

        sharedPref = this.getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);

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

            insertedSalary.setText(savedSalary);
            selectedCurrency.setText( (String)listOfCurrencies.get( c.getString(1) ) );

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
                savedCurrency = new MyCurrency( c.getInt(1), (String)listOfCurrencies.get( c.getString(1) ) );
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
                intent.putExtra("single", false);
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
                startActivityForResult(intent, OTHER_COUNTRY);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] params = {};
                ContentValues cv = new ContentValues();
                cv.put("salary", insertedSalary.getText().toString());
                cv.put("currency_id", selectedCurrency.getText().toString());

                params[0] = insertedSalary.getText().toString();
                params[1] = String.valueOf(savedCurrency.id);

                ArrayList<Integer> savedJobTypeId = new ArrayList<>();
                if( _jobtype.size() > 0 ){
                    for(int i=0;i<_jobtype.size();i++){
                        JobType __jobtype = _jobtype.get(i);
                        savedJobTypeId.add(__jobtype.id);
                    }
                    cv.put("job_type_id", (new JSONArray( savedJobTypeId )).toString() );
                }
                params[2] = (new JSONArray( savedJobTypeId )).toString();
                tableJobPreference.updateJobPreference(cv);

                ArrayList<Integer> savedStateId = new ArrayList<>();
                if( _state.size() > 0 ){
                    for(int i=0;i<_state.size();i++){
                        State __state = _state.get(i);
                        savedStateId.add(__state.id);

                        ContentValues cv2 = new ContentValues();
                        cv2.put("country_id", 127);
                        cv2.put("state_id", __state.id);
                        tableJobPreferenceLocation.updateJobPreference(cv2);
                    }
                }
                params[3] = (new JSONArray( savedStateId )).toString();

                ArrayList<Integer> savedCountryId = new ArrayList<>();
                if( _country.size() > 0 ) {
                    for(int i=0;i<_country.size();i++){
                        Country __country = _country.get(i);
                        savedCountryId.add(__country.id);

                        ContentValues cv2 = new ContentValues();
                        cv2.put("country_id", __country.id);
                        cv2.put("state_id", 0);
                        tableJobPreferenceLocation.updateJobPreference(cv2);
                    }
                }
                params[4] = (new JSONArray( savedCountryId )).toString();
                new UpdateTask().execute(params);
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
                }else{

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
                    }
                    selectedOtherCountry.setText(TextUtils.join(", ", country_name));
                }
            }
        }
    }


    public class UpdateTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            Object _response = null;

            String accessToken = sharedPref.getString("access_token", null);
            String url = "http://api.jenjobs.com/jobseeker/job-preference";
            url += "?access-token="+accessToken;

            final HttpClient httpclient = new DefaultHttpClient();
            final HttpPost httppost = new HttpPost( url );
            httppost.addHeader("Content-Type", "application/json");
            httppost.addHeader("Accept", "application/json");
            HttpResponse _http_response;

            JSONObject obj = new JSONObject();
            try {
                obj.put("salary", params[0]);
                obj.put("currency_id", params[1]);
                obj.put("job_type_id", params[2]);
                obj.put("country_id", params[3]);
                obj.put("state_id", params[4]);

                StringEntity entity = new StringEntity(obj.toString());
                entity.setContentEncoding(HTTP.UTF_8);
                entity.setContentType("application/json");
                httppost.setEntity(entity);

                _http_response = httpclient.execute(httppost);
                HttpEntity _entity = _http_response.getEntity();
                InputStream is = _entity.getContent();

                String responseString = JenHttpRequest.readInputStreamAsString(is);
                _response = JenHttpRequest.decodeJsonObjectString(responseString);

            } catch (JSONException e) {
                Log.e("JSONException", e.getMessage());
            } catch (ClientProtocolException e) {
                Log.e("ClientProtocolException", e.getMessage());
            } catch (UnsupportedEncodingException e) {
                Log.e("UnsupportedEncoding", e.getMessage());
            } catch (IOException e) {
                Log.e("IOException", e.getMessage());
            }

            return (JSONObject) _response;
        }

        @Override
        protected void onPostExecute(final JSONObject success) {
            if( success != null ){
                Log.e("success", success.toString());
            }
        }
    }
}
