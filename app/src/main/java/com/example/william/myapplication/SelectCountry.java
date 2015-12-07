package com.example.william.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SelectCountry extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_country);

        final ListView lv = (ListView)findViewById(R.id.listOfCountries);
        final CountryAdapter ca = new CountryAdapter(getApplicationContext());
        lv.setAdapter(ca);

        Bundle extra = getIntent().getExtras();
        // checked selected index
        if( extra != null ){
            ArrayList<Country> selectedCountries = (ArrayList<Country>) extra.get("country");
            if( selectedCountries != null && selectedCountries.size() > 0 ){
                for(int i=0; i < selectedCountries.size();i++){
                    Country value = selectedCountries.get(i);
                    int selectedIndex = ca.country.indexOf(value); // index of the selected value
                    if( selectedIndex != -1 ){
                        lv.setItemChecked(selectedIndex, true);
                    }
                }
            }
        }

        Button okButton = (Button)findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray a = lv.getCheckedItemPositions();
                ArrayList<Country> values = new ArrayList<>();
                for (int i = 0; i < a.size(); i++) {
                    if( a.valueAt(i) && a.keyAt(i) >= 0 ){
                        Country c = (Country) ca.getItem(a.keyAt(i));
                        values.add(c);
                    }
                }

                Intent intent = new Intent();
                intent.putExtra("country", values);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

        Button cancelButton = (Button)findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }
}
