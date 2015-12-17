package com.example.william.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class ProfileActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * list of fragment numbers
     */
    public static final int PROFILE_FRAGMENT = 1;
    public static final int JOB_FRAGMENT = 2;
    public static final int APPLICATION_FRAGMENT = 3;
    public static final int SETTINGS_FRAGMENT = 4;
    public static final int ONLINE_RESUME_FRAGMENT = 5;
    public static final int LOG_OUT_FRAGMENT = 6;

    /*
    * profile
    * */

    /*
    * job search
    * */
    public static final int FETCH_FILTER_PARAM = 1;

    /*
    * online resume
    * */
    public static final int ADD_WORK_EXP = 10;
    public static final int UPDATE_WORK_EXP = 11;
    public static final int ADD_EDU = 12;
    public static final int UPDATE_EDU = 13;
    public static final int UPDATE_RESUME_VISIBILITY = 14;
    public static final int UPDATE_JOB_SEEKING = 15;
    public static final int UPDATE_JOB_PREFERENCE = 16;
    public static final int ADD_SKILL = 17;
    public static final int UPDATE_SKILL = 18;
    public static final int ADD_LANGUAGE = 19;
    public static final int UPDATE_LANGUAGE = 20;
    public static final int UPDATE_ATTACHED_RESUME = 21;
    public static final int UPDATE_ADDITIONAL_INFO = 22;

    private static TextView resumeVisibility;
    private static TextView jobSeeking;
    private static TextView jobPreference;
    private static LinearLayout skill;
    private static TextView additionalInfo;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    SharedPreferences sharedPref;

    private DialogFragment filterDialog;
    private String JOBFILTER = "job_filter";

    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // dialog setup
        filterDialog = new JobSearchFilterDialog();

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        mNavigationDrawerFragment.setHasOptionsMenu(true);

        sharedPref = this.getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        String accessToken = sharedPref.getString("access_token", null);

        int jsProfileId = sharedPref.getInt("js_profile_id", 0);
        Log.e("js_profile_id", ""+jsProfileId);

        String emailAddress = sharedPref.getString("email", null);
        Log.e("email", ""+emailAddress);

        // redirect to login if no access token found
        if (accessToken == null) {
            Intent intent2 = new Intent();
            intent2.setClass(getApplicationContext(), MainActivity.class);
            startActivity(intent2);
            finish();
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getBoolean("downloadData")) {
                Log.e("start", "downloading data:"+extras.getBoolean("downloadData"));
                new DownloadDataTask().execute();
            }else{
                Log.e("start", "downloadData not true");
            }
        }else{
            Log.e("start", "downloadData is null");
        }

        this.context = getApplicationContext();
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Log.e("selectedPos", ""+position);
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case PROFILE_FRAGMENT:
                mTitle = getString(R.string.my_profile);
                break;
            case JOB_FRAGMENT:
                mTitle = getString(R.string.job_search);
                break;
            case APPLICATION_FRAGMENT:
                mTitle = getString(R.string.application);
                break;
            case SETTINGS_FRAGMENT:
                mTitle = getString(R.string.action_settings);
                break;
            case ONLINE_RESUME_FRAGMENT:
                mTitle = getString(R.string.online_resume);
                break;
        }
        restoreActionBar();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        if( actionBar != null ){
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    private static int sectionNumber;
    private static JobSearch jobSearch;

    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private TextView sectionLabel;
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNo) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNo);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = null;

            switch (sectionNumber) {
                case PROFILE_FRAGMENT:
                    rootView = inflater.inflate(R.layout.profile_layout, container, false);
                    setupProfileFragment(rootView);
                    break;
                case JOB_FRAGMENT:
                    rootView = inflater.inflate(R.layout.job_layout, container, false);
                    setupJobFragment(rootView);
                    break;
                case APPLICATION_FRAGMENT:
                    rootView = inflater.inflate(R.layout.fragment_profile, container, false);
                    setupApplicationFragment(rootView);
                    break;
                case SETTINGS_FRAGMENT:
                    rootView = inflater.inflate(R.layout.fragment_profile, container, false);
                    setupSettingsFragment(rootView);
                    break;
                case ONLINE_RESUME_FRAGMENT:
                    rootView = inflater.inflate(R.layout.online_resume_layout, container, false);
                    setupOnlineResumeFragment(rootView);
                    break;
                case LOG_OUT_FRAGMENT:
                    Intent _intent = new Intent(context, MainActivity.class);
                    SharedPreferences pref = getActivity().getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
                    pref.edit().clear().commit();

                    (new TableProfile(getActivity())).truncate();

                    getActivity().startActivity(_intent);
                    getActivity().finish();
                    break;
            }

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);

            int sectionNo = getArguments().getInt(ARG_SECTION_NUMBER);
            ((ProfileActivity) activity).onSectionAttached(sectionNo);

            sectionNumber = sectionNo;
        }

        private void setupProfileFragment(View rootView) {
            TableProfile tProfile = new TableProfile(getActivity());
            Profile theProfile = tProfile.getProfile();
            Log.e("profile", "" + theProfile);
            final TextView fullName = (TextView) rootView.findViewById(R.id.fullName);
            fullName.setText( ""+theProfile.name );

            //LinearLayout fullNameContainer = (LinearLayout)rootView.findViewById(R.id.fullNameContainer);
            Button buttonUpdateProfile = (Button)rootView.findViewById(R.id.buttonUpdateProfile);
            buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), UpdateProfile.class);
                    getActivity().startActivityForResult(intent, ADD_WORK_EXP);
                }
            });


            ImageView profileImage = (ImageView) rootView.findViewById(R.id.profile_image);
            if( theProfile.photo_file != null ){
                new ImageLoad(theProfile.photo_file, profileImage).execute();
            }

        }


        private void setupJobFragment(View rootView) {
            ListView lv = (ListView) rootView.findViewById(R.id.job_list_view);
            final JobSearchAdapter jobSearchAdapter = new JobSearchAdapter(getActivity());

            jobSearch = new JobSearch(jobSearchAdapter);
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
                            Log.e("load", "more more");
                            jobSearch.setPage( jobSearch.getPage()+1 );
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


        private void setupApplicationFragment(View rootView) {
            sectionLabel = (TextView) rootView.findViewById(R.id.section_label);
            if (sectionLabel != null) {
                sectionLabel.setText("xxx=" + sectionNumber);
            }
        }


        private void setupSettingsFragment(View rootView) {
            sectionLabel = (TextView) rootView.findViewById(R.id.section_label);
            if (sectionLabel != null) {
                sectionLabel.setText("xxx=" + sectionNumber);
            }
        }


        private void setupOnlineResumeFragment(View rootView) {
            /*
            * add work exp
            * update work exp is from the dynamic content
            * */
            Button addWorkExp = (Button)rootView.findViewById(R.id.add_work_exp);
            addWorkExp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(),  UpdateWorkExperience.class);
                    getActivity().startActivityForResult(intent, ADD_WORK_EXP);
                }
            });

            /*
            * add education
            * */
            Button addEdu = (Button)rootView.findViewById(R.id.add_education);
            addEdu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(),  UpdateEducation.class);
                    getActivity().startActivityForResult(intent, ADD_EDU);
                }
            });

            /*
            * resume visibility
            * */
            resumeVisibility = (TextView)rootView.findViewById(R.id.resume_visibility);
            LinearLayout updateResumeVisibility = (LinearLayout)rootView.findViewById(R.id.updateResumeVisibility);
            updateResumeVisibility.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), UpdateResumeVisibility.class);
                    getActivity().startActivityForResult(intent, UPDATE_RESUME_VISIBILITY);
                }
            });

            /*
            * job seeking info
            * */
            jobSeeking = (TextView) rootView.findViewById(R.id.jobseeking_information);
            LinearLayout updateJobSeekingInformation = (LinearLayout)rootView.findViewById(R.id.updateJobSeekingInformation);
            updateJobSeekingInformation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), UpdateJobSeeking.class);
                    getActivity().startActivityForResult(intent, UPDATE_JOB_SEEKING);
                }
            });

            /*
            * job preference
            * */
            jobPreference = (TextView) rootView.findViewById(R.id.job_preferences);
            LinearLayout updateJobPreferences = (LinearLayout)rootView.findViewById(R.id.updateJobPreferences);
            updateJobPreferences.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), UpdateJobPreference.class);
                    getActivity().startActivityForResult(intent, UPDATE_JOB_PREFERENCE);
                }
            });

            /*
            * skills
            * */
            skill = (LinearLayout) rootView.findViewById(R.id.listOfSkill);
            Button addSkillButton = (Button)rootView.findViewById(R.id.add_skill);
            addSkillButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), UpdateSkill.class);
                    getActivity().startActivityForResult(intent, ADD_SKILL);
                }
            });

            /*
            * language
            * */


            /*
            * attached resume
            * */


            /*
            * additional info
            * */
            additionalInfo = (TextView) rootView.findViewById(R.id.additional_info);
            LinearLayout updateAdditionalInfo = (LinearLayout)rootView.findViewById(R.id.updateAdditionalInfo);
            updateAdditionalInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), UpdateAdditionalInfo.class);
                    getActivity().startActivityForResult(intent, UPDATE_ADDITIONAL_INFO);
                }
            });
        }

    }


    /*
    * fragment action bar
    * on fragment menu item selected
    * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int clickedItem = item.getItemId();

        switch (sectionNumber) {
            case PROFILE_FRAGMENT:
                return super.onOptionsItemSelected(item);
            case JOB_FRAGMENT:
                if( clickedItem == R.id.filter_job_button ){
                    //filterDialog.show(getSupportFragmentManager(), JOBFILTER);
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), JobSearchFilter.class);
                    startActivityForResult(intent, ProfileActivity.FETCH_FILTER_PARAM);
                    return true;
                }else{
                    return super.onOptionsItemSelected(item);
                }
            case APPLICATION_FRAGMENT:
                return super.onOptionsItemSelected(item);
            case SETTINGS_FRAGMENT:
                return super.onOptionsItemSelected(item);
            case ONLINE_RESUME_FRAGMENT:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /*
    * inflate menu to each fragment
    * */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        switch (sectionNumber) {
            case PROFILE_FRAGMENT:
                inflater.inflate(R.menu.profile, menu);
                break;
            case JOB_FRAGMENT:
                inflater.inflate(R.menu.job_search_menu, menu);
                break;
            case APPLICATION_FRAGMENT:

                break;
            case SETTINGS_FRAGMENT:

                break;
            case ONLINE_RESUME_FRAGMENT:

                break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        Log.e("requestCode", ""+requestCode);
        Log.e("requestCode2", ""+UPDATE_RESUME_VISIBILITY);
        Log.e("resultCode", ""+resultCode);

        Bundle extra = null;
        if (resultCode == RESULT_OK) {
            extra = data.getExtras();
        }


        // Check which request we're responding to
        if (requestCode == FETCH_FILTER_PARAM) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                Log.e("filterdata", extra.getString("result"));
                Log.e("filterdata", extra.toString());
            }
        }else if( requestCode == ADD_WORK_EXP ){
            if (resultCode == RESULT_OK) {
                Log.e("filterdata", extra.getString("result"));
                Log.e("filterdata", extra.toString());
            }
        }else if( requestCode == UPDATE_WORK_EXP ){
            if (resultCode == RESULT_OK) {
                Log.e("filterdata", extra.getString("result"));
                Log.e("filterdata", extra.toString());
            }
        }else if( requestCode == ADD_EDU ){
            if (resultCode == RESULT_OK) {
                Log.e("filterdata", extra.getString("result"));
                Log.e("filterdata", extra.toString());
            }
        }else if( requestCode == UPDATE_EDU ){
            if (resultCode == RESULT_OK) {
                Log.e("filterdata", extra.getString("result"));
                Log.e("filterdata", extra.toString());
            }
        }else if( requestCode == UPDATE_RESUME_VISIBILITY ){
            if (resultCode == RESULT_OK) {
                Log.e("filterdata", extra.getString("selectedvisibility"));
                Log.e("filterdata", extra.toString());

                resumeVisibility.setText(extra.getString("selectedvisibility"));
            }
        }else if( requestCode == UPDATE_JOB_SEEKING ){
            if (resultCode == RESULT_OK) {
                Log.e("filterdata", extra.getString("result"));
                Log.e("filterdata", extra.toString());
            }
        }else if( requestCode == UPDATE_JOB_PREFERENCE ){
            if (resultCode == RESULT_OK) {
                Log.e("filterdata", extra.getString("result"));
                Log.e("filterdata", extra.toString());
            }
        }else if( requestCode == ADD_SKILL ){
            if (resultCode == RESULT_OK) {
                String skillName = extra.getString("skill_name");
                final int skillId = extra.getInt("skill_id");
                Log.e("skill_id", ""+skillId);

                if( skill.findViewById(R.id.emptyText) != null ){
                    ((ViewGroup)skill).removeView(skill.findViewById(R.id.emptyText));
                }

                View v = getLayoutInflater().inflate(R.layout.each_skill, null);
                skill.addView(v);

                ((Button)v.findViewById(R.id.deleteSkillButton)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("clicked", "open edit modal:" + skillId);
                    }
                });
                ((TextView)v.findViewById(R.id.skillText)).setText(skillName);

            }
        }else if( requestCode == UPDATE_SKILL ){
            if (resultCode == RESULT_OK) {
                Log.e("filterdata", extra.getString("result"));
                Log.e("filterdata", extra.toString());
            }
        }else if( requestCode == ADD_LANGUAGE ){
            if (resultCode == RESULT_OK) {
                Log.e("filterdata", extra.getString("result"));
                Log.e("filterdata", extra.toString());
            }
        }else if( requestCode == UPDATE_LANGUAGE ){
            if (resultCode == RESULT_OK) {
                Log.e("filterdata", extra.getString("result"));
                Log.e("filterdata", extra.toString());
            }
        }else if( requestCode == UPDATE_ATTACHED_RESUME ){
            if (resultCode == RESULT_OK) {
                Log.e("filterdata", extra.getString("result"));
                Log.e("filterdata", extra.toString());
            }
        }else if( requestCode == UPDATE_ADDITIONAL_INFO ){
            if (resultCode == RESULT_OK) {
                Log.e("filterdata", extra.getString("info"));
                additionalInfo.setText(extra.getString("info"));
            }
        }
    }

    // download data after success login
    public class DownloadDataTask extends AsyncTask<Void, Void, JSONObject> {

        DownloadDataTask(){}

        private String accessToken;

        @Override
        protected JSONObject doInBackground(Void... params) {
            Object _response = null;

            accessToken = sharedPref.getString("access_token", null);
            String url = "http://api.jenjobs.com/jobseeker/profile?access-token="+accessToken;

            final HttpClient httpclient = new DefaultHttpClient();
            final HttpGet httpget = new HttpGet( url );
            httpget.addHeader("Content-Type", "application/json");
            httpget.addHeader("Accept", "application/json");
            HttpResponse _http_response = null;

            try {
                _http_response = httpclient.execute(httpget);
                HttpEntity _entity = _http_response.getEntity();
                InputStream is = _entity.getContent();
                String responseString = JenHttpRequest.readInputStreamAsString(is);
                _response = JenHttpRequest.decodeJsonObjectString(responseString);
            } catch (ClientProtocolException e) {
                Log.e("ClientProtocolException", e.getMessage());
            } catch (UnsupportedEncodingException e) {
                Log.e("UnsupportedEncoding", e.getMessage());
            } catch (IOException e) {
                Log.e("IOException", e.getMessage());
            }

            return (JSONObject) _response;
        }

        @Override
        protected void onPostExecute(final JSONObject success) {
            if( success != null ){
                Log.e("success", success.toString());
                int js_profile_id = 0;

                // remove _link
                success.remove("_link");

                TableProfile tblProfile = new TableProfile(getApplicationContext());
                ContentValues cv = new ContentValues();

                /*
                Iterator a = success.keys();
                while ( a.hasNext() ){
                    String key = String.valueOf(a.next());
                    try {
                        cv.put(key, String.valueOf(success.get(key)));
                    } catch (JSONException e) {
                        Log.e("jsonexc", e.getMessage());
                    }
                }

                */

                try {
                    cv.put("access_token", String.valueOf(accessToken));
                    cv.put("_id", String.valueOf(success.get("id")));

                    cv.put("email", String.valueOf(success.get("email")));
                    cv.put("username", String.valueOf(success.get("username")));
                    cv.put("name", String.valueOf(success.get("name")));
                    cv.put("ic_no", String.valueOf(success.get("ic_no")));
                    cv.put("passport_no", String.valueOf(success.get("passport_no")));
                    cv.put("mobile_no", String.valueOf(success.get("mobile_no")));
                    cv.put("gender", String.valueOf(success.get("gender")));
                    cv.put("dob", String.valueOf(success.get("dob")));
                    cv.put("pr", String.valueOf(success.get("pr")));
                    cv.put("resume_file", String.valueOf(success.get("resume_file")));
                    cv.put("photo_file", String.valueOf(success.get("photo_file")));
                    cv.put("access", String.valueOf(success.get("access")));
                    cv.put("status", String.valueOf(success.get("status")));
                    cv.put("country_id", String.valueOf(success.get("country_id")));
                    cv.put("driving_license", String.valueOf(success.get("driving_license")));
                    cv.put("transport", String.valueOf(success.get("transport")));
                    cv.put("js_jobseek_status_id", String.valueOf(success.get("js_jobseek_status_id")));
                    cv.put("availability", String.valueOf(success.get("availability")));
                    cv.put("availability_unit", String.valueOf(success.get("availability_unit")));
                    cv.put("address", String.valueOf(success.get("address")));
                    cv.put("no_work_exp", String.valueOf(success.get("no_work_exp")));
                    cv.put("additional_info", String.valueOf(success.get("info")));
                    cv.put("created_at", String.valueOf(success.get("created_at")));
                    cv.put("updated_at", String.valueOf(success.get("updated_at")));

                } catch (JSONException e) {
                    Log.e("jsonexc", e.getMessage());
                }

                Long newId = tblProfile.addProfile(cv);
                js_profile_id = newId.intValue();
                Log.e("js_profile_id", ""+js_profile_id);

                SharedPreferences.Editor spEdit = sharedPref.edit();
                spEdit.putInt("js_profile_id", js_profile_id);
                spEdit.commit();
            }else{
                Log.e("success", "null");
            }
        }
    }

}
