package com.example.william.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details2);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //getActionBar().setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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
        Button applyButton = (Button)findViewById(R.id.applyButton);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO - save to local db


                // TODO - post to server


                // TODO - disabled button

                //finish();
            }
        });
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static int SECTION_OVERVIEW = 1;
        private static int SECTION_REQUIREMENTS = 2;
        private static int SECTION_EMPLOYER = 3;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            // TODO - inflate different view based on section number

            View rootView = inflater.inflate(R.layout.fragment_job_details2, container, false);
            //TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));

            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            if( sectionNumber == SECTION_OVERVIEW ){
                rootView = inflater.inflate(R.layout.job_details_overview, container, false);
                setupOverview(rootView);
            }else if( sectionNumber == SECTION_REQUIREMENTS ){
                rootView = inflater.inflate(R.layout.job_details_requirements, container, false);
                setupDescription(rootView);
            }else if( sectionNumber == SECTION_EMPLOYER ){
                rootView = inflater.inflate(R.layout.job_details_employer, container, false);
                setupEmployer(rootView);
            }

            return rootView;
        }

        public void setupOverview(View v) {
            ((TextView)v.findViewById(R.id.location)).setText(jobDetails.optString("location"));
            ((TextView)v.findViewById(R.id.location)).setText(jobDetails.optString("location"));
            ((TextView)v.findViewById(R.id.location)).setText(jobDetails.optString("location"));
        }

        public void setupDescription(View v) {

        }

        public void setupEmployer(View v) {

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
                positionTitle.setText(success.optString("title"));
            }
        }
    }


}
