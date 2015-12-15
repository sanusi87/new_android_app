package com.example.william.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ResumeVisibilityAdapter extends BaseAdapter implements ListAdapter{
    private Context context;
    public String[] visibility = {"Open","Limited","Hidden"};
    public String[] visibilityDesc = {"Open Desc","Limited Desc","Hidden Desc"};

    private ArrayList<String> vis = new ArrayList<>();

    public ResumeVisibilityAdapter(Context context) {
        this.context = context;

        for( int i=0; i < visibility.length; i++ ){
            vis.add(visibility[i]);
        }
    }

    @Override
    public int getCount() {
        return vis.size();
    }

    @Override
    public Object getItem(int position) {
        return vis.get(position);
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
            v = vi.inflate(R.layout.resume_visibility_item, parent, false);
        }
        String visibility = (String) getItem(position);
        TextView tvName = (TextView) v.findViewById(R.id.visibility_name);
        tvName.setText(visibility);

        TextView tvDesc = (TextView) v.findViewById(R.id.visibility_desc);
        tvDesc.setText(visibilityDesc[position]);

        return v;
    }
}
