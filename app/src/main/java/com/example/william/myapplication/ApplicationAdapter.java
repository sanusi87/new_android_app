package com.example.william.myapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ApplicationAdapter extends BaseAdapter implements ListAdapter{
    private Context context;
    public ArrayList<String[]> applications;
    public String accessToken;
    private TableApplication tableApplication;

    public ApplicationAdapter( Context context, String accessToken ){
        this.context = context;
        applications = new ArrayList<>();
        this.accessToken = accessToken;

        tableApplication = new TableApplication(context);
        Cursor _applications = tableApplication.getActiveApplication();
        if( _applications.moveToFirst() ){
            while( !_applications.isAfterLast() ){
                int id = _applications.getInt(0);
                int idOnServer = _applications.getInt(1);
                int postId = _applications.getInt(2);
                int applicationStatus = _applications.getInt(3);
                String dateApplied = _applications.getString(4);
                String dateUpdated = _applications.getString(5);
                if( dateUpdated != null ){
                    dateUpdated = Jenjobs.date(dateUpdated, null, null);
                }
                String positionTitle = _applications.getString(6);
                String isJobClosed = _applications.getString(7);

                String[] item = new String[8];
                item[0] = String.valueOf(id);
                item[1] = String.valueOf(idOnServer);
                item[2] = String.valueOf(postId);
                item[3] = String.valueOf(applicationStatus);
                item[4] = dateApplied;
                item[5] = dateUpdated;
                item[6] = positionTitle;
                item[7] = isJobClosed;

                applications.add(item);

                _applications.moveToNext();
            }
        }
        _applications.close();
    }

    @Override
    public int getCount() {
        return applications.size();
    }

    @Override
    public String[] getItem(int position) {
        return applications.get(position);
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
            v = vi.inflate(R.layout.each_application, parent, false);
        }

        String[] theApplication = getItem(position);
        final int postId = Integer.valueOf(theApplication[1]);

        LinearLayout container = (LinearLayout)v.findViewById(R.id.applicationDetails);
        TextView positionTitle = (TextView)v.findViewById(R.id.positionTitle);
        TextView applicationDate = (TextView)v.findViewById(R.id.applicationDate);
        TextView jobStatus = (TextView)v.findViewById(R.id.jobStatus);
        Button withdrawButton = (Button)v.findViewById(R.id.withdrawButton);

        positionTitle.setText( theApplication[6] );
        applicationDate.setText( theApplication[4] );
        String applicationStatus;
        int appStat = Integer.valueOf(theApplication[3]);
        if( appStat == TableApplication.STATUS_HIRED ) {
            applicationStatus = "Successful";
        }else if( appStat == TableApplication.STATUS_UNPROCESSED ){
            applicationStatus = "Received";
        }else if( appStat == TableApplication.STATUS_REJECTED ){
            applicationStatus = "Unsuccessful";
        }else if( appStat == TableApplication.STATUS_CLOSED ){
            applicationStatus = "Job Closed";
        }else{
            applicationStatus = "Processing";
        }

        if( theApplication[7].equals("1") ){
            applicationStatus = "Job Closed";
        }

        jobStatus.setText( applicationStatus );

        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("post_id", postId);
                intent.setClass(context, JobDetails2.class);
                context.startActivity(intent);
            }
        });

        final View finalV = v;
        withdrawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _v) {
                String[] params = {Jenjobs.APPLICATION_URL+"/"+postId+"?access-token="+accessToken};
                new DeleteRequest().execute(params);

                boolean isDeleted = tableApplication.deleteApplication(postId);
                if( isDeleted ){
                    Toast.makeText(context, "Application withdrawn.", Toast.LENGTH_LONG).show();
                    finalV.setVisibility(View.GONE);
                }
            }
        });

        return v;
    }
}
