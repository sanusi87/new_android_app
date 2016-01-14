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
import java.util.Map;

public class JobLevelAdapter extends BaseAdapter implements ListAdapter{
    public ArrayList<JobLevel> joblevel = new ArrayList<>();
    private Context context;
    private boolean single = false;

    public JobLevelAdapter(Context context){
        this.context = context;

        HashMap fields = Jenjobs.getJobLevel();
        for (Object o : fields.entrySet()) {
            HashMap.Entry e = (HashMap.Entry) o;
            joblevel.add(new JobLevel((int) e.getKey(), String.valueOf(e.getValue())));
        }
    }

    @Override
    public int getCount() {
        return joblevel.size();
    }

    @Override
    public Object getItem(int position) {
        return joblevel.get(position);
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
            if( this.single ){
                v = vi.inflate(android.R.layout.simple_list_item_1, parent, false);
            }else{
                v = vi.inflate(android.R.layout.simple_list_item_multiple_choice, parent, false);
            }
        }
        JobLevel c = (JobLevel) getItem(position);
        TextView tvName = (TextView) v.findViewById(android.R.id.text1);
        tvName.setText(c.name);
        tvName.setTextColor(context.getResources().getColor(R.color.primary_material_dark));
        //v.setBackgroundColor(context.getResources().getColor(R.color.white));

        return v;
    }

    public void setSingle(boolean single) {
        this.single = single;
    }

    public int findPosition( int theCode ){
        int index = 0;
        for(int i=0;i< this.joblevel.size();i++){
            if( this.joblevel.get(i).id == theCode ){
                index = i;
                break;
            }
        }
        return index;
    }
}
