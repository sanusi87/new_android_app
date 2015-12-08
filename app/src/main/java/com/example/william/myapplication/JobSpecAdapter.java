package com.example.william.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class JobSpecAdapter extends BaseAdapter implements ListAdapter{

    public ArrayList<JobSpec> jobspec = new ArrayList<>();
    private Context context;

    public JobSpecAdapter(Context context){
        this.context = context;

        // populate jobspec

    }

    @Override
    public int getCount() {
        return jobspec.size();
    }

    @Override
    public Object getItem(int position) {
        return jobspec.get(position);
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
            v = vi.inflate(android.R.layout.simple_list_item_multiple_choice, parent, false);
        }
        JobSpec c = (JobSpec) getItem(position);
        TextView tvName = (TextView) v.findViewById(android.R.id.text1);
        tvName.setText(c.name);
        tvName.setTextColor(context.getResources().getColor(R.color.primary_material_dark));
        v.setBackgroundColor(context.getResources().getColor(R.color.white));

        return v;
    }
}