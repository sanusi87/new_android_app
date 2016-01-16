package com.example.william.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
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

public class ForgotPasswordActivity extends ActionBarActivity {
    Button forgotButton;
    TableForgotPassword tableForgotPassword;
    TableSettings tableSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
        setTitle("Forgot Password?");
        if( getSupportActionBar() != null ){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final EditText emailView = (EditText)findViewById(R.id.forgot_email_address);

        forgotButton = (Button) findViewById(R.id.request_password_reset_button);
        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAddress = emailView.getText().toString();
                String[] params = {Jenjobs.FORGOT_PASSWORD_URL, emailAddress};
                new ForgotPasswordTask().execute(params);

                forgotButton.setEnabled(false);
                forgotButton.setClickable(false);
            }
        });

        // get max request allowed from database
        tableSettings = new TableSettings(this);
        Integer maxRequest = Integer.valueOf(tableSettings.getSetting("max_password_reset_request"));

        // count and compare request done for today
        tableForgotPassword = new TableForgotPassword(this);
        int todayTotalRequest = tableForgotPassword.countTodayRequest();
        Log.e("requestCount", ""+todayTotalRequest);

        // disable button id exceed
        if( todayTotalRequest > maxRequest ){
            forgotButton.setEnabled(false);
            forgotButton.setClickable(false);
            Toast.makeText(getApplicationContext(), "Total number of password reset request reached!", Toast.LENGTH_SHORT).show();
        }
    }


    public class ForgotPasswordTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject _response = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost( params[0] );

            httppost.addHeader("Content-Type", "application/json");
            httppost.addHeader("Accept", "application/json");

            try {
                StringEntity entity = new StringEntity(params[1]);
                entity.setContentEncoding(HTTP.UTF_8);
                entity.setContentType("application/json");
                httppost.setEntity(entity);

                HttpResponse _http_response = httpclient.execute(httppost);
                HttpEntity _entity = _http_response.getEntity();
                InputStream is = _entity.getContent();
                String responseString = JenHttpRequest.readInputStreamAsString(is);
                Log.e("respp", responseString);
                _response = JenHttpRequest.decodeJsonObjectString(responseString);
            } catch (ClientProtocolException e) {
                Log.e("ClientProtocolException", e.getMessage());
            } catch (UnsupportedEncodingException e) {
                Log.e("UnsupportedEncoding", e.getMessage());
            } catch (IOException e) {
                Log.e("IOException", e.getMessage());
                Toast.makeText(getApplicationContext(), "Network error!", Toast.LENGTH_LONG).show();
            }

            return _response;
        }

        @Override
        protected void onPostExecute(final JSONObject result) {
            if( result != null ){
                try {
                    String statusText = result.getString("status_text");
                    Toast.makeText(getApplicationContext(), statusText, Toast.LENGTH_SHORT).show();

                    // log request for counting
                    tableForgotPassword.logRequest();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Response error!", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getApplicationContext(), "No response received!", Toast.LENGTH_SHORT).show();
            }

            // enable button after 20s
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    forgotButton.setEnabled(true);
                    forgotButton.setClickable(true);
                }
            }, 20000);
        }
    }
}
