package com.example.william.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;

public class GetRequest extends AsyncTask<Void, Void, Object> {

    public String _url;
    public int OBJECT_TYPE = 1;

    GetRequest( String target ){
        this._url = target;
    }

    @Override
    protected Object doInBackground(Void... params) {
        Object _response = null;

        final HttpClient httpclient = new DefaultHttpClient();
        final HttpGet httpget = new HttpGet(_url);
        httpget.addHeader("Content-Type", "application/json");
        httpget.addHeader("Accept", "application/json");

        HttpResponse _http_response = null;
        try {
            _http_response = httpclient.execute(httpget);
            HttpEntity _entity = _http_response.getEntity();
            InputStream is = _entity.getContent();

            String responseString = JenHttpRequest.readInputStreamAsString(is);
            if( responseString.substring(0,1).equals("[") ){
                _response = JenHttpRequest.decodeJsonArrayString(responseString);
                OBJECT_TYPE = JenHttpRequest.JSON_ARRAY;
            }else{
                _response = JenHttpRequest.decodeJsonObjectString(responseString);
            }
            //return _response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return _response;
    }


    @Override
    protected void onPostExecute(final Object success) {
        Log.e("onPostEx", "" + success);
        if( success != null ){
            Log.e("onPostEx", ""+success);
        }
    }
}
