package com.example.william.myapplication;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

public class JobSearchAdapter extends BaseAdapter implements ListAdapter{
    private Context context;
    private ArrayList<JSONObject> jobList = null;
    private String url = "http://api.jenjobs.com/jobs/search";
    private ArrayList<String> filters = null;
    private ArrayList<String> orders = null;
    private String defaultOrder = "o=date_posted";
    public int page = 1;

    public JobSearchAdapter( Context context ){
        this.context = context;
        orders.add(defaultOrder);
    }

    public void searchJob(){
        JenHttpRequest jenReq = new JenHttpRequest(JenHttpRequest.GET_REQUEST, getSearchUrl(), null);
        while( jenReq.response == null ){
            // wait
        }
        Log.e("response2", "" + jenReq.response);


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
        return filterUrl;
    }

    public void resetFilter(){
        filters.clear();
    }
    // ---------

    @Override
    public int getCount() {
        return jobList.size();
    }

    @Override
    public JSONObject getItem(int position) {
        return jobList.get(position);
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
            v = vi.inflate(R.layout.each_job_summary, parent, false);
        }

        // get a job
        JSONObject p = getItem(position);

        // set the Text IDs
        if( p != null ){
            TextView jobTitle = (TextView) v.findViewById(R.id.job_title);
            jobTitle.setText(p.optString("title"));
            final int postId = p.optInt("post_id");

            jobTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, JobDetails.class);
                    intent.putExtra("post_id", postId);
                    context.startActivity(intent);
                }
            });

            int showSalary = p.optInt( "salary_display" );
            String salary = null;
            if( showSalary == 0 ){
                salary = "Salary undisclosed";
            }else{
                String currency = p.optString("currency");
                salary = currency + p.optInt("salary_min")+" - "+ p.optInt("salary_max");
            }
            ((TextView) v.findViewById(R.id.salary)).setText( salary );

            ((TextView) v.findViewById(R.id.company_name)).setText(p.optString("company_name"));
            ((TextView) v.findViewById(R.id.job_location)).setText( p.optString("job_location") );
            ((TextView) v.findViewById(R.id.job_type)).setText( p.optString("job_type") );
            ((TextView) v.findViewById(R.id.date_closed)).setText( p.optString("date_closed") );
            ((TextView) v.findViewById(R.id.job_description)).setText( p.optString("job_desc_brief") );

        }

        return v;
    }
}
