package com.example.william.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class SelectJobIndustry extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_job_industry);

        Button okbutton = (Button)findViewById(R.id.okButton);
        Button cancelButton = (Button)findViewById(R.id.cancelButton);

        final ListView listOfJobIndustry = (ListView) findViewById(R.id.listOfJobIndustry);
        final IndustryAdapter adapter = new IndustryAdapter(this);
        listOfJobIndustry.setAdapter(adapter);

        listOfJobIndustry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("industry", (Industry) adapter.getItem(position));
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }
}
