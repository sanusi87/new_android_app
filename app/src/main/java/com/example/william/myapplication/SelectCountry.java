package com.example.william.myapplication;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;

public class SelectCountry extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_country);

        final ListView lv = (ListView)findViewById(R.id.listOfCountries);
        final CountryAdapter ca = new CountryAdapter(getApplicationContext());

        Bundle extra = getIntent().getExtras();
        Boolean single = false;
        // checked selected index
        if( extra != null ){
            single = (Boolean) extra.get("single");
            if( single ){
                ca.setLayoutSingle(true);
                ((ViewGroup)lv.getParent()).getChildAt(1).setVisibility(View.GONE);
            }

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
        lv.setAdapter(ca);

        if( single ) {
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Country country = (Country) ca.getItem(position);
                    Log.e("selected", ""+country.name);
                    Log.e("selected", ""+country.id);

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }
}
