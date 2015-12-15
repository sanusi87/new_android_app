package com.example.william.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class UpdateEducation extends ActionBarActivity {

    SharedPreferences sharedPref;
    // SQLite id, not the one from JenJOBS
    int currentEducationId = 0;
    int remoteEducationId = 0;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_education);

        sharedPref = this.getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);

        // education level
        final Spinner educationLevel = (Spinner)findViewById(R.id.education_level);
        EducationLevelAdapter eduLevelAdapter = new EducationLevelAdapter(this);
        educationLevel.setAdapter(eduLevelAdapter);

        final EditText school = (EditText)findViewById(R.id.school_text);
        final EditText major = (EditText)findViewById(R.id.edu_major_text);

        // education field
        final Spinner educationField = (Spinner)findViewById(R.id.education_field);
        EducationFieldAdapter eduFieldAdapter = new EducationFieldAdapter(this);
        educationField.setAdapter(eduFieldAdapter);

        final EditText grade = (EditText)findViewById(R.id.grade_text);

        final Spinner year = (Spinner) findViewById(R.id.graduation_year);
        ArrayList<Integer> years = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        for( int y = currentYear; y > currentYear-50; y-- ){
            years.add(y);
        }
        year.setAdapter(new ArrayAdapter<Integer>(this, android.R.layout.simple_list_item_1, years));

        final EditText info = (EditText)findViewById(R.id.extra_info);

        // extras send for update, maybe only ID passed, then read form DB to get other values
        Bundle extras = getIntent().getExtras();
        if( !extras.isEmpty() ){
            currentEducationId = extras.getInt("id");

            // read db
            remoteEducationId = 0;
        }

        // save button
        Button saveEducation = (Button) findViewById(R.id.save_education);
        saveEducation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                "_id INTEGER, "+
                "school TEXT, " +
                "major TEXT, " +
                "edu_level_id INTEGER(4), " +
                "edu_field_id INTEGER(4), " +
                "country_id INTEGER(4), " +
                "grade TEXT, " +
                "info TEXT, " +
                "date_graduated NUMERIC);";
                */
                ContentValues cv = new ContentValues();
                Log.e("selectedItem", ""+educationLevel.getSelectedItemId());
                Log.e("selectedItem2", "" + educationLevel.getSelectedItemPosition());
                cv.put("edu_level_id", educationLevel.getSelectedItemId());
                cv.put("school", school.getText().toString());
                cv.put("major", major.getText().toString());
                cv.put("edu_field_id", educationField.getSelectedItemId());
                cv.put("grade", grade.getText().toString());
                cv.put("date_graduated", year.getSelectedItemId());
                cv.put("info", info.getText().toString());

                // JenJOBS id
                cv.put("_id", remoteEducationId);

                //initSaveEducation();

                TableEducation edu = new TableEducation(context);

                if( currentEducationId > 0 ){
                    edu.updateEducation(cv, currentEducationId);
                }else{
                    Long newEducationId = edu.addEducation(cv);
                }

                AsyncTask a = new SaveEducation(remoteEducationId);
                a.execute(cv);
            }
        });
    }


    public class SaveEducation extends AsyncTask<Void, Void, Object> {

        private int currentEduId = 0;
        SaveEducation( int currentEduId ){
            this.currentEduId = currentEduId;
        }

        @Override
        protected Object doInBackground(Void... params) {
            Object _response = null;

            Log.d("params", ""+params);
            for( Object o: params ){
                Log.d("params1", ""+o);
            }
            Log.d("params2", ""+params[0]);

            // create JSON string
            /*
            JSONObject obj = new JSONObject();
            for(String key: extras.keySet()){
                Object value = extras.get(key);
                try {
                    obj.put(key, value.toString());
                } catch (JSONException e) {
                    Log.e("test", e.getMessage());
                }
            }
            return obj.toString();
            */
            // end create JSON string

            String accessToken = sharedPref.getString("access_token", null);
            String url = "http://api.jenjobs.com/jobseeker/qualification";
            if( currentEduId > 0 ){
                url += "/"+currentEduId;
            }
            url += "?access-token="+accessToken;

            final HttpClient httpclient = new DefaultHttpClient();
            final HttpPost httppost = new HttpPost( url );
            httppost.addHeader("Content-Type", "application/json");
            httppost.addHeader("Accept", "application/json");

            HttpResponse _http_response = null;
            try {
                StringEntity entity = new StringEntity("{}");
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

            }
        }
    }
}
