package jen.jobs.application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

public class SelectJobMatcherProfile extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_job_matcher_profile);

        ListView lv = (ListView)findViewById(R.id.listOfJobMatcherProfile);
        final JobSearchProfileAdapter2 adapter = new JobSearchProfileAdapter2(getApplicationContext());
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                String[] profile = adapter.getItem(position);
                intent.putExtra("profile", Integer.valueOf(profile[1]));
                intent.putExtra("profile_name", profile[2]);
                setResult(RESULT_OK, intent);
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
