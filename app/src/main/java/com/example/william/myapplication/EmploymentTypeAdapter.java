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

public class EmploymentTypeAdapter extends BaseAdapter implements ListAdapter{
    public ArrayList<JobType> jobtype = new ArrayList<>();
    private Context context;
    private boolean single = false;

    public JobTypeAdapter(Context context){
        this.context = context;

        HashMap fields = Jenjobs.getJobType();
        for (Object o : fields.entrySet()) {
            HashMap.Entry e = (HashMap.Entry) o;
            jobtype.add(new JobType((int) e.getKey(), String.valueOf(e.getValue())));
        }
    }

    @Override
    public int getCount() {
        return jobtype.size();
    }

    @Override
    public Object getItem(int position) {
        return jobtype.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        JobType c = (JobType) getItem(position);

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

        if( tvName != null ){
            tvName.setText(c.name);
        }
        v.setBackgroundColor(context.getResources().getColor(R.color.white));

        return v;
    }

    public void setSingle( boolean inflateSingle ){
        this.single = inflateSingle;
    }

    /*
    public int findPosition( int theCode ){
        int index = 0;
        for(int i=0;i< this.jobtype.size();i++){
            if( this.jobtype.get(i).id == theCode ){
                index = i;
                break;
            }
        }
        return index;
    }
    */
}
