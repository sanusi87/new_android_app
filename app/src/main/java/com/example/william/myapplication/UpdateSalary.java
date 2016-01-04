package com.example.william.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class UpdateSalary extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_salary);

        CurrencyAdapter ca = new CurrencyAdapter(this);
        final Spinner currenciesSpinner = (Spinner)findViewById(R.id.currency);
        currenciesSpinner.setAdapter(ca);

        final EditText salary = (EditText)findViewById(R.id.salary);

        Button save = (Button)findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyCurrency c = (MyCurrency) currenciesSpinner.getSelectedItem();
                //Log.e("currency", ""+c.name);
                Intent intent = new Intent();
                intent.putExtra("the_text", salary.getText().toString());
                intent.putExtra("the_currency", c);

                finish();
            }
        });

    }
}
