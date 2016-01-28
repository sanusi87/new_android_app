package com.example.william.myapplication;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.ListView;

public class JobSearchProfile extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_search_profile);

        ListView lv = (ListView) findViewById(R.id.listOfSearchProfile);
        JobSearchProfileAdapter jobSearchProfileAdapter = new JobSearchProfileAdapter(getApplicationContext());
        lv.setAdapter(jobSearchProfileAdapter);
    }
}
