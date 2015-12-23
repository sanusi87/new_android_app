package com.example.william.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class SelectEducationLevel extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_education_level);
        setTitle("Select Education Level");

        boolean single = true;

        ListView lv = (ListView) findViewById(R.id.listView);
        final EducationLevelAdapter ela = new EducationLevelAdapter(this);
        ela.setSingle(single);
        lv.setAdapter(ela);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EducationLevel values = (EducationLevel) ela.getItem(position);
                Intent intent = new Intent();
                intent.putExtra("edulevel", values);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        Button okButton = (Button)findViewById(R.id.okButton);
        Button cancelButton = (Button)findViewById(R.id.cancelButton);

        if( single ){
            okButton.setVisibility(View.GONE);
            cancelButton.setVisibility(View.GONE);
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
