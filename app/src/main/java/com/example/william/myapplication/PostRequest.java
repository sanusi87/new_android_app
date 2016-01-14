package com.example.william.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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

/*
*
* new PostInput().execute(new String[]{url,json});
*
* */

public class PostRequest extends AsyncTask<String, Void, JSONObject> {

    public static int UPLOAD_RESUME_ATTACHMENT = 1;

    private Context context;
    private TextView textView;
    private int REQUEST_TYPE = 0;

    SharedPreferences sharedPref;
    String accessToken;
    int js_profile_id;

    public PostRequest(){}

    public PostRequest(Context context){
        this.context = context;
        sharedPref = context.getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        accessToken = sharedPref.getString("access_token", null);
        js_profile_id = sharedPref.getInt("js_profile_id", 0);
    }

    public void setViewToUpdate(TextView tv){
        textView = tv;
    }

    public void setRequestType(int requestType){
        REQUEST_TYPE = requestType;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject _response = null;

        final HttpClient httpclient = new DefaultHttpClient();
        final HttpPost httppost = new HttpPost( params[0] );

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
        //super.onPostExecute(result);
        //Log.e("reqresult", ""+result);
        if( result != null ){
            if( REQUEST_TYPE == UPLOAD_RESUME_ATTACHMENT ){
                // if successul
                if( result.optInt( "status_code" ) == 1 ){
                    if( textView != null ){
                        textView.setText(result.optString("resume") );
                    }

                    TableProfile tableProfile = new TableProfile(context);
                    ContentValues cv = new ContentValues();
                    cv.put("resume_file", result.optString("resume_url"));
                    tableProfile.updateProfile(cv, js_profile_id);
                }
                Toast.makeText(context, result.optString("status_text"), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
