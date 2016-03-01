package com.example.william.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
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

public class MainActivity extends Activity {

    SharedPreferences sharedPref;
    public static String JENJOBS_SHARED_PREFERENCE = "jenjobs";

    private EditText emailView;
    private EditText passwordView;
    Button loginButton;
    Button registerButton;
    Button forgotButton;
    Button jobSearch;

    boolean isOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isOnline = Jenjobs.isOnline(getApplicationContext());
        setup();
    }

    private void setup() {
        // initialize table
        new TableProfile(this);

        sharedPref = this.getSharedPreferences(JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);

        emailView = (EditText) findViewById(R.id.username_input);
        passwordView = (EditText) findViewById(R.id.password_input);

        loginButton = (Button) findViewById(R.id.login_button);
        registerButton = (Button) findViewById(R.id.register_button);
        forgotButton = (Button) findViewById(R.id.forgot_button);
        jobSearch = (Button) findViewById(R.id.job_search);

        // handle login button click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailView.getText().toString();
                String password = passwordView.getText().toString();

                if( isOnline ){
                    ArrayList<String> errors = new ArrayList<>();
                    if( email.length() == 0 ){
                        errors.add("Username is required!");
                    }

                    if( password.length() == 0 ){
                        errors.add("Password is required!");
                    }

                    if( errors.size() == 0 ){
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("username", email);
                            obj.put("password", password);
                            obj.put("grant_type", "password");
                            obj.put("client_id", "testclient");
                            obj.put("client_secret", "testpass");

                            PostRequest p = new PostRequest();
                            p.setResultListener(new PostRequest.ResultListener() {
                                @Override
                                public void processResult(JSONObject success) {
                                    toggleButtonState(true);

                                    if (success != null) {
                                        try {
                                            if( success.getString("access_token") != null ){
                                                SharedPreferences.Editor spEdit = sharedPref.edit();
                                                spEdit.putString("access_token", success.getString("access_token"));
                                                spEdit.putString("email", email);
                                                spEdit.apply();

                                                Toast.makeText(getApplicationContext(), "Login success!", Toast.LENGTH_LONG).show();

                                                Intent intent2 = new Intent();
                                                intent2.putExtra("downloadData", true);
                                                intent2.setClass(getApplicationContext(), ProfileActivity.class);
                                                startActivity(intent2);
                                                finish();
                                            }else{
                                                if( success.getString("error") != null ){
                                                    Toast.makeText(getApplicationContext(), success.getString("error"), Toast.LENGTH_LONG).show();
                                                }else{
                                                    Toast.makeText(getApplicationContext(), R.string.unknown_error, Toast.LENGTH_LONG).show();
                                                }

                                                toggleButtonState(true);
                                            }
                                        } catch (JSONException e) {
                                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }else{
                                        Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            String[] s = {Jenjobs.AUTH_URL,obj.toString()};
                            p.execute(s);
                        } catch (JSONException e) {
                            Log.e("JSONException", e.getMessage());
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        toggleButtonState(false);
                    }else{
                        Toast.makeText(getApplicationContext(), TextUtils.join(".", errors), Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();
                }
            }
        });

        // handle register button click
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        // handle forgot password button click
        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        jobSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), JobSearchMain.class);
                startActivity(intent);
            }
        });
    }

    public void toggleButtonState( boolean state ){
        loginButton.setEnabled(state);
        loginButton.setClickable(state);
        registerButton.setEnabled(state);
        registerButton.setClickable(state);
        jobSearch.setEnabled(state);
        jobSearch.setClickable(state);
        forgotButton.setEnabled(state);
        forgotButton.setClickable(state);
    }

    // onPause event
    @Override
    public void onPause() {
        super.onPause();
    }
}
