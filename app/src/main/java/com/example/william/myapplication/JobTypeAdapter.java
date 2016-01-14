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

public class JobTypeAdapter extends BaseAdapter implements ListAdapter{
    public ArrayList<JobType> jobtype = new ArrayList<>();
    private Context context;
    private boolean single = false;

    public JobTypeAdapter(Context context){
        this.context = context;

        HashMap fields = Jenjobs.getJobType();
        //ArrayList<JobSeekingStatus> tempArr = new ArrayList<>();

        Iterator i = fields.entrySet().iterator();
        while( i.hasNext() ){
            HashMap.Entry e = (HashMap.Entry)i.next();
            //tempArr.add(new JobSeekingStatus( (int)e.getKey(), String.valueOf(e.getValue()) ));
            jobtype.add(new JobType( (int)e.getKey(), String.valueOf(e.getValue()) ));
        }
    }

    @Override
    public int getCount() {
        return jobtype.size();
    }

    @Override
    public Object getItem(int position) {
        return jobtype.get(position);
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
        JobType c = (JobType) getItem(position);
        TextView tvName = (TextView) v.findViewById(android.R.id.text1);
        tvName.setText(c.name);
        tvName.setTextColor(context.getResources().getColor(R.color.primary_material_dark));
        //v.setBackgroundColor(context.getResources().getColor(R.color.light_gray));

        return v;
    }

    public void setSingle( boolean inflateSingle ){
        this.single = inflateSingle;
    }

    public int findPosition( int theCode ){
        int index = 0;
        for(int i=0;i< this.jobtype.size();i++){
            if( this.jobtype.get(i).id == theCode ){
                index = i;
                break;
            }
        }
        return index;
    }
}
