package com.example.william.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class UpdateEducation extends ActionBarActivity {

    private static final int SELECT_EDU_LEVEL = 1;
    private static final int SELECT_EDU_FIELD = 2;
    private static final int SELECT_GRADUATION_YEAR = 3;
    private static final int ENTER_SCHOOL = 4;
    private static final int ENTER_MAJOR = 5;
    private static final int ENTER_GRADE = 6;
    private static final int ENTER_EDUCATION_INFO = 7;

    SharedPreferences sharedPreferences;
    String accessToken;

    // SQLite id, not the one from JenJOBS
    int currentEducationId = 0;
    int remoteEducationId = 0;
    int currentViewPosition = 0;
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

        HashMap listOfEducationLevel = Jenjobs.getEducationLevel();
        HashMap listOfEducationField = Jenjobs.getEducationField();

        LinearLayout selectEducationLevel = (LinearLayout)findViewById(R.id.selectEducationLevel);
        LinearLayout selectEducationField = (LinearLayout)findViewById(R.id.selectEducationField);
        LinearLayout selectGraduationYear = (LinearLayout)findViewById(R.id.selectGraduationYear);
        LinearLayout enterSchool = (LinearLayout)findViewById(R.id.enterSchool);
        LinearLayout enterMajor = (LinearLayout)findViewById(R.id.enterMajor);
        LinearLayout enterGrade = (LinearLayout)findViewById(R.id.enterGrade);
        LinearLayout enterEducationInfo = (LinearLayout)findViewById(R.id.enterEducationInfo);

        selectedEducationLevel = (TextView) findViewById(R.id.selectedEducationLevel);
        enteredSchool = (TextView) findViewById(R.id.enteredSchool);
        enteredMajor = (TextView) findViewById(R.id.enteredMajor);
        selectedEducationField = (TextView) findViewById(R.id.selectedEducationField);
        enteredGrade = (TextView) findViewById(R.id.enteredGrade);
        selectedGraduationYear = (TextView) findViewById(R.id.selectedGraduationYear);
        enteredEducationInfo = (TextView) findViewById(R.id.enteredEducationInfo);

        //final EditText school = (EditText)findViewById(R.id.school_text);
        //final EditText major = (EditText)findViewById(R.id.edu_major_text);
        //final EditText grade = (EditText)findViewById(R.id.grade_text);
        //final EditText info = (EditText)findViewById(R.id.extra_info);

        // extras send for update, maybe only ID passed, then read form DB to get other values
        Bundle extras = getIntent().getExtras();
        if( extras != null ){
            currentEducationId = extras.getInt("id");
            currentViewPosition = extras.getInt("currentViewPosition");

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
                String theEducationLevel = (String) listOfEducationLevel.get( ce.getInt(4) );
                String theEducationField = (String) listOfEducationField.get( ce.getInt(5) );
                //Log.e( "theEducationLevel", ""+theEducationLevel );

                educationLevel = new EducationLevel(ce.getInt(4), theEducationLevel );
                educationField = new EducationField(ce.getInt(5), theEducationField );
                year = ce.getString(9).substring(0,4);

                selectedEducationLevel.setText(theEducationLevel);
                selectedEducationField.setText(theEducationField);
                selectedGraduationYear.setText(year);
                enteredSchool.setText(ce.getString(2));
                enteredMajor.setText(ce.getString(3));
                enteredGrade.setText(ce.getString(7));

                if( !ce.getString(8).equals("null") ){
                    enteredEducationInfo.setText(Html.fromHtml(ce.getString(8)) );
                }

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

        enterSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), UpdateName.class);
                String school = "";
                if( !enteredSchool.getText().toString().equals(getResources().getString(R.string.no_value)) ){
                    school = enteredSchool.getText().toString();
                }
                intent.putExtra("the_text", school);
                startActivityForResult(intent, ENTER_SCHOOL);
            }
        });

        enterMajor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), UpdateName.class);
                String major = "";
                if( !enteredMajor.getText().toString().equals(getResources().getString(R.string.no_value)) ){
                    major = enteredMajor.getText().toString();
                }
                intent.putExtra("the_text", major);
                startActivityForResult(intent, ENTER_MAJOR);
            }
        });

        enterGrade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), UpdateName.class);
                String grade = "";
                if( !enteredGrade.getText().toString().equals(getResources().getString(R.string.no_value)) ){
                    grade = enteredGrade.getText().toString();
                }
                intent.putExtra("the_text", grade);
                startActivityForResult(intent, ENTER_GRADE);
            }
        });

        enterEducationInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), UpdateName.class);
                String info = "";
                if( !enteredEducationInfo.getText().toString().equals(getResources().getString(R.string.no_value)) ){
                    info = enteredEducationInfo.getText().toString();
                }
                intent.putExtra("the_text", info);
                intent.putExtra("multiline", true);
                startActivityForResult(intent, ENTER_EDUCATION_INFO);
            }
        });

        // save button
        Button saveEducation = (Button) findViewById(R.id.okButton);
        saveEducation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: validate input
                ArrayList<String> errors = new ArrayList<>();

                if( selectedEducationLevel.getText().toString().equals(getResources().getString(R.string.no_value)) ){
                    errors.add("Please select education level.");
                }

                if( enteredSchool.getText().toString().equals(getResources().getString(R.string.no_value)) ){
                    errors.add("Please enter your educational institution.");
                }

                if( selectedEducationField.getText().toString().equals(getResources().getString(R.string.no_value)) ){
                    errors.add("Please select field of education.");
                }

                if( selectedGraduationYear.getText().toString().equals(getResources().getString(R.string.no_value)) ){
                    errors.add("Please select year of graduation.");
                }

                if( errors.size() == 0 ){
                    // TODO: save to local db
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
                    cv.put("school", enteredSchool.getText().toString());
                    cv.put("major", enteredMajor.getText().toString());
                    cv.put("edu_field_id", educationField.id);
                    cv.put("grade", enteredGrade.getText().toString());
                    cv.put("date_graduated", year+"-01-01");
                    cv.put("info", enteredEducationInfo.getText().toString());
                    cv.put("_id", remoteEducationId); // JenJOBS id

                    if( currentEducationId > 0 ){
                        tableEducation.updateEducation(cv, currentEducationId);
                    }else{
                        Long newEducationId = tableEducation.addEducation(cv);
                        currentEducationId = newEducationId.intValue();
                    }

                    JSONObject obj = new JSONObject();
                    String url = Jenjobs.EDUCATION_URL;
                    if( remoteEducationId > 0 ){ url += "/"+ remoteEducationId; }
                    url += "?access-token=" + accessToken;

                    try {
                        /*
                        edu_level_id
                        edu_field_id
                        --country_id
                        school
                        major
                        --edu_field_desc
                        grade
                        date_graduated
                        info
                        */
                        obj.put("edu_level_id",educationLevel.id);
                        obj.put("edu_field_id",educationField.id);
                        obj.put("country_id",127);
                        obj.put("school",enteredSchool.getText().toString());
                        obj.put("major",enteredMajor.getText().toString());
                        obj.put("edu_field_desc",""); // TODO: enter this when edu_field_id is "Others"
                        obj.put("grade",enteredGrade.getText().toString());
                        obj.put("date_graduated",year);
                        obj.put("info",enteredEducationInfo.getText().toString());

                        // TODO: send POST request
                        Log.e("obj", obj.toString());
                        String[] s = {url, obj.toString()};
                        new PostEducation().execute(s);

                    } catch (JSONException e) {
                        Log.e("objErr", e.getMessage());
                    }

                    Intent intent = new Intent();
                    intent.putExtra("id", currentEducationId);
                    if( currentViewPosition > 0 ){
                        intent.putExtra("currentViewPosition", currentViewPosition);
                    }
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), TextUtils.join(". ", errors), Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button cancelButton = (Button)findViewById(R.id.cancelButton);
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
        }else if( requestCode == ENTER_SCHOOL ){
            if (resultCode == RESULT_OK) {
                Bundle extra = data.getExtras();
                String school = (String)extra.get("the_text");
                enteredSchool.setText(school);
            }
        }else if( requestCode == ENTER_MAJOR ){
            if (resultCode == RESULT_OK) {
                Bundle extra = data.getExtras();
                String major = (String)extra.get("the_text");
                enteredMajor.setText(major);
            }
        }else if( requestCode == ENTER_GRADE ){
            if (resultCode == RESULT_OK) {
                Bundle extra = data.getExtras();
                String grade = (String)extra.get("the_text");
                enteredGrade.setText(grade);
            }
        }else if( requestCode == ENTER_EDUCATION_INFO ){
            if (resultCode == RESULT_OK) {
                Bundle extra = data.getExtras();
                String info = (String)extra.get("the_text");
                enteredEducationInfo.setText(info);
            }
        }
    }

    public class PostEducation extends AsyncTask<String, Void, JSONObject> {
        private View v;
        private int viewType;

        public PostEducation(){}

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject _response = null;

            final HttpClient httpclient = new DefaultHttpClient();
            final HttpPost httppost = new HttpPost( params[0] );

            httppost.addHeader("Content-Type", "application/json");
            httppost.addHeader("Accept", "application/json");
            HttpResponse _http_response = null;

            try {
                StringEntity entity = new StringEntity(params[1]);
                entity.setContentEncoding(HTTP.UTF_8);
                entity.setContentType("application/json");
                httppost.setEntity(entity);

                _http_response = httpclient.execute(httppost);
                HttpEntity _entity = _http_response.getEntity();
                InputStream is = _entity.getContent();
                String responseString = JenHttpRequest.readInputStreamAsString(is);
                _response = JenHttpRequest.decodeJsonObjectString(responseString);
            } catch (ClientProtocolException e) {
                Log.e("ClientProtocolException", e.getMessage());
            } catch (UnsupportedEncodingException e) {
                Log.e("UnsupportedEncoding", e.getMessage());
            } catch (IOException e) {
                Log.e("IOException", e.getMessage());
            }

            return _response;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if( result.optInt("id") > 0 ){
                ContentValues cv = new ContentValues();
                cv.put("_id", result.optInt("id"));
                tableEducation.updateEducation(cv, currentEducationId);
            }
        }

    }
}
