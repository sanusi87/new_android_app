package jen.jobs.application;

import android.app.AlarmManager;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;


public class MainService extends Service{
    private NotificationManager mNM;
    private int NOTIFICATION_APPLICATION = 1;

    private static int DAILY_ALARM = 1;
    private static int WEEKLY_ALARM = 2;

    SharedPreferences sharedPref;
    String accessToken;

    TableApplication tableApplication;
    TableInvitation tableInvitation;
    TableJob tableJob;
    TableSettings tableSettings;
    TableJobSearchProfile tableJobSearchProfile;
    HashMap applicationStatus;

    // list of alarms
    AlarmManager alarm;
    Context context;

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
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setAutoCancel(true)
            .build();

        // Send the notification.
        int NOTIFICATION = 1;
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
            sharedPref = getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
            accessToken = sharedPref.getString("access_token", null);

            /*
            * TODO
            * - check for network connectivity first
            * - is the user currently login ?
            * - yes -> can proceed
            * - no -> shut up
            * */
            if( isOnline() && accessToken != null ){
                // check active application status
                Cursor applications = tableApplication.getActiveApplication();
                if (applications.getCount() > 0) {
                    applications.moveToFirst();

                    JSONArray arr = new JSONArray();
                    while (!applications.isAfterLast()) {
                        int postId = applications.getInt(2);
                        arr.put(postId);
                        applications.moveToNext();
                    }

                    String[] params = {Jenjobs.APPLICATION_STATUS_URL + "?id=" + arr.toString()
                            + "&access-token=" + accessToken};
                    new CheckApplication().execute(params);
                }
                applications.close();

                // check for job invitation and resume access request
                GetRequest g = new GetRequest();
                g.setResultListener(new GetRequest.ResultListener() {
                    @Override
                    public void processResultArray(JSONArray result) {
                        //Log.e("invitation", ""+result);
                        /*
                        [
                            {
                                "id": 111398,
                                "company": "Oriental Daily Classified Postings",
                                "company_id": 66263,
                                "type": "J",
                                "opened": 0,
                                "status": "P",
                                "date_created": "2016-01-27 11:04:05",
                                "date_updated": "2016-01-27 11:04:05",
                                "post": {
                                    "post_id": 326036,
                                    "post_title": "銷售員(Full/PartTime) 數位",
                                    "date_closed": "2016-03-26 23:59:59",
                                    "closed": false
                                }
                            }
                        ]
                        */

                        if (result != null) {
                            if (result.length() > 0) {
                                for (int i = 0; i < result.length(); i++) {
                                    ContentValues cv = new ContentValues();
                                    try {
                                        JSONObject s = result.getJSONObject(i);

                                        // check for existence
                                        Cursor c = tableInvitation.getInvitation(s.optInt("id"), 0);
                                        if (c.getCount() == 0) {
                                            // if not yet exists, add new
                                            cv.put("id", s.optInt("id"));
                                            cv.put("emp_profile_name", s.optString("company"));
                                            cv.put("emp_profile_id", s.optInt("company_id"));
                                            cv.put("status", s.optString("status"));
                                            cv.put("date_added", s.optString("date_created"));
                                            String dateUpdated = s.optString("date_updated");
                                            if (dateUpdated != null && !dateUpdated.equals("") && !dateUpdated.equals("null")) {
                                                cv.put("date_updated", dateUpdated);
                                            }

                                            // this is for type "J" = Job Application Invitation
                                            String post = s.getString("post");
                                            if (post != null && !post.equals("null")) {
                                                JSONObject _post = new JSONObject(post);

                                                final int postId = _post.getInt("post_id");
                                                boolean isJobClosed = _post.getBoolean("closed");

                                                // for each job application invitation
                                                // if the job is still active
                                                if (!isJobClosed) {
                                                    // download the job details
                                                    GetRequest getRequest = new GetRequest();
                                                    getRequest.setResultListener(new GetRequest.ResultListener() {
                                                        @Override
                                                        public void processResultArray(JSONArray result) {
                                                        }

                                                        @Override
                                                        public void processResult(JSONObject success) {
                                                            if (success != null && success.toString().length() > 0) {
                                                                // and save to database
                                                                ContentValues cv2 = new ContentValues();
                                                                cv2.put("id", postId);
                                                                cv2.put("title", success.optString("title"));
                                                                cv2.put("company", success.optString("company"));
                                                                cv2.put("job_data", success.toString());
                                                                cv2.put("date_closed", success.optString("date_closed"));

                                                                tableJob.addJob(cv2);
                                                            }
                                                        }
                                                    });
                                                    String[] param = {Jenjobs.JOB_DETAILS + "/" + postId};
                                                    getRequest.execute(param);
                                                }

                                                cv.put("post_id", postId);
                                                cv.put("post_title", _post.getString("post_title"));
                                                cv.put("post_closed_on", _post.getString("date_closed"));
                                            }

                                            tableInvitation.saveInvitation(cv, 0);
                                        } else {
                                            c.moveToFirst();
                                            String _status = c.getString(3);
                                            String _post_closed_on = c.getString(6);
                                            c.close();

                                            // if the downloaded status != saved status
                                            if (!_status.equals(s.optString("status"))) {
                                                // update status and date
                                                cv.put("status", s.optString("status"));
                                                String dateUpdated = s.optString("date_updated");
                                                if (dateUpdated != null && !dateUpdated.equals("") && !dateUpdated.equals("null")) {
                                                    cv.put("date_updated", dateUpdated);
                                                }

                                                JSONObject _post = new JSONObject(s.optString("post"));
                                                if (_post_closed_on != null
                                                        && !_post_closed_on.equals("")
                                                        && !_post_closed_on.equals("null")) {
                                                    cv.put("post_title", _post.optString("post_title"));
                                                    cv.put("post_closed_on", _post.optString("date_closed"));
                                                }

                                                tableInvitation.updateInvitation(cv, s.optInt("id"));
                                            }
                                        }

                                    } catch (JSONException e) {
                                        Log.e("jsonObjFailed", e.getMessage());
                                    }
                                } // end for

                                Intent intent = new Intent();
                                intent.putExtra("statusText", "Notification");
                                intent.putExtra("defaultPage", ProfileActivity.INVITATION_AND_REQUEST);
                                intent.setClass(getApplicationContext(), ProfileActivity.class);

                                if( result.length() > 1 ){
                                    intent.putExtra("contentText", "Employers have sent you some requests. Click to have a look.");
                                }else{
                                    intent.putExtra("contentText", "An employer had sent you a request. Click to have a look.");
                                }

                                showNotification(intent);

                            }
                        }
                    }

                    @Override
                    public void processResult(JSONObject result) {
                    }
                });
                String[] _params = {Jenjobs.INVITATION_URL+"?access-token=" + accessToken};
                g.execute(_params);
                // end invitation and resume access request check

                // start downloading job suggestion
                GetRequest gr = new GetRequest();
                String[] __params = {Jenjobs.JOB_SUGGESTION_DOWNLOAD+"?access-token="+accessToken};
                gr.setResultListener(new GetRequest.ResultListener() {
                    @Override
                    public void processResultArray(JSONArray result) {
                        if( result != null && result.length() > 0 ){
                            for(int i=0;i<result.length();i++){
                                try {
                                    JSONObject suggestion = result.getJSONObject(i);
                                    final int postId = Integer.valueOf(suggestion.getString("post_id"));
                                    final String suggestedOn = suggestion.getString("suggested_on");

                                    // download JOB DETAILS
                                    GetRequest getRequest = new GetRequest();
                                    getRequest.setResultListener(new GetRequest.ResultListener() {
                                        @Override
                                        public void processResultArray(JSONArray result) {}

                                        @Override
                                        public void processResult(JSONObject success) {
                                            if (success != null && success.toString().length() > 0) {
                                                // and save to database
                                                ContentValues cv2 = new ContentValues();
                                                cv2.put("id", postId);
                                                cv2.put("title", success.optString("title"));
                                                cv2.put("company", success.optString("company"));
                                                cv2.put("job_data", success.toString());
                                                cv2.put("date_closed", success.optString("date_closed"));

                                                // set this to determine that this is a job suggestion
                                                cv2.put("suggested_on", suggestedOn);

                                                tableJob.addJob(cv2);
                                            }
                                        }
                                    });
                                    String[] param = {Jenjobs.JOB_DETAILS + "/" + postId};
                                    getRequest.execute(param);
                                    // download JOB DETAILS

                                    // mark this suggestion as downloaded
                                    PostRequest p = new PostRequest();
                                    p.setResultListener(new PostRequest.ResultListener() {
                                        @Override
                                        public void processResult(JSONObject success) {
                                            Log.e("marked", ""+success);
                                        }
                                    });
                                    String[] ___param = {Jenjobs.JOB_SUGGESTION_MARK+"/"+postId+"?access-token="+accessToken, "{}"};
                                    p.execute(___param);
                                    // mark this suggestion as downloaded

                                } catch (JSONException e) {
                                    Log.e("suggestion", e.getMessage());
                                }
                            } // end for

                            Intent intent = new Intent();
                            intent.putExtra("statusText", "Notification");
                            intent.putExtra("defaultPage", ProfileActivity.JOB_SUGGESTION);
                            intent.setClass(getApplicationContext(), ProfileActivity.class);

                            if( result.length() > 1 ){
                                intent.putExtra("contentText", "We have suggested a few jobs for you. Click to have a look.");
                            }else{
                                intent.putExtra("contentText", "We have suggested a job for you. Click to have a look.");
                            }

                            showNotification(intent);
                        }
                    }

                    @Override
                    public void processResult(JSONObject success) {}
                });
                gr.execute(__params);
                // end downloading job suggestion
            }
            /*
            * run checking every 1 hour
            * */
            handler.postDelayed(updateData, 60000 * 60);
        }
    };

    private void handleCommand(Intent intent){
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE); // notification service
        alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE); // alarm manager service

        if( context == null ){
            context = getApplicationContext();
        }

        tableSettings = new TableSettings(context);
        tableApplication = new TableApplication(context);
        tableInvitation = new TableInvitation(context);
        tableJob = new TableJob(context);
        tableJobSearchProfile = new TableJobSearchProfile(context);
        applicationStatus = Jenjobs.getApplicationStatus();

        /*
        * run checking after 1 hour
        * */
        handler.postDelayed(updateData, 60000 * 60);

        /*
        * check for jobmatcher every day
        * - check for active jobMatcher profile table
        * - allow user to update the alert time of each profile
        */
        setNewAlarm("D"); // daily alarm
        setNewAlarm("W"); // weekly alarm
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

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /*
    * searchProfileId == Job Search Profile ID
    * alertNotification == [D]aily / [W]eekly
    * */
    public void setNewAlarm( String alertNotification ){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        Random r = new Random();
        int m = r.nextInt(58)+1; // randomize minute, minimum 1
        int h = r.nextInt(12); // randomize hour
        if( h < 8 ){ h = 8; }

        Long alarmInterval = AlarmManager.INTERVAL_DAY;
        //Long alarmInterval = (long)(1000 * 60 * 3); // 3 minute
        int alarmID = DAILY_ALARM;
        if( alertNotification.equals("D") ){
            //Log.e("alarm", "daily");
            calendar.set(Calendar.HOUR_OF_DAY, h);
            calendar.set(Calendar.MINUTE, m);
        }else if( alertNotification.equals("W") ){
            //Log.e("alarm", "weekly");
            alarmInterval = alarmInterval * 7;
            alarmID = WEEKLY_ALARM;
            calendar.set(Calendar.HOUR_OF_DAY, h);
            calendar.set(Calendar.MINUTE, m);
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        }
        Intent _intent = createIntent( alertNotification );

        boolean alarmUp = PendingIntent.getService(context, alarmID, _intent, PendingIntent.FLAG_NO_CREATE) != null;
        if( !alarmUp ){
            PendingIntent alarmIntent = PendingIntent.getService(context, alarmID, _intent, PendingIntent.FLAG_NO_CREATE);
            alarm.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), alarmInterval, alarmIntent);
        }
    }

    public void stopAlarm( int searchProfileId, String alertNotification ){
        Intent _intent = createIntent( alertNotification );
        PendingIntent alarmIntent = PendingIntent.getService(context, searchProfileId, _intent, 0);
        alarm.cancel(alarmIntent);
    }

    private Intent createIntent( String alertNotification ){
        Intent _intent = new Intent(context, DailyJobMatcher.class);
        _intent.putExtra("alertType", alertNotification);
        //_intent.putExtra("jobMatcherProfileId", searchProfileId);
        return _intent;
    }
}
