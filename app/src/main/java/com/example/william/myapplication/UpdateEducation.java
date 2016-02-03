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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    int currentViewPosition = -1;
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

        // extras send for update, maybe only ID passed, then read form DB to get other values
        Bundle extras = getIntent().getExtras();
        if( extras != null ){
            currentEducationId = extras.getInt("id");
            currentViewPosition = extras.getInt("currentViewPosition");

            Cursor ce = tableEducation.getEducationById(currentEducationId);
            if( ce.moveToFirst() ){
                remoteEducationId = ce.getInt(1);

                String theEducationLevel = (String) listOfEducationLevel.get( ce.getInt(4) );
                String theEducationField = (String) listOfEducationField.get( ce.getInt(5) );

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
            setTitle(R.string.update_education);
        }else{
            setTitle(R.string.add_education);
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
                intent.putExtra("the_title", getText(R.string.school));
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
                intent.putExtra("the_title", getText(R.string.education_major));
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
                intent.putExtra("the_title", getText(R.string.grade));
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
                intent.putExtra("the_title", getText(R.string.education_info));
                startActivityForResult(intent, ENTER_EDUCATION_INFO);
            }
        });

        // save button
        Button saveEducation = (Button) findViewById(R.id.okButton);
        saveEducation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int clickedItem = item.getItemId();
        if (clickedItem == R.id.save) {
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
                    cv.put("date_updated", Jenjobs.date(null,"yyyy-MM-dd hh:mm:ss",null));
                    tableEducation.updateEducation(cv, currentEducationId);
                }else{
                    cv.put("date_added", Jenjobs.date(null,"yyyy-MM-dd hh:mm:ss",null));
                    Long newEducationId = tableEducation.addEducation(cv);
                    currentEducationId = newEducationId.intValue();
                }

                JSONObject obj = new JSONObject();
                String url = Jenjobs.EDUCATION_URL;
                if( remoteEducationId > 0 ){ url += "/"+ remoteEducationId; }
                url += "?access-token=" + accessToken;

                try {
                    obj.put("edu_level_id",educationLevel.id);
                    obj.put("edu_field_id",educationField.id);
                    obj.put("country_id",127);
                    obj.put("school",enteredSchool.getText().toString());
                    obj.put("major",enteredMajor.getText().toString());
                    obj.put("edu_field_desc",""); // TODO: enter this when edu_field_id is "Others"
                    obj.put("grade",enteredGrade.getText().toString());
                    obj.put("date_graduated",year);
                    obj.put("info", enteredEducationInfo.getText().toString());

                    String[] s = {url, obj.toString()};
                    PostRequest p = new PostRequest();
                    p.setResultListener(new PostRequest.ResultListener() {
                        @Override
                        public void processResult(JSONObject result) {
                            if( result != null ) {
                                if (result.optInt("id") > 0) {
                                    // update the saved ID to local table
                                    ContentValues cv = new ContentValues();
                                    cv.put("_id", result.optInt("id"));
                                    tableEducation.updateEducation(cv, currentEducationId);
                                }
                            }
                        }
                    });
                    p.execute(s);

                } catch (JSONException e) {
                    Log.e("objErr", e.getMessage());
                }

                Intent intent = new Intent();
                intent.putExtra("id", currentEducationId);
                intent.putExtra("currentViewPosition", currentViewPosition);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }else{
                Toast.makeText(getApplicationContext(), TextUtils.join(". ", errors), Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_EDU_LEVEL) {
            if (resultCode == RESULT_OK) {
                Bundle extra = data.getExtras();
                educationLevel = (EducationLevel)extra.get("edulevel");
                if( educationLevel != null ){
                    selectedEducationLevel.setText(educationLevel.name);
                }
            }
        }else if( requestCode == SELECT_EDU_FIELD ){
            if (resultCode == RESULT_OK) {
                Bundle extra = data.getExtras();
                educationField = (EducationField)extra.get("edufield");
                if( educationField != null ){
                    selectedEducationField.setText(educationField.name);
                }
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

}
