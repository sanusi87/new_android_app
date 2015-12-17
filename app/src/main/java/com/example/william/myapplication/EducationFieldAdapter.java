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

public class EducationFieldAdapter extends BaseAdapter implements ListAdapter{

    public ArrayList<EducationField> eduField = new ArrayList<>();
    private Context context;

    public EducationFieldAdapter( Context context ){
        this.context = context;

        HashMap<Integer, String> fields = Jenjobs.getEducationField();
        ArrayList<EducationField> tempArr = new ArrayList<>();

        Iterator i = fields.entrySet().iterator();
        while( i.hasNext() ){
            HashMap.Entry e = (HashMap.Entry)i.next();
            tempArr.add(new EducationField( (int)e.getKey(), String.valueOf(e.getValue()) ));
        }

        Collections.sort(tempArr, new Comparator<EducationField>() {
            @Override
            public int compare(EducationField lhs, EducationField rhs) {
                String[] t = {lhs.name, rhs.name};
                Arrays.sort(t);
                if (t[0].equals(lhs.name)) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        eduField = tempArr;
    }

    @Override
    public int getCount() {
        return eduField.size();
    }

    @Override
    public Object getItem(int position) {
        return eduField.get(position);
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
        EducationField c = (EducationField) getItem(position);
        TextView tvName = (TextView) v.findViewById(android.R.id.text1);
        tvName.setText(c.name);
        tvName.setTextColor(context.getResources().getColor(R.color.primary_material_dark));
        v.setBackgroundColor(context.getResources().getColor(R.color.white));

        return v;
    }
}