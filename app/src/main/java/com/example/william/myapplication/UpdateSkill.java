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

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateSkill extends Activity {

    SharedPreferences sharedPref;
    TableSkill tSkill;

    boolean isOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_skill);
        setTitle(getText(R.string.skill));

        isOnline = Jenjobs.isOnline(getApplicationContext());

        sharedPref = this.getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        tSkill = new TableSkill(getApplicationContext());

        Button okButton = (Button)findViewById(R.id.okButton);
        Button cancelButton = (Button)findViewById(R.id.cancelButton);

        final EditText skill = (EditText)findViewById(R.id.skill);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( isOnline ){
                    String theSkill = skill.getText().toString();

                    // insert
                    ContentValues cv = new ContentValues();
                    cv.put("name", theSkill);
                    final int newId = tSkill.addSkill(cv).intValue();

                    // post
                    String accessToken = sharedPref.getString("access_token", null);
                    String[] s = {Jenjobs.SKILL_URL+"?access-token="+accessToken,theSkill};

                    PostRequest p = new PostRequest();
                    p.setResultListener(new PostRequest.ResultListener() {
                        @Override
                        public void processResult(JSONObject success) {
                            if( success != null ){
                                try {
                                    //int status_code = (Integer) success.get("status_code");
                                    ContentValues cv = new ContentValues();
                                    cv.put("_id", (Integer) success.get("id"));
                                    tSkill.updateSkill(cv, newId);
                                } catch (JSONException e) {
                                    Log.e("JSONException", e.getMessage());
                                }
                            }
                        }
                    });
                    p.execute(s);

                    // finish the job
                    Intent intent = new Intent();
                    intent.putExtra("skill_name", theSkill);
                    intent.putExtra("skill_id", newId);
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
