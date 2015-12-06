package com.example.william.myapplication;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class JobSearchAdapter extends BaseAdapter implements ListAdapter{
    private Context context;
    private ArrayList<JSONObject> jobList = null;

    public JobSearchAdapter( Context context ){
        this.context = context;
    }

    public void setJob( ArrayList<JSONObject> jobList ){
        if( this.jobList == null ){
            this.jobList = jobList;
        }else{
            if(jobList == null ){
                this.jobList.clear();
            }else{
                this.jobList.addAll(jobList);
            }
        }
    }


    // ---------

    @Override
    public int getCount() {
        return jobList != null ? jobList.size() : 0;
    }

    @Override
    public JSONObject getItem(int position) {
        return jobList.get(position);
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
            v = vi.inflate(R.layout.each_job_summary, parent, false);
        }

        // get a job
        JSONObject p = getItem(position);

        // set the Text IDs
        if( p != null ){
            TextView jobTitle = (TextView) v.findViewById(R.id.job_title);
            jobTitle.setText(p.optString("title"));
            final int postId = p.optInt("post_id");

            jobTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, JobDetails.class);
                    intent.putExtra("post_id", postId);
                    context.startActivity(intent);
                }
            });

            Log.e("position%2", ""+(position%2));
            if( position%2 == 0 ){
                v.setBackgroundColor( context.getResources().getColor(R.color.white));
            }

            int showSalary = p.optInt( "salary_display" );
            String salary;
            TextView vSalary = (TextView) v.findViewById(R.id.salary);
            if( showSalary == 0 ){
                salary = "Salary undisclosed";
            }else{
                String currency = p.optString("currency");
                salary = currency +" "+ p.optInt("salary_min")+" - "+ p.optInt("salary_max");
                vSalary.setTextColor(context.getResources().getColor(R.color.green));
            }
            vSalary.setText(salary);

            ((TextView) v.findViewById(R.id.company_name)).setText( p.optString("company_name") );
            ((TextView) v.findViewById(R.id.job_location)).setText(p.optString("job_location"));
            ((TextView) v.findViewById(R.id.job_type)).setText(p.optString("job_type"));

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            SimpleDateFormat newFormatter = new SimpleDateFormat("dd MMM yyyy");
            try {
                Date theDate = formatter.parse(p.optString("date_closed"));
                ((TextView) v.findViewById(R.id.date_closed)).setText( newFormatter.format(theDate) );
            } catch (ParseException e) {
                e.printStackTrace();
            }

            ((TextView) v.findViewById(R.id.job_description)).setText(Html.fromHtml(p.optString("job_desc_brief")));
        }

        return v;
    }
}
