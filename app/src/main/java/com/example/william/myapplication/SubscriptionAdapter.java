package com.example.william.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import java.util.ArrayList;

public class SubscriptionAdapter extends BaseAdapter implements ListAdapter{
    private Context context;
    public ArrayList<Subscription> listOfSubscription = new ArrayList<>();

    public SubscriptionAdapter(Context context){
        this.context = context;
        TableSubscription sct = new TableSubscription(context);
        Cursor c = sct.getSubscription();
        c.moveToFirst();
        while(!c.isAfterLast()){
            listOfSubscription.add(new Subscription(c.getString(0), c.getInt(1), c.getInt(2)));
            c.moveToNext();
        }
    }

    @Override
    public int getCount() {
        return listOfSubscription.size();
    }

    @Override
    public Object getItem(int position) {
        return listOfSubscription.get(position);
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
            // v = vi.inflate(R.layout.each_job_summary, parent, false);
        }

        return v;
    }
}
