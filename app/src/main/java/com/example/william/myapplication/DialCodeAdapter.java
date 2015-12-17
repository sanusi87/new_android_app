package com.example.william.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class DialCodeAdapter extends BaseAdapter implements ListAdapter{

    public ArrayList<DialCode> dc = new ArrayList<>();
    private Context context;

    public DialCodeAdapter( Context context ){
        this.context = context;

        HashMap<String, String> dialCodes = Jenjobs.getDialCode();
        Iterator i = dialCodes.entrySet().iterator();
        while( i.hasNext() ){
            HashMap.Entry e = (HashMap.Entry)i.next();
            dc.add(new DialCode( String.valueOf(e.getKey()), String.valueOf(e.getValue()) ));
        }

    }

    @Override
    public int getCount() {
        return dc.size();
    }

    @Override
    public DialCode getItem(int position) {
        return dc.get(position);
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
            v = vi.inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        DialCode c = (DialCode) getItem(position);
        TextView tvName = (TextView) v.findViewById(android.R.id.text1);
        tvName.setText(c.name);
        tvName.setTextColor(context.getResources().getColor(R.color.primary_material_dark));
        v.setBackgroundColor(context.getResources().getColor(R.color.white));

        return v;
    }
}
