package jen.jobs.application;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class SettingsSection {

    private View rootView;
    private Context context;
    private String accessToken;
    private boolean isOnline = false;

    public SettingsSection(Context c, View v){
        this.context = c;
        this.rootView = v;
    }

    public void setAccessToken(String accessToken){
        this.accessToken = accessToken;
    }

    public void setIsOnline(boolean online){
        this.isOnline = online;
    }

    public void render(){
        final TableSettings tableSettings = new TableSettings(context);
        final TableSubscription tableSubscription = new TableSubscription(context);

        LinearLayout notification = (LinearLayout)rootView.findViewById(R.id.notification);
        final CheckBox notificationCb = (CheckBox)rootView.findViewById(R.id.notification_checkbox);

        LinearLayout newsletter = (LinearLayout)rootView.findViewById(R.id.newsletter);
        final CheckBox newsletterCb = (CheckBox)rootView.findViewById(R.id.newsletter_checkbox);

        LinearLayout partners = (LinearLayout)rootView.findViewById(R.id.partners);
        final CheckBox partnersCb = (CheckBox)rootView.findViewById(R.id.partners_checkbox);

        LinearLayout sms = (LinearLayout)rootView.findViewById(R.id.sms);
        final CheckBox smsCb = (CheckBox)rootView.findViewById(R.id.sms_checkbox);

        LinearLayout updatePasswordContainer = (LinearLayout)rootView.findViewById(R.id.updatePasswordContainer);

        int alert = Integer.valueOf(tableSettings.getSetting("notification_alert"));
        if( alert > 0 ){ notificationCb.setChecked(true); }

        Cursor subscriptions = tableSubscription.getSubscription();
        if( subscriptions.moveToFirst() ){
            while( !subscriptions.isAfterLast() ){

                int subscripId = subscriptions.getInt(2);
                int subscripStat = subscriptions.getInt(3);

                if( subscripId == TableSubscription.NEWSLETTER ){
                    if( subscripStat > 0 ){
                        newsletterCb.setChecked(true);
                    }
                }else if( subscripId == TableSubscription.PROMOTION ){
                    if( subscripStat > 0 ){
                        partnersCb.setChecked(true);
                    }
                }else if( subscripId == TableSubscription.SMS_JOB_ALERT ){
                    if( subscripStat > 0 ){
                        smsCb.setChecked(true);
                    }
                }

                subscriptions.moveToNext();
            }
        }
        subscriptions.close();

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationCb.setChecked(!notificationCb.isChecked());
                tableSettings.updateSettings("notification_alert", notificationCb.isChecked() ? "1" : "0");
            }
        });

        newsletter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( isOnline ){
                    newsletterCb.setChecked(!newsletterCb.isChecked());

                    ContentValues cv = new ContentValues();
                    cv.put("status", newsletterCb.isChecked() ? 1 : 0);
                    tableSubscription.updateSubscription(cv, TableSubscription.NEWSLETTER);

                    JSONObject jsonString = new JSONObject();
                    try {
                        jsonString.put("subscription_id", TableSubscription.NEWSLETTER);
                        jsonString.put("status", cv.getAsInteger("status"));

                        String[] param = {Jenjobs.SUBSCRIPTION_URL+"?access-token="+accessToken, jsonString.toString()};
                        PostRequest postRequest = new PostRequest();
                        postRequest.setResultListener(new PostRequest.ResultListener() {
                            @Override
                            public void processResult(JSONObject result) {
                                Log.e("subscribed?", "" + result);
                            }
                        });
                        postRequest.execute(param);
                    } catch (JSONException e) {
                        Log.e("error", e.getMessage());
                    }
                }else{
                    Toast.makeText(context, R.string.offline_notification, Toast.LENGTH_LONG).show();
                }
            }
        });

        partners.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( isOnline ){
                    partnersCb.setChecked( !partnersCb.isChecked() );

                    ContentValues cv = new ContentValues();
                    cv.put("status", partnersCb.isChecked() ? 1 : 0);
                    tableSubscription.updateSubscription(cv, TableSubscription.PROMOTION);

                    JSONObject jsonString = new JSONObject();
                    try {
                        jsonString.put("subscription_id", TableSubscription.PROMOTION);
                        jsonString.put("status", cv.getAsInteger("status"));

                        String[] param = {Jenjobs.SUBSCRIPTION_URL+"?access-token="+accessToken, jsonString.toString()};
                        PostRequest postRequest = new PostRequest();
                        postRequest.setResultListener(new PostRequest.ResultListener() {
                            @Override
                            public void processResult(JSONObject result) {
                                Log.e("subscribed2?", ""+result);
                            }
                        });
                        postRequest.execute(param);
                    } catch (JSONException e) {
                        Log.e("error", e.getMessage());
                    }
                }else{
                    Toast.makeText(context, R.string.offline_notification, Toast.LENGTH_LONG).show();
                }
            }
        });

        sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( isOnline ){
                    smsCb.setChecked( !smsCb.isChecked() );

                    ContentValues cv = new ContentValues();
                    cv.put("status", smsCb.isChecked() ? 1 : 0);
                    tableSubscription.updateSubscription(cv, TableSubscription.SMS_JOB_ALERT);

                    JSONObject jsonString = new JSONObject();
                    try {
                        jsonString.put("subscription_id", TableSubscription.SMS_JOB_ALERT);
                        jsonString.put("status", cv.getAsInteger("status"));

                        String[] param = {Jenjobs.SUBSCRIPTION_URL+"?access-token="+accessToken, jsonString.toString()};
                        PostRequest postRequest = new PostRequest();
                        postRequest.setResultListener(new PostRequest.ResultListener() {
                            @Override
                            public void processResult(JSONObject result) {
                                Log.e("subscribed2?", ""+result);
                            }
                        });
                        postRequest.execute(param);
                    } catch (JSONException e) {
                        Log.e("error", e.getMessage());
                    }
                }else{
                    Toast.makeText(context, R.string.offline_notification, Toast.LENGTH_LONG).show();
                }
            }
        });


        updatePasswordContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdatePassword.class);
                context.startActivity(intent);
            }
        });
    }
}
