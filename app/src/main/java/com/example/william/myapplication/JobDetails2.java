package com.example.william.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class JobDetails2 extends ActionBarActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    TextView positionTitle;
    int jobPostingId = 0;
    static JSONObject jobDetails = null;
    ProgressBar loading;
    Button applyButton;

    SharedPreferences sharedPref;
    String accessToken;

    TableProfile tableProfile;
    TableJobPreference tableJobPreference;
    TableJobPreferenceLocation tableJobPreferenceLocation;
    TableApplication tableApplication;
    TableJob tableJob;
    TableWorkExperience tableWorkExperience;
    TableEducation tableEducation;
    TableSkill tableSkill;
    TableLanguage tableLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details2);

        jobDetails = null;
        sharedPref = this.getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        accessToken = sharedPref.getString("access_token", null);

        // -----
        tableProfile = new TableProfile(this);
        tableJobPreference = new TableJobPreference(this);
        tableJobPreferenceLocation = new TableJobPreferenceLocation(this);
        tableApplication = new TableApplication(this);
        tableJob = new TableJob(this);
        tableWorkExperience = new TableWorkExperience(this);
        tableEducation = new TableEducation(this);
        tableSkill = new TableSkill(this);
        tableLanguage = new TableLanguage(this);
        // -----

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if( extras != null ){
            jobPostingId = extras.getInt("post_id");
            String[] param = {Jenjobs.JOB_DETAILS+"/"+jobPostingId};
            new GetJobRequest().execute(param);
        }else{
            Toast.makeText(getApplicationContext(), "Job posting ID not found!", Toast.LENGTH_SHORT).show();
        }

        positionTitle = (TextView)findViewById(R.id.positionTitle);
        applyButton = (Button)findViewById(R.id.applyButton);
        applyButton.setEnabled(false);
        applyButton.setClickable(false);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Profile profile = tableProfile.getProfile();

                ArrayList<String> errors = new ArrayList<String>();

                // if got resume file, send application without checking anything
                if( profile.resume_file != null && profile.resume_file.length() > 0 ){

                }else{
                    // check for profile completeness
                    if( profile.country_id == 0 ){
                        errors.add("Please set your nationality!");
                    }

                    if( profile.dob == null ){
                        errors.add("Please set your date of birth!");
                    }

                    if( profile.gender == null ){
                        errors.add("Please set gender!");
                    }

                    if( profile.mobile_no == null ){
                        errors.add("Please set mobile number!");
                    }

                    if( profile.access == null ){
                        errors.add("Please update your resume visibility!");
                    }

                    if( profile.js_jobseek_status_id == 0 ){
                        errors.add("Please update your jobseeking infomation!");
                    }

                    // work exp
                    if( !profile.no_work_exp ){ // if no_work_exp == false // got work experience
                        // check for entered work exp
                        Cursor works = tableWorkExperience.getWorkExperience();
                        if( works.getCount() == 0 ){
                            errors.add("Please add your work experience!");
                        }
                        works.close();
                    }

                    // education
                    Cursor edus = tableEducation.getEducation();
                    if( edus.getCount() == 0 ){
                        errors.add("Please add your qualification!");
                    }
                    edus.close();

                    // skills
                    Cursor skills = tableSkill.getSkill();
                    if( skills.getCount() == 0 ){
                        errors.add("Please add your skills!");
                    }
                    skills.close();

                    // languages
                    Cursor languages = tableLanguage.getLanguage(null);
                    if( languages.getCount() == 0 ){
                        errors.add("Please add your language proficiencies!");
                    }
                    languages.close();
                }

                if( errors.size() == 0 ){
                    // TODO - save to local db (applicationTable)
                    ContentValues cv = new ContentValues();
                    cv.put("post_id", jobPostingId);
                    cv.put("status", TableApplication.STATUS_UNPROCESSED);
                    cv.put("date_created", Jenjobs.date(null, null, "yyyy-MM-dd hh:mm:ss"));
                    cv.put("title", jobDetails.optString("title"));
                    cv.put("closed", 0);
                    tableApplication.addApplication(cv);

                    // TODO - post to server
                    String[] params = {Jenjobs.APPLICATION_URL+"/"+jobPostingId+"?access-token="+accessToken, "{}"};
                    new SubmitApplication().execute(params);

                    // TODO - disabled button
                    applyButton.setEnabled(false);
                    applyButton.setClickable(false);

                    // save the job
                    Cursor jobs = tableJob.getJob(jobPostingId);
                    if( jobs.getCount() == 0 ){
                        ContentValues cv2 = new ContentValues();
                        cv2.put("id", jobPostingId);
                        cv2.put("title", jobDetails.optString("title"));
                        cv2.put("company", jobDetails.optString("company"));
                        cv2.put("job_data", jobDetails.toString());
                        tableJob.addJob(cv2);
                    }
                    jobs.close();

                    //finish();
                }else{
                    Toast.makeText(getApplicationContext(), TextUtils.join(". ", errors), Toast.LENGTH_SHORT).show();
                }
            }
        });

        loading = (ProgressBar)findViewById(R.id.progressBar);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {}

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);

            View rootView = null;
            int SECTION_OVERVIEW = 1;
            int SECTION_REQUIREMENTS = 2;
            int SECTION_EMPLOYER = 3;
            if( sectionNumber == SECTION_OVERVIEW){
                rootView = inflater.inflate(R.layout.job_details_overview, container, false);
                setupOverview(rootView);
            }else if( sectionNumber == SECTION_REQUIREMENTS){
                rootView = inflater.inflate(R.layout.job_details_requirements, container, false);
                setupDescription(rootView);
            }else if( sectionNumber == SECTION_EMPLOYER){
                rootView = inflater.inflate(R.layout.job_details_employer, container, false);
                setupEmployer(rootView);
            }

            return rootView;
        }


    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }

    /*
    * download job data
    * */
    public class GetJobRequest extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject _response = null;

            final HttpClient httpclient = new DefaultHttpClient();
            final HttpGet httpget = new HttpGet(params[0]);
            httpget.addHeader("Content-Type", "application/json");
            httpget.addHeader("Accept", "application/json");
            try {
                HttpResponse _http_response = httpclient.execute(httpget);
                HttpEntity _entity = _http_response.getEntity();
                InputStream is = _entity.getContent();

                String responseString = JenHttpRequest.readInputStreamAsString(is);
                _response = JenHttpRequest.decodeJsonObjectString(responseString);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return _response;
        }

        @Override
        protected void onPostExecute(JSONObject success) {
            jobDetails = success;
            Log.e("onPostEx", "" + success);
            if( success != null ){
                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
                mViewPager = (ViewPager) findViewById(R.id.container);
                mViewPager.setAdapter(mSectionsPagerAdapter);
                positionTitle.setText(success.optString("title"));
                loading.setVisibility(View.GONE);

                Cursor application = tableApplication.getApplication(jobPostingId);
                if( application.getCount() > 0 ){
                    applyButton.setText(getResources().getString(R.string.already_applied));
                    applyButton.setOnClickListener(null);
                }else{
                    applyButton.setEnabled(true);
                    applyButton.setClickable(true);
                }
            }
        }
    }

    /*
    * function used to set textviews
    * */
    public static void setupOverview(View v) {
        if( jobDetails != null ){
            if( jobDetails.optString("location") != null ){
                ((TextView)v.findViewById(R.id.location)).setText( Html.fromHtml(jobDetails.optString("location")) );
            }else{
                v.findViewById(R.id.location).setVisibility(View.GONE);
            }

            ((TextView)v.findViewById(R.id.jobType)).setText(jobDetails.optString("type"));
            ((TextView)v.findViewById(R.id.jobLevel)).setText(jobDetails.optString("level"));
            ((TextView)v.findViewById(R.id.jobSpecialisation)).setText( Html.fromHtml(jobDetails.optString("specialisation")) );
            ((TextView)v.findViewById(R.id.qualification)).setText(jobDetails.optString("education"));
            ((TextView)v.findViewById(R.id.skill)).setText(jobDetails.optString("skills"));
            ((TextView)v.findViewById(R.id.language)).setText(jobDetails.optString("languages"));
            ((TextView)v.findViewById(R.id.closedOn)).setText( Jenjobs.date(jobDetails.optString("date_closed"), "dd MMM yyyy", "yyyy-MM-dd hh:mm:ss") );
        }
    }

    public static void setupDescription(View v) {
        if( jobDetails != null ){
            ((TextView)v.findViewById(R.id.description)).setText(Html.fromHtml(jobDetails.optString("description")));
        }
    }

    public static void setupEmployer(View v) {
        if( jobDetails != null ){
            try {
                JSONObject companyDetails = new JSONObject(jobDetails.getString("company_details"));

                ImageView companyLogo = (ImageView)v.findViewById(R.id.companyLogo);
                new ImageLoad(companyDetails.getString("logo_file"), companyLogo).execute();

                ((TextView)v.findViewById(R.id.companyName)).setText( jobDetails.optString("company") );
                ((TextView)v.findViewById(R.id.companyRegistrationNumber)).setText( companyDetails.optString("registration_no") );
                ((TextView)v.findViewById(R.id.companyIndustry)).setText( companyDetails.optString("industry") );
                ((TextView)v.findViewById(R.id.companyWorkingHours)).setText( companyDetails.optString("work_hour") );
                ((TextView)v.findViewById(R.id.companyBenefits)).setText( companyDetails.optString("benefits") );
                ((TextView)v.findViewById(R.id.companyWebsite)).setText( companyDetails.optString( "website" ) );
                ((TextView)v.findViewById(R.id.companyFacebookPage)).setText( companyDetails.optString( "fb_page" ) );

                ImageView workLocationImage = (ImageView)v.findViewById(R.id.workLocationImage);
                new ImageLoad(companyDetails.getString("map_image"), workLocationImage).execute();

            } catch (JSONException e) {
                Log.e("compErr", e.getMessage());
            }
        }
    }
    /*
    * function used to set textviews
    * */

    /*
    * post application
    * */
    public class SubmitApplication extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject _response = null;

            final HttpClient httpclient = new DefaultHttpClient();
            final HttpPost httppost = new HttpPost( params[0] ); // 0=url

            httppost.addHeader("Content-Type", "application/json");
            httppost.addHeader("Accept", "application/json");

            try {
                StringEntity entity = new StringEntity(params[1]); // 1=JSON string of post data
                entity.setContentEncoding(HTTP.UTF_8);
                entity.setContentType("application/json");
                httppost.setEntity(entity);

                HttpResponse _http_response = httpclient.execute(httppost);
                HttpEntity _entity = _http_response.getEntity();
                InputStream is = _entity.getContent();
                String responseString = JenHttpRequest.readInputStreamAsString(is);
                Log.e("respp", responseString);
                _response = JenHttpRequest.decodeJsonObjectString(responseString);
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
        protected void onPostExecute(JSONObject response) {
            Log.e("onPostEx", "" + response);
            if( response != null ){

            }
        }
    }
}
