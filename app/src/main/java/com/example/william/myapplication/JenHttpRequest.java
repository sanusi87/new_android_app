package com.example.william.myapplication;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class JenHttpRequest {
    public static int TYPE_STRING = 0;
    public static int JSON_OBJECT = 1;
    public static int JSON_ARRAY = 2;

    // used to decode json object
    public static JSONObject decodeJsonObjectString(String json){
        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            Log.e("decodeJOExp", e.getLocalizedMessage());
        }
        return null;
    }

    // used to decode json array
    public static JSONArray decodeJsonArrayString(String json){
        try {
            return new JSONArray(json);
        } catch (JSONException e) {
            Log.e("decodeJAExp", e.getLocalizedMessage());
        }
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
        return buf.toString();
    }
}
