package com.example.william.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Bookmark {
    TableJob tableJob;
    TableBookmark tableBookmark;
    String accessToken;

    public Bookmark(Context context){
        tableJob = new TableJob(context);
        tableBookmark = new TableBookmark(context);
    }

    public void setAccessToken( String accessToken ){
        this.accessToken = accessToken;
    }

    public void saveBookmark( final int postId ){
        // download job posting
        GetRequest g = new GetRequest();
        g.setResultListener(new GetRequest.ResultListener() {
            @Override
            public void processResultArray(JSONArray result) {}

            @Override
            public void processResult(JSONObject success) {
                if( success != null && success.toString().length() > 0 ){
                    ContentValues cv2 = new ContentValues();
                    try {
                        // save job
                        cv2.put("id", postId);
                        cv2.put("title", success.getString("title"));
                        cv2.put("company", success.getString("company"));
                        cv2.put("job_data", success.toString());
                        cv2.put("date_closed", success.getString("date_closed"));
                        tableJob.addJob(cv2);

                        // save bookmark
                        ContentValues cv = new ContentValues();
                        cv.put("title", success.getString("title"));
                        cv.put("post_id", postId);
                        cv.put("date_added", Jenjobs.date(null, "yyyy-MM-dd hh:mm:ss", null));
                        cv.put("date_closed", success.getString("date_closed"));
                        tableBookmark.addBookmark(cv);

                        // post to server
                        JSONObject obj = new JSONObject();
                        obj.put("post_id", postId);
                        String[] param = {Jenjobs.BOOKMARK_URL + "?access-token=" + accessToken,obj.toString()};
                        PostRequest postRequest = new PostRequest();
                        postRequest.execute(param);
                    } catch (JSONException e) {
                        Log.e("bookmark", e.getMessage());
                    }
                }
            }
        });
        String[] args = {Jenjobs.JOB_DETAILS+"/"+postId};
        g.execute(args);
    }

    public void deleteBookmark( int postId ){
        // delete bookmark
        tableBookmark.deleteBookmark(postId);

        // delete job
        tableJob.deleteJob(postId);

        // post to server
        String[] param = {Jenjobs.BOOKMARK_URL + "/"+postId+"?access-token=" + accessToken,"{}"};
        new DeleteRequest().execute(param);
    }
}
