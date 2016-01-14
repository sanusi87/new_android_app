package com.example.william.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class MainService extends Service{
    private NotificationManager mNM;
    private int NOTIFICATION = R.string.local_service_started;

    SharedPreferences sharedPref;
    String accessToken;

    TableApplication tableApplication;
    HashMap applicationStatus;

    public class LocalBinder extends Binder {
        MainService getService() {
            return MainService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //@Override
    //public void onCreate() {
        // Display a notification about us starting.  We put an icon in the status bar.
        //showNotification();
    //}

    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.local_service_started);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
            .setSmallIcon(R.drawable.icon)  // the status icon
            .setTicker(text)  // the status text
            .setWhen(System.currentTimeMillis())  // the time stamp
            .setContentTitle(getText(R.string.local_service_label))  // the label of the entry
            .setContentText(text)  // the contents of the entry
            .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
            .build();

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);

    }

    // This is the old onStart method that will be called on the pre-2.0
    // platform.  On 2.0 or later we override onStartCommand() so this
    // method will not be called.
    @Override
    public void onStart(Intent intent, int startId) {
        handleCommand(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleCommand(intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    // +- setInterval()  - to run for every 30 seconds
    final Handler handler = new Handler();
    Runnable updateData = new Runnable(){
        @Override
        public void run() {
            // TODO - check for network connectivity first, only then can proceed
            Cursor applications = tableApplication.getActiveApplication();
            //Log.e("active apps",""+applications.getCount());
            if (applications.getCount() > 0) {
                applications.moveToFirst();

                JSONArray arr = new JSONArray();
                while (!applications.isAfterLast()) {
                    int postId = applications.getInt(2);
                    arr.put(postId);
                    applications.moveToNext();
                }

                String[] params = {Jenjobs.APPLICATION_STATUS_URL + "?id=" + arr.toString() + "&access-token=" + accessToken};
                //Log.e("url",params[0]);
                new CheckApplication().execute(params);
            }
            applications.close();

            handler.postDelayed(updateData, 30000);
        }
    };

    private void handleCommand(Intent intent){
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        tableApplication = new TableApplication(getApplicationContext());
        applicationStatus = Jenjobs.getApplicationStatus();
        sharedPref = this.getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        accessToken = sharedPref.getString("access_token", null);
        if( accessToken != null ){
            handler.postDelayed(updateData, 30000);
        }
    }

    // check application
    public class CheckApplication extends AsyncTask<String, Void, JSONArray> {

        /*
        * params[0] = url
        * params[1] = json object string
        * */

        @Override
        protected JSONArray doInBackground(String... params) {
            JSONArray _response = null;
            //Log.e("appurl", ""+params[0]);
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(params[0]);
            httpget.addHeader("Content-Type", "application/json");
            httpget.addHeader("Accept", "application/json");

            try {
                HttpResponse _http_response = httpclient.execute(httpget);
                HttpEntity _entity = _http_response.getEntity();
                InputStream is = _entity.getContent();

                String responseString = JenHttpRequest.readInputStreamAsString(is);
                _response = JenHttpRequest.decodeJsonArrayString(responseString);
            } catch (IOException e) {
                Log.e("err", e.getMessage());
            }
            return _response;
        }

        @Override
        protected void onPostExecute(JSONArray success) {
            Log.e("onGet", "" + success);
            if( success != null ){
                for(int i=0;i<success.length();i++){
                    try {
                        JSONObject app = success.getJSONObject(i);

                        int status = app.optInt("status");
                        int applicationId = app.optInt("id");
                        Log.e("status", ""+status);
                        Log.e("appid", ""+applicationId);

                        String statusText = (String) applicationStatus.get(status);

                        if( tableApplication.isDifferentApplicationStatus( applicationId, status ) ){
                            showNotification();

                            ContentValues cv = new ContentValues();
                            cv.put("status", status);
                            cv.put("date_updated", Jenjobs.date(null,"yyyy-MM-dd hh:mm:ss",null));
                            tableApplication.updateApplication(cv, applicationId);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
