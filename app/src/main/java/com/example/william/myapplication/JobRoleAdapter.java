package com.example.william.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class JobRoleAdapter extends BaseAdapter implements ListAdapter{

    public ArrayList<JobRole> jobrole = new ArrayList<>();
    private Context context;

    public JobRoleAdapter(Context context){
        this.context = context;

        // populate job role
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
            v = vi.inflate(android.R.layout.simple_list_item_multiple_choice, parent, false);
        }
        JobRole c = (JobRole) getItem(position);
        TextView tvName = (TextView) v.findViewById(android.R.id.text1);
        tvName.setText(c.name);
        tvName.setTextColor(context.getResources().getColor(R.color.primary_material_dark));
        v.setBackgroundColor(context.getResources().getColor(R.color.white));

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
}
