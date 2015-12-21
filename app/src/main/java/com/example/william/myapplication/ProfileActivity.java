package com.example.william.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import org.json.JSONArray;
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
    * download sections
    * */
    private int DOWNLOAD_PROFILE = 1; //http://api.jenjobs.com/jobseeker/profile
    private int DOWNLOAD_APPLICATION = 2; //http://api.jenjobs.com/jobseeker/application
    private int DOWNLOAD_WORK_EXPERIENCE = 3; //http://api.jenjobs.com/jobseeker/work-experience
    private int DOWNLOAD_EDUCATION = 4; //http://api.jenjobs.com/jobseeker/qualification
    private int DOWNLOAD_JOB_PREFERENCE = 5; //http://api.jenjobs.com/jobseeker/job-preference
    private int DOWNLOAD_SKILL = 6; //http://api.jenjobs.com/jobseeker/skill
    private int DOWNLOAD_LANGUAGE = 7; //http://api.jenjobs.com/jobseeker/language
    private int DOWNLOAD_BOOKMARK = 8; //http://api.jenjobs.com/jobseeker/bookmark
    private int DOWNLOAD_SUBSCRIPTION = 9; //http://api.jenjobs.com/jobseeker/subscription

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
    public static final int UPDATE_PROFILE = 23;

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
        String emailAddress = sharedPref.getString("email", null);

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
                Log.e("start", "downloading data:" + extras.getBoolean("downloadData"));

                String[] profileUrl = {Jenjobs.PROFILE_URL};
                new DownloadDataTask(DOWNLOAD_PROFILE).execute(profileUrl);

                String[] applicationUrl = {Jenjobs.APPLICATION_URL};
                new DownloadDataTask(DOWNLOAD_APPLICATION).execute(applicationUrl);

                String[] workExperienceUrl = {Jenjobs.WORK_EXPERIENCE_URL};
                new DownloadDataTask(DOWNLOAD_WORK_EXPERIENCE).execute(workExperienceUrl);

                String[] educationUrl = {Jenjobs.EDUCATION_URL};
                new DownloadDataTask(DOWNLOAD_EDUCATION).execute(educationUrl);

                String[] jobPreferenceUrl = {Jenjobs.JOB_PREFERENCE_URL};
                new DownloadDataTask(DOWNLOAD_JOB_PREFERENCE).execute(jobPreferenceUrl);

                String[] skillUrl = {Jenjobs.SKILL_URL};
                new DownloadDataTask(DOWNLOAD_SKILL).execute(skillUrl);

                String[] languageUrl = {Jenjobs.LANGUAGE_URL};
                new DownloadDataTask(DOWNLOAD_LANGUAGE).execute(languageUrl);

                String[] bookmarkUrl = {Jenjobs.BOOKMARK_URL};
                new DownloadDataTask(DOWNLOAD_BOOKMARK).execute(bookmarkUrl);

                String[] subscriptionUrl = {Jenjobs.SUBSCRIPTION_URL};
                new DownloadDataTask(DOWNLOAD_SUBSCRIPTION).execute(subscriptionUrl);
            }
        }

        context = getApplicationContext();
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
    private static LinearLayout profileLayout = null;

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
                    pref.edit().clear().apply();

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

            profileLayout = (LinearLayout) rootView.findViewById(R.id.profileLayout);
            TableProfile tProfile = new TableProfile(getActivity());
            Profile theProfile = tProfile.getProfile();
            TextView name = (TextView) profileLayout.findViewById(R.id.fullName);
            TextView email = (TextView) profileLayout.findViewById(R.id.email);
            TextView mobile_no = (TextView) profileLayout.findViewById(R.id.mobile_no);
            TextView ic_no = (TextView) profileLayout.findViewById(R.id.ic_no);
            TextView gender = (TextView) profileLayout.findViewById(R.id.gender);
            TextView dob = (TextView) profileLayout.findViewById(R.id.dob);
            TextView country = (TextView) profileLayout.findViewById(R.id.country);

            name.setText( theProfile.name );
            email.setText( theProfile.email );
            mobile_no.setText( theProfile.mobile_no );
            ic_no.setText( theProfile.ic );
            gender.setText( theProfile.gender );

            if( theProfile.country_id > 0 ){
                TableCountry tableCountry = new TableCountry(getActivity());
                Country c = tableCountry.findCountryById( theProfile.country_id );
                if( c != null ){
                    country.setText( c.name );
                }
            }

            String _dob = theProfile.dob;
            if( _dob != null ){
                dob.setText( Jenjobs.date(_dob, null, "yyyy-MM-dd") );
            }

            Button buttonUpdateProfile = (Button)rootView.findViewById(R.id.buttonUpdateProfile);
            buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), UpdateProfile.class);
                    getActivity().startActivityForResult(intent, UPDATE_PROFILE);
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

            TableSkill tableSkill = new TableSkill(getActivity());
            Cursor c = tableSkill.getSkill();

            c.moveToFirst();

            if( c.moveToFirst() && c.getCount() > 0 ){
                ((ViewGroup)skill).removeView(skill.findViewById(R.id.emptyText));

                while( !c.isAfterLast() ){
                    final int savedId = c.getInt(0); //id
                    final int actualId = c.getInt(1); //_id
                    String skillName = c.getString(2); //name

                    final View v = getActivity().getLayoutInflater().inflate(R.layout.each_skill, null);
                    skill.addView(v);

                    ((Button)v.findViewById(R.id.deleteSkillButton)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View vv) {
                            Log.e("clicked", "delete skill " + savedId);
                            skill.removeView(v);
                        }
                    });
                    ((TextView)v.findViewById(R.id.skillText)).setText(skillName);

                    c.moveToNext();
                }
            }

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
                        // delete
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
        }else if( requestCode == UPDATE_PROFILE ){
            if (resultCode == RESULT_OK) {
                // refresh profile
                profileLayout = (LinearLayout) findViewById(R.id.profileLayout);
                TableProfile tProfile = new TableProfile(getApplicationContext());
                Profile theProfile = tProfile.getProfile();
                TextView name = (TextView) profileLayout.findViewById(R.id.fullName);
                TextView email = (TextView) profileLayout.findViewById(R.id.email);
                TextView mobile_no = (TextView) profileLayout.findViewById(R.id.mobile_no);
                TextView ic_no = (TextView) profileLayout.findViewById(R.id.ic_no);
                TextView gender = (TextView) profileLayout.findViewById(R.id.gender);
                TextView dob = (TextView) profileLayout.findViewById(R.id.dob);
                TextView country = (TextView) profileLayout.findViewById(R.id.country);

                name.setText( theProfile.name );
                email.setText( theProfile.email );
                mobile_no.setText( theProfile.mobile_no );
                ic_no.setText( theProfile.ic );
                gender.setText( theProfile.gender );

                if( theProfile.country_id > 0 ){
                    TableCountry tableCountry = new TableCountry(getApplicationContext());
                    Country c = tableCountry.findCountryById( theProfile.country_id );
                    if( c != null ){
                        country.setText( c.name );
                    }
                }

                String _dob = theProfile.dob;
                if( _dob != null ){
                    dob.setText( Jenjobs.date(_dob, null, "yyyy-M-d") );
                }
                // refresh profile
            }
        }
    }

    /*
    * download data after success login
    * */
    public class DownloadDataTask extends AsyncTask<String, Void, String> {

        private int downloadSection = 1;

        DownloadDataTask( int sectionToDownload ){
            downloadSection = sectionToDownload;
        }

        private String accessToken;

        @Override
        protected String doInBackground(String... params) {
            String _response = null;

            accessToken = sharedPref.getString("access_token", null);
            String url = params[0]+"?access-token="+accessToken;

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
                //_response = JenHttpRequest.decodeJsonObjectString(responseString);
                _response = responseString;
            } catch (ClientProtocolException e) {
                Log.e("ClientProtocolException", e.getMessage());
            } catch (UnsupportedEncodingException e) {
                Log.e("UnsupportedEncoding", e.getMessage());
            } catch (IOException e) {
                Log.e("IOException", e.getMessage());
            }

            return _response;
        }

        @Override
        protected void onPostExecute(final String nsuccess) {
            if( nsuccess != null ){
                Log.e("success", nsuccess.toString());

                if( downloadSection == DOWNLOAD_PROFILE ){

                    JSONObject success = null;
                    int js_profile_id = 0;

                    TableProfile tblProfile = new TableProfile(getApplicationContext());
                    TableAddress tblAddress = new TableAddress(getApplicationContext());
                    ContentValues cv = new ContentValues();

                    try {
                        success = new JSONObject(nsuccess);
                        success.remove("_link"); // remove _link

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
                        //cv.put("address", String.valueOf(success.get("address")));
                        cv.put("no_work_exp", String.valueOf(success.get("no_work_exp")));
                        cv.put("additional_info", String.valueOf(success.get("info")));
                        cv.put("created_at", String.valueOf(success.get("created_at")));
                        cv.put("updated_at", String.valueOf(success.get("updated_at")));

                        Long newId = tblProfile.addProfile(cv);
                        js_profile_id = newId.intValue();
                        Log.e("js_profile_id", "" + js_profile_id);

                        SharedPreferences.Editor spEdit = sharedPref.edit();
                        spEdit.putInt("js_profile_id", js_profile_id);
                        spEdit.apply();

                        // default address
                        ContentValues cv2 = new ContentValues();
                        cv2.put("address1", "");
                        cv2.put("address2", "");
                        cv2.put("postcode", 0);
                        cv2.put("city_id", 0);
                        cv2.put("city_name", "");
                        cv2.put("state_id", 0);
                        cv2.put("state_name", "");
                        cv2.put("country_id", 0);
                        cv2.put("updated_at", Jenjobs.date(null, null, null));
                        tblAddress.addAddress(cv2);

                        if( sectionNumber == PROFILE_FRAGMENT && profileLayout != null ){
                            TextView name = (TextView) profileLayout.findViewById(R.id.fullName);
                            TextView email = (TextView) profileLayout.findViewById(R.id.email);
                            TextView mobile_no = (TextView) profileLayout.findViewById(R.id.mobile_no);
                            TextView ic_no = (TextView) profileLayout.findViewById(R.id.ic_no);
                            TextView gender = (TextView) profileLayout.findViewById(R.id.gender);
                            TextView dob = (TextView) profileLayout.findViewById(R.id.dob);
                            TextView country = (TextView) profileLayout.findViewById(R.id.country);

                            name.setText( String.valueOf(success.get("name")) );
                            email.setText( String.valueOf(success.get("email")) );
                            mobile_no.setText( String.valueOf(success.get("mobile_no")) );
                            ic_no.setText( String.valueOf(success.get("ic_no")) );
                            gender.setText( String.valueOf(success.get("gender")) );
                            country.setText( String.valueOf(success.get("country")) );

                            String _dob = String.valueOf(success.get("dob"));
                            if( _dob != null ){
                                dob.setText( Jenjobs.date(_dob, null, "yyyy-MM-dd") );
                            }

                            ImageView profileImage = (ImageView) profileLayout.findViewById(R.id.profile_image);
                            if( String.valueOf(success.get("photo_file")) != null ){
                                new ImageLoad(String.valueOf(success.get("photo_file")), profileImage).execute();
                            }
                        }

                        // save address
                        String address = String.valueOf(success.get("address"));

                        if( address != null ){
                            JSONObject jsonAddr = new JSONObject(address);
                            if( jsonAddr != null ){
                                ContentValues cv3 = new ContentValues();
                                cv3.put("address1", jsonAddr.getString("address1"));
                                cv3.put("address2", jsonAddr.getString("address2"));
                                cv3.put("postcode", jsonAddr.getInt("postcode"));
                                cv3.put("city_id", jsonAddr.getInt("city_id"));
                                cv3.put("city_name", jsonAddr.getString("city_name"));
                                cv3.put("state_id", jsonAddr.getInt("state_id"));
                                cv3.put("state_name", jsonAddr.getString("state_name"));
                                cv3.put("country_id", jsonAddr.getInt("country_id"));
                                cv3.put("updated_at", jsonAddr.getString("date_updated"));
                                tblAddress.updateAddress(cv3);
                            }
                        }
                        // end save address
                    } catch (JSONException e) {
                        Log.e("profileExcp", e.getMessage());
                    }

                }else if( downloadSection == DOWNLOAD_APPLICATION ){

                    JSONArray success = null;
                    TableApplication tableApplication = new TableApplication(getApplicationContext());
                    ContentValues cv = new ContentValues();

                    try {
                        success = new JSONArray(nsuccess);
                        if( success.length() > 0 ){
                            for( int i=0;i< success.length();i++ ){
                                JSONObject s = success.getJSONObject(i);
                                cv.put("_id", s.getInt("_id"));
                                cv.put("post_id", s.getInt("post_id"));
                                cv.put("status", s.getInt("status"));
                                cv.put("date_created", s.getString("date_created"));
                                cv.put("date_updated", s.getString("date_updated"));
                                cv.put("title", s.getString("title"));
                                cv.put("closed", s.getInt("closed"));

                                tableApplication.addApplication(cv);
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("appExcp", e.getMessage());
                    }

                }else if( downloadSection == DOWNLOAD_WORK_EXPERIENCE ){

                    TableWorkExperience tableWorkExperience = new TableWorkExperience(getApplicationContext());
                    ContentValues cv = new ContentValues();
                    JSONArray success = null;

                    try {
                        success = new JSONArray(nsuccess);
                        if( success.length() > 0 ){
                            for( int i=0;i< success.length();i++ ){
                                JSONObject s = success.getJSONObject(i);

                                cv.put("_id", s.getInt("id"));
                                cv.put("position", s.optString("position"));
                                cv.put("company", s.optString("company"));

                                /////
                                JSONObject jobSpec = new JSONObject(s.optString("job_spec"));
                                cv.put("job_spec_id", jobSpec.optInt("id") > 0 ? jobSpec.optInt("id") : 0 );

                                JSONObject jobRole = new JSONObject(s.optString("job_role"));
                                cv.put("job_role_id", jobRole.optInt("id") > 0 ? jobRole.optInt("id") : 0 );

                                JSONObject jobType = new JSONObject(s.optString("job_type"));
                                cv.put("job_type_id", jobType.optInt("id") > 0 ? jobType.optInt("id") : 0 );

                                JSONObject jobLevel = new JSONObject(s.optString("job_level"));
                                cv.put("job_level_id", jobLevel.optInt("id") > 0 ? jobLevel.optInt("id") : 0 );

                                JSONObject jobIndustry = new JSONObject(s.optString("industry"));
                                cv.put("industry_id", jobIndustry.optInt("id") > 0 ? jobIndustry.optInt("id") : 0 );
                                /////

                                cv.put("experience", s.optString("experience"));
                                cv.put("salary", s.optString("salary"));
                                cv.put("started_on", s.optString("started_on"));
                                cv.put("resigned_on", s.optString("resigned_on"));
                                cv.put("update_at", "");

                                tableWorkExperience.addWorkExperience(cv);
                            }
                        }


                    } catch (JSONException e) {
                        Log.e("workExpExcp", e.getMessage());
                    }

                }else if( downloadSection == DOWNLOAD_EDUCATION ){

                    TableEducation tableEducation = new TableEducation(getApplicationContext());
                    ContentValues cv = new ContentValues();
                    JSONArray success = null;

                    try {
                        success = new JSONArray(nsuccess);

                        if( success.length() > 0 ){
                            for( int i=0;i< success.length();i++ ){
                                JSONObject s = success.getJSONObject(i);

                                cv.put("_id", s.getInt("id"));
                                cv.put("school", s.getString("school"));
                                cv.put("major", s.optString("major"));

                                JSONObject eduLevel = new JSONObject(s.optString("level"));
                                cv.put("edu_level_id", eduLevel.optInt("id") > 0 ? eduLevel.optInt("id") : 0 );

                                JSONObject eduField = new JSONObject(s.optString("field"));
                                cv.put("edu_field_id", eduField.optInt("id") > 0 ? eduField.optInt("id") : 0 );

                                cv.put("country_id", s.optString("country"));
                                cv.put("grade", s.optString("grade"));
                                cv.put("info", s.optString("info"));
                                cv.put("date_graduated", s.getString("date_graduated"));

                                tableEducation.addEducation(cv);
                            }
                        }

                    } catch (JSONException e) {
                        Log.e("eduExcp", e.getMessage());
                    }


                }else if( downloadSection == DOWNLOAD_JOB_PREFERENCE ){

                    TableJobPreference tableJobPreference = new TableJobPreference(getApplicationContext());
                    ContentValues cv = new ContentValues();
                    JSONObject success = null;
                    try {
                        success = new JSONObject(nsuccess);

                        cv.put("salary", success.optString("salary"));
                        cv.put("currency_id", success.optInt("currency_id"));
                        cv.put("state_id", success.optString("state_id"));
                        cv.put("country_id", success.optString("country_id"));
                        cv.put("job_type_id", success.optString("job_type_id"));

                        tableJobPreference.updateJobPreference(cv);
                    } catch (JSONException e) {
                        Log.e("jobPrefExcp", e.getMessage());
                    }

                }else if( downloadSection == DOWNLOAD_SKILL ){

                    TableSkill tableSkill = new TableSkill(getApplicationContext());
                    JSONArray success = null;

                    try {
                        success = new JSONArray(nsuccess);
                        if( success.length() > 0 ) {
                            for (int i = 0; i < success.length(); i++) {
                                JSONObject s = success.getJSONObject(i);

                                ContentValues cv = new ContentValues();
                                cv.put("_id", s.optInt("id"));
                                cv.put("name", s.optString("value"));
                                tableSkill.addSkill(cv);
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("skillExcp", e.getMessage());
                    }

                }else if( downloadSection == DOWNLOAD_LANGUAGE ){

                    TableLanguage tableLanguage = new TableLanguage(getApplicationContext());
                    JSONArray success = null;

                    try {
                        success = new JSONArray(nsuccess);
                        if( success.length() > 0 ) {
                            for (int i = 0; i < success.length(); i++) {
                                JSONObject s = success.getJSONObject(i);
                                ContentValues cv = new ContentValues();
                                cv.put("language_id", s.optInt("language_id"));
                                cv.put("spoken_language_level_id", s.optInt("spoken_language_level_id"));
                                cv.put("written_language_level_id", s.optInt("written_language_level_id"));
                                cv.put("native", s.optInt("native"));
                                tableLanguage.addLanguage(cv);
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("langExcp", e.getMessage());
                    }

                }else if( downloadSection == DOWNLOAD_BOOKMARK ){

                    TableBookmark tableBookmark = new TableBookmark(getApplicationContext());
                    JSONArray success = null;

                    try {
                        success = new JSONArray(nsuccess);
                        if( success.length() > 0 ) {
                            for (int i = 0; i < success.length(); i++) {
                                JSONObject s = success.getJSONObject(i);
                                ContentValues cv = new ContentValues();

                                cv.put("post_id", s.optInt("post_id"));
                                cv.put("title", s.optString("title"));
                                cv.put("date_added", s.optString("on"));
                                cv.put("date_closed", s.optString("date_closed"));

                                tableBookmark.addBookmark(cv);
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("bookExcp", e.getMessage());
                    }

                }else if( downloadSection == DOWNLOAD_SUBSCRIPTION ){

                    TableSubscription tableSubscription = new TableSubscription(getApplicationContext());
                    JSONArray success = null;

                    try {
                        success = new JSONArray(nsuccess);
                        if( success.length() > 0 ) {
                            for (int i = 0; i < success.length(); i++) {
                                JSONObject s = success.getJSONObject(i);
                                ContentValues cv = new ContentValues();
                                cv.put("status", s.optBoolean("status") ? 1 : 0);
                                int subscriptionID = s.getInt("subscription_id");
                                tableSubscription.updateSubscription(cv, subscriptionID);
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("subExcp", e.getMessage());
                    }

                }
            }else{
                Log.e("success", "null");
            }
        }
    }
}
