package com.example.william.myapplication;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JobDetails extends ActionBarActivity {

    TextView positionTitle;
    int jobPostingId = 0;
    int invitationId = 0;
    static JSONObject jobDetails = null;
    ProgressBar loading;
    Button applyButton;
    LinearLayout noItem;
    SharedPreferences sharedPref;
    String accessToken;
    TableProfile tableProfile;
    TableApplication tableApplication;
    TableJob tableJob;
    LinearLayout tabButton;
    ViewPager mViewPager;
    SectionsPagerAdapter mSectionsPagerAdapter;

    static Context context;
    static boolean isOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);

        context = this;
        jobDetails = null;
        sharedPref = this.getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        accessToken = sharedPref.getString("access_token", null);
        isOnline = Jenjobs.isOnline(getApplicationContext());

        // -----
        tableProfile = new TableProfile(this);
        tableApplication = new TableApplication(this);
        tableJob = new TableJob(this);
        // -----

        final LinearLayout applyButtonContainer = (LinearLayout)findViewById(R.id.applyButtonContainer);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if( extras != null ){
            jobPostingId = extras.getInt("post_id");
            invitationId = extras.getInt("invitation_id");

            String[] param = {Jenjobs.JOB_DETAILS+"/"+jobPostingId};
            GetRequest g = new GetRequest();
            g.setResultListener(new GetRequest.ResultListener() {
                @Override
                public void processResultArray(JSONArray result) {}

                @Override
                public void processResult(JSONObject success) {
                    Log.e("onPostEx", "" + success);
                    jobDetails = success;
                    if( success != null ){
                        mViewPager.setAdapter(mSectionsPagerAdapter);
                        positionTitle.setText(success.optString("title"));

                        applyButtonContainer.setVisibility(View.VISIBLE);
                        tabButton.setVisibility(View.VISIBLE);
                    }else{
                        Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_SHORT).show();

                        Cursor job = tableJob.getJob(jobPostingId);
                        if( job.moveToFirst() ){
                            String savedJobDetails = job.getString(3);
                            try {
                                jobDetails = new JSONObject(savedJobDetails);
                                mViewPager.setAdapter(mSectionsPagerAdapter);
                                positionTitle.setText(jobDetails.getString("title"));

                                applyButtonContainer.setVisibility(View.VISIBLE);
                                tabButton.setVisibility(View.VISIBLE);
                            } catch (JSONException e) {
                                Log.e("err", e.getMessage());
                            }
                        }else{
                            // if network error
                            noItem.setVisibility(View.VISIBLE);
                        }
                        job.close();
                    }
                    loading.setVisibility(View.GONE);
                }
            });
            g.execute(param);
        }else{
            Toast.makeText(getApplicationContext(), "Job posting ID not found!", Toast.LENGTH_SHORT).show();
        }

        // ------------- custom tab button
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        tabButton = (LinearLayout)findViewById(R.id.tabButton);
        final Button overviewButton = (Button)findViewById(R.id.overviewButton);
        Button descriptionButton = (Button)findViewById(R.id.descriptionButton);
        Button companyButton = (Button)findViewById(R.id.companyButton);

        overviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0, true);
            }
        });

        descriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(1, true);
            }
        });

        companyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(2, true);
            }
        });
        // ------------- custom tab button

        positionTitle = (TextView)findViewById(R.id.positionTitle);
        applyButton = (Button)findViewById(R.id.applyButton);
        applyButton.setEnabled(false);
        applyButton.setClickable(false);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _v) {
                if( isOnline ){
                    ArrayList<String> errors = tableProfile.isProfileComplete();
                    if( errors.size() == 0 ){
                        // TODO - save to local db (applicationTable)
                        ContentValues cv = new ContentValues();
                        cv.put("post_id", jobPostingId);
                        cv.put("status", TableApplication.STATUS_UNPROCESSED);
                        cv.put("date_created", Jenjobs.date(null, "yyyy-MM-dd hh:mm:ss", null));
                        cv.put("title", jobDetails.optString("title"));
                        cv.put("closed", 0);
                        tableApplication.addApplication(cv);

                        // attach invitation together with apply POST
                        // TODO - handle invitation in apply POST request
                        JSONObject obj = new JSONObject();
                        if( invitationId > 0 ){
                            try {
                                obj.put("emp_invitation_id", invitationId);
                            } catch (JSONException e) {
                                Log.e("err", "failed to put invitation ID to post");
                            }
                        }

                        // post to server
                        String[] params = { Jenjobs.APPLICATION_URL+"/"+jobPostingId+"?access-token="+accessToken,obj.toString() };

                        PostRequest postRequest = new PostRequest();
                        postRequest.setResultListener(new PostRequest.ResultListener() {
                            @Override
                            public void processResult(JSONObject success) {
                                if( success != null ){
                                    Toast.makeText(getApplicationContext(), success.optString("status_text"), Toast.LENGTH_SHORT).show();

                                    // if success, update invitation
                                    if( invitationId > 0 && success.optInt("status_code") == 1 ){
                                        TableInvitation tableInvitation = new TableInvitation(context);
                                        ContentValues cv = new ContentValues();
                                        cv.put("status", TableInvitation.STATUS_APPLIED);
                                        cv.put("date_updated", Jenjobs.date(null,"yyyy-MM-dd hh:mm:ss",null));
                                        tableInvitation.updateInvitation(cv, invitationId);
                                    }
                                }else{
                                    Toast.makeText(getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        postRequest.execute(params);

                        // disabled button
                        applyButton.setEnabled(false);
                        applyButton.setClickable(false);
                        applyButton.setText(getString(R.string.application_sent));

                        // save the job
                        Cursor jobs = tableJob.getJob(jobPostingId);
                        if( jobs.getCount() == 0 ){
                            ContentValues cv2 = new ContentValues();
                            cv2.put("id", jobPostingId);
                            cv2.put("title", jobDetails.optString("title"));
                            cv2.put("company", jobDetails.optString("company"));
                            cv2.put("date_closed", jobDetails.optString("date_closed"));
                            cv2.put("job_data", jobDetails.toString());
                            tableJob.addJob(cv2);
                        }
                        jobs.close();
                    }else{
                        Toast.makeText(getApplicationContext(), TextUtils.join(". ", errors), Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), R.string.offline_notification, Toast.LENGTH_LONG).show();
                }
            }
        });

        loading = (ProgressBar)findViewById(R.id.progressBar);
        LinearLayout ll = (LinearLayout)findViewById(R.id.loginLayout);
        TextView notice = (TextView)ll.findViewById(R.id.noticeText);
        notice.setText(R.string.login_or_register_to_apply);

        // hide apply, show login+register
        if( accessToken == null ){
            ll.setVisibility(View.VISIBLE);
            applyButton.setVisibility(View.GONE);
        }

        // check for application
        if( jobPostingId > 0 ){
            Cursor application = tableApplication.getApplication(jobPostingId);
            if( application.getCount() > 0 ){
                applyButton.setText(getResources().getString(R.string.already_applied));
                applyButton.setOnClickListener(null);
            }else{
                applyButton.setEnabled(true);
                applyButton.setClickable(true);
            }
        }

        // handle login button
        Button loginButton = (Button)findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // handle register button
        Button registerButton = (Button)findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        noItem = (LinearLayout)findViewById(R.id.no_item);
        ((TextView)findViewById(R.id.noticeText2)).setText(R.string.network_error);
        ((ImageView)findViewById(R.id.noticeIcon2)).setImageDrawable(getResources().getDrawable(R.drawable.ic_warning_black_24dp));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.job_details, menu);
        // TODO - change this to show(true)/hide(false) menu
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bookmark:
                // TODO - bookmarkThisJob();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
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

                final ImageView companyLogo = (ImageView)v.findViewById(R.id.companyLogo);
                final ProgressBar progressBar = (ProgressBar)v.findViewById(R.id.progressBar);
                ImageLoad empLogo = new ImageLoad(companyDetails.getString("logo_file"), companyLogo);
                empLogo.setResultListener(new ImageLoad.ResultListener() {
                    @Override
                    public void processResult(Bitmap result) {
                        if( result != null ){
                            companyLogo.setVisibility(View.VISIBLE);
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
                empLogo.execute();

                ((TextView)v.findViewById(R.id.companyName)).setText( jobDetails.optString("company") );
                ((TextView)v.findViewById(R.id.companyRegistrationNumber)).setText(companyDetails.optString("registration_no"));
                ((TextView)v.findViewById(R.id.companyIndustry)).setText(companyDetails.optString("industry"));
                ((TextView)v.findViewById(R.id.companyWorkingHours)).setText(companyDetails.optString("work_hour"));
                ((TextView)v.findViewById(R.id.companyBenefits)).setText(companyDetails.optString("benefits"));
                ((TextView)v.findViewById(R.id.companyWebsite)).setText(companyDetails.optString("website"));
                ((TextView)v.findViewById(R.id.companyFacebookPage)).setText(companyDetails.optString("facebook_page"));

                final ImageView workLocationImage = (ImageView)v.findViewById(R.id.workLocationImage);
                final ProgressBar progressBar3 = (ProgressBar)v.findViewById(R.id.progressBar3);
                ImageLoad img = new ImageLoad(companyDetails.getString("map_image"), workLocationImage);
                img.setResultListener(new ImageLoad.ResultListener() {
                    @Override
                    public void processResult(Bitmap result) {
                        workLocationImage.setVisibility(View.VISIBLE);
                        if( result == null ){
                            workLocationImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                            workLocationImage.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        }
                        progressBar3.setVisibility(View.GONE);
                    }
                });
                img.execute();

                // this is work location
                final String latitude = jobDetails.optString("latitude");
                final String longitude = jobDetails.optString("longitude");

                if( Float.parseFloat(latitude) > 0 && Float.parseFloat(longitude) > 0 ){
                    workLocationImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Uri gmmIntentUri = Uri.parse("google.streetview:cbll=46.414382,10.013988");
                            Uri gmmIntentUri = Uri.parse("geo:"+latitude+","+longitude+"?q="+latitude+","+longitude+"("+jobDetails.optString("company")+")&z=16");
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                                context.startActivity(mapIntent);
                            }else{
                                Toast.makeText(context, "Google Maps not found!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            } catch (JSONException e) {
                Log.e("compErr", e.getMessage());
            }
        }
    }
}
