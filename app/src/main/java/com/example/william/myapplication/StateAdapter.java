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

public class StateAdapter extends BaseAdapter implements ListAdapter{

    public ArrayList<State> listOfStates = new ArrayList<>();
    private Context context;
    private boolean layoutSingle = false;

    public StateAdapter(Context context){
        this.context = context;

        HashMap<Integer, String> states = Jenjobs.getState();
        ArrayList<State> tempArr = new ArrayList<>();

        Iterator i = states.entrySet().iterator();
        while( i.hasNext() ){
            HashMap.Entry e = (HashMap.Entry)i.next();
            tempArr.add(new State( (int)e.getKey(), String.valueOf(e.getValue()) ));
        }

        Collections.sort(tempArr, new Comparator<State>() {
            @Override
            public int compare(State lhs, State rhs) {
                String[] t = {lhs.name, rhs.name};
                Arrays.sort(t);
                if (t[0] == lhs.name) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        listOfStates = tempArr;
    }

    @Override
    public int getCount() {
        return listOfStates.size();
    }

    @Override
    public Object getItem(int position) {
        return listOfStates.get(position);
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

            if( layoutSingle ){
                v = vi.inflate(android.R.layout.simple_list_item_1, parent, false);
            }else{
                v = vi.inflate(android.R.layout.simple_list_item_multiple_choice, parent, false);
            }
        }

        State state = (State) getItem(position);
        TextView tvName = (TextView) v.findViewById(android.R.id.text1);
        tvName.setText(state.name);
        tvName.setTextColor(context.getResources().getColor(R.color.primary_material_dark));
        //v.setBackgroundColor(context.getResources().getColor(R.color.white));

        return v;
    }

    public void setLayoutSingle(boolean layoutSingle) {
        this.layoutSingle = layoutSingle;
    }
}
