package com.example.william.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class SelectJobRole extends ActionBarActivity {
    int viewIndex = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_job_role);
        setTitle("Select Sub Specialisation");

        Bundle extra = getIntent().getExtras();
        int jobSpecId = 200;
        if( extra != null ){
            jobSpecId = extra.getInt("jobspecid");
            viewIndex = extra.getInt("jsViewIndex");
        }

        final ListView lv = (ListView)findViewById(R.id.listOfJobRole);
        final JobRoleAdapter ca = new JobRoleAdapter(getApplicationContext(), jobSpecId);
        lv.setAdapter(ca);

        // checked selected index
        if( extra != null ){
            ArrayList selectedJobRoles = (ArrayList) extra.get("jobrole");
            if( selectedJobRoles != null && selectedJobRoles.size() > 0 ){
                for(int i=0; i < selectedJobRoles.size();i++){
                    JobRole value = (JobRole) selectedJobRoles.get(i);
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
                intent.putExtra("viewIndex", viewIndex);
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
