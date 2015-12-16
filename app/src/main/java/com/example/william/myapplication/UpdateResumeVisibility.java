package com.example.william.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

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

public class UpdateResumeVisibility extends Activity {

    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_resume_visibility);

        sharedPref = this.getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);

        ListView listOFResumeVisibility = (ListView) findViewById(R.id.listOfResumeVisibility);
        final ResumeVisibilityAdapter rvAdapter = new ResumeVisibilityAdapter(getApplicationContext());
        listOFResumeVisibility.setAdapter(rvAdapter);

        listOFResumeVisibility.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                /*
                * save visibility first before finishing activity
                * */
                new UpdateTask().execute(new String[]{rvAdapter.visibility[position]});


                /*
                * only then send the result back to the previous page
                * */
                Intent intent = new Intent();
                intent.putExtra("selectedvisibility", rvAdapter.visibility[position]);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    public class UpdateTask extends AsyncTask<String, Void, JSONObject> {

        UpdateTask(){}

        @Override
        protected JSONObject doInBackground(String... params) {

            Object _response = null;

            String accessToken = sharedPref.getString("access_token", null);
            String url = "http://api.jenjobs.com/jobseeker/access-level";
            url += "?access-token="+accessToken;

            final HttpClient httpclient = new DefaultHttpClient();
            final HttpPost httppost = new HttpPost( url );
            httppost.addHeader("Content-Type", "application/json");
            httppost.addHeader("Accept", "application/json");
            HttpResponse _http_response = null;

            JSONObject obj = new JSONObject();
            try {
                obj.put("access", params[0]);

                StringEntity entity = new StringEntity(obj.toString());
                entity.setContentEncoding(HTTP.UTF_8);
                entity.setContentType("application/json");
                httppost.setEntity(entity);

                _http_response = httpclient.execute(httppost);
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
                Log.e("IOException", e.getMessage());
            }

            return (JSONObject) _response;
        }

        @Override
        protected void onPostExecute(final JSONObject success) {
            if( success != null ){
                Log.e("success", success.toString());

                try {
                    int status_code = Integer.getInteger(success.get("status_code").toString());
                    Log.e("status_code", ""+status_code);
                } catch (JSONException e) {
                    Log.e("JSONException", e.getMessage());
                }
            }
        }
    }
}
