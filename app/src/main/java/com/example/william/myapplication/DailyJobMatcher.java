package com.example.william.myapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class DailyJobMatcher extends Service{
    SharedPreferences sharedPref;
    String accessToken;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    public class LocalBinder extends Binder {
        DailyJobMatcher getService() {
            return DailyJobMatcher.this;
        }
    }

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

    private void handleCommand(Intent intent) {
        sharedPref = getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        accessToken = sharedPref.getString("access_token", null);
        Log.e("alarmStarted", Jenjobs.date(null, "yyyy-MM-dd hh:mm:ss", null));
        if( isOnline() && accessToken != null ){
            /*
            * check for active jobMatcher profile table
            * */
            stopThisService(); // when done
        }else{
            stopThisService();
        }
    }

    public void stopThisService(){
        /*
            * stop this service after 30 seconds
            * supposed to stop when the job finished
            * */
        Handler handler = new Handler();
        Runnable updateData = new Runnable(){
            @Override
            public void run() {
                stopSelf();
            }
        };
        handler.postDelayed(updateData, 30000);
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
