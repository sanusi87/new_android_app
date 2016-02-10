package com.example.william.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SelectJobMatcherDate extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_job_matcher_date);

        TableJobSearchMatched tableJobSearchMatched = new TableJobSearchMatched(this);
        String[] listOfDates = tableJobSearchMatched.getDateAdded();
        final ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.spinner_item, listOfDates);

        ListView lv = (ListView)findViewById(R.id.listOfDateAdded);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("date_added", adapter.getItem(position).toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
