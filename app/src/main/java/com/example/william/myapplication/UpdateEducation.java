package com.example.william.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class UpdateEducation extends ActionBarActivity {

    private static final int SELECT_EDU_LEVEL = 1;
    private static final int SELECT_EDU_FIELD = 2;
    private static final int SELECT_GRADUATION_YEAR = 3;

    SharedPreferences sharedPreferences;
    String accessToken;

    // SQLite id, not the one from JenJOBS
    int currentEducationId = 0;
    int remoteEducationId = 0;
    private static Context context;

    TableEducation tableEducation;

    TextView selectedEducationLevel;
    TextView enteredSchool;
    TextView enteredMajor;
    TextView selectedEducationField;
    TextView enteredGrade;
    TextView selectedGraduationYear;
    TextView enteredEducationInfo;

    EducationLevel educationLevel;
    EducationField educationField;
    String year;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_education);

        tableEducation = new TableEducation(this);
        sharedPreferences = this.getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        accessToken = sharedPreferences.getString("access_token", null);

        HashMap listOfeducationLevel = Jenjobs.getEducationLevel();
        HashMap listOfeducationField = Jenjobs.getEducationField();

        LinearLayout selectEducationLevel = (LinearLayout)findViewById(R.id.selectEducationLevel);
        LinearLayout selectEducationField = (LinearLayout)findViewById(R.id.selectEducationField);
        LinearLayout selectGraduationYear = (LinearLayout)findViewById(R.id.selectGraduationYear);

        final EditText school = (EditText)findViewById(R.id.school_text);
        final EditText major = (EditText)findViewById(R.id.edu_major_text);
        final EditText grade = (EditText)findViewById(R.id.grade_text);
        final EditText info = (EditText)findViewById(R.id.extra_info);

        // extras send for update, maybe only ID passed, then read form DB to get other values
        Bundle extras = getIntent().getExtras();
        if( !extras.isEmpty() ){
            currentEducationId = extras.getInt("id");
            Cursor ce = tableEducation.getEducationById(currentEducationId);
            if( ce.moveToFirst() ){
                /*
                id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT //0
                _id INTEGER //1
                school TEXT //2
                major TEXT //3
                edu_level_id INTEGER(4) //4
                edu_field_id INTEGER(4) //5
                country_id INTEGER(4) //6
                grade TEXT //7
                info TEXT //8
                date_graduated //9
                */
                remoteEducationId = ce.getInt(1);

                //educationLevel.setSelection( eduLevelAdapter.getItemPosition( ce.getInt(4) ) );
                Object theEducationLevel = listOfeducationLevel.get( ce.getInt(4) );
                Log.e( "theEducationLevel", ""+theEducationLevel.toString() );

                educationLevel = new EducationLevel(ce.getInt(4), String.valueOf(listOfeducationLevel.get( ce.getInt(4) ) ) );
                educationField = new EducationField(ce.getInt(5), String.valueOf(listOfeducationField.get( ce.getInt(5) ) ) );
                year = ce.getString(9).substring(0,3);

                ce.close();
            }
        }

        selectEducationLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), SelectEducationLevel.class);
                startActivityForResult(intent, SELECT_EDU_LEVEL);
            }
        });

        selectEducationField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), SelectEducationField.class);
                startActivityForResult(intent, SELECT_EDU_FIELD);
            }
        });

        selectGraduationYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), SelectGraduationYear.class);
                startActivityForResult(intent, SELECT_GRADUATION_YEAR);
            }
        });

        // save button
        Button saveEducation = (Button) findViewById(R.id.save_education);
        saveEducation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: validate input

                // TODO: save to local db

                // TODO: send POST request

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
                cv.put("edu_level_id", educationLevel.id);
                cv.put("school", school.getText().toString());
                cv.put("major", major.getText().toString());
                cv.put("edu_field_id", educationField.id);
                cv.put("grade", grade.getText().toString());
                cv.put("date_graduated", year);
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

                //new SaveEducation(remoteEducationId).execute(cv);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_EDU_LEVEL) {
            if (resultCode == RESULT_OK) {
                Bundle extra = data.getExtras();
                educationLevel = (EducationLevel)extra.get("edulevel");
                selectedEducationLevel.setText(educationLevel.name);
            }
        }else if( requestCode == SELECT_EDU_FIELD ){
            if (resultCode == RESULT_OK) {
                Bundle extra = data.getExtras();
                educationField = (EducationField)extra.get("edufield");
                selectedEducationField.setText(educationField.name);
            }
        }else if( requestCode == SELECT_GRADUATION_YEAR ){
            if (resultCode == RESULT_OK) {
                Bundle extra = data.getExtras();
                year = (String)extra.get("year");
                selectedGraduationYear.setText(year);
            }
        }
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
