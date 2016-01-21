package com.example.william.myapplication;

import android.content.Context;
import android.database.Cursor;
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

public class JobSpecAdapter extends BaseAdapter implements ListAdapter{

    public ArrayList<JobSpec> jobspec = new ArrayList<>();
    private Context context;
    private boolean single = false;

    public JobSpecAdapter(Context context){
        this.context = context;

        // TODO: populate jobspec
        ArrayList<JobSpec> tempArr = new ArrayList<>();
        TableJobSpec tableJobSpec = new TableJobSpec(context);
        Cursor c = tableJobSpec.getAllJobSpec();
        if( c.moveToFirst() ){
            for( int i=0;i<c.getCount();i++ ){
                tempArr.add(new JobSpec(c.getInt(0), c.getString(1)));
                c.moveToNext();
            }
        }else{
            // no job spec found, so download
        }

        Collections.sort(tempArr, new Comparator<JobSpec>() {
            @Override
            public int compare(JobSpec lhs, JobSpec rhs) {
                String[] t = {lhs.name, rhs.name};
                Arrays.sort(t);
                if (t[0].equals(lhs.name)) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        jobspec = tempArr;
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
            if( this.single ){
                v = vi.inflate(android.R.layout.simple_list_item_1, parent, false);
            }else{
                v = vi.inflate(android.R.layout.simple_list_item_multiple_choice, parent, false);
            }
        }
        JobSpec c = (JobSpec) getItem(position);
        TextView tvName = (TextView) v.findViewById(android.R.id.text1);
        tvName.setText(c.name);
        tvName.setTextColor(context.getResources().getColor(R.color.primary_material_dark));
        //v.setBackgroundColor(context.getResources().getColor(R.color.white));

        return v;
    }

    public int findPosition( int theCode ){
        int index = 0;
        for(int i=0;i< this.jobspec.size();i++){
            if( this.jobspec.get(i).id == theCode ){
                index = i;
                break;
            }
        }
        return index;
    }

    public void setSingle( boolean single ){
        this.single = single;
    }
}
