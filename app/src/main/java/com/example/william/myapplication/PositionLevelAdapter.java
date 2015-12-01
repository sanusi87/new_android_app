package com.example.william.myapplication;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import java.util.ArrayList;

public class PositionLevelAdapter extends BaseAdapter implements ListAdapter{

    public ArrayList<PositionLevel> positionLevel = new ArrayList<>();
    private Context context;

    public PositionLevelAdapter(Context context){
        this.context = context;

        positionLevel.add(new PositionLevel(1, "Non-Executive"));
        positionLevel.add(new PositionLevel(2, "Executive"));
        positionLevel.add(new PositionLevel(3, "Management"));
        positionLevel.add(new PositionLevel(4, "Senior Management"));
        positionLevel.add(new PositionLevel(5, "Entry Level"));
        positionLevel.add(new PositionLevel(6, "Senior Executive"));
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
        return null;
    }
}
