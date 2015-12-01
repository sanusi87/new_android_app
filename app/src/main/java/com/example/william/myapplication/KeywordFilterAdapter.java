package com.example.william.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class KeywordFilterAdapter extends BaseAdapter implements ListAdapter {
    private Context context;
    private ArrayList<KeywordFilter> keywordFilters = new ArrayList<KeywordFilter>();

    public KeywordFilterAdapter(Context context){
        this.context = context;
        keywordFilters.add(new KeywordFilter(1,"Position Title"));
        keywordFilters.add(new KeywordFilter(2,"Company Name"));
        keywordFilters.add(new KeywordFilter(3, "Skills"));
        keywordFilters.add(new KeywordFilter(4, "Job Description"));
    }

    @Override
    public int getCount() {
        return keywordFilters.size();
    }

    @Override
    public Object getItem(int position) {
        return keywordFilters.get(position);
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
            v = vi.inflate(R.layout.spinner_item, parent, false);
        }

        KeywordFilter k = (KeywordFilter) getItem(position);

        TextView tvName = (TextView) v.findViewById(R.id.spinner_item);
        tvName.setText(k.name);

        return v;
    }
}
