package com.example.william.myapplication;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;

public class JobSearchProfile extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_search_profile);

        ListView lv = (ListView) findViewById(R.id.listOfSearchProfile);
        final JobSearchProfileAdapter jobSearchProfileAdapter = new JobSearchProfileAdapter(getApplicationContext());
        lv.setAdapter(jobSearchProfileAdapter);
    }
}
