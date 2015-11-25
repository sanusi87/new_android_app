package com.example.william.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {

    SharedPreferences sharedPref;
    public static String JENJOBS_SHARED_PREFERENCE = "jenjobs";

    private EditText emailView;
    private EditText passwordView;
    private UserLoginTask mAuthTask = null;
    private ProgressBar mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setup();
    }

    private void setup() {
        sharedPref = this.getSharedPreferences(JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        mProgress = (ProgressBar) findViewById(R.id.progressBar);
        mProgress.setVisibility(View.INVISIBLE);

        emailView = (EditText) findViewById(R.id.username_input);
        passwordView = (EditText) findViewById(R.id.password_input);

        Button loginButton = (Button) findViewById(R.id.login_button);
        Button registerButton = (Button) findViewById(R.id.register_button);
        Button forgotButton = (Button) findViewById(R.id.forgot_button);

        // handle login button click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress.setVisibility(View.VISIBLE);
                String email = emailView.getText().toString();
                String password = passwordView.getText().toString();

                mAuthTask = new UserLoginTask(email, password);
                mAuthTask.execute((Void) null);
            }
        });

        // handle register button click
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // handle forgot password button click
        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }



    // onPause event
    @Override
    public void onPause() {
        super.onPause();

    }


    public class UserLoginTask extends AsyncTask<Void, Void, JSONObject> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            Intent intent = new Intent();
            intent.putExtra("username", mEmail);
            intent.putExtra("password", mPassword);
            intent.putExtra("grant_type", "password");
            intent.putExtra("client_id", "testclient");
            intent.putExtra("client_secret", "testpass");

            JenHttpRequest jenReq = new JenHttpRequest(JenHttpRequest.POST_REQUEST, "http://api.jenjobs.com/oauth2/token", intent);
            while( jenReq.response == null ){
                Log.e("response2", "" + jenReq.response);
                return (JSONObject)jenReq.response;
            }

            return null;
        }

        @Override
        protected void onPostExecute(final JSONObject success) {
            mAuthTask = null;

            if (success != null) {
                Log.e("finished", ""+success);
                mProgress.setVisibility(View.INVISIBLE);

                try {
                    if( success.getString("error") != null ){

                    }else if( success.getString("access_token") != null ){
                        SharedPreferences.Editor spEdit = sharedPref.edit();
                        spEdit.putString("access_token", success.getString("access_token"));
                        spEdit.commit();

                        Intent intent2 = new Intent();
                        intent2.setClass(getApplicationContext(), ProfileActivity.class);
                        startActivity(intent2);
                    }
                } catch (JSONException e) {
                    Log.e("jsonExc", e.getMessage());
                }

                finish();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}
