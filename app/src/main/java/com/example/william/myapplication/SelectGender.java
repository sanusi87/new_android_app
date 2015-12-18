package com.example.william.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SelectGender extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_gender);

        ListView lv = (ListView)findViewById(R.id.listOfGender);
        final String[] genders = {"Male", "Female"};
        lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, genders));

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("gender", genders[position]);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }
}
