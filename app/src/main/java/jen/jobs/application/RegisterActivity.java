package jen.jobs.application;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class RegisterActivity extends ActionBarActivity {

    Button registerButton;
    SharedPreferences sharedPref;
    String enteredEmail;
    boolean isOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        setTitle("New Registration");
        if( getSupportActionBar() != null ){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        sharedPref = this.getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        isOnline = Jenjobs.isOnline(getApplicationContext());
        registerButton = (Button)findViewById(R.id.registerButton);

        final EditText fullName = (EditText)findViewById(R.id.fullname);
        final EditText emailAddress = (EditText)findViewById(R.id.email);
        final EditText password = (EditText)findViewById(R.id.password);
        final EditText repeatPassword = (EditText)findViewById(R.id.passwordRepeat);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( isOnline ){
                    String _fullName = fullName.getText().toString();
                    String _emailAddress = emailAddress.getText().toString();
                    String _password = password.getText().toString();
                    String _repeatPassword = repeatPassword.getText().toString();

                    ArrayList<String> errors = new ArrayList<>();

                    if( _fullName.length() == 0 || _emailAddress.length() == 0 || _password.length() == 0 || _repeatPassword.length() == 0 ){
                        errors.add("Please complete the form!");
                    }

                    if( _password.length() < 6 ){
                        errors.add("Minimum password length is 6 characters!");
                    }

                    if( !_password.equals(_repeatPassword) ){
                        errors.add("Password and Password Repeat do not match!");
                    }

                    if( errors.size() == 0 ){
                        registerButton.setEnabled(false);
                        registerButton.setClickable(false);

                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("name", _fullName);
                            obj.put("email", _emailAddress);
                            obj.put("password", _password);
                            obj.put("repeat_password", _repeatPassword);

                            enteredEmail = _emailAddress;

                            String[] params = {Jenjobs.REGISTRATION_URL,obj.toString()};
                            //new SubmitRegistration().execute(params);

                            PostRequest p = new PostRequest();
                            p.setResultListener(new PostRequest.ResultListener() {
                                @Override
                                public void processResult(JSONObject result) {
                                    if( result != null ){
                                        try {
                                            if( result.getInt("status_code") == 1 ){
                                                // success
                                                Toast.makeText(getApplicationContext(), result.getString("status_text"), Toast.LENGTH_LONG).show();

                                                // successfull registration return access token, so save this token
                                            /*
                                            {
                                                "status_code": 1,
                                                "status_text": "Registration successful!",
                                                "token": {
                                                    "access_token": "30b309f726d7b6485f4674e6b97a94efb624c32f",
                                                    "expires_in": 86400,
                                                    "token_type": "Bearer",
                                                    "scope": null,
                                                    "refresh_token": "7d3ad377dab650c688f891d9ed1d400a3bbfe3f9"
                                                }
                                            }
                                            */
                                                // and start ProfileActivity and finish();

                                                String token = result.getString("token");
                                                if( token != null ){
                                                    JSONObject success = new JSONObject(token);
                                                    String accessToken = success.optString("access_token");
                                                    if( accessToken != null ){
                                                        SharedPreferences.Editor spEdit = sharedPref.edit();
                                                        spEdit.putString("access_token", accessToken);
                                                        spEdit.putString("email", enteredEmail);
                                                        spEdit.apply();

                                                        Toast.makeText(getApplicationContext(), "Registration success!", Toast.LENGTH_LONG).show();

                                                        Intent intent2 = new Intent();
                                                        intent2.putExtra("downloadData", true);
                                                        intent2.setClass(getApplicationContext(), ProfileActivity.class);
                                                        startActivity(intent2);
                                                        finish();
                                                    }
                                                }else{
                                                    Toast.makeText(getApplicationContext(), "Registration success, but empty access token returned. Please try to login.", Toast.LENGTH_LONG).show();
                                                }
                                            }else{
                                                // failed
                                                JSONObject _statusText = new JSONObject(result.getString("error"));
                                                ArrayList<String> _errors = new ArrayList<>();

                                                Iterator<String> k = _statusText.keys();
                                                while( k.hasNext() ){
                                                    String key = k.next();
                                                    JSONArray _e = new JSONArray(_statusText.getString(key));
                                                    if( _e.length() > 0 ){
                                                        _errors.add(_e.getString(0));
                                                    }
                                                }

                                                Toast.makeText(getApplicationContext(), TextUtils.join(", ", _errors), Toast.LENGTH_LONG).show();

                                                registerButton.setEnabled(true);
                                                registerButton.setClickable(true);
                                            }
                                        } catch (JSONException e) {
                                            Log.e("err", e.getMessage());
                                            registerButton.setEnabled(true);
                                            registerButton.setClickable(true);
                                        }
                                    }
                                }
                            });
                            p.execute(params);
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), TextUtils.join(", ", errors), Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
