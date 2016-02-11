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
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DailyJobMatcher extends Service{
    SharedPreferences sharedPref;
    String accessToken;
    private NotificationManager mNM;

    int DAILY_NOTIFICATION = 1;
    int WEEKLY_NOTIFICATION = 2;

    String myAlarmType;

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

    //int itemCount = 0;
    private void handleCommand(final Intent intent) {
        sharedPref = getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        accessToken = sharedPref.getString("access_token", null);

        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE); // notification service
        final TableJobSearchMatched tableJob = new TableJobSearchMatched(getApplicationContext());

        Bundle extras = intent.getExtras();
        if( extras != null ){
            myAlarmType = extras.getString("alertType");
            //Log.e("alarmType", ""+myAlarmType);

//            for (Object key : extras.keySet()) {
//                Log.e("key", "" + key);
//                String what = "" + extras.get((String) key);
//                Log.e("what", "" + what);
//            }

            if( isOnline() && accessToken != null && myAlarmType != null ){
                //Log.e("alarmStarted", myAlarmType" - "+Jenjobs.date(null, "yyyy-MM-dd hh:mm:ss", null));
                /*
                * delete closed jobs
                * */
                tableJob.deleteClosedJob();

                /*
                * check for active jobMatcher profile table
                */
                TableJobSearchProfile tableJobSearchProfile = new TableJobSearchProfile(getApplicationContext());
                final Cursor c = tableJobSearchProfile.getSubscribedProfile( myAlarmType );
                if( c.moveToFirst() ){
                    final int totalProfile = c.getCount();
                    final ArrayList<String> notifyText = new ArrayList<>();
                    int itemCount = 0;

                    final Intent _intent = new Intent();
                    _intent.putExtra("statusText", "JenJOBS JobMatcher");
                    _intent.putExtra("notificationId", DAILY_NOTIFICATION);
                    _intent.setClass(getApplicationContext(), JobSuggestion.class);

                    // loop subscribed job matcher profile
                    while( !c.isAfterLast() ){
                        final int jobMatcherProfileId = c.getInt(c.getColumnIndex("_id"));
                        final String jobMatcherProfileName = c.getString(c.getColumnIndex("profile_name"));
                        String[] s = {Jenjobs.JOB_MATCHED+"/"+jobMatcherProfileId+"?access-token="+accessToken};
                        final int finalItemCount = itemCount;

                        if( !tableJob.alreadyDownloaded(jobMatcherProfileId) ){
                            GetRequest g = new GetRequest();
                            g.setResultListener(new GetRequest.ResultListener() {
                                @Override
                                public void processResultArray(JSONArray result) {
                                    if (result != null && result.length() > 0) {
                                        for(int j=0;j<result.length();j++){
                                            try {
                                                JSONObject job = result.getJSONObject(j);
                                                ContentValues cv2 = new ContentValues();

                                                cv2.put("id", Integer.valueOf(job.getString("post_id")));
                                                cv2.put("jm_profile_id", jobMatcherProfileId);
                                                cv2.put("title", job.optString("title"));
                                                cv2.put("company", job.optString("company_name"));
                                                cv2.put("job_data", job.toString());
                                                cv2.put("date_closed", job.optString("date_closed"));
                                                cv2.put("date_added", Jenjobs.date(null, "yyyy-MM-dd hh:mm:ss", null));

                                                tableJob.addJob(cv2);
                                            } catch (JSONException e) {
                                                Log.e("err", e.getMessage());
                                            }
                                        }

                                        String _notifyText;
                                        if (result.length() == 1) {
                                            _notifyText = result.length() + " job opportunity for " + jobMatcherProfileName;
                                        } else {
                                            _notifyText = result.length() + " job opportunities for " + jobMatcherProfileName;
                                        }
                                        notifyText.add(_notifyText);
                                    }

                                    if( finalItemCount == totalProfile-1 ){
                                        //Log.e("contentText", TextUtils.join(", ", notifyText));
                                        if( notifyText.size() > 0 ){
                                            _intent.putExtra("contentText", TextUtils.join(", ", notifyText));
                                            showNotification(_intent);
                                        }
                                        stopSelf();
                                    }
                                }

                                @Override
                                public void processResult(JSONObject success) {}
                            });
                            g.execute(s);
                        }else{
                            if( finalItemCount == totalProfile-1 ){
                                stopSelf();
                            }
                        }
                        itemCount++;
                        c.moveToNext();
                    }
                }else{
                    stopSelf();
                }
                c.close();
            }else{
                stopSelf();
            }
        }else{
            stopSelf();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    private void showNotification(Intent intent) {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        //CharSequence text = getText(R.string.local_service_started);

        Bundle extra = intent.getExtras();
        String statusText = extra.getString("statusText");
        String contentText = extra.getString("contentText");
        int notificationID = extra.getInt("notificationId");

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
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
                .build();

        // Send the notification.
        mNM.notify(notificationID, notification);
    }
}
