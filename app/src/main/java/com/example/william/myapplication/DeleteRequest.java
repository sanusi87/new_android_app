package com.example.william.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class DeleteRequest extends AsyncTask<String, Void, JSONObject> {

    //private int idToDelete = 0;
    //public DeleteRequest( int id ){
       //this.idToDelete = id;
    //}

    public DeleteRequest(){}

    @Override
    protected JSONObject doInBackground(String... params) {
        JSONObject _response = null;
        HttpClient httpclient = new DefaultHttpClient();

        //String url = params[0];
        //if( idToDelete > 0 ){ url += "/"+idToDelete; }
        Log.e("deleteUrl", params[0]);
        HttpDelete httpDelete = new HttpDelete( params[0] );
        httpDelete.addHeader("Content-Type", "application/json");
        httpDelete.addHeader("Accept", "application/json");

        try {
            HttpResponse _http_response = httpclient.execute(httpDelete);;
            HttpEntity _entity = _http_response.getEntity();
            InputStream is = _entity.getContent();
            String responseString = JenHttpRequest.readInputStreamAsString(is);

            _response = JenHttpRequest.decodeJsonObjectString(responseString);
        } catch (IOException e) {
            Log.e("deleteExc", e.getMessage());
        }

        return _response;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        if( result != null ){
            Log.e("deleted?", result.toString());
        }
        resultListener.processResult(result);
    }

    public interface ResultListener {
        void processResult(JSONObject success); // available listener method
    }

    private ResultListener resultListener;
    public DeleteRequest setResultListener(ResultListener resultListener){
        this.resultListener = resultListener;
        return this;
    }
}
