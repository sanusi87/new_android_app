package com.example.william.myapplication;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

public class JobSearchProfileForm extends ActionBarActivity {

    Bundle searchParameter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_search_filter_form);

        final CheckBox notificationAlert = (CheckBox)findViewById(R.id.notification_checkbox);
        findViewById(R.id.notification_alert).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationAlert.setChecked(!notificationAlert.isChecked());
            }
        });

        Bundle extra = getIntent().getExtras();
        if( extra != null ){
            searchParameter = extra.getBundle("searchParameter");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int clickedItem = item.getItemId();
        if( clickedItem == R.id.save ){

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
