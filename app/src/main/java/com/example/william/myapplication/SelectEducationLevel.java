package com.example.william.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class SelectEducationLevel extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_education_level);

        ListView lv = (ListView) findViewById(R.id.listView);
        final EducationLevelAdapter ela = new EducationLevelAdapter(this);
        ela.setSingle(true);
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
    }
}
