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
import android.widget.ListAdapter;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

public class JobSearchProfileAdapter extends BaseAdapter implements ListAdapter{

    private ArrayList<String[]> _profile;
    private Context context;
    private Activity activity;
    private TableJobSearchProfile tableJobSearchProfile;

    private String accessToken;

    public JobSearchProfileAdapter( Context context ){
        this.context = context;
        _profile = new ArrayList<>();

        tableJobSearchProfile = new TableJobSearchProfile( context );
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
        final int searchProfileId = Integer.valueOf(searchProfile[0]);
        final String notificationFreq = searchProfile[4];
        ((TextView)v.findViewById(R.id.profileName)).setText(searchProfile[2]);
        ((TextView)v.findViewById(R.id.dateCreated)).setText(Jenjobs.date(searchProfile[5], null,"yyyy-MM-dd hh:mm:ss"));

        v.findViewById(R.id.profileContainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, JobSearchActivity.class);
                intent.putExtra("search_profile", searchProfile[3]);
                activity.startActivity(intent);
                activity.finish();
            }
        });

        v.findViewById(R.id.deleteButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Confirmation confirmation = new Confirmation(activity);
                confirmation.setStatusListener(new Confirmation.ConfirmationListener() {
                    @Override
                    public void statusSelected(boolean status) {
                        if( status ){
                            tableJobSearchProfile.deleteSearchProfile(searchProfileId);
                            _profile.remove(position);
                            notifyDataSetChanged();

                            DeleteRequest p = new DeleteRequest();
                            p.setResultListener(new DeleteRequest.ResultListener() {
                                @Override
                                public void processResult(JSONObject success) {
                                    Log.e("result", ""+success);
                                }
                            });
                            String[] param = {Jenjobs.SEARCH_PROFILE+"/"+searchProfileId+"?access-token="+accessToken};
                            p.execute(param);
                        }
                    }
                });
                confirmation.showDialog();
            }
        });

        return v;
    }

    public void setActivity(Activity _activity){
        this.activity = _activity;
    }

    public void setAccessToken(String accessToken){
        this.accessToken = accessToken;
    }
}
