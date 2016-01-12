package com.example.william.myapplication;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ApplicationAdapter extends BaseAdapter implements ListAdapter{
    private Context context;
    public ArrayList<String[]> applications;
    public String accessToken;

    public ApplicationAdapter( Context context, String accessToken ){
        this.context = context;
        applications = new ArrayList<>();
        this.accessToken = accessToken;

        TableApplication tableApplication = new TableApplication(context);
        Cursor _applications = tableApplication.getApplication(0);
        if( _applications.moveToFirst() ){
            while( !_applications.isAfterLast() ){
                /*
                id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + //0
                "_id INTEGER, " + //1
                "post_id INTEGER, " + //2
                "status INTEGER(3), " + //3
                "date_created NUMERIC," + //4
                "date_updated NUMERIC," + //5
                "title TEXT," + //6
                "closed NUMERIC//7
                */

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
                String closingDate = _applications.getString(7);
                if( closingDate != null ){
                    closingDate = Jenjobs.date(closingDate, "dd MMM yyyy", "yyyy-MM-dd hh:mm:ss");
                }else{
                    closingDate = Jenjobs.date(null, "dd MMM yyyy", null);
                }

                String[] item = new String[8];
                item[0] = String.valueOf(id);
                item[1] = String.valueOf(idOnServer);
                item[2] = String.valueOf(postId);
                item[3] = String.valueOf(applicationStatus);
                item[4] = dateApplied;
                item[5] = dateUpdated;
                item[6] = positionTitle;
                item[7] = closingDate;

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
        String applicationStatus = "";
        int appStat = Integer.valueOf(theApplication[3]);
        if( appStat == TableApplication.STATUS_HIRED ) {
            applicationStatus = "Successful";
        }else if( appStat == TableApplication.STATUS_UNPROCESSED ){
            applicationStatus = "Received";
        }else if( appStat == TableApplication.STATUS_REJECTED ){
            applicationStatus = "Unsuccessful";
        }else{
            applicationStatus = "Processing";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        Date strDate = null;
        try {
            strDate = sdf.parse( theApplication[7] );
            if (System.currentTimeMillis() > strDate.getTime()) {
                applicationStatus = "Job Closed";
            }
        } catch (ParseException e) {
            Log.e("error", e.getMessage());
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

        withdrawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] params = {Jenjobs.APPLICATION_URL+"/"+postId+"?access-token="+accessToken};
                new DeleteRequest().execute();
            }
        });

        return v;
    }
}
