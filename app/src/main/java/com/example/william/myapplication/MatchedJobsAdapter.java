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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MatchedJobsAdapter extends BaseAdapter implements ListAdapter{
    private Context context;
    private ArrayList<String[]> jobs;
    ArrayList<Integer> expandedItem;
    Drawable bookmark;
    Drawable bookmarked;
    private String accessToken;
    private TableBookmark tableBookmark;
    private TableJob tableJob;

    String matchedOn;
    int profileId;

    public MatchedJobsAdapter(Context context, int jobmatcherProfileId, String matchedOn) {
        this.context = context;
        this.matchedOn = matchedOn;
        this.profileId = jobmatcherProfileId;
        jobs = new ArrayList<>();
        expandedItem = new ArrayList<>();
        bookmark = context.getResources().getDrawable(R.drawable.ic_bookmark_border_black_48dp);
        bookmarked = context.getResources().getDrawable(R.drawable.ic_bookmark_black_48dp);
        tableBookmark = new TableBookmark(context);
        tableJob = new TableJob(context);

        this.profileId = this.profileId > 0 ? this.profileId : 0;

        loadItem(false);
    }

    public void loadItem(boolean refresh) {
        if( refresh ){ jobs.clear(); }

        TableJobSearchMatched tableJobSearchMatched = new TableJobSearchMatched(context);
        Cursor c = tableJobSearchMatched.getJob(this.profileId, this.matchedOn);
        if( c.moveToFirst() ){
            while( !c.isAfterLast() ){
                /*
                id INTEGER, " +
                "title TEXT, " +
                "company TEXT, " +
                "job_data TEXT, " +
                "date_closed NUMERIC," +
                "date_added NUMERIC
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

    public void setAccessToken( String token ){
        this.accessToken = token;
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
            final int postId = Integer.valueOf(p[0]);
            final String postTitle = p[1];
            final String company = p[2];

            TextView jobTitle = (TextView) v.findViewById(R.id.job_title);
            jobTitle.setText(postTitle);


            jobTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _v) {
                    Intent intent = new Intent(context, JobDetails.class);
                    intent.putExtra("post_id", postId);
                    context.startActivity(intent);
                }
            });

            (v.findViewById(R.id.jobDetailsContainer)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _v) {
                    Intent intent = new Intent(context, JobDetails.class);
                    intent.putExtra("post_id", postId);
                    context.startActivity(intent);
                }
            });

            try {
                JSONObject pd = new JSONObject(p[3]);

                int showSalary = Integer.valueOf(pd.getString("salary_display"));
                //String company = pd.getString("company_name");
                final String dateClosed = pd.optString("date_closed");
                final String _dateClosed = Jenjobs.date(dateClosed, "dd MMM yyyy", "yyyy-MM-dd hh:mm:ss");

                /*
                {
                    "post_id": "325810",
                    "title": "Krew Event",
                    "salary_min": "1300",
                    "salary_max": "1800",
                    "salary_display": "1",
                    "company_name": "Ideal Approach ",
                    "date_closed": "2016-03-02 23:59:59",
                    "job_spec": "Advertising/Media/Arts/Entertainm...",
                    "job_type": "Permanent",
                    "job_location": "Kuala Lumpur > Sentul > Jln Ipoh"
                }
                */
                String salary;
                TextView vSalary = (TextView) v.findViewById(R.id.salary);
                if( showSalary == 0 ){
                    salary = "Salary undisclosed";
                }else{
                    String currency = pd.optString("currency"); // TODO - add currency
                    salary = currency +" "+ pd.optInt("salary_min")+" - "+currency +" "+ pd.optInt("salary_max");
                    vSalary.setTextColor(context.getResources().getColor(R.color.green));
                }
                vSalary.setText(salary);

                ((TextView) v.findViewById(R.id.company_name)).setText(company);
                ((TextView) v.findViewById(R.id.job_location)).setText(Html.fromHtml(pd.optString("job_location")));
                ((TextView) v.findViewById(R.id.job_type)).setText(pd.optString("job_type"));
                ((TextView) v.findViewById(R.id.date_closed)).setText(_dateClosed);
                ((TextView) v.findViewById(R.id.job_spec)).setText(Html.fromHtml(pd.optString("job_spec")));

                // bookmark start
                final ImageButton bookmarkButton = (ImageButton)v.findViewById(R.id.bookmarkButton);
                bookmarkButton.setImageDrawable(bookmark); // reset drawable

                Cursor bookmarkList = tableBookmark.getBookmark(postId);
                if( bookmarkList.getCount() > 0) {
                    bookmarkButton.setImageDrawable(bookmarked);
                }
                bookmarkList.close();

                if( accessToken == null ){
                    bookmarkButton.setEnabled(false);
                    bookmarkButton.setClickable(false);
                }

                bookmarkButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (accessToken == null || !Jenjobs.isOnline(context)) {
                            Toast.makeText(context, "Please login or register to use this feature", Toast.LENGTH_SHORT).show();
                        } else {
                            if (bookmarkButton.getDrawable() == bookmarked) {
                                bookmarkButton.setImageDrawable(bookmark);

                                // delete bookmark
                                tableBookmark.deleteBookmark(postId);

                                // delete job
                                tableJob.deleteJob(postId);

                                // post to server
                                String[] param = {Jenjobs.BOOKMARK_URL + "/" + postId + "?access-token=" + accessToken, "{}"};
                                Log.e("url-delete", Jenjobs.BOOKMARK_URL + "/" + postId + "?access-token=" + accessToken);
                                new DeleteRequest().execute(param);
                            } else {
                                bookmarkButton.setImageDrawable(bookmarked);

                                // save bookmark
                                ContentValues cv = new ContentValues();
                                cv.put("title", postTitle);
                                cv.put("post_id", postId);
                                cv.put("date_added", Jenjobs.date(null, "yyyy-MM-dd hh:mm:ss", null));
                                cv.put("date_closed", dateClosed);
                                tableBookmark.addBookmark(cv);

                                // save job
                                GetRequest g = new GetRequest();
                                g.setResultListener(new GetRequest.ResultListener() {
                                    @Override
                                    public void processResultArray(JSONArray result) {
                                    }

                                    @Override
                                    public void processResult(JSONObject success) {
                                        //Log.e("err", ""+success);
                                        if (success != null && success.toString().length() > 0) {
                                            ContentValues cv2 = new ContentValues();
                                            cv2.put("id", postId);
                                            cv2.put("title", postTitle);
                                            cv2.put("company", company);
                                            cv2.put("job_data", success.toString());
                                            cv2.put("date_closed", dateClosed);
                                            tableJob.addJob(cv2);
                                        }
                                    }
                                });
                                String[] args = {Jenjobs.JOB_DETAILS + "/" + postId};
                                g.execute(args);

                                // post to server
                                JSONObject obj = new JSONObject();
                                try {
                                    obj.put("post_id", postId);
                                    String[] param = {Jenjobs.BOOKMARK_URL + "?access-token=" + accessToken, obj.toString()};
                                    //Log.e("url-post", Jenjobs.BOOKMARK_URL + "/"+postId+"?access-token=" + accessToken);
                                    PostRequest postRequest = new PostRequest();
                                    postRequest.setResultListener(new PostRequest.ResultListener() {
                                        @Override
                                        public void processResult(JSONObject success) {
                                            Log.e("bookmarked", "" + success);
                                        }
                                    });
                                    postRequest.execute(param);
                                } catch (JSONException e) {
                                    Log.e("err", e.getMessage());
                                }
                            }
                        }
                    }
                });
                // bookmark end

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
            } catch (JSONException e) {
                Log.e("jobdata", e.getMessage());
            }
        }
        return v;
    }

    public void setMatchedOn(String matchedOn){ this.matchedOn = matchedOn; }

    public void setProfileId(int profileId){ this.profileId = profileId; }

}
