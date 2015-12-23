package com.example.william.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class SelectJobRole extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_job_role);
        setTitle("Select Sub Specialisation");

        Bundle extra = getIntent().getExtras();
        int jobSpecId = 200;
        if( extra != null ){
            jobSpecId = extra.getInt("jobspecid");
        }

        final ListView lv = (ListView)findViewById(R.id.listOfJobRole);
        final JobRoleAdapter ca = new JobRoleAdapter(getApplicationContext(), jobSpecId);
        lv.setAdapter(ca);

        // checked selected index
        if( extra != null ){
            ArrayList<JobRole> selectedJobRoles = (ArrayList<JobRole>) extra.get("jobrole");
            if( selectedJobRoles != null && selectedJobRoles.size() > 0 ){
                for(int i=0; i < selectedJobRoles.size();i++){
                    JobRole value = selectedJobRoles.get(i);
                    int selectedIndex = ca.jobrole.indexOf(value); // index of the selected value
                    if( selectedIndex != -1 ){
                        lv.setItemChecked(selectedIndex, true);
                    }
                }
            }
        }

        Button okButton = (Button)findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray a = lv.getCheckedItemPositions();
                ArrayList<JobRole> values = new ArrayList<>();
                for (int i = 0; i < a.size(); i++) {
                    if (a.valueAt(i) && a.keyAt(i) >= 0) {
                        JobRole c = (JobRole) ca.getItem(a.keyAt(i));
                        values.add(c);
                    }
                }

                Intent intent = new Intent();
                intent.putExtra("jobrole", values);
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
