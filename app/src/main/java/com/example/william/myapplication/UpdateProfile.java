package com.example.william.myapplication;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class UpdateProfile extends FragmentActivity {

    private static int SELECT_NATIONALITY = 1;
    private static int SELECT_NAME = 2;
    private static int SELECT_GENDER = 3;
    private static int SELECT_EMAIL = 4;

    SharedPreferences sharedPref;

    private Country selectedNationalityValues;
    private String selectedDialCodeValue;
    private TextView nationality;
    private TextView selectedGender;
    private static TextView selectedDoB;
    private TextView fullName;
    private TextView emailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_update_layout);

        sharedPref = this.getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);

        // list of labels
        final TextView labelName = (TextView)findViewById(R.id.labelName);
        final TextView labelNationality = (TextView)findViewById(R.id.labelNationality);
        final TextView labelDob = (TextView)findViewById(R.id.labelDob);
        final TextView labelGender = (TextView)findViewById(R.id.labelGender);
        final TextView labelMobileNo = (TextView)findViewById(R.id.labelMobileNo);

        // load db
        TableProfile tProfile = new TableProfile(this);
        Profile theProfile = tProfile.getProfile();

        LinearLayout selectFullName = (LinearLayout)findViewById(R.id.selectFullName);
        fullName = (TextView)findViewById(R.id.fullname_profile);

        LinearLayout selectEmailAddress = (LinearLayout)findViewById(R.id.selectSecondaryEmail);
        emailAddress = (TextView)findViewById(R.id.email);

        LinearLayout selectNationality = (LinearLayout)findViewById(R.id.selectNationality);
        nationality = (TextView)findViewById(R.id.selectedNationality);

        final LinearLayout selectDoB = (LinearLayout)findViewById(R.id.selectDateOfBirth);
        selectedDoB = (TextView)findViewById(R.id.selectedDateOfBirth);

        LinearLayout selectGender = (LinearLayout)findViewById(R.id.selectGender);
        selectedGender = (TextView)findViewById(R.id.selectedGender);

        final Spinner dialCode = (Spinner)findViewById(R.id.dial_code);
        final EditText mobileNumber = (EditText)findViewById(R.id.mobile_no);
        final DialCodeAdapter dca = new DialCodeAdapter(this);
        dialCode.setAdapter(dca);

        // populate input
        fullName.setText(theProfile.name);
        emailAddress.setText(theProfile.email);
        if( theProfile.country_id > 0 ){
            TableCountry tCountry = new TableCountry(this);
            Country theCountry = tCountry.findCountryById(theProfile.country_id);
            nationality.setText(theCountry.name);
            selectedNationalityValues = theCountry;
        }

        if( !theProfile.dob.equals("") && !theProfile.dob.equals(null) ){
            selectedDoB.setText(Jenjobs.date(theProfile.dob, null, "yyyy-MM-dd"));
        }

        selectedGender.setText(theProfile.gender);

        if( theProfile.dial_code != null ){
            dialCode.setSelection(dca.findDialCodePosition(theProfile.dial_code));
            selectedDialCodeValue = theProfile.dial_code;
        }else{
            if( theProfile.mobile_no != null ){
                // get dial code
                String[] parts = theProfile.mobile_no.split("\\)");
                if( parts.length == 2 ){
                    String dialCodePart = parts[0].replaceAll("[^0-9]", "");
                    dialCode.setSelection( dca.findDialCodePosition( dialCodePart ) );
                    mobileNumber.setText( parts[1] );
                }else{
                    mobileNumber.setText( parts[0] );
                }
            }
        }

        dialCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DialCode dcc = dca.getItem(position);
                selectedDialCodeValue = dcc.code;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        selectFullName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UpdateName.class);
                intent.putExtra("the_text", fullName.getText());
                startActivityForResult(intent, SELECT_NAME);
            }
        });

        selectEmailAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UpdateName.class);
                intent.putExtra("the_text", emailAddress.getText());
                startActivityForResult(intent, SELECT_EMAIL);
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
                DialogFragment dpFragment = new DatePickerFragment();
                if( !( selectedDoB.getText().toString().equals( getResources().getString( R.string.no_value ) ) ) ){
                    String currentDate = Jenjobs.date( selectedDoB.getText().toString(), "yyyy-M-d", "dd MMM yyyy" );
                    if( currentDate != null ){
                        String[] split = currentDate.split("[\\-]");
                        Bundle b = new Bundle();
                        b.putInt("year", Integer.valueOf(split[0]));
                        b.putInt("month", Integer.valueOf(split[1]));
                        b.putInt("day", Integer.valueOf(split[2]));
                        dpFragment.setArguments(b);
                    }
                }
                dpFragment.show( getSupportFragmentManager(), "datepickerdob" );
            }
        });

        // bind ok and cancel
        Button okButton = (Button)findViewById(R.id.okButton);
        Button cancelButton = (Button)findViewById(R.id.cancelButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> errors = new ArrayList<String>();
                String accessToken = sharedPref.getString("access_token", null);
                int profileId = sharedPref.getInt("js_profile_id", 1);
                String theFullName = fullName.getText().toString();
                String theDateOfBirth = selectedDoB.getText().toString();
                String theEmailAddress = emailAddress.getText().toString();
                String theGender = selectedGender.getText().toString();
                String theMobileNumber = mobileNumber.getText().toString();

                if( theFullName.equals("") ){
                    errors.add("Please enter your full name.");
                    //labelName.setTextColor(getResources().getColor(R.color.red));
                }

                int theCountryId = 127;
                if( selectedNationalityValues == null ){
                    errors.add("Please set your nationanlity.");
                    //labelNationality.setTextColor(getResources().getColor(R.color.red));
                }else{
                    theCountryId = selectedNationalityValues.id;
                }

                if( theDateOfBirth.equals(getResources().getString(R.string.no_value)) ){
                    errors.add("Please set your date of birth.");
                    //labelDob.setTextColor(getResources().getColor(R.color.red));
                }

                if( theGender.equals(getResources().getString(R.string.no_value)) ){
                    errors.add("Please set your gender.");
                    //labelGender.setTextColor(getResources().getColor(R.color.red));
                }

                if( ( selectedDialCodeValue == null || selectedDialCodeValue.equals("") ) || theMobileNumber.equals("") ){
                    errors.add("Please set your mobile number.");
                    //labelMobileNo.setTextColor(getResources().getColor(R.color.red));
                }

                if( errors.size() == 0 ){
                    TableProfile tableProfile = new TableProfile(getApplicationContext());
                    // insert
                    ContentValues cv = new ContentValues();
                    cv.put("name", theFullName);
                    cv.put("country_id", theCountryId);
                    cv.put("dob", Jenjobs.date(theDateOfBirth, "yyyy-MM-dd", "dd MMM yyyy"));

                    if( !theEmailAddress.equals("") ){
                        cv.put("email", theEmailAddress);
                    }

                    if( !theGender.equals(getResources().getString(R.string.no_value)) ){
                        cv.put("gender", theGender);
                    }

                    if( selectedDialCodeValue != null && !selectedDialCodeValue.equals("") && !theMobileNumber.equals("") ){
                        cv.put("mobile_no", "(+"+selectedDialCodeValue+")"+theMobileNumber);
                    }

                    tableProfile.updateProfile(cv, profileId);

                    // post
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("name", theFullName);
                        obj.put("country_id",selectedNationalityValues.id);
                        obj.put("dob", Jenjobs.date(theDateOfBirth, "yyyy-MM-dd", "dd MMM yyyy"));
                        if( !theEmailAddress.equals("") ){
                            obj.put("email", theEmailAddress);
                        }
                        if( !theGender.equals(getResources().getString(R.string.no_value)) ){
                            obj.put("gender", theGender);
                        }
                        if( selectedDialCodeValue != null && !selectedDialCodeValue.equals("") && !theMobileNumber.equals("") ){
                            obj.put("dial_code", selectedDialCodeValue);
                            obj.put("mobile_no", theMobileNumber);
                        }

                        String[] s = {"http://api.jenjobs.com/jobseeker/profile?access-token=" + accessToken, obj.toString()};
                        new PostRequest().execute(s);
                    } catch (JSONException e) {
                        Log.e("jsonExc", e.getMessage());
                    }
                }else{
                    Toast.makeText(getApplicationContext(), TextUtils.join(", ", errors), Toast.LENGTH_SHORT).show();
                }

                Intent intent = new Intent();
                intent.putExtra("saved", "ok");
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
        }else if( requestCode == SELECT_NAME ){
            if (resultCode == RESULT_OK) {
                Bundle filters = data.getExtras();
                String selectedValues = (String) filters.get("the_text");
                if( selectedValues != null && selectedValues != "" ){
                    fullName.setText(selectedValues);
                }
            }
        }else if( requestCode == SELECT_EMAIL ){
            if (resultCode == RESULT_OK) {
                Bundle filters = data.getExtras();
                String selectedValues = (String) filters.get("the_text");
                if( selectedValues != null && selectedValues != "" ){
                    emailAddress.setText(selectedValues);
                }
            }
        }
    }

    // date picker
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        int year = 0;
        int month = 0;
        int day = 0;

        public DatePickerFragment(){}

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            Bundle args = getArguments();
            if( args != null ){
                year = args.getInt("year", 0);
                month = args.getInt("month", 0)-1;
                day = args.getInt("day", 0);
            }

            if( year == 0 && month == 0 && day == 0 ){
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
            }

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            monthOfYear+=1;
            selectedDoB.setText( Jenjobs.date(year+"-"+monthOfYear+"-"+dayOfMonth, null, "yyyy-M-d") );
        }
    }

}
