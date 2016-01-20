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
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BookmarkAdapter extends BaseAdapter implements ListAdapter {
    private Context context;
    private String accessToken;
    public ArrayList<String[]> bookmarks;
    private TableBookmark tableBookmark;
    private TableJob tableJob;
    private Activity activity;
    private View noItemView;

    public BookmarkAdapter(Context context, String accessToken) {
        this.context = context;
        this.accessToken = accessToken;
        tableBookmark = new TableBookmark(context);
        tableJob = new TableJob(context);

        bookmarks = new ArrayList<>();

        Cursor c = tableBookmark.getBookmark(0);
        if( c.moveToFirst() ){
            while( !c.isAfterLast() ){

                String[] item = new String[4];
                item[0] = c.getString(1); //title
                item[1] = String.valueOf(c.getInt(2)); //post_id
                item[2] = Jenjobs.date(c.getString(3), null, null); //date added
                item[3] = Jenjobs.date(c.getString(4), null, null); //date closed
                bookmarks.add(item);

                c.moveToNext();
            }
        }
        c.close();
    }

    @Override
    public int getCount() {
        return bookmarks.size();
    }

    @Override
    public String[] getItem(int position) {
        return bookmarks.get(position);
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
            v = vi.inflate(R.layout.each_bookmark, parent, false);
        }

        final String[] bookmark = getItem(position);

        String postTitle = bookmark[0];
        final int postId = Integer.valueOf(bookmark[1]);
        String bookmarkedDate = bookmark[2];
        String closedDate = bookmark[3];

        LinearLayout ll = (LinearLayout)v.findViewById(R.id.itemDetails);
        Button deleteButton = (Button)v.findViewById(R.id.deleteButton);
        ((TextView) v.findViewById(R.id.positionTitle)).setText(postTitle);
        ((TextView)v.findViewById(R.id.bookmarkedDate)).setText(bookmarkedDate);
        ((TextView)v.findViewById(R.id.closedDate)).setText(closedDate);

        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _v) {
                Intent intent = new Intent();
                intent.putExtra("post_id", postId);
                intent.setClass(context, JobDetails.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //context.startActivity(intent);
                activity.startActivity(intent);
            }
        });

        final View finalV = v;
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _v) {
                Confirmation confirmation = new Confirmation(activity);
                confirmation.setConfirmationText(null); // use default text
                confirmation.setStatusListener(new Confirmation.ConfirmationListener() {
                    @Override
                    public void statusSelected(boolean status) {
                        if( status ){
                            // delete bookmark
                            tableBookmark.deleteBookmark(postId);

                            // delete job
                            tableJob.deleteJob(postId);

                            // post to server
                            String[] param = {Jenjobs.BOOKMARK_URL + "/" + postId + "?access-token=" + accessToken, "{}"};
                            new DeleteRequest().execute(param);

                            // remove view
                            //((ViewGroup) finalV.getParent()).removeView(finalV);
                            bookmarks.remove(position);
                            notifyDataSetChanged();

                            if( bookmarks.size() == 0 ){
                                if( noItemView != null ){
                                    noItemView.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                }).showDialog();
            }
        });

        return v;
    }

    public void setActivity(Activity activity){
        this.activity = activity;
    }

    public void setNoItemView(View v) {
        this.noItemView = v;
    }
}
