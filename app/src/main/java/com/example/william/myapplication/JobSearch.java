package com.example.william.myapplication;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class JobSearch {
    private ArrayList<String> filters = new ArrayList<>();
    private ArrayList<String> orders = new ArrayList<>();
    private String defaultOrder = "o=date_posted";
    private int page = 1;
    private JobSearchAdapter adapter;
    private ProgressBar loading;
    private boolean continueRequest = true;

    JobSearch(JobSearchAdapter adapter){
        orders.add(defaultOrder);
        this.adapter = adapter;
    }

    public void search(boolean newSearch){
        if( newSearch ){
            if( loading != null ){
                loading.setVisibility(View.VISIBLE);
            }

            adapter.setJob(null);
            continueRequest = true;
            setPage(1);
        }

        if( continueRequest ){
            GetRequest gr = new GetRequest();
            gr.setResultListener(new GetRequest.ResultListener() {
                @Override
                public void processResultArray(JSONArray result) {}

                @Override
                public void processResult(JSONObject success) {
                    View v = ((ViewGroup) loading.getParent());
                    LinearLayout ll = (LinearLayout) v.findViewById(R.id.no_item);

                    if (success != null) {
                        ArrayList<JSONObject> arr = new ArrayList<>();
                        JSONArray jArr = success.optJSONArray("data");

                        if (jArr != null && jArr.length() > 0) {
                            //Log.e("jArr.length", ""+jArr.length());
                            (v.findViewById(R.id.no_item)).setVisibility(View.GONE);
                            (v.findViewById(R.id.job_list_view)).setVisibility(View.VISIBLE);

                            for (int i = 0; i < jArr.length(); i++) {
                                try {
                                    arr.add(jArr.getJSONObject(i));
                                } catch (JSONException e) {
                                    Log.e("injecting", "" + e.getMessage());
                                }
                            }
                            adapter.setJob(arr);
                        } else {
                            //Log.e("page", ""+page);
                            if (page == 1) {
                                ll.setVisibility(View.VISIBLE);
                                ((TextView) ll.findViewById(R.id.noticeText)).setText(R.string.no_job_posting);
                                (v.findViewById(R.id.job_list_view)).setVisibility(View.GONE);
                            } else {
                                continueRequest = false;
                            }
                        }
                    } else {
                        ll.setVisibility(View.VISIBLE);
                        ((TextView) ll.findViewById(R.id.noticeText)).setText(R.string.no_job_posting);
                        (v.findViewById(R.id.job_list_view)).setVisibility(View.GONE);
                    }
                    loading.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
            });
            String[] args = {getSearchUrl()};
            gr.execute(args);
        }else{
            adapter.notifyDataSetChanged();
        }
    }

    public void setKeyword(String keyword){
        if( keyword != null && keyword.length() > 0 ){
            try {
                filters.add( "keyword="+URLEncoder.encode(keyword, "utf-8") );
            } catch (UnsupportedEncodingException e) {
                Log.e("error", e.getMessage());
            }
            setOrderPreference("relevance");
        }
    }

    public void setKeywordFilter( String kFilter ){
        filters.add( "search_by="+kFilter );
    }

    public void setOrderPreference( String order ){
        orders.clear();
        if( order != null ){
            orders.add("o=" + order);
        }else{
            orders.add(defaultOrder);
        }
    }

    public void setPage( int page ){
        if( page >= 0 ){
            this.page = page;
        }
    }

    public int getPage(){
        return this.page;
    }

    public void setJobLevel( String[] jobLevel ){
        if( jobLevel.length > 0 ){
            try {
                filters.add("job_level_id=" + new JSONArray(jobLevel).toString());
            } catch (JSONException e) {
                Log.e("error", e.getMessage());
            }
//            for (String aJobLevel : jobLevel) {
//                filters.add("job_level_id[]=" + aJobLevel);
//            }
        }
    }

    public void setJobSpec( String[] jobSpec ){
        if( jobSpec.length > 0 ){
            try {
                filters.add("job_spec_id=" + new JSONArray(jobSpec).toString());
            } catch (JSONException e) {
                Log.e("error", e.getMessage());
            }
//            for (String aJobSpec : jobSpec) {
//                filters.add("job_spec_id[]=" + aJobSpec);
//            }
        }
    }

    public void setJobRole( String[] jobRole ){
        if( jobRole.length > 0 ){
            try {
                filters.add("job_role_id=" + new JSONArray(jobRole).toString());
            } catch (JSONException e) {
                Log.e("error", e.getMessage());
            }
//            for (String aJobRole : jobRole) {
//                filters.add("job_role_id[]=" + aJobRole);
//            }
        }
    }

    public void setCountry( String[] country ){
        if( country.length > 0 ){
            try {
                filters.add("country_id=" + new JSONArray(country).toString());
            } catch (JSONException e) {
                Log.e("error", e.getMessage());
            }
//            for (String aCountry : country) {
//                filters.add("country_id[]=" + aCountry);
//            }
        }
    }

    public void setState( String[] state ){
        if( state.length > 0 ){
            try {
                filters.add("state_id=" + new JSONArray(state).toString());
            } catch (JSONException e) {
                Log.e("error", e.getMessage());
            }
            //for (String aState : state) {
            //    filters.add("state_id[]=" + aState);
            //}
        }
    }

    public void setJobType( String[] types ){
        if( types.length > 0 ){
            try {
                filters.add("job_type_id=" + new JSONArray(types).toString());
            } catch (JSONException e) {
                Log.e("error", e.getMessage());
            }
            //for (String type : types) {
            //    filters.add("job_type_id[]=" + type);
            //}
        }
    }

    public void setSalaryMin( int salary ){
        filters.add( "salary_min="+salary );
    }

    public void setSalaryMax( int salary ){
        filters.add( "salary_max="+salary );
    }

    public void setAdvertiser( boolean isAdvertiser ){
        if( isAdvertiser ){
            filters.add( "advertiser=1" );
        }
    }

    public void setDirectEmployer( boolean isDirectEmployer ){
        if( isDirectEmployer ){
            filters.add( "direct_employer=1" );
        }
    }

    // setup search URL
    private String getSearchUrl(){
        String filterUrl = "?page="+this.page;
        if( filters.size() > 0 ){
            filterUrl += "&" + TextUtils.join("&", filters);
        }
        filterUrl += "&" + TextUtils.join("&", orders);
        //Log.e("filter", filterUrl);
        return Jenjobs.JOB_DETAILS+filterUrl;
    }

    public void resetFilter(){
        this.page = 1;
        setOrderPreference(null);
        filters.clear();
    }

    public void setLoading(ProgressBar loading) {
        this.loading = loading;
    }
}
