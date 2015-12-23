package com.example.william.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;

public class SelectGraduationYear extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_graduation_year);

        final ListView lv = (ListView) findViewById(R.id.listView);
        ArrayList<Integer> years = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        for( int y = currentYear; y > currentYear-50; y-- ){
            years.add(y);
        }
        lv.setAdapter(new ArrayAdapter<Integer>(this, android.R.layout.simple_list_item_1, years));

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String values = String.valueOf(lv.getSelectedItem());
                Intent intent = new Intent();
                intent.putExtra("year", values);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }
}
