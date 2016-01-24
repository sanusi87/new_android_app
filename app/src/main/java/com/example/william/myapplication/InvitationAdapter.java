package com.example.william.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class InvitationAdapter extends BaseAdapter implements ListAdapter{
    private Context context;
    private ArrayList<String[]> listOfInvitataion;
    private TableInvitation tableInvitation;

    public InvitationAdapter(Context context){
        this.context = context;

        listOfInvitataion = new ArrayList<>();
        tableInvitation = new TableInvitation(context);

        Cursor c = tableInvitation.getInvitation(0);
        if( c.moveToFirst() ){
            while( !c.isAfterLast() ){
                //int id = c.getInt(0);
                String id = c.getString(0);
                String postId = c.getString(1) == null ? "" : c.getString(1);
                String postTitle = c.getString(2) == null ? "" : c.getString(2);
                String empProfileId = c.getString(3);
                String empProfileName = c.getString(4);
                String status = c.getString(5) == null ? "0" : c.getString(5);
                String dateAdded = c.getString(6);
                String dateUpdated = c.getString(7);

                String[] invitation = {id,postId,postTitle,empProfileId,empProfileName,status,dateAdded,dateUpdated};
                listOfInvitataion.add(invitation);
                c.moveToNext();
            }
        }
    }

    @Override
    public int getCount() {
        return listOfInvitataion.size();
    }

    @Override
    public String[] getItem(int position) {
        return listOfInvitataion.get(position);
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
            v = vi.inflate(R.layout.each_invitation, parent, false);
        }

        String[] invitation = getItem(position);
        final int invitationID = Integer.valueOf(invitation[0]);
        final String postId = invitation[1];
        String positionTitle = invitation[2];
        String companyName = invitation[4];
        int status = Integer.valueOf(invitation[5]);

        TextView messageText = (TextView)v.findViewById(R.id.messageText);
        TextView company = (TextView)v.findViewById(R.id.company);
        TextView jobPost = (TextView)v.findViewById(R.id.jobPost);
        Button allowButton = (Button)v.findViewById(R.id.allowButton);
        Button rejectButton = (Button)v.findViewById(R.id.rejectButton);
        Button applyButton = (Button)v.findViewById(R.id.applyButton);
        Button notInterestedButton = (Button)v.findViewById(R.id.notInterestedButton);

        allowButton.setVisibility(View.GONE);
        rejectButton.setVisibility(View.GONE);
        applyButton.setVisibility(View.GONE);
        notInterestedButton.setVisibility(View.GONE);

        // if no job posting ID,
        // then it is a resume access request
        if( postId.equals("") ){
            messageText.setText(String.format(context.getResources().getString(R.string.request_to_view), companyName));
            jobPost.setVisibility(View.GONE);

            if( status == TableInvitation.STATUS_ALLOWED ){
                allowButton.setText(R.string.allowed);
                allowButton.setVisibility(View.VISIBLE);
                allowButton.setEnabled(false);
                allowButton.setClickable(false);
            }else if( status == TableInvitation.STATUS_REJECTED ){
                rejectButton.setText(R.string.rejected);
                rejectButton.setVisibility(View.VISIBLE);
                rejectButton.setEnabled(false);
                rejectButton.setClickable(false);
            }else{
                allowButton.setVisibility(View.VISIBLE);
                rejectButton.setVisibility(View.VISIBLE);
            }

            allowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _v) {
                    ((Button)_v).setText(R.string.allowed);
                    _v.setEnabled(false);
                    _v.setClickable(false);

                    // TODO - allow resume access
                    ContentValues cv = new ContentValues();
                    cv.put("status", TableInvitation.STATUS_ALLOWED);
                    tableInvitation.updateInvitation(cv, invitationID);
                }
            });

            rejectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _v) {
                    ((Button)_v).setText(R.string.rejected);
                    _v.setEnabled(false);
                    _v.setClickable(false);

                    // TODO - reject resume access
                    ContentValues cv = new ContentValues();
                    cv.put("status", TableInvitation.STATUS_REJECTED);
                    tableInvitation.updateInvitation(cv, invitationID);
                }
            });
        }else{
            messageText.setText(String.format(context.getResources().getString(R.string.invite_to_apply), companyName, positionTitle));
            jobPost.setVisibility(View.VISIBLE);
            jobPost.setText(positionTitle);

            if( status == TableInvitation.STATUS_APPLIED ){
                applyButton.setText(R.string.application_sent);
                applyButton.setVisibility(View.VISIBLE);
                applyButton.setEnabled(false);
                applyButton.setClickable(false);
            }else if( status == TableInvitation.STATUS_NOT_INTERESTED ){
                notInterestedButton.setVisibility(View.VISIBLE);
                notInterestedButton.setEnabled(false);
                notInterestedButton.setClickable(false);
            }else{
                applyButton.setVisibility(View.VISIBLE);
                notInterestedButton.setVisibility(View.VISIBLE);
            }

            applyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _v) {
                    ((Button)_v).setText(R.string.application_sent);
                    _v.setEnabled(false);
                    _v.setClickable(false);

                    // TODO - check resume completeness, submit application
                }
            });

            notInterestedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _v) {
                    _v.setEnabled(false);
                    _v.setClickable(false);

                    ContentValues cv = new ContentValues();
                    cv.put("status", TableInvitation.STATUS_NOT_INTERESTED);
                    tableInvitation.updateInvitation(cv, invitationID);

                    // TODO - anything else?
                }
            });

            jobPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _v) {
                    Intent intent = new Intent();
                    intent.setClass(context, JobDetails.class);
                    intent.putExtra("post_id", Integer.valueOf(postId));
                    context.startActivity(intent);
                }
            });
        }

        company.setText(companyName);
        company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - open employer profile activity
            }
        });

        return v;
    }
}
