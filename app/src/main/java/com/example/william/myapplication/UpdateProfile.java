package com.example.william.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateProfile extends FragmentActivity {

    private static int SELECT_NATIONALITY = 1;
    private static int SELECT_DOB = 2;
    private static int SELECT_GENDER = 3;

    SharedPreferences sharedPref;

    private Country selectedNationalityValues;
    private TextView nationality;
    private TextView selectedGender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_update_layout);

        sharedPref = this.getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);

        // load db
        TableProfile tProfile = new TableProfile(this);
        Profile theProfile = tProfile.getProfile();

        // load bundle if available
        //Bundle extra = getIntent().getExtras();
        //final String url = extra.getString("url");
        //final String jsonString = extra.getString("json");

        // populate input
        final EditText fullName = (EditText)findViewById(R.id.fullname_profile);
        final EditText emailAddress = (EditText)findViewById(R.id.email);
        LinearLayout selectNationality = (LinearLayout)findViewById(R.id.selectNationality);
        nationality = (TextView)findViewById(R.id.selectedNationality);
        LinearLayout selectDoB = (LinearLayout)findViewById(R.id.selectDateOfBirth);
        TextView selectedDoB = (TextView)findViewById(R.id.selectedDateOfBirth);
        LinearLayout selectGender = (LinearLayout)findViewById(R.id.selectGender);
        selectedGender = (TextView)findViewById(R.id.selectedGender);

        Spinner dialCode = (Spinner)findViewById(R.id.dial_code);
        final EditText mobileNumber = (EditText)findViewById(R.id.mobile_no);
        final DialCodeAdapter dca = new DialCodeAdapter(this);
        dialCode.setAdapter(dca);
        dialCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DialCode dcc = dca.getItem(position);
                Log.e("selectedItem", "" + dcc);
                Log.e("selectedItem", "" + dcc.name);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        selectNationality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectCountry.class);
                intent.putExtra("single", true);
                startActivityForResult(intent, SELECT_NATIONALITY);
            }
        });

        selectGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectGender.class);
                startActivityForResult(intent, SELECT_GENDER);
            }
        });

        selectDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DialogFragment dpFragment = new DatePickerFragment();
                //dpFragment.show(getFragmentManager(), "dp");
            }
        });

        // bind ok and cancel
        Button okButton = (Button)findViewById(R.id.okButton);
        Button cancelButton = (Button)findViewById(R.id.cancelButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accessToken = sharedPref.getString("access_token", null);
                String theFullName = fullName.getText().toString();
                //int theCountryId =

                // insert
                ContentValues cv = new ContentValues();
                cv.put("full_name", theFullName);
                //tSkill.addSkill(cv);

                // post
                JSONObject obj = new JSONObject();
                try {
                    obj.put("name", theFullName);
                    if( selectedNationalityValues != null ){
                        obj.put("country_id",selectedNationalityValues.id);
                    }

                    /*
                    obj.put("dob",);
                    obj.put("email",);
                    obj.put("gender",);
                    obj.put("ic_no",);
                    obj.put("mobile_no",);
                    obj.put("passport_no",);
                    */
                    // obj.put("photo_file",);

                    String[] s = {"http://api.jenjobs.com/jobseeker/profile?access-token=" + accessToken, obj.toString()};
                    new PostRequest().execute(s);
                } catch (JSONException e) {
                    Log.e("jsonExc", e.getMessage());
                }

                // finish the job
                Intent intent = new Intent();
                intent.putExtra("full_name", theFullName);
                setResult(Activity.RESULT_OK, intent);
                finish();
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

        Bundle extra = null;
        if( data != null ){
            extra = data.getExtras();
        }

        if ( requestCode == SELECT_NATIONALITY ) {
            if (resultCode == RESULT_OK) {
                Bundle filters = data.getExtras();
                Country selectedValues = (Country) filters.get("country");
                if( selectedValues != null ){
                    selectedNationalityValues = selectedValues;
                    nationality.setText(selectedValues.name);
                }
            }
        }else if( requestCode == SELECT_GENDER ){
            if (resultCode == RESULT_OK) {
                Bundle filters = data.getExtras();
                String selectedValues = (String) filters.get("gender");
                if( selectedValues != null ){
                    selectedGender.setText(selectedValues);
                }
            }
        }else if( requestCode == SELECT_DOB ){
            if (resultCode == RESULT_OK) {
                Log.e("filterdata", extra.getString("result"));
                Log.e("filterdata", extra.toString());
            }
        }
    }

}
