package com.example.william.myapplication;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class UpdateProfile extends ActionBarActivity{

    private static int SELECT_NATIONALITY = 1;
    private static int SELECT_NAME = 2;
    private static int SELECT_GENDER = 3;
    private static int SELECT_EMAIL = 4;
    private static int SELECT_IDENTITY_CARD_NO = 5;

    SharedPreferences sharedPref;

    private Country selectedNationalityValues;
    private String selectedDialCodeValue;
    private TextView nationality;
    private TextView selectedGender;
    private static TextView selectedDoB;
    private TextView fullName;
    private TextView identityCardNumber;
    private TextView emailAddress;
    EditText mobileNumber;

    String accessToken;
    int profileId;
    boolean isOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_update_layout);
        setTitle(getText(R.string.my_profile));

        if( getSupportActionBar() != null ){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        sharedPref = this.getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        accessToken = sharedPref.getString("access_token", null);
        profileId = sharedPref.getInt("js_profile_id", 0);
        isOnline = Jenjobs.isOnline(getApplicationContext());

        // list of labels
        //final TextView labelName = (TextView)findViewById(R.id.labelName);
        //final TextView labelNationality = (TextView)findViewById(R.id.labelNationality);
        //final TextView labelDob = (TextView)findViewById(R.id.labelDob);
        //final TextView labelGender = (TextView)findViewById(R.id.labelGender);
        //final TextView labelMobileNo = (TextView)findViewById(R.id.labelMobileNo);

        // load db
        TableProfile tProfile = new TableProfile(this);
        Profile theProfile = tProfile.getProfile();

        LinearLayout selectFullName = (LinearLayout)findViewById(R.id.selectFullName);
        fullName = (TextView)findViewById(R.id.fullname_profile);

        LinearLayout selectIdentityCardNumber = (LinearLayout)findViewById(R.id.selectIdentityCardNumber);
        identityCardNumber = (TextView)findViewById(R.id.identityCardNumber);

        LinearLayout selectEmailAddress = (LinearLayout)findViewById(R.id.selectSecondaryEmail);
        emailAddress = (TextView)findViewById(R.id.email);

        LinearLayout selectNationality = (LinearLayout)findViewById(R.id.selectNationality);
        nationality = (TextView)findViewById(R.id.selectedNationality);

        final LinearLayout selectDoB = (LinearLayout)findViewById(R.id.selectDateOfBirth);
        selectedDoB = (TextView)findViewById(R.id.selectedDateOfBirth);

        LinearLayout selectGender = (LinearLayout)findViewById(R.id.selectGender);
        selectedGender = (TextView)findViewById(R.id.selectedGender);

        final Spinner dialCode = (Spinner)findViewById(R.id.dial_code);
        mobileNumber = (EditText)findViewById(R.id.mobile_no);
        final DialCodeAdapter dca = new DialCodeAdapter(this);
        dialCode.setAdapter(dca);

        // populate input
        fullName.setText(theProfile.name);
        emailAddress.setText(theProfile.email);
        selectedGender.setText(theProfile.gender);
        identityCardNumber.setText(theProfile.ic);

        if( theProfile.country_id > 0 ){
            TableCountry tCountry = new TableCountry(this);
            Country theCountry = tCountry.findCountryById(theProfile.country_id);
            nationality.setText(theCountry.name);
            selectedNationalityValues = theCountry;
        }

        if( !theProfile.dob.equals("") && !theProfile.dob.equals("null") ){
            selectedDoB.setText(Jenjobs.date(theProfile.dob, null, "yyyy-MM-dd"));
        }

        if( theProfile.dial_code != null && theProfile.dial_code.length() > 0 ){
            dialCode.setSelection(dca.findDialCodePosition(theProfile.dial_code));
            selectedDialCodeValue = theProfile.dial_code;
            mobileNumber.setText( theProfile.mobile_no );
        }else{
            if( theProfile.mobile_no != null && !theProfile.mobile_no.equals("null") ){
                // get dial code
                String[] parts = theProfile.mobile_no.split("\\)");
                if( parts.length == 2 ){
                    String dialCodePart = parts[0].replaceAll("[^0-9]", "");
                    dialCode.setSelection(dca.findDialCodePosition(dialCodePart));
                    mobileNumber.setText(parts[1]);
                }else{
                    mobileNumber.setText( parts[0] );
                }
            }else{
                dialCode.setSelection( dca.findDialCodePosition( "60" ) );
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
                intent.putExtra("the_title", getText(R.string.fullname));
                startActivityForResult(intent, SELECT_NAME);
            }
        });

        selectIdentityCardNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UpdateName.class);
                intent.putExtra("the_text", identityCardNumber.getText());
                intent.putExtra("the_title", getText(R.string.identityCardNumber));
                startActivityForResult(intent, SELECT_IDENTITY_CARD_NO);
            }
        });

        selectEmailAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UpdateName.class);
                intent.putExtra("the_text", emailAddress.getText());
                intent.putExtra("the_title", getText(R.string.email));
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
                dpFragment.show(getSupportFragmentManager(), "datepickerdob" );
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
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
                String theFullName = fullName.getText().toString();
                String theDateOfBirth = selectedDoB.getText().toString();
                String theEmailAddress = emailAddress.getText().toString();
                String theGender = selectedGender.getText().toString();
                String theMobileNumber = mobileNumber.getText().toString();
                //String theIdentityCardNumber = identityCardNumber.getText().toString();

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
                        cv.put("dial_code", selectedDialCodeValue);
                        cv.put("mobile_no", theMobileNumber);
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

                        String[] s = {Jenjobs.PROFILE_URL+"?access-token=" + accessToken, obj.toString()};
                        PostRequest postRequest = new PostRequest();
                        postRequest.setResultListener(new PostRequest.ResultListener() {
                            @Override
                            public void processResult(JSONObject result) {
                                Log.e("profileUpdated", ""+result);
                            }
                        });
                        postRequest.execute(s);
                    } catch (JSONException e) {
                        Log.e("jsonExc", e.getMessage());
                    }

                    Intent intent = new Intent();
                    intent.putExtra("saved", "ok");
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), TextUtils.join(" ", errors), Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();
            }
        }else if( clickedItem == android.R.id.home ){
            finish();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( requestCode == SELECT_NATIONALITY ) {
            if (resultCode == RESULT_OK) {
                if( data != null ){
                    Bundle filters = data.getExtras();
                    Country selectedValues = (Country) filters.get("country");
                    if( selectedValues != null ){
                        selectedNationalityValues = selectedValues;
                        nationality.setText(selectedValues.name);
                    }
                }
            }
        }else if( requestCode == SELECT_GENDER ){
            if (resultCode == RESULT_OK) {
                if( data != null ){
                    Bundle filters = data.getExtras();
                    String selectedValues = (String) filters.get("gender");
                    if( selectedValues != null ){
                        selectedGender.setText(selectedValues);
                    }
                }
            }
        }else if( requestCode == SELECT_NAME ){
            if (resultCode == RESULT_OK) {
                if( data != null ){
                    Bundle filters = data.getExtras();
                    String selectedValues = (String) filters.get("the_text");
                    if( selectedValues != null && !selectedValues.equals("") ){
                        fullName.setText(selectedValues);
                    }
                }
            }
        }else if( requestCode == SELECT_EMAIL ){
            if (resultCode == RESULT_OK) {
                if( data != null ){
                    Bundle filters = data.getExtras();
                    String selectedValues = (String) filters.get("the_text");
                    if( selectedValues != null && !selectedValues.equals("") ){
                        emailAddress.setText(selectedValues);
                    }
                }
            }
        }else if( requestCode == SELECT_IDENTITY_CARD_NO ){
            if (resultCode == RESULT_OK) {
                if( data != null ){
                    Bundle filters = data.getExtras();
                    String selectedValues = (String) filters.get("the_text");
                    if( selectedValues != null && !selectedValues.equals("") ){
                        identityCardNumber.setText(selectedValues);
                    }
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

        @NonNull
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
