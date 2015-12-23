package com.example.william.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class SelectSingleJobSpec extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_job_spec);
        setTitle("Select Specialisation");

        final ListView lv = (ListView)findViewById(R.id.listOfJobSpec);
        final JobSpecAdapter ca = new JobSpecAdapter(getApplicationContext());
        ca.setSingle(true);
        lv.setAdapter(ca);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JobSpec spec = (JobSpec) ca.getItem(position);

                Intent intent = new Intent();
                intent.putExtra("jobspec", spec);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        Button okButton = (Button)findViewById(R.id.okButton);
        Button cancelButton = (Button)findViewById(R.id.cancelButton);

        okButton.setVisibility(View.GONE);
        cancelButton.setVisibility(View.GONE);
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
