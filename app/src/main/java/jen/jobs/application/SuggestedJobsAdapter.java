package jen.jobs.application;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SuggestedJobsAdapter extends BaseAdapter implements ListAdapter{
    private Context context;
    private ArrayList<String[]> jobs;
    ArrayList<Integer> expandedItem;
    private Activity activity;

    public SuggestedJobsAdapter(Context context) {
        this.context = context;
        jobs = new ArrayList<>();
        expandedItem = new ArrayList<>();

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
    public View getView(final int position, View convertView, ViewGroup parent) {
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
            final int postId = Integer.valueOf(p[0]);

            jobTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, JobDetails.class);
                    intent.putExtra("post_id", postId);
                    activity.startActivity(intent);
                }
            });

            (v.findViewById(R.id.jobDetailsContainer)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _v) {
                    Intent intent = new Intent(context, JobDetails.class);
                    intent.putExtra("post_id", postId);
                    activity.startActivity(intent);
                }
            });
            //Log.e("jobs_data", ""+p[3]);
            try {
                JSONObject pd = new JSONObject(p[3]);

                String showSalary = pd.getString("show_salary");
                //int showSalary = Integer.valueOf();
                String company = pd.getString("company");
                final String _dateClosed = pd.optString("date_closed");

                String salary;
                TextView vSalary = (TextView) v.findViewById(R.id.salary);
                if( showSalary.equals("false") ){
                    salary = "Salary undisclosed";
                }else{
                    String currency = "RM";
                    //TODO - String currency = pd.optString("currency");
                    salary = currency +" "+ pd.optInt("salary_min")+" - "+currency +" "+ pd.optInt("salary_max");
                    vSalary.setTextColor(context.getResources().getColor(R.color.green));
                }
                vSalary.setText(salary);

                ((TextView) v.findViewById(R.id.company_name)).setText(company);
                ((TextView) v.findViewById(R.id.job_location)).setText(Html.fromHtml(pd.optString("location")));
                ((TextView) v.findViewById(R.id.job_type)).setText(pd.optString("type"));
                ((TextView) v.findViewById(R.id.date_closed)).setText(_dateClosed);
                ((TextView) v.findViewById(R.id.job_spec)).setText(Html.fromHtml(pd.optString("specialisation")));

            } catch (JSONException e) {
                Log.e("jobdata", e.getMessage());
            }
        }

        final LinearLayout expandItemContainer = (LinearLayout)v.findViewById(R.id.expandItemContainer);
        final LinearLayout moreItem = (LinearLayout) v.findViewById(R.id.moreItem);

        if( expandedItem.indexOf(position) == -1 ){
            // reset
            moreItem.setVisibility(View.GONE);
            expandItemContainer.setVisibility(View.VISIBLE);
        }else{
            // retain
            moreItem.setVisibility(View.VISIBLE);
            expandItemContainer.setVisibility(View.GONE);
        }

        // set
        expandItemContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandedItem.add(position);

                Animation in = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
                moreItem.startAnimation(in);
                moreItem.setVisibility(View.VISIBLE);

                Animation out = AnimationUtils.makeOutAnimation(context, true);
                expandItemContainer.startAnimation(out);
                expandItemContainer.setVisibility(View.GONE);
            }
        });

        v.findViewById(R.id.bookmarkButton).setVisibility(View.GONE);
        return v;
    }

    public void setActivity(Activity activity){ this.activity = activity; }
}
