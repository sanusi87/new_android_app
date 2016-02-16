package com.example.william.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class JobSearchProfileAdapter extends BaseAdapter implements ListAdapter{

    private ArrayList<String[]> _profile;
    private Context context;
    private Activity activity;
    private TableJobSearchProfile tableJobSearchProfile;
    private TableJobSearchMatched tableJobSearchMatched;
    private String accessToken;
    private String matchedOn;

    private boolean showDelete = true;

    public JobSearchProfileAdapter( Context context ){
        this.context = context;
        _profile = new ArrayList<>();
        tableJobSearchProfile = new TableJobSearchProfile( context );
        tableJobSearchMatched = new TableJobSearchMatched( context );

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            v = vi.inflate(R.layout.each_search_profile, parent, false);
        }

        final String[] searchProfile = getItem(position);
        final int id = Integer.valueOf(searchProfile[0]);
        final int jm_profile_id = Integer.valueOf(searchProfile[1]);
        //final String notificationFreq = searchProfile[4];
        ((TextView)v.findViewById(R.id.profileName)).setText(searchProfile[2]);
        ((TextView)v.findViewById(R.id.dateCreated)).setText(Jenjobs.date(searchProfile[5], null, "yyyy-MM-dd hh:mm:ss"));

        if( this.showDelete ){
            // for update
            v.findViewById(R.id.profileContainer).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, JobSearchProfileForm.class);
                    intent.putExtra("id", id);
                    activity.startActivity(intent);
                    //activity.finish();
                }
            });

            v.findViewById(R.id.deleteButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Confirmation confirmation = new Confirmation(activity);
                    confirmation.setConfirmationText(new String[]{"Delete this profile?", "Yes", "No"});
                    confirmation.setStatusListener(new Confirmation.ConfirmationListener() {
                        @Override
                        public void statusSelected(final boolean status) {
                            if (status) {
                                if( Jenjobs.isOnline(context) ){
                                    tableJobSearchProfile.deleteSearchProfile(id);
                                    _profile.remove(position);
                                    notifyDataSetChanged();

                                    DeleteRequest p = new DeleteRequest();
                                    p.setResultListener(new DeleteRequest.ResultListener() {
                                        @Override
                                        public void processResult(JSONObject success) {
                                            Log.e("result", "" + success);
                                            String statusText;
                                            if (success != null) {
                                                try {
                                                    statusText = success.getString("status_text");
                                                } catch (JSONException e) {
                                                    statusText = e.getMessage();
                                                }
                                            } else {
                                                statusText = context.getString(R.string.empty_response);
                                            }
                                            Toast.makeText(context, statusText, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    String[] param = {Jenjobs.SEARCH_PROFILE + "/" + jm_profile_id + "?access-token=" + accessToken};
                                    p.execute(param);
                                }else{
                                    Toast.makeText(context, R.string.offline_notification, Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    });
                    confirmation.showDialog();
                }
            });
        }else{
            // for browsing matched jobs
            v.findViewById(R.id.profileContainer).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MatchedJobs.class);
                    intent.putExtra("jm_profile_id", jm_profile_id);
                    intent.putExtra("matched_on", matchedOn);
                    activity.startActivity(intent);
                    //activity.finish();
                }
            });

            v.findViewById(R.id.deleteButton).setVisibility(View.GONE);

            if( matchedOn == null ){
                matchedOn = Jenjobs.date(null, "yyyy-MM-dd", null);
            }
            int ttlMatchedJobs = tableJobSearchMatched.countJobs(jm_profile_id, matchedOn);

            Button noOfJobsButton = (Button)v.findViewById(R.id.noOfJobsButton);
            noOfJobsButton.setVisibility(View.VISIBLE);
            if( ttlMatchedJobs == 0 ){
                noOfJobsButton.setVisibility(View.GONE);
            }
            noOfJobsButton.setText(String.valueOf(ttlMatchedJobs));
            noOfJobsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MatchedJobs.class);
                    intent.putExtra("jm_profile_id", jm_profile_id);
                    intent.putExtra("matched_on", matchedOn);
                    activity.startActivity(intent);
                    //activity.finish();
                }
            });
        }

        return v;
    }

    public void setActivity(Activity _activity){
        this.activity = _activity;
    }

    public void setAccessToken(String accessToken){
        this.accessToken = accessToken;
    }

    public void showDeleteButton(boolean show){
        this.showDelete = show;
    }

    public void setMatchedOn(String matchedOn){ this.matchedOn = matchedOn; }

    public void loadItem(boolean refresh){
        if( refresh ){ _profile.clear(); }

        Cursor c = tableJobSearchProfile.getSearchProfile(0);
        if( c.moveToFirst() ){
            while( !c.isAfterLast() ){
                String[] item = new String[7];
                item[0] = String.valueOf(c.getInt(0)); // id
                item[1] = c.getString(1); // _id
                item[2] = c.getString(2); // profile_name
                item[3] = c.getString(3); // parameters
                item[4] = c.getString(4); // notification frequency
                item[5] = c.getString(5); // date_created
                item[6] = c.getString(6); // date_updated

                _profile.add(item);
                c.moveToNext();
            }
        }
        c.close();
    }
}
