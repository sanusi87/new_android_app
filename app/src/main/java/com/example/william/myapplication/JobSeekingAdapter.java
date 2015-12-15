package com.example.william.myapplication;

import android.content.Context;
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
import java.util.HashMap;
import java.util.Iterator;

public class JobSeekingAdapter extends BaseAdapter implements ListAdapter{

    public ArrayList<JobSeekingStatus> statuses = new ArrayList<>();
    private Context context;

    public JobSeekingAdapter( Context context ){
        this.context = context;

        HashMap<Integer, String> fields = Jenjobs.getJobSeekingStatus();
        ArrayList<JobSeekingStatus> tempArr = new ArrayList<>();

        Iterator i = fields.entrySet().iterator();
        while( i.hasNext() ){
            HashMap.Entry e = (HashMap.Entry)i.next();
            tempArr.add(new JobSeekingStatus( (int)e.getKey(), String.valueOf(e.getValue()) ));
        }

        Collections.sort(tempArr, new Comparator<JobSeekingStatus>() {
            @Override
            public int compare(JobSeekingStatus lhs, JobSeekingStatus rhs) {
                String[] t = {lhs.name, rhs.name};
                Arrays.sort(t);
                if (t[0].equals(lhs.name)) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        statuses = tempArr;
    }

    @Override
    public int getCount() {
        return statuses.size();
    }

    @Override
    public Object getItem(int position) {
        return statuses.get(position);
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
        JobSeekingStatus c = (JobSeekingStatus) getItem(position);
        TextView tvName = (TextView) v.findViewById(android.R.id.text1);
        tvName.setText(c.name);
        tvName.setTextColor(context.getResources().getColor(R.color.primary_material_dark));
        v.setBackgroundColor(context.getResources().getColor(R.color.white));

        return v;
    }
}
