package com.example.william.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class SelectCountry extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_country);
        setTitle("Select Country");

        final ListView lv = (ListView)findViewById(R.id.listOfCountries);
        final CountryAdapter ca = new CountryAdapter(getApplicationContext());

        Bundle extra = getIntent().getExtras();
        boolean single = false;
        boolean removeMalaysia = false;

        // checked selected index
        if( extra != null ){
            single = extra.getBoolean("single");
            removeMalaysia = extra.getBoolean("remove_malaysia");

            if( single ){
                ca.setLayoutSingle(true);
                ((ViewGroup)lv.getParent()).getChildAt(1).setVisibility(View.GONE);
            }

            if( removeMalaysia ){
                ca.removeMalaysia();
            }

            lv.setAdapter(ca);

            ArrayList selectedCountries = (ArrayList) extra.get("country");
            if( selectedCountries != null && selectedCountries.size() > 0 ){
                for(int i=0; i < selectedCountries.size();i++){
                    Country value = (Country) selectedCountries.get(i);
                    int selectedIndex = ca.country.indexOf(value); // index of the selected value
                    if( selectedIndex != -1 ){
                        lv.setItemChecked(selectedIndex, true);
                    }
                }
            }
        }else{
            lv.setAdapter(ca);
        }

        if( single ) {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Country country = (Country) ca.getItem(position);
                    Intent intent = new Intent();
                    intent.putExtra("country", country);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            });
        }else{
            Button okButton = (Button) findViewById(R.id.okButton);
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SparseBooleanArray a = lv.getCheckedItemPositions();
                    ArrayList<Country> values = new ArrayList<>();
                    for (int i = 0; i < a.size(); i++) {
                        if (a.valueAt(i) && a.keyAt(i) >= 0) {
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

            Button cancelButton = (Button) findViewById(R.id.cancelButton);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(Activity.RESULT_CANCELED);
                    finish();
                }
            });
        }
    }
}
