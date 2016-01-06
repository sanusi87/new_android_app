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

public class LanguageAdapter extends BaseAdapter implements ListAdapter{
    public ArrayList<Language> language = new ArrayList<>();
    private Context context;

    public LanguageAdapter(Context context){
        this.context = context;

        HashMap fields = Jenjobs.getLanguage();
        for (Object o : fields.entrySet()) {
            HashMap.Entry e = (HashMap.Entry) o;
            language.add(new Language((int) e.getKey(), String.valueOf(e.getValue())));
        }
    }

    @Override
    public int getCount() {
        return language.size();
    }

    @Override
    public Object getItem(int position) {
        return language.get(position);
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
        JobType c = (JobType) getItem(position);
        TextView tvName = (TextView) v.findViewById(android.R.id.text1);
        tvName.setText(c.name);
        tvName.setTextColor(context.getResources().getColor(R.color.primary_material_dark));
        v.setBackgroundColor(context.getResources().getColor(R.color.white));

        return v;
    }

    public int findPosition( int theCode ){
        int index = 0;
        for(int i=0;i< language.size();i++){
            if( language.get(i).id == theCode ){
                index = i;
                break;
            }
        }
        return index;
    }
}
