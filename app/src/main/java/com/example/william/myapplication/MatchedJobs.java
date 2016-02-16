package com.example.william.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MatchedJobs extends ActionBarActivity {

    SharedPreferences sharedPref;
    static String accessToken = null;
    Context context;

    private static final int SELECT_MATCHED_DATE = 1;
    private static final int SELECT_PROFILE = 2;

    TextView tMatchedOn;
    TextView tProfileName;
    MatchedJobsAdapter matchedJobsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matched_jobs);
        setTitle(R.string.matched_jobs);

        context = getApplicationContext();

        sharedPref = this.getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        accessToken = sharedPref.getString("access_token", null);

        TableJobSearchProfile tableJobSearchProfile = new TableJobSearchProfile(this);

        tMatchedOn = (TextView)findViewById(R.id.matchedOn);
        tProfileName = (TextView)findViewById(R.id.profileName);

        Bundle extras = getIntent().getExtras();
        if( extras != null ){
            int jm_profile_id = extras.getInt("jm_profile_id");
            String matched_on = extras.getString("matched_on");
            //Log.e("jm_profile_id", ""+jm_profile_id);
            //Log.e("matched_on", ""+matched_on);

            matchedJobsAdapter = new MatchedJobsAdapter(this, jm_profile_id, matched_on);
            matchedJobsAdapter.setAccessToken(accessToken);

            ListView lv = (ListView)findViewById(R.id.listOfMatchedJobs);
            lv.setAdapter(matchedJobsAdapter);

            Cursor c = tableJobSearchProfile.getSearchProfileByJobMatcherId(jm_profile_id);
            c.moveToFirst();
            String profileName = c.getString(c.getColumnIndex("profile_name"));
            c.close();

            tMatchedOn.setText(matched_on);
            tProfileName.setText(profileName);

            if( matchedJobsAdapter.getCount() == 0 ){
                findViewById(R.id.noItemContainer).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.noItemText)).setText(R.string.no_matched_jobs);
            }
        }else{
            Toast.makeText(getApplicationContext(), R.string.no_job_search_profile, Toast.LENGTH_LONG).show();
            finish();
        }

        findViewById(R.id.matchedOnContainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SelectJobMatcherDate.class);
                startActivityForResult(intent, SELECT_MATCHED_DATE);
            }
        });

        findViewById(R.id.profileNameContainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SelectJobMatcherProfile.class);
                startActivityForResult(intent, SELECT_PROFILE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_MATCHED_DATE) {
                Bundle extra = data.getExtras();
                // update selected date which has been previously assigned to adapter
                String matchedOn = extra.getString("date_added");
                matchedJobsAdapter.setMatchedOn(matchedOn);
                matchedJobsAdapter.loadItem(true);
                matchedJobsAdapter.notifyDataSetChanged();
                tMatchedOn.setText(matchedOn);

                if( matchedJobsAdapter.getCount() == 0 ){
                    findViewById(R.id.noItemContainer).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.noItemText)).setText(R.string.no_matched_jobs);
                }else{
                    findViewById(R.id.noItemContainer).setVisibility(View.GONE);
                }
            }else if( requestCode == SELECT_PROFILE ){
                Bundle extra = data.getExtras();
                int profile = extra.getInt("profile");
                String profile_name = extra.getString("profile_name");
                matchedJobsAdapter.setProfileId(profile);
                matchedJobsAdapter.loadItem(true);
                matchedJobsAdapter.notifyDataSetChanged();
                tProfileName.setText(profile_name);

                if( matchedJobsAdapter.getCount() == 0 ){
                    findViewById(R.id.noItemContainer).setVisibility(View.VISIBLE);
                    ((TextView)findViewById(R.id.noItemText)).setText(R.string.no_matched_jobs);
                }else{
                    findViewById(R.id.noItemContainer).setVisibility(View.GONE);
                }
            }
        }
    }

}
