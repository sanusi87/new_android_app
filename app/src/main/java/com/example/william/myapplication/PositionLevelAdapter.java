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

public class PositionLevelAdapter extends BaseAdapter implements ListAdapter{

    public ArrayList<PositionLevel> positionLevel = new ArrayList<>();
    private Context context;
    private boolean single = false;

    public PositionLevelAdapter(Context context){
        this.context = context;

        HashMap positionLevels = Jenjobs.getPositionLevel();
        ArrayList<PositionLevel> tempArr = new ArrayList<>();

        for (Object o : positionLevels.entrySet()) {
            HashMap.Entry e = (HashMap.Entry) o;
            tempArr.add(new PositionLevel((int) e.getKey(), String.valueOf(e.getValue())));
        }

        /*
        Collections.sort(tempArr, new Comparator<PositionLevel>() {
            @Override
            public int compare(PositionLevel lhs, PositionLevel rhs) {
                String[] t = {lhs.name, rhs.name};
                Arrays.sort(t);
                if (t[0] == lhs.name) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
        */

        positionLevel = tempArr;
    }

    @Override
    public int getCount() {
        return positionLevel.size();
    }

    @Override
    public Object getItem(int position) {
        return positionLevel.get(position);
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
        PositionLevel c = (PositionLevel) getItem(position);
        if( tvName != null ){
            tvName.setText(c.name);
        }
        v.setBackgroundColor(context.getResources().getColor(R.color.white));

        return v;
    }

    public void setSingle(boolean single) {
        this.single = single;
    }
}
