package com.example.william.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class SelectJobSeekingStatus extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_job_seeking_status);
        setTitle("Select Jobseeking Status");

        final ListView lv = (ListView) findViewById(R.id.listOfJobSeekingStatus);
        final JobSeekingAdapter ja = new JobSeekingAdapter(this);
        lv.setAdapter(ja);

        Bundle extra = getIntent().getExtras();
        // checked selected index
        if( extra != null ){
            JobSeekingStatus value = (JobSeekingStatus) extra.get("jobSeekingStatus");
            int selectedIndex = ja.statuses.indexOf(value); // index of the selected value
            if( selectedIndex != -1 ){
                lv.setItemChecked(selectedIndex, true);
            }
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JobSeekingStatus values = (JobSeekingStatus) ja.getItem(position);
                //Log.e("jobseekingstatus", ""+values.name);
                Intent intent = new Intent();
                intent.putExtra("jobseekingstatus", values);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }
}
