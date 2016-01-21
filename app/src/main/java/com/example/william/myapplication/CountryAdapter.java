package com.example.william.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class CountryAdapter extends BaseAdapter implements ListAdapter {

    public ArrayList<Country> country = new ArrayList<>();
    private Context context;
    private boolean layoutSingle = false;

    public CountryAdapter(Context context){
        this.context = context;

        //HashMap<Integer, String> countries = Jenjobs.getCountry();
        TableCountry tableCountry = new TableCountry(context);
        ArrayList<Country> tempArr = tableCountry.getCountries();

        //Iterator i = countries.entrySet().iterator();
        //while( i.hasNext() ){
        //    HashMap.Entry e = (HashMap.Entry)i.next();
        //    tempArr.add(new Country( (int)e.getKey(), String.valueOf(e.getValue()) ));
        //}

        Collections.sort(tempArr, new Comparator<Country>() {
            @Override
            public int compare(Country lhs, Country rhs) {
                String[] t = {lhs.name,rhs.name};
                 Arrays.sort(t);
                if( t[0] == lhs.name ){
                    return -1;
                }else{
                    return 1;
                }
            }
        });

        country = tempArr;
    }

    @Override
    public int getCount() {
        return country.size();
    }

    @Override
    public Object getItem(int position) {
        return country.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            if( layoutSingle ){
                v = vi.inflate(android.R.layout.simple_list_item_1, parent, false);
            }else{
                v = vi.inflate(android.R.layout.simple_list_item_multiple_choice, parent, false);
            }
        }
        Country c = (Country) getItem(position);
        TextView tvName = (TextView) v.findViewById(android.R.id.text1);
        tvName.setText(c.name);
        tvName.setTextColor(context.getResources().getColor(R.color.primary_material_dark));
        //v.setBackgroundColor(context.getResources().getColor(R.color.white));

        return v;
    }

    public void setLayoutSingle(boolean layoutSingle) {
        this.layoutSingle = layoutSingle;
    }

    public void removeMalaysia(){
        for(int i=0;i<country.size();i++){
            Country _country = country.get(i);
            if( _country.id == 127 ){
                country.remove(i);
                break;
            }
        }
    }
}
