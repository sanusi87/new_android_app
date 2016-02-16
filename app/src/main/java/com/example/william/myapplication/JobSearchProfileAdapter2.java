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

public class JobSearchProfileAdapter2 extends BaseAdapter implements ListAdapter{

    private ArrayList<String[]> _profile;
    private TableJobSearchProfile tableJobSearchProfile;
    private Context context;

    public JobSearchProfileAdapter2( Context context ){
        this.context = context;
        _profile = new ArrayList<>();
        tableJobSearchProfile = new TableJobSearchProfile( context );
        loadItem(false);
    }

    @Override
    public int getCount() {
        return _profile.size();
    }

    @Override
    public String[] getItem(int position) {
        return _profile.get(position);
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
            v = vi.inflate(R.layout.each_search_profile, parent, false);
        }

        String[] searchProfile = getItem(position);
        ((ViewGroup)v.findViewById(R.id.deleteButton).getParent()).setVisibility(View.GONE);

        TextView profileName = (TextView)v.findViewById(R.id.profileName);
        profileName.setText(searchProfile[2]);
        profileName.setTextColor(context.getResources().getColor(android.R.color.white));

        ((TextView)v.findViewById(R.id.dateCreated)).setText(Jenjobs.date(searchProfile[3], null, "yyyy-MM-dd hh:mm:ss"));

        return v;
    }


    public void loadItem(boolean refresh){
        if( refresh ){ _profile.clear(); }

        Cursor c = tableJobSearchProfile.getSearchProfile(0);
        if( c.moveToFirst() ){
            while( !c.isAfterLast() ){
                String[] item = new String[4];
                item[0] = String.valueOf(c.getInt(c.getColumnIndex("id"))); // id
                item[1] = c.getString(c.getColumnIndex("_id")); // _id
                item[2] = c.getString(c.getColumnIndex("profile_name")); // profile_name
                item[3] = c.getString(c.getColumnIndex("date_created")); // date_created

                _profile.add(item);
                c.moveToNext();
            }
        }
        c.close();
    }

}
