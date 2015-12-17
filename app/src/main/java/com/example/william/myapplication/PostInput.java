package com.example.william.myapplication;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

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

public class PostInput extends AsyncTask<String, Void, JSONObject> {

    private View v;
    private int viewType;

    public PostInput(){}

    public PostInput( String url, String jsonObjectString, View v, int viewType ){
        this.v = v; // TextView, EditText
        this.viewType = viewType; // 1, 2
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject _response = null;

        final HttpClient httpclient = new DefaultHttpClient();
        final HttpPost httppost = new HttpPost( params[0] );
        httppost.addHeader("Content-Type", "application/json");
        httppost.addHeader("Accept", "application/json");
        HttpResponse _http_response = null;

        try {
            StringEntity entity = new StringEntity(params[1]);
            entity.setContentEncoding(HTTP.UTF_8);
            entity.setContentType("application/json");
            httppost.setEntity(entity);

            _http_response = httpclient.execute(httppost);
            HttpEntity _entity = _http_response.getEntity();
            InputStream is = _entity.getContent();
            String responseString = JenHttpRequest.readInputStreamAsString(is);
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
        super.onPostExecute(result);
    }

}
