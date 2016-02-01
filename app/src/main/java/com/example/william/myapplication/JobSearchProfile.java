package com.example.william.myapplication;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class JobSearchProfile extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_search_profile);

        ListView lv = (ListView) findViewById(R.id.listOfSearchProfile);
        JobSearchProfileAdapter jobSearchProfileAdapter = new JobSearchProfileAdapter(getApplicationContext());
        jobSearchProfileAdapter.setActivity(this);
        lv.setAdapter(jobSearchProfileAdapter);

        if( jobSearchProfileAdapter.getCount() == 0 ){
            LinearLayout ll = (LinearLayout)findViewById(R.id.no_item);
            ll.setVisibility(View.VISIBLE);
            ((TextView)ll.findViewById(R.id.noticeText)).setText(R.string.no_job_search_profile);
        }
    }
}
