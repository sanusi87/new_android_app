package com.example.william.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

public class SelectJobType extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_job_type);

        final JobTypeAdapter jta = new JobTypeAdapter(this);
        boolean single = true;

        if( getIntent() != null ){
            Bundle extra = getIntent().getExtras();
            single = extra.getBoolean("single");
        }
        Log.e("single?", ""+single);
        if( single ){
            jta.setSingle(single);
        }

        ListView listOfJobType = (ListView) findViewById(R.id.listOfJobType);
        listOfJobType.setAdapter( jta );

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
