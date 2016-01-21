package com.example.william.myapplication;

import android.content.Context;
import android.database.Cursor;
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

public class JobRoleAdapter extends BaseAdapter implements ListAdapter{

    public ArrayList<JobRole> jobrole = new ArrayList<>();
    private Context context;
    private boolean single = false;

    public JobRoleAdapter(Context context, int jobSpecId){
        this.context = context;

        // populate job role
        TableJobRole tableJobRole = new TableJobRole(context);
        Cursor c = tableJobRole.getAllJobRole(jobSpecId);
        if( c.moveToFirst() ){
            ArrayList<JobRole> tempArr = new ArrayList<>();
            while( !c.isAfterLast() ){
                tempArr.add(new JobRole(c.getInt(0), c.getInt(1), c.getString(2)));
                c.moveToNext();
            }

            Collections.sort(tempArr, new Comparator<JobRole>() {
                @Override
                public int compare(JobRole lhs, JobRole rhs) {
                    String[] t = {lhs.name, rhs.name};
                    Arrays.sort(t);
                    if (t[0].equals(lhs.name)) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            });

            jobrole = tempArr;
        }
    }

    @Override
    public int getCount() {
        return jobrole.size();
    }

    @Override
    public Object getItem(int position) {
        return jobrole.get(position);
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
        JobRole c = (JobRole) getItem(position);
        TextView tvName = (TextView) v.findViewById(android.R.id.text1);
        tvName.setText(c.name);
        tvName.setTextColor(context.getResources().getColor(R.color.primary_material_dark));
        //v.setBackgroundColor(context.getResources().getColor(R.color.white));

        return v;
    }

    public int findPosition( int theCode ){
        int index = 0;
        for(int i=0;i< this.jobrole.size();i++){
            if( this.jobrole.get(i).id == theCode ){
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
