package com.example.william.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageLoad extends AsyncTask<Void, Void, Bitmap> {
    private String url;
    private ImageView imageView;
    private ResultListener resultListener;

    public ImageLoad(String url, ImageView imageView) {
        this.url = url;
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        try {
            URL urlConnection = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        //super.onPostExecute(result);
        if( result != null ){
            imageView.setImageBitmap(result);
            if( resultListener != null ){
                resultListener.processResult(result);
            }
        }
    }

    public interface ResultListener {
        void processResult(Bitmap result);
    }

    public ImageLoad setResultListener(ResultListener resultListener){
        this.resultListener = resultListener;
        return this;
    }
}