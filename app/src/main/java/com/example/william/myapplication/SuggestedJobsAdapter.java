package com.example.william.myapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SuggestedJobsAdapter extends BaseAdapter implements ListAdapter{
    private Context context;
    private ArrayList<String[]> jobs;

    public SuggestedJobsAdapter(Context context) {
        this.context = context;
        jobs = new ArrayList<>();

        TableJob tableJob = new TableJob(context);
        Cursor c = tableJob.getSuggestedJob();
        if( c.moveToFirst() ){
            while( !c.isAfterLast() ){
                /*
                id INTEGER
                title TEXT
                company TEXT
                job_data TEXT
                date_closed NUMERIC
                suggested_on NUMERIC
                */

                String id = String.valueOf(c.getInt(c.getColumnIndex("id")));
                String title = c.getString(c.getColumnIndex("title"));
                String company = c.getString(c.getColumnIndex("company"));
                String job_data = c.getString(c.getColumnIndex("job_data"));
                String date_closed = Jenjobs.date(c.getString(c.getColumnIndex("date_closed")), null, "yyyy-MM-dd hh:mm:ss");

                String[] _job = {id,title,company,job_data,date_closed};
                jobs.add(_job);

                c.moveToNext();
            }
        }
    }

    @Override
    public int getCount() {
        return jobs.size();
    }

    @Override
    public String[] getItem(int position) {
        return jobs.get(position);
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
            v = vi.inflate(R.layout.each_jobmatcher_post_layout, parent, false);
        }

        final String[] p = getItem(position);
        if( p != null ){
            TextView jobTitle = (TextView) v.findViewById(R.id.job_title);
            jobTitle.setText(p[1]);

            jobTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, JobDetails.class);
                    intent.putExtra("post_id", p[0]);
                    context.startActivity(intent);
                }
            });

            (v.findViewById(R.id.jobDetailsContainer)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _v) {
                    Intent intent = new Intent(context, JobDetails.class);
                    intent.putExtra("post_id", p[0]);
                    context.startActivity(intent);
                }
            });

            try {
                JSONObject pd = new JSONObject(p[3]);

                int showSalary = Integer.valueOf(pd.getString("salary_display"));
                String company = pd.getString("company_name");
                final String dateClosed = pd.optString("date_closed");
                final String _dateClosed = Jenjobs.date(dateClosed, "dd MMM yyyy", "yyyy-MM-dd hh:mm:ss");

                String salary;
                TextView vSalary = (TextView) v.findViewById(R.id.salary);
                if( showSalary == 0 ){
                    salary = "Salary undisclosed";
                }else{
                    String currency = pd.optString("currency");
                    salary = currency +" "+ pd.optInt("salary_min")+" - "+currency +" "+ pd.optInt("salary_max");
                    vSalary.setTextColor(context.getResources().getColor(R.color.green));
                }
                vSalary.setText(salary);

                ((TextView) v.findViewById(R.id.company_name)).setText(company);
                ((TextView) v.findViewById(R.id.job_location)).setText(Html.fromHtml(pd.optString("job_location")));
                ((TextView) v.findViewById(R.id.job_type)).setText(pd.optString("job_type"));
                ((TextView) v.findViewById(R.id.date_closed)).setText(_dateClosed);
                ((TextView) v.findViewById(R.id.job_spec)).setText(Html.fromHtml(pd.optString("job_spec")));

            } catch (JSONException e) {
                Log.e("jobdata", e.getMessage());
            }
        }
        return v;
    }
}
