package com.example.william.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class SelectJobType extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_job_type);
        setTitle("Select Job Type");

        Button okbutton = (Button)findViewById(R.id.okButton);
        Button cancelButton = (Button)findViewById(R.id.cancelButton);

        final ListView listOfJobType = (ListView) findViewById(R.id.listOfJobType);
        final JobTypeAdapter jta = new JobTypeAdapter(this);
        boolean single = true;

        if( getIntent() != null ){
            Bundle extra = getIntent().getExtras();
            single = extra.getBoolean("single");
            jta.setSingle(single);
            listOfJobType.setAdapter(jta);
            if( single ){
                ((ViewGroup)okbutton.getParent()).setVisibility(View.GONE);
            }

            ArrayList selectedJobType = (ArrayList) extra.get("jobtype");
            if( selectedJobType != null && selectedJobType.size() > 0 ){
                for( int i=0;i<selectedJobType.size();i++ ){
                    JobType jobType = (JobType)selectedJobType.get(i);
                    int selectedIndex = jta.jobtype.indexOf(jobType); // index of the selected value
                    if( selectedIndex != -1 ){
                        listOfJobType.setItemChecked(selectedIndex, true);
                    }
                }
            }
        }else{
            listOfJobType.setAdapter( jta );
        }

        if( single ){
            listOfJobType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent();
                    intent.putExtra("jobType", (JobType)jta.getItem(position));

                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            });
        }else{
            okbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();

                    SparseBooleanArray a = listOfJobType.getCheckedItemPositions();

                    if( a.size() > 0 ){
                        ArrayList<JobType> values = new ArrayList<>();
                        for (int i = 0; i < a.size(); i++) {
                            if (a.valueAt(i) && a.keyAt(i) >= 0) {
                                JobType c = (JobType) jta.getItem(a.keyAt(i));
                                values.add(c);
                            }
                        }

                        intent.putExtra("jobType", values);
                        setResult(Activity.RESULT_OK, intent);
                    }

                    finish();
                }
            });

            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
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
