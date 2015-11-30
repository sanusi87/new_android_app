package com.example.william.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class JenHttpRequest {

    /**
     * type 1 = GET
     * type 2 = POST
     * type 3 = DELETE
     * */

    public static final int GET_REQUEST = 1;
    public static final int POST_REQUEST = 2;
    public static final int DELETE_REQUEST = 3;

    public static int JSON_OBJECT = 1;
    public static int JSON_ARRAY = 2;

    // the response object
    public Object response = null;
    // the type of the response object
    public int OBJECT_TYPE = JSON_OBJECT; // 1=JSONObject, 2=JSONArray
    /*
    if( JenHttpRequest.OBJECT_TYPE == JenHttpRequest.JSON_ARRAY ){

    }
    */

    public JenHttpRequest(int type, String url, Intent intent){
        if( type == JenHttpRequest.GET_REQUEST ){
            sendGetRequest(url);
        }else if( type == JenHttpRequest.POST_REQUEST ){
            sendPostRequest(url, intent);
        }else if( type == JenHttpRequest.DELETE_REQUEST ){
            sendDeleteRequest(url);
        }
    }

    public static String createJson( Bundle extras ){
        JSONObject obj = new JSONObject();
        for(String key: extras.keySet()){
            Object value = extras.get(key);
            try {
                obj.put(key, value.toString());
            } catch (JSONException e) {
                Log.e("test", e.getMessage());
            }
        }
        return obj.toString();
    }

    // used to decode json object
    public static JSONObject decodeJsonObjectString(String json){
        Log.e("jsonResp", json);

        try {
            JSONObject jo = new JSONObject(json);
            return jo;
        } catch (JSONException e) {
            Log.e("decodeJOExp", e.getLocalizedMessage());
        }

        return null;
    }

    // used to decode json array
    public static JSONArray decodeJsonArrayString(String json){
        try {
            JSONArray ja = new JSONArray(json);
            return ja;
        } catch (JSONException e) {
            Log.e("decodeJAExp", e.getLocalizedMessage());
        }
        return null;
    }

    // send POST request
    public JSONObject sendPostRequest(final String url, Intent intent){
        final Bundle extras = intent.getExtras();
        final String obj = JenHttpRequest.createJson(extras);

        final HttpClient httpclient = new DefaultHttpClient();
        final HttpPost httppost = new HttpPost(url);
        httppost.addHeader("Content-Type", "application/json");
        httppost.addHeader("Accept", "application/json");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e("post", "trying...");
                    Log.e("test", "length: "+obj.toString().length());
                    StringEntity entity = new StringEntity(obj.toString());
                    entity.setContentEncoding(HTTP.UTF_8);
                    entity.setContentType("application/json");

                    httppost.setEntity(entity);
                    HttpResponse _response = httpclient.execute(httppost);
                    HttpEntity _entity = _response.getEntity();
                    InputStream is = _entity.getContent();

                    String responseString = readInputStreamAsString(is);
                    if( responseString.substring(0).equals("[") ){
                        response = decodeJsonArrayString(responseString);
                        OBJECT_TYPE = JenHttpRequest.JSON_ARRAY;
                    }else{
                        response = decodeJsonObjectString(responseString);
                    }
                    response.notify();
                    is.close();
                }catch (Exception e){
                    Log.e("test", e.getMessage());
                }
            }
        }).start();

        return null;
    }


    // send GET request
    public JSONObject sendGetRequest(final String url){
        final HttpClient httpclient = new DefaultHttpClient();
        final HttpGet httpget = new HttpGet(url);
        httpget.addHeader("Content-Type", "application/json");
        httpget.addHeader("Accept", "application/json");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //wait();
                    HttpResponse _response = httpclient.execute(httpget);
                    HttpEntity _entity = _response.getEntity();
                    InputStream is = _entity.getContent();

                    String responseString = readInputStreamAsString(is);
                    if( responseString.substring(0,1).equals("[") ){
                        response = decodeJsonArrayString(responseString);
                        OBJECT_TYPE = JenHttpRequest.JSON_ARRAY;
                    }else{
                        response = decodeJsonObjectString(responseString);
                    }
                    //notify();
                    is.close();
                }catch (IOException e) {
                    Log.e("sendGet", "IOExcp=" + e.getMessage());
                }
            }
        }).start();

        return null;
    }


    // send DELETE request
    public Object sendDeleteRequest(final String url){
        final HttpClient httpclient = new DefaultHttpClient();
        final HttpDelete httpdelete = new HttpDelete(url);
        httpdelete.addHeader("Content-Type", "application/json");
        httpdelete.addHeader("Accept", "application/json");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e("delete", "trying...");
                    HttpResponse _response = httpclient.execute(httpdelete);
                    HttpEntity _entity = _response.getEntity();
                    InputStream is = _entity.getContent();

                    String responseString = readInputStreamAsString(is);
                    if( responseString.substring(0).equals("[") ){
                        response = decodeJsonArrayString(responseString);
                        OBJECT_TYPE = JenHttpRequest.JSON_ARRAY;
                    }else{
                        response = decodeJsonObjectString(responseString);
                    }
                    is.close();
                }catch (Exception e){
                    Log.e("test", e.getMessage());
                }
            }
        }).start();

        return null;
    }


    public static String readInputStreamAsString(InputStream in) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(in);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while(result != -1) {
            byte b = (byte)result;
            buf.write(b);
            result = bis.read();
        }
        //Log.e("response", buf.toString());
        return buf.toString();
    }

    // AsyncTask<Parameter, Progress, Result>

}
