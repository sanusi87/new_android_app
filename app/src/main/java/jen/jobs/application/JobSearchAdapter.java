package jen.jobs.application;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.text.Html;
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

    ArrayList<Integer> expandedItem;

    public JobSearchAdapter( Context context ){
        this.context = context;
        tableBookmark = new TableBookmark(context);
        tableJob = new TableJob(context);

        bookmark = context.getResources().getDrawable(R.drawable.ic_bookmark_border_black_48dp);
        bookmarked = context.getResources().getDrawable(R.drawable.ic_bookmark_black_48dp);

        expandedItem = new ArrayList<>();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            v = vi.inflate(R.layout.each_job_summary, parent, false);
        }

        v.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        if( position%2 == 1 ){
            v.setBackgroundColor(context.getResources().getColor(android.R.color.white));
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

            TextView jobTitle = (TextView) v.findViewById(R.id.job_title);
            jobTitle.setText(postTitle);

            (v.findViewById(R.id.jobDetailsContainer)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _v) {
                    Intent intent = new Intent();
                    intent.setClass(context, JobDetails.class);
                    intent.putExtra("post_id", postId);
                    context.startActivity(intent);
                }
            });

            (v.findViewById(R.id.job_title)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _v) {
                    Intent intent = new Intent();
                    intent.setClass(context, JobDetails.class);
                    intent.putExtra("post_id", postId);
                    context.startActivity(intent);
                }
            });

            String salary;
            TextView vSalary = (TextView) v.findViewById(R.id.salary);
            vSalary.setTextColor(context.getResources().getColor(R.color.muted_text));
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
            bookmarkButton.setImageDrawable(bookmark); // reset drawable

            Cursor bookmarkList = tableBookmark.getBookmark(postId);
            if( bookmarkList.getCount() > 0) {
                bookmarkButton.setImageDrawable(bookmarked);
            }
            bookmarkList.close();

            //if( accessToken == null ){
                //bookmarkButton.setEnabled(false);
                //bookmarkButton.setClickable(false);
            //}

            bookmarkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if( accessToken == null || !Jenjobs.isOnline(context) ){
                        Toast.makeText(context, "Please login or register to use this feature", Toast.LENGTH_SHORT).show();
                    }else{
                        Bookmark _bookmark = new Bookmark(context);
                        _bookmark.setAccessToken(accessToken);

                        if(bookmarkButton.getDrawable() == bookmarked){
                            bookmarkButton.setImageDrawable(bookmark);
                            _bookmark.deleteBookmark(postId);
                        }else{
                            bookmarkButton.setImageDrawable(bookmarked);
                            _bookmark.saveBookmark(postId);
                        }
                    }
                }
            });

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
        }

        return v;
    }
}
