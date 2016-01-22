package com.example.william.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class GetRequest extends AsyncTask<String, Void, Object> {

    public int OBJECT_TYPE = 1;

    public GetRequest(){}

    @Override
    protected Object doInBackground(String... params) {
        Object _response = null;

        final HttpClient httpclient = new DefaultHttpClient();
        final HttpGet httpget = new HttpGet(params[0]);
        httpget.addHeader("Content-Type", "application/json");
        httpget.addHeader("Accept", "application/json");

        try {
            HttpResponse _http_response = httpclient.execute(httpget);
            HttpEntity _entity = _http_response.getEntity();
            InputStream is = _entity.getContent();

            String responseString = JenHttpRequest.readInputStreamAsString(is);
            if( responseString.substring(0,1).equals("[") ){
                _response = JenHttpRequest.decodeJsonArrayString(responseString);
                OBJECT_TYPE = JenHttpRequest.JSON_ARRAY;
            }else{
                _response = JenHttpRequest.decodeJsonObjectString(responseString);
                OBJECT_TYPE = JenHttpRequest.JSON_OBJECT;
            }
            //return _response;
        } catch (IOException e) {
            Log.e("IOException", e.getMessage());
            // TODO - toast network error
        }
        return _response;
    }


    @Override
    protected void onPostExecute(Object success) {
        Log.e("onPostEx", "" + success);
        if( success != null ){
            Log.e("onPostEx", ""+success);
        }

        if( OBJECT_TYPE == JenHttpRequest.JSON_OBJECT ){
            resultListener.processResult((JSONObject) success);
        }
    }

    public interface ResultListener {
        void processResult(JSONObject success); // available listener method
    }

    private ResultListener resultListener;
    public GetRequest setResultListener(ResultListener resultListener){
        this.resultListener = resultListener;
        return this;
    }
}
