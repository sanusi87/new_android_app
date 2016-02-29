package com.example.william.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JobSearchActivity extends ActionBarActivity {

    JobSearch jobSearch;
    private Bundle searchParameters = new Bundle();
    public static final int FETCH_FILTER_PARAM = 1;

    private Context context;
    SharedPreferences sharedPref;
    static String accessToken = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.job_layout);
        setTitle("Job Search");

        if( getSupportActionBar() != null ){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        context = getApplicationContext();
        sharedPref = this.getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        accessToken = sharedPref.getString("access_token", null);

        ProgressBar loading = (ProgressBar) findViewById(R.id.progressBar4);
        ListView lv = (ListView) findViewById(R.id.job_list_view);
        JobSearchAdapter jobSearchAdapter = new JobSearchAdapter(this);

        jobSearch = new JobSearch(jobSearchAdapter);
        jobSearch.setLoading( loading );

        Bundle extra = getIntent().getExtras();
        if( extra != null ){
            String searchProfileParameters = extra.getString("search_profile");
            if( searchProfileParameters != null ){
                try {
                    JSONObject searchProfile = new JSONObject(searchProfileParameters);

                    int search_by = searchProfile.getInt("search_by");
                    String keyword = searchProfile.getString("keyword");
                    int salary_min = searchProfile.getInt("salary_min");
                    int salary_max = searchProfile.getInt("salary_max");
                    boolean direct_employer = searchProfile.getBoolean("direct_employer");
                    boolean advertiser = searchProfile.getBoolean("advertiser");

                    /*
                    * TODO
                    * searchParameters is a bundle of serializable objects, not a list of native objects
                    * it was used to repopulate the JobSearchFilter inputs, so that user are able to update their job search
                    * if you want to populate serializable, you have to create the object
                    *
                    * exp: KeywordFilter kf = new KeywordFilter(ID, NAME);
                    *
                    * */

                    jobSearch.setKeywordFilter(search_by);

                    if( searchProfile.getString("keyword") != null ){
                        jobSearch.setKeyword(keyword);
                    }

                    if( salary_min > 0 ){
                        jobSearch.setSalaryMin(salary_min);
                    }

                    if( salary_max > 0 ){
                        jobSearch.setSalaryMax(salary_max);
                    }

                    if( !searchProfile.isNull("job_spec_id") ){
                        JSONArray _jobSpecs = new JSONArray(searchProfile.getString("job_spec_id"));
                        if( _jobSpecs.length() > 0 ){
                            Integer[] jobSpecs = new Integer[_jobSpecs.length()];
                            ArrayList<Integer> jobRoles = new ArrayList<>();

                            for( int i=0;i<_jobSpecs.length();i++ ){
                                JSONObject _jobSpec = new JSONObject(_jobSpecs.getString(i));
                                jobSpecs[i] = _jobSpec.getInt("id");

                                JSONArray _jobRoles = _jobSpec.getJSONArray("job_role_id");
                                if( _jobRoles.length() > 0 ){
                                    for( int j=0;j<_jobRoles.length();j++ ){
                                        jobRoles.add(_jobRoles.getInt(j));
                                    }
                                }
                            }


                            if( jobRoles.size() > 0 ){
                                jobSearch.setJobRole(jobRoles.toArray(new Integer[jobRoles.size()]));
                            }

                            jobSearch.setJobSpec(jobSpecs);
                        }
                    }

                    if( !searchProfile.isNull("job_type_id") ){
                        JSONArray _jobTypes = new JSONArray(searchProfile.getString("job_type_id"));
                        if( _jobTypes.length() > 0 ){
                            Integer[] jobTypes = new Integer[_jobTypes.length()];
                            for( int i=0;i<_jobTypes.length();i++ ){
                                jobTypes[i] = _jobTypes.getInt(i);
                            }
                            jobSearch.setJobType(jobTypes);
                        }
                    }

                    if( !searchProfile.isNull("job_level_id") ){
                        JSONArray _jobLevels = new JSONArray(searchProfile.getString("job_level_id"));
                        if( _jobLevels.length() > 0 ){
                            Integer[] jobLevels = new Integer[_jobLevels.length()];
                            for( int i=0;i<_jobLevels.length();i++ ){
                                jobLevels[i] = _jobLevels.getInt(i);
                            }
                            jobSearch.setJobLevel(jobLevels);
                        }
                    }

                    if( !searchProfile.isNull("country_id") ){
                        JSONArray _countries = searchProfile.getJSONArray("country_id");
                        if( _countries.length() > 0 ){
                            Integer[] countries = new Integer[_countries.length()];
                            for( int i=0;i<_countries.length();i++ ){
                                countries[i] = _countries.getInt(i);
                            }
                            jobSearch.setCountry(countries);
                        }
                    }

                    if( !searchProfile.isNull("state_id") ){
                        JSONArray _states = searchProfile.getJSONArray("state_id");
                        if( _states.length() > 0 ){
                            Integer[] states = new Integer[_states.length()];
                            for( int i=0;i<_states.length();i++ ){
                                states[i] = _states.getInt(i);
                            }
                            jobSearch.setCountry(states);
                        }
                    }

                    jobSearch.setAdvertiser(advertiser);
                    jobSearch.setDirectEmployer(direct_employer);

                } catch (JSONException e) {
                    Log.e("err", e.getMessage()+e.getLocalizedMessage());
                }
            }

            KeywordFilter kf = (KeywordFilter) extra.get("keyword_filter");
            if( kf != null ){
                jobSearch.setKeywordFilter(kf.id);
                searchParameters.putSerializable("keyword_filter", kf);
            }

            String keyword = extra.getString("keyword");
            if( keyword != null && keyword.length() > 0 ){
                jobSearch.setKeyword(keyword);
                searchParameters.putString("keyword", keyword);
            }
        }

        jobSearch.search(true);

        lv.setAdapter(jobSearchAdapter);
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            int previousLastPosition = 0;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                final int lastPosition = firstVisibleItem + visibleItemCount;
                if (lastPosition == totalItemCount) {
                    if (previousLastPosition != lastPosition) {
                        jobSearch.setPage(jobSearch.getPage() + 1);
                        jobSearch.search(false);
                    }
                    previousLastPosition = lastPosition;
                } else if (lastPosition < previousLastPosition - 5) {
                    resetLastIndex();
                }
            }

            public void resetLastIndex() {
                previousLastPosition = 0;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.job_fragment_menu, menu);

        if( accessToken == null ){
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int clickedItem = item.getItemId();
        if( clickedItem == R.id.filter_job_button ){
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), JobSearchFilter.class);
            // send back the intent
            intent.putExtra("searchParameters", searchParameters);
            startActivityForResult(intent, FETCH_FILTER_PARAM);
            return true;
        }else if( clickedItem == R.id.about_job_ads ) {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), AboutJobSearch.class);
            startActivity(intent);
            return true;
        }else if( clickedItem == R.id.save_filter_button ){
            if( !searchParameters.isEmpty() ){
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), JobSearchProfileForm.class);
                intent.putExtra("searchParameter", searchParameters);
                startActivity(intent);
            }else{
                Toast.makeText(context, "Please update your search parameter first.", Toast.LENGTH_LONG).show();
            }
            return true;
        }else if( clickedItem == R.id.job_search_profile_list ){
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), JobSearchProfile.class);
            startActivity(intent);
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == FETCH_FILTER_PARAM) {
                Bundle extra = data.getExtras();

                jobSearch.resetFilter();
                searchParameters.clear();
                KeywordFilter keywordFilter = (KeywordFilter) extra.get("keyword_filter");
                if( keywordFilter != null ){
                    jobSearch.setKeywordFilter(keywordFilter.id);
                    searchParameters.putSerializable("keyword_filter", keywordFilter);
                }

                String keyword = extra.getString("keyword");
                if( keyword != null ){
                    jobSearch.setKeyword(keyword);
                    searchParameters.putString("keyword", keyword);
                }

                String _salaryMin = extra.getString("salary_min");
                if( _salaryMin != null ){
                    int salaryMin = Integer.valueOf(_salaryMin);
                    jobSearch.setSalaryMin(salaryMin);
                    searchParameters.putString("salary_min", _salaryMin);
                }

                String _salaryMax = extra.getString("salary_max");
                if( _salaryMax != null ){
                    int salaryMax = Integer.valueOf(_salaryMax);
                    jobSearch.setSalaryMax(salaryMax);
                    searchParameters.putString("salary_max", _salaryMax);
                }

                ArrayList jobSpecs = (ArrayList) extra.get("job_spec");
                if( jobSpecs != null && jobSpecs.size() > 0 ){
                    ArrayList<Integer> _jobSpec = new ArrayList<>();
                    for(Object jobSpec : jobSpecs){
                        JobSpec __jobSpec = (JobSpec)jobSpec;
                        _jobSpec.add(__jobSpec.id);
                    }
                    jobSearch.setJobSpec(_jobSpec.toArray(new Integer[_jobSpec.size()]));
                    searchParameters.putIntegerArrayList("job_spec", _jobSpec);
                }

                ArrayList jobRoles = (ArrayList) extra.get("job_role");
                if( jobRoles != null && jobRoles.size() > 0 ){
                    ArrayList<Integer> _jobRole = new ArrayList<>();
                    for(Object jobRole : jobRoles){
                        JobRole __jobRole = (JobRole)jobRole;
                        _jobRole.add(__jobRole.id);
                    }
                    jobSearch.setJobRole(_jobRole.toArray(new Integer[_jobRole.size()]));
                    searchParameters.putIntegerArrayList("job_role", _jobRole);
                }

                ArrayList jobTypes = (ArrayList) extra.get("job_type");
                if( jobTypes != null && jobTypes.size() > 0 ){
                    ArrayList<Integer> _jobType = new ArrayList<>();
                    for(Object jobType : jobTypes){
                        JobType __jobType = (JobType)jobType;
                        _jobType.add(__jobType.id);
                    }
                    jobSearch.setJobType(_jobType.toArray(new Integer[_jobType.size()]));
                    searchParameters.putIntegerArrayList("job_type", _jobType);
                }

                ArrayList states = (ArrayList) extra.get("state");
                if( states != null && states.size() > 0 ){
                    ArrayList<Integer> _state = new ArrayList<>();
                    for(Object state : states){
                        State __state = (State)state;
                        _state.add(__state.id);
                    }
                    jobSearch.setState(_state.toArray(new Integer[_state.size()]));
                    searchParameters.putIntegerArrayList("state", _state);
                }

                ArrayList countries = (ArrayList) extra.get("country");
                if( countries != null && countries.size() > 0 ){
                    ArrayList<Integer> _country = new ArrayList<>();
                    for(Object country : countries){
                        Country __country = (Country)country;
                        _country.add(__country.id);
                    }
                    jobSearch.setCountry(_country.toArray(new Integer[_country.size()]));
                    searchParameters.putIntegerArrayList("country", _country);
                }

                ArrayList positionLevels = (ArrayList) extra.get("position_level");
                //Log.e("positionLevels", ""+positionLevels);
                if( positionLevels != null && positionLevels.size() > 0 ){
                    ArrayList<Integer> _positionLevel = new ArrayList<>();
                    for(Object positionLevel : positionLevels){
                        PositionLevel __positionLevel = (PositionLevel)positionLevel;
                        _positionLevel.add(__positionLevel.id);
                    }
                    jobSearch.setJobLevel(_positionLevel.toArray(new Integer[_positionLevel.size()]));
                    searchParameters.putIntegerArrayList("position_level", _positionLevel);
                }else{
                    if( positionLevels != null ){
                        Log.e("positionLevels", ""+positionLevels.size());
                    }
                }


                boolean directEmployer = extra.getBoolean("direct_employer");
                jobSearch.setDirectEmployer(directEmployer);
                searchParameters.putBoolean("direct_employer", directEmployer);

                boolean recruitmentAgency = extra.getBoolean("recruitment_agency");
                jobSearch.setAdvertiser(recruitmentAgency); // same different
                searchParameters.putBoolean("recruitment_agency", recruitmentAgency);

                // TODO--
                jobSearch.search(true);
            }
        }
    }
}
