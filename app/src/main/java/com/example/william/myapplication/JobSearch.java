package com.example.william.myapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

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
    private String url = "http://api.jenjobs.com/jobs/search";
    private ArrayList<String> filters = new ArrayList<String>();
    private ArrayList<String> orders = new ArrayList<String>();
    private String defaultOrder = "o=date_posted";
    public int page = 1;
    private JobSearchAdapter adapter;

    JobSearch(JobSearchAdapter adapter){
        orders.add(defaultOrder);
        this.adapter = adapter;
    }

    public void search(){
        GetRequest gr = new GetRequest( getSearchUrl() );
        gr.execute();
    }

    public void setKeyword(String keyword){
        if( keyword != null && keyword.length() > 0 ){
            filters.add( "keyword="+keyword );
            setOrderPreference( "relevance" );
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
        }
    }

    public void setJobLevel( String[] jobLevel ){
        if( jobLevel.length > 0 ){
            for( int i=0; i < jobLevel.length; i++ ){
                filters.add( "job_level_id[]="+jobLevel[i] );
            }
        }
    }

    public void setJobSpec( String[] jobSpec ){
        if( jobSpec.length > 0 ){
            for( int i=0; i < jobSpec.length; i++ ){
                filters.add( "job_spec_id[]="+jobSpec[i] );
            }
        }
    }

    public void setJobRole( String[] jobRole ){
        if( jobRole.length > 0 ){
            for( int i=0; i < jobRole.length; i++ ){
                filters.add( "job_role_id[]="+jobRole[i] );
            }
        }
    }

    public void setCountry( String[] country ){
        if( country.length > 0 ){
            for( int i=0; i < country.length; i++ ){
                filters.add( "country_id[]="+country[i] );
            }
        }
    }

    public void setState( String[] state ){
        if( state.length > 0 ){
            for( int i=0; i < state.length; i++ ){
                filters.add( "state_id[]="+state[i] );
            }
        }
    }

    public void setJobType( String[] types ){
        if( types.length > 0 ){
            for( int i=0; i < types.length; i++ ){
                filters.add( "job_type_id[]="+types[i] );
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
        String filterUrl = "?" + TextUtils.join("&", filters) + "&" + TextUtils.join("&", orders);
        Log.e("filter", filterUrl);
        return url+filterUrl;
    }

    public void resetFilter(){
        filters.clear();
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

            HttpResponse _http_response = null;
            try {
                _http_response = httpclient.execute(httpget);
                HttpEntity _entity = _http_response.getEntity();
                InputStream is = _entity.getContent();

                String responseString = JenHttpRequest.readInputStreamAsString(is);
                if( responseString.substring(0,1).equals("[") ){
                    _response = JenHttpRequest.decodeJsonArrayString(responseString);
                    OBJECT_TYPE = JenHttpRequest.JSON_ARRAY;
                }else{
                    _response = JenHttpRequest.decodeJsonObjectString(responseString);
                }
                //return _response;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return _response;
        }


        @Override
        protected void onPostExecute(final Object success) {
            Log.e("onPostEx", "" + success);
            if( success != null ){
                Log.e("onPostEx", ""+success);

                ArrayList<JSONObject> arr = new ArrayList<JSONObject>();
                JSONObject jObj = (JSONObject) success;
                if( jObj != null ){
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
                    }
                }
            }
        }
    }
}
