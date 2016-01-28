package com.example.william.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import java.util.ArrayList;

public class JobSearchProfileAdapter extends BaseAdapter implements ListAdapter{

    private ArrayList<String[]> _profile;
    public JobSearchProfileAdapter( Context context ){
        _profile = new ArrayList<>();

        TableJobSearchProfile jsp = new TableJobSearchProfile( context );
        Cursor c = jsp.getSearchProfile(0);
        if( c.moveToFirst() ){
            while( !c.isAfterLast() ){
                String[] item = new String[5];
                //item[0] = c.getInt(0);
                _profile.add(item);
                c.moveToNext();
            }
        }
        c.close();

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
