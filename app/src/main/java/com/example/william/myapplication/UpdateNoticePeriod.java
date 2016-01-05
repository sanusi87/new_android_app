package com.example.william.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class UpdateNoticePeriod extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_notice_period);

        final EditText availability = (EditText)findViewById(R.id.availability);
        final Spinner availabilityUnit = (Spinner)findViewById(R.id.availabilityUnit);
        Button okButton = (Button)findViewById(R.id.okButton);
        Button cancelButton = (Button)findViewById(R.id.cancelButton);

        String[] units = getResources().getStringArray(R.array.availability_unit);
        availabilityUnit.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, units));

        if( getIntent() != null ){
            Bundle extra = getIntent().getExtras();
            if( extra != null ){
                String _availability = extra.getString("availability");
                String _availabilityUnit = extra.getString("availabilityUnit");

                availability.setText(_availability);

                for(int i=0;i<units.length;i++){
                    if( _availabilityUnit != null && _availabilityUnit.equals( units[i] ) ){
                        availabilityUnit.setSelection( i );
                    }
                }
            }
        }

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredAvailability = availability.getText().toString();
                String selectedAvailabilityUnit = availabilityUnit.getSelectedItem().toString();

                ArrayList<String> errors = new ArrayList<>();
                if( enteredAvailability.length() == 0 ){
                    errors.add("Availability value is required!");
                }

                if( errors.size() == 0 ){
                    Intent intent = new Intent();
                    intent.putExtra("availability", enteredAvailability);
                    intent.putExtra("availabilityUnit", selectedAvailabilityUnit);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), TextUtils.join(", ", errors), Toast.LENGTH_SHORT).show();
                }
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
