package com.example.william.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

public class UpdateAdditionalInfo extends Activity {

    SharedPreferences sharedPref;
    TableProfile tProfile;
    boolean isOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_additional_info);
        setTitle(getText(R.string.update_additional_info));
        isOnline = Jenjobs.isOnline(getApplicationContext());

        sharedPref = this.getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        tProfile = new TableProfile(getApplicationContext());
        //final Profile myProfile = tProfile.getProfile();

        Button okButton = (Button)findViewById(R.id.okButton);
        Button cancelButton = (Button)findViewById(R.id.cancelButton);

        final EditText info = (EditText)findViewById(R.id.theInfo);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( isOnline ){
                    String theInfo = info.getText().toString();
                    ContentValues cv = new ContentValues();
                    cv.put("additional_info", theInfo);
                    tProfile.updateProfile(cv, sharedPref.getInt("js_profile_id", 0));

                    String accessToken = sharedPref.getString("access_token", null);
                    String url = Jenjobs.ADDITIONAL_INFO+"?access-token="+accessToken;
                    String[] param = {url,theInfo};

                    // post
                    PostRequest p = new PostRequest();
                    p.setResultListener(new PostRequest.ResultListener() {
                        @Override
                        public void processResult(JSONObject success) {
                            if( success != null ){
                                Log.e("success", success.toString());
                            }
                        }
                    });
                    p.execute(param);

                    // finish the job
                    Intent intent = new Intent();
                    intent.putExtra("info", theInfo);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // In order to not be too narrow, set the window size based on the screen resolution:
        final int screen_width = getResources().getDisplayMetrics().widthPixels;
        final int new_window_width = screen_width * 90 / 100;
        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.width = Math.max(layout.width, new_window_width);
        getWindow().setAttributes(layout);
    }
}
