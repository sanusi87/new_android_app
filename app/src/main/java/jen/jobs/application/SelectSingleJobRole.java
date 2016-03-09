package jen.jobs.application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class SelectSingleJobRole extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_job_role);
        setTitle("Select Sub Specialisation");

        int jobSpecId = 200;
        Bundle extra = getIntent().getExtras();
        if( extra != null ){
            jobSpecId = extra.getInt("jobspecid");
        }

        final ListView lv = (ListView)findViewById(R.id.listOfJobRole);
        final JobRoleAdapter ca = new JobRoleAdapter(getApplicationContext(), jobSpecId);
        ca.setSingle(true);
        lv.setAdapter(ca);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JobRole values = (JobRole) ca.getItem(position);

                Intent intent = new Intent();
                intent.putExtra("jobrole", values);
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
