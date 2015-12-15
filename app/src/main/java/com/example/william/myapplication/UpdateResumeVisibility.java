package com.example.william.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class UpdateResumeVisibility extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_resume_visibility);

        ListView listOFResumeVisibility = (ListView) findViewById(R.id.listOfResumeVisibility);
        final ResumeVisibilityAdapter rvAdapter = new ResumeVisibilityAdapter(getApplicationContext());
        listOFResumeVisibility.setAdapter(rvAdapter);

        listOFResumeVisibility.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                Log.e("selectedvisibility", ""+position);
                intent.putExtra("selectedvisibility", rvAdapter.visibility[position]);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

}
