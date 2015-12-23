package com.example.william.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class SelectJobSeekingStatus extends ActionBarActivity {

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

        Button okButton = (Button)findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray a = lv.getCheckedItemPositions();
                JobSeekingStatus values = (JobSeekingStatus) ja.getItem( a.keyAt(0) );
                Log.e("jobseekingstatus", ""+values.name);
                Intent intent = new Intent();
                intent.putExtra("jobseekingstatus", values);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        Button cancelButton = (Button)findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }
}
