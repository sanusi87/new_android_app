package com.example.william.myapplication;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class JobSearch {
    //private String url = "http://api.jenjobs.com/jobs/search";
    private ArrayList<String> filters = new ArrayList<String>();
    private ArrayList<String> orders = new ArrayList<String>();
    private String defaultOrder = "o=date_posted";
    private int page = 1;
    private JobSearchAdapter adapter;
    private ProgressBar loading;

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
            setPage(1);
        }

        GetRequest gr = new GetRequest( getSearchUrl() );
        gr.execute();
    }

    public void setKeyword(String keyword){
        if( keyword != null && keyword.length() > 0 ){
            filters.add("keyword=" + keyword);
            setOrderPreference("relevance");
        }
    }

    public void setKeywordFilter( String kFilter ){
        filters.add( "search_by="+kFilter );
    }

    public void setOrderPreference( String order ){
        if( order != null ){
            orders.clear();
            orders.add("o=" + order);
        }
    }

    public void setPage( int page ){
        if( page >= 0 ){
            this.page = page;
            //filters.add( "page="+page );
        }
    }

    public int getPage(){
        return this.page;
    }

    public void setJobLevel( String[] jobLevel ){
        if( jobLevel.length > 0 ){
            for (String aJobLevel : jobLevel) {
                filters.add("job_level_id[]=" + aJobLevel);
            }
        }
    }

    public void setJobSpec( String[] jobSpec ){
        if( jobSpec.length > 0 ){
            for (String aJobSpec : jobSpec) {
                filters.add("job_spec_id[]=" + aJobSpec);
            }
        }
    }

    public void setJobRole( String[] jobRole ){
        if( jobRole.length > 0 ){
            for (String aJobRole : jobRole) {
                filters.add("job_role_id[]=" + aJobRole);
            }
        }
    }

    public void setCountry( String[] country ){
        if( country.length > 0 ){
            for (String aCountry : country) {
                filters.add("country_id[]=" + aCountry);
            }
        }
    }

    public void setState( String[] state ){
        if( state.length > 0 ){
            for (String aState : state) {
                filters.add("state_id[]=" + aState);
            }
        }
    }

    public void setJobType( String[] types ){
        if( types.length > 0 ){
            for (String type : types) {
                filters.add("job_type_id[]=" + type);
            }
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
        String filterUrl = "?page="+this.page + "&" + TextUtils.join("&", filters) + "&" + TextUtils.join("&", orders);
        Log.e("filter", filterUrl);
        return Jenjobs.JOB_DETAILS+filterUrl;
    }

    public void resetFilter(){
        filters.clear();
    }

    public void setLoading(ProgressBar loading) {
        this.loading = loading;
    }

    // async task
    public class GetRequest extends AsyncTask<Void, Void, Object> {

        public String _url;
        public int OBJECT_TYPE = 1;

        GetRequest( String target ){
            this._url = target;
        }

        @Override
        protected Object doInBackground(Void... params) {
            Object _response = null;

            final HttpClient httpclient = new DefaultHttpClient();
            final HttpGet httpget = new HttpGet(_url);
            httpget.addHeader("Content-Type", "application/json");
            httpget.addHeader("Accept", "application/json");

            try {
                HttpResponse _http_response = httpclient.execute(httpget);
                HttpEntity _entity = _http_response.getEntity();
                InputStream is = _entity.getContent();

                String responseString = JenHttpRequest.readInputStreamAsString(is);
                if( responseString.substring(0,1).equals("[") ){
                    _response = JenHttpRequest.decodeJsonArrayString(responseString);
                    OBJECT_TYPE = JenHttpRequest.JSON_ARRAY;
                }else{
                    _response = JenHttpRequest.decodeJsonObjectString(responseString);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return _response;
        }


        @Override
        protected void onPostExecute(final Object success) {
            if( success != null ){
                ArrayList<JSONObject> arr = new ArrayList<JSONObject>();
                JSONObject jObj = (JSONObject) success;
                JSONArray jArr = jObj.optJSONArray("data");
                if( jArr != null ){
                    for( int i=0;i< jArr.length();i++ ){
                        try {
                            arr.add(jArr.getJSONObject(i));
                        } catch (JSONException e) {
                            Log.e("injecting", "" + e.getMessage());
                        }
                    }
                    adapter.setJob( arr );
                    adapter.notifyDataSetChanged();

                    if( loading != null ){
                        loading.setVisibility(View.GONE);
                    }
                }
            }
        }
    }
}
