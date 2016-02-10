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
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class UpdateJobSeeking extends ActionBarActivity {

    public static final int SELECT_JOB_SEEKING_STATUS = 1;
    public static final int SELECT_COUNTRY = 2;
    public static final int SELECT_STATE = 3;
    public static final int SELECT_JOB_NOTICE = 4;

    SharedPreferences sharedPref;
    TableProfile tableProfile;
    TableAddress tableAddress;
    int profileId;

    private TextView selectedMalaysiaState;
    private TextView selectedCountry;
    private TextView selectedJobSeekingStatus;
    private TextView selectedJobNotice;
    private LinearLayout selectMalaysiaState;
    private View selectMalaysiaStateSibling;
    CheckBox cbLicense;
    CheckBox cbTransport;

    private State selectedMalaysiaStateValues = null;
    private Country selectedCountryValues = null;
    private JobSeekingStatus selectedJobSeekingStatusValues = null;
    private String selectedAvailability = "";
    private String selectedAvailabilityUnit = "";

    boolean isOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_job_seeking);
        setTitle(getText(R.string.update_job_seeking));

        sharedPref = this.getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        profileId = sharedPref.getInt("js_profile_id", 0);
        isOnline = Jenjobs.isOnline(getApplicationContext());

        selectedMalaysiaState = (TextView)findViewById(R.id.selectedMalaysiaState);
        selectedCountry = (TextView)findViewById(R.id.selectedCountry);
        selectedJobSeekingStatus = (TextView)findViewById(R.id.selectedJobSeekingStatus);
        selectedJobNotice = (TextView)findViewById(R.id.selectedJobNotice);
        TextView licenseLabel = (TextView) findViewById(R.id.licenseLabel);
        TextView ownTransportLabel = (TextView) findViewById(R.id.ownTransportLabel);
        cbLicense = (CheckBox)findViewById(R.id.license);
        cbTransport = (CheckBox)findViewById(R.id.own_transport);

        LinearLayout selectJobSeekingStatus = (LinearLayout)findViewById(R.id.selectJobSeekingStatus);
        LinearLayout selectCountry = (LinearLayout)findViewById(R.id.selectCountry);
        selectMalaysiaState = (LinearLayout)findViewById(R.id.selectMalaysiaState);
        selectMalaysiaStateSibling = findViewById(R.id.selectMalaysiaStateSibling);
        LinearLayout selectJobNotice = (LinearLayout)findViewById(R.id.selectJobNotice);

        // TODO - read default values from db
        tableProfile = new TableProfile(getApplicationContext());
        tableAddress = new TableAddress(getApplicationContext());
        Profile profile = tableProfile.getProfile();

        Cursor c = tableAddress.getAddress();
        if( c.moveToFirst() ){
            int _country_id = c.getInt(8);
            String _country_name = c.getString(11);
            int _state_id = c.getInt(6);
            String _state_name = c.getString(7);

            //Log.e("country_id", ""+_country_id);
            //Log.e("country_name", _country_name);

            if( _country_id > 0 && _country_name.length() > 0 ){
                selectedCountry.setText(_country_name);
                selectedCountryValues = new Country(_country_id, _country_name);
            }

            if( _state_id > 0 && _state_name.length() > 0 ){
                selectedMalaysiaStateValues = new State( _state_id, _state_name );
                selectedMalaysiaState.setText(_state_name);
            }
        }
        c.close();

        if( profile.js_jobseek_status_id > 0 ) {
            HashMap jobseekStatus = Jenjobs.getJobSeekingStatus();
            String _jssValue = (String) jobseekStatus.get(profile.js_jobseek_status_id);
            selectedJobSeekingStatusValues = new JobSeekingStatus(profile.js_jobseek_status_id, _jssValue);
            selectedJobSeekingStatus.setText( _jssValue );
        }

        if( profile.availability > 0 && profile.availability_unit.length() > 0 ){
            selectedAvailability = String.valueOf(profile.availability);

            String[] _av = getResources().getStringArray(R.array.availability_unit);
            for (String a_av : _av) {
                if (a_av.substring(0, 1).equals(profile.availability_unit)) {
                    selectedAvailabilityUnit = a_av;
                }
            }

            String a = selectedAvailability+" "+selectedAvailabilityUnit;
            selectedJobNotice.setText( a );
        }

        if( profile.driving_license ){
            cbLicense.setChecked(true);
        }

        if( profile.transport ){
            cbTransport.setChecked(true);
        }

        licenseLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbLicense.setChecked(!cbLicense.isChecked());
            }
        });

        ownTransportLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbTransport.setChecked(!cbTransport.isChecked());
            }
        });

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
                Intent intent = new Intent(getApplicationContext(), UpdateNoticePeriod.class);
                // TODO - set saved period
                startActivityForResult(intent, SELECT_JOB_NOTICE);
            }
        });

        Button update = (Button) findViewById(R.id.save_jobseeking_information);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        Button cancelButton = (Button)findViewById(R.id.cancel);
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
                if( selectedJobSeekingStatusValues == null ){
                    errors.add("Please select jobseeking status!");
                }

                if( selectedAvailability.length() == 0 && selectedAvailabilityUnit.length() == 0 ){
                    errors.add("Please select your notice period!");
                }

                if( selectedCountryValues == null ){
                    errors.add("Please select the country!");
                }else{
                    if( selectedCountryValues.id == 127 ){
                        if( selectedMalaysiaStateValues == null ){
                            errors.add("Please select the state!");
                        }
                    }
                }

                if( errors.size() == 0 ) {
                    JSONObject obj = new JSONObject();

                    // TODO - save to profile, local db
                    ContentValues cv = new ContentValues();
                    cv.put("js_jobseek_status_id", selectedJobSeekingStatusValues.id);
                    cv.put("driving_license", cbLicense.isChecked() ? 1 : 0);
                    cv.put("transport", cbTransport.isChecked() ? 1 : 0);
                    cv.put("availability", selectedAvailability);
                    cv.put("availability_unit", selectedAvailabilityUnit.substring(0, 1));
                    tableProfile.updateProfile(cv, profileId);

                    // TODO - save to address, add address to parameter
                    ContentValues cv2 = new ContentValues();
                    if(selectedMalaysiaStateValues != null){
                        cv2.put("state_id", selectedMalaysiaStateValues.id);
                        cv2.put("state_name", selectedMalaysiaStateValues.name);
                    }else{
                        cv2.put("state_id", 0);
                        cv2.put("state_name", "");
                    }
                    cv2.put("country_id", selectedCountryValues.id);
                    cv2.put("updated_at", Jenjobs.date(null, "yyyy-MM-dd", null));
                    tableAddress.updateAddress(cv2);

                    // update POST data
                    try {
                        obj.put("js_jobseek_status_id", cv.getAsString("js_jobseek_status_id"));
                        obj.put("driving_license", cv.getAsString("driving_license"));
                        obj.put("transport", cv.getAsString("transport"));
                        obj.put("availability", cv.getAsString("availability"));
                        obj.put("availability_unit", cv.getAsString("availability_unit"));

                        obj.put("state_id", cv.getAsString("state_id"));
                        obj.put("country_id", cv.getAsString("country_id"));
                    } catch (JSONException e) {
                        Log.e("err", e.getMessage());
                    }

                    // TODO - change cv to ArrayList -> String, post to server
                    String accessToken = sharedPref.getString("access_token", null);
                    PostRequest p = new PostRequest();
                    p.setResultListener(new PostRequest.ResultListener() {
                        @Override
                        public void processResult(JSONObject success) {
                            if( success != null ){
                                try {
                                    success.get("status_code");
                                } catch (JSONException e) {
                                    Log.e("test", e.getMessage());
                                }
                            }
                        }
                    });

                    String[] s = {Jenjobs.JOBSEEKING_INFO+"?access-token="+accessToken,obj.toString()};
                    p.execute(s);

                    Intent intent = new Intent();
                    intent.putExtra("summary", selectedJobSeekingStatusValues.name);
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
                Country selectedValues = (Country) filters.get("country");

                if( selectedValues != null ){
                    selectedCountryValues = selectedValues;
                    selectedCountry.setText(selectedValues.name);

                    if( selectedValues.id == 127 ){
                        selectMalaysiaState.setVisibility(View.VISIBLE);
                        selectMalaysiaStateSibling.setVisibility(View.VISIBLE);
                    }else{
                        selectMalaysiaState.setVisibility(View.GONE);
                        selectMalaysiaStateSibling.setVisibility(View.GONE);
                    }
                }
            }
        }else if( requestCode == SELECT_STATE ){
            if (resultCode == RESULT_OK) {
                Bundle filters = data.getExtras();
                State c = (State) filters.get("state");
                selectedMalaysiaStateValues = c;
                if( c != null ){
                    selectedMalaysiaState.setText(c.name);
                }
            }
        }else if( requestCode == SELECT_JOB_NOTICE ){
            if (resultCode == RESULT_OK) {
                Bundle filters = data.getExtras();
                selectedAvailability = filters.getString("availability");
                selectedAvailabilityUnit = filters.getString("availabilityUnit");
                String a = selectedAvailability+" "+selectedAvailabilityUnit;
                selectedJobNotice.setText( a );
            }
        }
    }
}
