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

public class MainActivity extends Activity {

    SharedPreferences sharedPref;
    public static String JENJOBS_SHARED_PREFERENCE = "jenjobs";

    private EditText emailView;
    private EditText passwordView;
    Button loginButton;
    Button registerButton;
    Button forgotButton;
    Button jobSearch;
    private UserLoginTask mAuthTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                String email = emailView.getText().toString();
                String password = passwordView.getText().toString();

                mAuthTask = new UserLoginTask(email, password);
                mAuthTask.execute((Void) null);

                toggleButtonState(false);
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
                Intent intent = new Intent(getApplicationContext(), JobSearchActivity.class);
                startActivity(intent);
            }
        });

        // check for sharedPreference data
        String accessToken = sharedPref.getString("access_token", null);
        if( accessToken != null ){
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
            finish();
        }
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

    public class UserLoginTask extends AsyncTask<Void, Void, JSONObject> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            Object _response = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost( Jenjobs.AUTH_URL );
            httppost.addHeader("Content-Type", "application/json");
            httppost.addHeader("Accept", "application/json");

            JSONObject obj = new JSONObject();
            try {
                obj.put("username", mEmail);
                obj.put("password", mPassword);
                obj.put("grant_type", "password");
                obj.put("client_id", "testclient");
                obj.put("client_secret", "testpass");

                StringEntity entity = new StringEntity(obj.toString());
                entity.setContentEncoding(HTTP.UTF_8);
                entity.setContentType("application/json");
                httppost.setEntity(entity);

                HttpResponse _http_response = httpclient.execute(httppost);
                HttpEntity _entity = _http_response.getEntity();
                InputStream is = _entity.getContent();

                String responseString = JenHttpRequest.readInputStreamAsString(is);
                _response = JenHttpRequest.decodeJsonObjectString(responseString);

            } catch (JSONException e) {
                Log.e("JSONException", e.getMessage());
            } catch (ClientProtocolException e) {
                Log.e("ClientProtocolException", e.getMessage());
            } catch (UnsupportedEncodingException e) {
                Log.e("UnsupportedEncoding", e.getMessage());
            } catch (IOException e) {
                //Log.e("IOException", e.getMessage());
                Toast.makeText(getApplicationContext(), "Network error!", Toast.LENGTH_LONG).show();
                toggleButtonState(true);
            }

            return (JSONObject) _response;
        }

        @Override
        protected void onPostExecute(final JSONObject success) {
            mAuthTask = null;

            if (success != null) {
                if( success.optString("access_token") != null ){
                    SharedPreferences.Editor spEdit = sharedPref.edit();
                    spEdit.putString("access_token", success.optString("access_token"));
                    spEdit.putString("email", mEmail);
                    spEdit.apply();

                    Toast.makeText(getApplicationContext(), "Login success!", Toast.LENGTH_LONG).show();

                    Intent intent2 = new Intent();
                    intent2.putExtra("downloadData", true);
                    intent2.setClass(getApplicationContext(), ProfileActivity.class);
                    startActivity(intent2);
                    finish();
                }else{
                    if( success.optString("error") != null ){
                        Toast.makeText(getApplicationContext(), success.optString("error"), Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(), "Unknown error!", Toast.LENGTH_LONG).show();
                    }

                    toggleButtonState(true);
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}
