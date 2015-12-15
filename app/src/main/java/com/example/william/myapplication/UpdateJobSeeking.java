package com.example.william.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class UpdateJobSeeking extends ActionBarActivity {

    public static final int SELECT_JOB_SEEKING_STATUS = 1;
    public static final int SELECT_COUNTRY = 2;
    public static final int SELECT_STATE = 3;
    public static final int SELECT_JOB_NOTICE = 4;

    SharedPreferences sharedPref;

    private TextView selectedMalaysiaState;
    private ArrayList<State> selectedMalaysiaStateValues = new ArrayList<>();
    private TextView selectedCountry;
    private ArrayList<Country> selectedCountryValues = new ArrayList<>();
    private TextView selectedJobSeekingStatus;
    private JobSeekingStatus selectedJobSeekingStatusValues = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_job_seeking);

        sharedPref = this.getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);

        selectedMalaysiaState = (TextView)findViewById(R.id.selectedMalaysiaState);
        selectedCountry = (TextView)findViewById(R.id.selectedCountry);

        LinearLayout selectJobSeekingStatus = (LinearLayout)findViewById(R.id.selectJobSeekingStatus);
        LinearLayout selectCountry = (LinearLayout)findViewById(R.id.selectCountry);
        LinearLayout selectMalaysiaState = (LinearLayout)findViewById(R.id.selectMalaysiaState);
        LinearLayout selectJobNotice = (LinearLayout)findViewById(R.id.selectJobNotice);

        selectJobSeekingStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectJobSeekingStatus.class);
                if( selectedJobSeekingStatusValues != null ){
                    intent.putExtra("jobseekingstatus", selectedJobSeekingStatusValues);
                }
                startActivityForResult(intent, SELECT_JOB_SEEKING_STATUS);
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

        Button update = (Button) findViewById(R.id.save_jobseeking_information);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues cv = new ContentValues();

                new SaveDataTask().execute(cv);
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
                JobSeekingStatus selectedValues = (JobSeekingStatus) filters.get("jobseekingstatus");
                if (selectedValues != null) {
                    selectedJobSeekingStatusValues = selectedValues;
                    selectedJobSeekingStatus.setText( selectedValues.name );
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

    public class SaveDataTask extends AsyncTask<ContentValues, Void, Object> {

        SaveDataTask(){}

        @Override
        protected Object doInBackground(ContentValues... params) {
            Object _response = null;

            String accessToken = sharedPref.getString("access_token", null);
            String url = "http://api.jenjobs.com/jobseeker/jobseeking-info";
            url += "?access-token="+accessToken;

            final HttpClient httpclient = new DefaultHttpClient();
            final HttpPost httppost = new HttpPost( url );
            httppost.addHeader("Content-Type", "application/json");
            httppost.addHeader("Accept", "application/json");
            HttpResponse _http_response = null;

            JSONObject obj = new JSONObject();
            ContentValues cv2 = params[0];

            Iterator t = cv2.keySet().iterator();
            while( t.hasNext() ){
                String key = String.valueOf(t.next());
                try {
                    obj.put(key, cv2.get(key));
                } catch (JSONException e) {
                    Log.e("test", e.getMessage());
                }
            }

            try {
                StringEntity entity = new StringEntity(obj.toString());
                entity.setContentEncoding(HTTP.UTF_8);
                entity.setContentType("application/json");
                httppost.setEntity(entity);

                _http_response = httpclient.execute(httppost);
                HttpEntity _entity = _http_response.getEntity();
                InputStream is = _entity.getContent();

                String responseString = JenHttpRequest.readInputStreamAsString(is);
                _response = JenHttpRequest.decodeJsonObjectString(responseString);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return _response;
        }


        @Override
        protected void onPostExecute(final Object success) {
            Log.e("onPostEx", "" + success);
            if( success != null ){
                JSONObject _success = (JSONObject) success;
                try {
                    _success.get("status_code");
                } catch (JSONException e) {
                    Log.e("test", e.getMessage());
                }
            }
        }
    }
}
