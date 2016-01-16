package com.example.william.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class UpdateSalary extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_salary);
        setTitle(getText(R.string.expected_salary));

        CurrencyAdapter ca = new CurrencyAdapter(this);
        final Spinner currenciesSpinner = (Spinner)findViewById(R.id.currency);
        currenciesSpinner.setAdapter(ca);
        final EditText salaryInput = (EditText)findViewById(R.id.salary);

        if( getIntent() != null ){
            Bundle extra = getIntent().getExtras();
            String salary = extra.getString("salary");
            salaryInput.setText(salary);

            MyCurrency c = (MyCurrency) extra.get("currency");
            if( c != null ){
                currenciesSpinner.setSelection( ca.findPosition(c.id) );
            }
        }

        Button save = (Button)findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyCurrency c = (MyCurrency) currenciesSpinner.getSelectedItem();
                Intent intent = new Intent();
                intent.putExtra("the_text", salaryInput.getText().toString());
                intent.putExtra("the_currency", c);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        Button cancelBuuton = (Button)findViewById(R.id.cancelButton);
        cancelBuuton.setOnClickListener(new View.OnClickListener() {
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
