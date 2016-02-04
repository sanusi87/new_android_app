package com.example.william.myapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

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
        return START_STICKY;
    }

    private void handleCommand(Intent intent) {
        sharedPref = getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        accessToken = sharedPref.getString("access_token", null);
        Log.e("alarmStarted", Jenjobs.date(null, "yyyy-MM-dd hh:mm:ss", null));

        Bundle extras = intent.getExtras();
        if( extras != null ){
            //Log.e("intent1", ""+extras.getString("alertEach"));
            Log.e("intent2", ""+extras.getInt("jobMatcherProfileId"));

            if( isOnline() && accessToken != null ){

                /*
                * check for active jobMatcher profile table
                *
                //stopThisService(); // call when done

                TableJobSearchProfile tableJobSearchProfile = new TableJobSearchProfile(getApplicationContext());
                Cursor c = tableJobSearchProfile.getSubscribedProfile( extras.getString("alertEach") );
                if( c.moveToFirst() ){
                    while( !c.isAfterLast() ){
                        int jobMatcherProfileId = c.getInt(c.getColumnIndex("_id"));
                        String[] s = {Jenjobs.JOB_MATCHED+"/"+jobMatcherProfileId+"?access-token="+accessToken};
                        Log.e("url", s[0]);


                        GetRequest g = new GetRequest();
                        g.setResultListener(new GetRequest.ResultListener() {
                            @Override
                            public void processResultArray(JSONArray result) {

                            }

                            @Override
                            public void processResult(JSONObject success) {}
                        });
                        g.execute(s);


                        c.moveToNext();
                    }
                }
                c.close();
                */

                int jobMatcherProfileId = extras.getInt("jobMatcherProfileId");
                String[] s = {Jenjobs.JOB_MATCHED+"/"+jobMatcherProfileId+"?access-token="+accessToken};
                Log.e("url", s[0]);
                GetRequest g = new GetRequest();
                g.setResultListener(new GetRequest.ResultListener() {
                    @Override
                    public void processResultArray(JSONArray result) {

                    }

                    @Override
                    public void processResult(JSONObject success) {}
                });
                g.execute(s);

            }else{
                stopThisService();
            }
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
