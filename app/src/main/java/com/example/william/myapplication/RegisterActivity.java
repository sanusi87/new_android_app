package com.example.william.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

public class RegisterActivity extends ActionBarActivity {

    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        setTitle("New Registration");
        if( getSupportActionBar() != null ){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        registerButton = (Button)findViewById(R.id.registerButton);

        final EditText fullName = (EditText)findViewById(R.id.fullname);
        final EditText emailAddress = (EditText)findViewById(R.id.email);
        final EditText password = (EditText)findViewById(R.id.password);
        final EditText repeatPassword = (EditText)findViewById(R.id.passwordRepeat);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String _fullName = fullName.getText().toString();
                String _emailAddress = emailAddress.getText().toString();
                String _password = password.getText().toString();
                String _repeatPassword = repeatPassword.getText().toString();

                ArrayList<String> errors = new ArrayList<>();

                if( _fullName.length() == 0 || _emailAddress.length() == 0 || _password.length() == 0 || _repeatPassword.length() == 0 ){
                    errors.add("Please complete the form!");
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

                        String[] params = {
                                Jenjobs.REGISTRATION_URL,
                                obj.toString()
                        };
                        new SubmitRegistration().execute(params);
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), TextUtils.join(", ", errors), Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public class SubmitRegistration extends AsyncTask<String, Void, JSONObject> {

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
        protected void onPostExecute(JSONObject result) {
            if( result != null ){
                try {
                    if( result.getInt("status_code") == 1 ){
                        // success
                        Toast.makeText(getApplicationContext(), result.getString("status_text"), Toast.LENGTH_LONG).show();
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
    }
}
