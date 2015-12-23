package com.example.william.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class SelectEducationField extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_education_field);

        ListView lv = (ListView) findViewById(R.id.listView);
        final EducationFieldAdapter elf = new EducationFieldAdapter(this);
        elf.setSingle(true);
        lv.setAdapter(elf);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EducationField values = (EducationField) elf.getItem(position);
                Intent intent = new Intent();
                intent.putExtra("edufield", values);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }
}
