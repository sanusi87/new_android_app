package com.example.william.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MatchedJobs extends ActionBarActivity {

    SharedPreferences sharedPref;
    static String accessToken = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matched_jobs);
        setTitle(R.string.matched_jobs);

        sharedPref = this.getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        accessToken = sharedPref.getString("access_token", null);

        TableJobSearchProfile tableJobSearchProfile = new TableJobSearchProfile(this);

        Bundle extras = getIntent().getExtras();
        if( extras != null ){
            int jm_profile_id = extras.getInt("jm_profile_id");
            String matched_on = extras.getString("matched_on");
            Log.e("jm_profile_id", ""+jm_profile_id);
            Log.e("matched_on", ""+matched_on);

            MatchedJobsAdapter matchedJobsAdapter = new MatchedJobsAdapter(this, jm_profile_id);
            matchedJobsAdapter.setAccessToken(accessToken);

            ListView lv = (ListView)findViewById(R.id.listOfMatchedJobs);
            lv.setAdapter(matchedJobsAdapter);

            Cursor c = tableJobSearchProfile.getSearchProfileByJobMatcherId(jm_profile_id);
            c.moveToFirst();
            String profileName = c.getString(c.getColumnIndex("profile_name"));
            c.close();

            ((TextView)findViewById(R.id.profileName)).setText(profileName);
            ((TextView)findViewById(R.id.matchedOn)).setText(matched_on);
        }else{
            Toast.makeText(getApplicationContext(), R.string.no_job_search_profile, Toast.LENGTH_LONG).show();
            finish();
        }
    }

}
