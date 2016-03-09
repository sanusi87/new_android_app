package jen.jobs.application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class SelectJobSpec extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_job_spec);
        setTitle("Select Specialisation");

        final ListView lv = (ListView)findViewById(R.id.listOfJobSpec);
        final JobSpecAdapter ca = new JobSpecAdapter(getApplicationContext());
        lv.setAdapter(ca);

        Bundle extra = getIntent().getExtras();
        // checked selected index
        if( extra != null ){
            ArrayList selectedJobSpecs = (ArrayList) extra.get("jobspec");
            if( selectedJobSpecs != null && selectedJobSpecs.size() > 0 ){
                for(int i=0; i < selectedJobSpecs.size();i++){
                    JobSpec value = (JobSpec) selectedJobSpecs.get(i);
                    int selectedIndex = ca.jobspec.indexOf(value); // index of the selected value
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
                ArrayList<JobSpec> values = new ArrayList<>();
                // TODO: loop SparseBooleanArray
                for (int i = 0; i < a.size(); i++) {
                    if (a.valueAt(i) && a.keyAt(i) >= 0) {
                        JobSpec c = (JobSpec) ca.getItem(a.keyAt(i));
                        values.add(c);
                    }
                }

                Intent intent = new Intent();
                intent.putExtra("jobspec", values);
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

    /*
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
    */
}
