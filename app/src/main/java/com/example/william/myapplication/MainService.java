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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
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
import java.util.HashMap;


public class MainService extends Service{
    private NotificationManager mNM;
    private int NOTIFICATION_APPLICATION = 1;
    private int NOTIFICATION = 1; // set default to application notification

    SharedPreferences sharedPref;
    String accessToken;

    TableApplication tableApplication;
    TableSettings tableSettings;
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

    private void showNotification(Intent intent) {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        //CharSequence text = getText(R.string.local_service_started);

        Bundle extra = intent.getExtras();
        String statusText = extra.getString("statusText");
        String contentText = extra.getString("contentText");

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
            .setSmallIcon(R.drawable.jenjobs_notification)  // the status icon
            //.setTicker(statusText)  // the status text
            .setWhen(System.currentTimeMillis())  // the time stamp
            .setContentTitle(statusText)  // the label of the entry
            .setContentText(contentText)  // the contents of the entry
            .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
            .setAutoCancel(true)
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
            if( isOnline() ){
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
                    new CheckApplication().execute(params);
                }
                applications.close();
            }
            handler.postDelayed(updateData, 30000);
        }
    };

    private void handleCommand(Intent intent){
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        tableSettings = new TableSettings(getApplicationContext());
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
            //Log.e("onGet", "" + success);
            if( success != null ){
                int applicationUpdated = 0;
                for(int i=0;i<success.length();i++){
                    try {
                        JSONObject app = success.getJSONObject(i);

                        int status = app.optInt("status");
                        int postId = app.getInt("post_id");
                        //Log.e("status", ""+status);
                        //Log.e("postid", ""+postId);

                        String statusText = (String) applicationStatus.get(status);
                        //Log.e("so?", ""+tableApplication.isDifferentApplicationStatus( postId, status ));
                        if( tableApplication.isDifferentApplicationStatus( postId, status ) ){

                            applicationUpdated++;

                            ContentValues cv = new ContentValues();
                            cv.put("status", status);
                            cv.put("date_updated", Jenjobs.date(null, "yyyy-MM-dd hh:mm:ss", null));
                            boolean isUpdated = tableApplication.updateApplication(cv, postId);
                            //Log.e("isUpdated", "" + isUpdated);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if( applicationUpdated > 0 ){
                    String notificationAlert = tableSettings.getSetting("notification_alert");
                    // if notification alert is enabled
                    if( Integer.valueOf(notificationAlert) > 0 ){
                        Intent intent = new Intent();
                        intent.putExtra("statusText", "Notification");
                        intent.putExtra("defaultPage", ProfileActivity.APPLICATION_FRAGMENT);
                        intent.setClass(getApplicationContext(), ProfileActivity.class);
                        intent.putExtra("contentText", "Your application status has been updated.");

                        showNotification(intent);
                    }
                }
            }
        }
    }

    // check application
    public class CheckInvitationAndRequest extends AsyncTask<String, Void, JSONArray> {

        public CheckInvitationAndRequest(){}

        @Override
        protected JSONArray doInBackground(String... params) {
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray success) {

        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
