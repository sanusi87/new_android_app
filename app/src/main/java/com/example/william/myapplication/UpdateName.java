package com.example.william.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class UpdateName extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_name);

        Bundle extra = getIntent().getExtras();
        final String url = extra.getString("url");
        final String jsonString = extra.getString("json");

        final EditText t = (EditText)findViewById(R.id.theText);

        Button okButton = (Button)findViewById(R.id.okButton);
        Button cancelButton = (Button)findViewById(R.id.cancelButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String theSkill = t.getText().toString();

                // insert
                //ContentValues cv = new ContentValues();
                //cv.put("name", theSkill);
                //tSkill.addSkill(cv);

                // post
                //AsyncTask updateTask = new UpdateTask(newId);
                //updateTask.execute(new String[]{theSkill});

                new PostInput().execute(new String[]{url, jsonString});

                // finish the job
                Intent intent = new Intent();
                intent.putExtra("skill_name", theSkill);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }
}
