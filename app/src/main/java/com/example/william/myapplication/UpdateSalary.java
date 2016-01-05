package com.example.william.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class UpdateSalary extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_salary);

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

    }
}
