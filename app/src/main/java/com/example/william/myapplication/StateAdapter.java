package com.example.william.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class StateAdapter extends BaseAdapter implements ListAdapter{

    public ArrayList<State> listOfStates = new ArrayList<>();
    private Context context;

    public StateAdapter(Context context){
        this.context = context;

        listOfStates.add(new State(4, "Johor"));
        listOfStates.add(new State(10,"Kedah"));
        listOfStates.add(new State(22,"Kelantan"));
        listOfStates.add(new State(28,"Melaka"));
        listOfStates.add(new State(34,"Negeri Sembilan"));
        listOfStates.add(new State(39,"Penang"));
        listOfStates.add(new State(44,"Pahang"));
        listOfStates.add(new State(48,"Perak"));
        listOfStates.add(new State(52,"Perlis"));
        listOfStates.add(new State(56,"Sabah"));
        listOfStates.add(new State(60,"Selangor"));
        listOfStates.add(new State(64,"Sarawak"));
        listOfStates.add(new State(68,"Terengganu"));
        listOfStates.add(new State(103,"Kuala Lumpur"));
        listOfStates.add(new State(104,"Labuan"));
        listOfStates.add(new State(365,"Putrajaya"));
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
            v = vi.inflate(R.layout.spinner_item, parent, false);
        }

        State state = (State)getItem(position);

        TextView tvName = (TextView) v.findViewById(R.id.spinner_item);
        tvName.setText(state.name);

        return v;
    }
}
