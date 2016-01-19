package com.example.william.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JobSearchAdapter extends BaseAdapter implements ListAdapter{
    private Context context;
    private ArrayList<JSONObject> jobList = null;
    private TableBookmark tableBookmark;
    private TableJob tableJob;
    private String accessToken;

    Drawable bookmark;
    Drawable bookmarked;

    public JobSearchAdapter( Context context ){
        this.context = context;
        tableBookmark = new TableBookmark(context);
        tableJob = new TableJob(context);

        bookmark = context.getResources().getDrawable(R.drawable.ic_turned_in_not_black_24dp);
        bookmarked = context.getResources().getDrawable(R.drawable.ic_turned_in_black_24dp);
    }

    public void setAccessToken( String token ){
        this.accessToken = token;
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
        final JSONObject p = getItem(position);

        // set the Text IDs
        if( p != null ){
            final String postTitle = p.optString("title");
            final int postId = p.optInt("post_id");
            final String dateClosed = p.optString("date_closed");
            final String company = p.optString("company_name");
            int showSalary = p.optInt("salary_display");
            final String _dateClosed = Jenjobs.date(dateClosed, "dd MMM yyyy", "yyyy-MM-dd hh:mm:ss");
            boolean isBookmarked = false;

            TextView jobTitle = (TextView) v.findViewById(R.id.job_title);
            jobTitle.setText(postTitle);

            (v.findViewById(R.id.jobDetailsContainer)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _v) {
                    Intent intent = new Intent(context, JobDetails.class);
                    intent.putExtra("post_id", postId);
                    context.startActivity(intent);
                }
            });

            (v.findViewById(R.id.job_title)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _v) {
                    Intent intent = new Intent(context, JobDetails.class);
                    intent.putExtra("post_id", postId);
                    context.startActivity(intent);
                }
            });

            String salary;
            TextView vSalary = (TextView) v.findViewById(R.id.salary);
            if( showSalary == 0 ){
                salary = "Salary undisclosed";
            }else{
                String currency = p.optString("currency");
                salary = currency +" "+ p.optInt("salary_min")+" - "+currency +" "+ p.optInt("salary_max");
                vSalary.setTextColor(context.getResources().getColor(R.color.green));
            }
            vSalary.setText(salary);

            ((TextView) v.findViewById(R.id.company_name)).setText(company);
            ((TextView) v.findViewById(R.id.job_location)).setText(Html.fromHtml(p.optString("job_location")));
            ((TextView) v.findViewById(R.id.job_type)).setText(p.optString("job_type"));
            ((TextView) v.findViewById(R.id.date_closed)).setText( _dateClosed );
            ((TextView) v.findViewById(R.id.job_description)).setText(Html.fromHtml(p.optString("job_desc_brief")));
            ((TextView) v.findViewById(R.id.job_spec)).setText(Html.fromHtml(p.optString("job_spec")));

            final ImageButton bookmarkButton = (ImageButton)v.findViewById(R.id.bookmarkButton);
            final Cursor bookmarkList = tableBookmark.getBookmark(postId);
            Log.e("bm", postTitle+"="+(bookmarkList.getCount()>0));
            if( bookmarkList.getCount() > 0 ){
                bookmarkButton.setImageDrawable(bookmarked);
                isBookmarked = true;
            }
            bookmarkList.close();

            if( accessToken == null ){
                bookmarkButton.setEnabled(false);
                bookmarkButton.setClickable(false);
            }

            Log.e("bm", postTitle+"="+isBookmarked);

            final boolean finalIsBookmarked = isBookmarked;
            bookmarkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if( accessToken == null ){
                        Toast.makeText(context, "Please login or register to use this feature", Toast.LENGTH_SHORT).show();
                    }else{
                        if(finalIsBookmarked){
                            bookmarkButton.setImageDrawable(bookmark);
                            // delete bookmark
                            tableBookmark.deleteBookmark(postId);

                            // delete job
                            tableJob.deleteJob(postId);

                            // post to server
                            String[] param = {Jenjobs.BOOKMARK_URL + "/"+postId+"?access-token=" + accessToken,"{}"};
                            new DeleteRequest().execute(param);
                        }else{
                            bookmarkButton.setImageDrawable(bookmarked);
                            // save bookmark
                            ContentValues cv = new ContentValues();
                            cv.put("title", postTitle);
                            cv.put("post_id", postId);
                            cv.put("date_added", Jenjobs.date(null, "yyyy-MM-dd hh:mm:ss", null));
                            cv.put("date_closed", dateClosed);
                            tableBookmark.addBookmark(cv);

                            // save job
                            ContentValues cv2 = new ContentValues();
                            cv2.put("id", postId);
                            cv2.put("title", postTitle);
                            cv2.put("company", company);
                            cv2.put("job_data", p.toString());
                            cv2.put("date_closed", dateClosed);
                            tableJob.addJob(cv2);

                            // post to server
                            JSONObject obj = new JSONObject();
                            try {
                                obj.put("post_id", postId);
                                String[] param = {Jenjobs.BOOKMARK_URL + "?access-token=" + accessToken,obj.toString()};
                                new PostRequest().execute(param);
                            } catch (JSONException e) {
                                Log.e("err", e.getMessage());
                            }
                        }
                    }
                }
            });
        }

        return v;
    }
}
