package com.example.william.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class ForgotPasswordActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
        setTitle("Forgot Password?");
        if( getSupportActionBar() != null ){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        final EditText emailView = (EditText)findViewById(R.id.forgot_email_address);

        Button forgotButton = (Button) findViewById(R.id.request_password_reset_button);
        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAddress = emailView.getText().toString();

                String[] params = {Jenjobs.FORGOT_PASSWORD_URL,emailAddress};
                new ForgotPasswordTask().execute(params);
            }
        });
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
            }

            return _response;
        }

        @Override
        protected void onPostExecute(final JSONObject result) {
            if( result != null ){

            }
        }
    }
}
