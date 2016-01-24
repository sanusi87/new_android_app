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

public class EducationLevelAdapter extends BaseAdapter implements ListAdapter{

    public ArrayList<EducationLevel> eduLevel = new ArrayList<>();
    private Context context;
    private boolean single = false;

    public EducationLevelAdapter( Context context ){
        this.context = context;

        HashMap<Integer, String> levels = Jenjobs.getEducationLevel();
        ArrayList<EducationLevel> tempArr = new ArrayList<>();

        Iterator i = levels.entrySet().iterator();
        while( i.hasNext() ){
            HashMap.Entry e = (HashMap.Entry)i.next();
            tempArr.add(new EducationLevel( (int)e.getKey(), String.valueOf(e.getValue()) ));
        }

        Collections.sort(tempArr, new Comparator<EducationLevel>() {
            @Override
            public int compare(EducationLevel lhs, EducationLevel rhs) {
                String[] t = {lhs.name, rhs.name};
                Arrays.sort(t);
                if (t[0] == lhs.name) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        eduLevel = tempArr;
    }

    @Override
    public int getCount() {
        return eduLevel.size();
    }

    @Override
    public Object getItem(int position) {
        return eduLevel.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        TextView tvName = null;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            if( this.single ){
                v = vi.inflate(R.layout.spinner_item, parent, false);
                tvName = (TextView) v.findViewById(R.id.spinner_item);
            }else{
                v = vi.inflate(android.R.layout.simple_list_item_multiple_choice, parent, false);
                tvName = (TextView) v.findViewById(android.R.id.text1);
                tvName.setTextAppearance(context, R.style.AppThemeBaseLabel);
                tvName.setTextColor(context.getResources().getColor(R.color.primary_material_dark));
            }
        }
        EducationLevel c = (EducationLevel) getItem(position);
        if( tvName != null ){
            tvName.setText(c.name);
        }
        v.setBackgroundColor(context.getResources().getColor(R.color.white));

        return v;
    }

    public int getItemPosition(int anInt) {
        return 0;
    }

    public void setSingle(boolean single){
        this.single = single;
    }
}
