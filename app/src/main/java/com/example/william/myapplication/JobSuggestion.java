package com.example.william.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class JobSuggestion extends ActionBarActivity{
    private LinearLayout tabButton;
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_suggestion);

        context = getApplicationContext();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        tabButton = (LinearLayout)findViewById(R.id.tabButton);

        mViewPager.setAdapter(mSectionsPagerAdapter);

        Button jobMatcherButton = (Button)findViewById(R.id.jobMatcherButton);
        jobMatcherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0, true);
            }
        });

        Button jobSuggestedButton = (Button)findViewById(R.id.jobSuggestedButton);
        jobSuggestedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(1, true);
            }
        });
    }

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
            int SECTION_MATCHED_JOBS = 1;
            int SECTION_SUGGESTED_JOBS = 2;

            if( sectionNumber == SECTION_MATCHED_JOBS){
                rootView = inflater.inflate(R.layout.suggestion_matched_jobs, container, false);
                setupMatchedJobs(rootView);
            }else if( sectionNumber == SECTION_SUGGESTED_JOBS){
                rootView = inflater.inflate(R.layout.suggestion_suggested_jobs, container, false);
                setupSuggestedJobs(rootView);
            }
            return rootView;
        }

        private void setupMatchedJobs(View v){
            // TODO - setup matched jobs
            LinearLayout noItem = (LinearLayout)v.findViewById(R.id.no_item);
            ((TextView)noItem.findViewById(R.id.noticeText)).setText("No matched jobs!");

            ListView matchedJobs = (ListView)v.findViewById(R.id.matchedJobs);
            //matchedJobs.setAdapter();

            if( matchedJobs.getCount() == 0 ){
                matchedJobs.setVisibility(View.GONE);
                noItem.setVisibility(View.VISIBLE);
            }
        }

        private void setupSuggestedJobs(View v){
            // TODO - setup suggested jobs
            LinearLayout noItem = (LinearLayout)v.findViewById(R.id.no_item);
            ((TextView)noItem.findViewById(R.id.noticeText)).setText("No suggested jobs!");

            ListView suggestedJobs = (ListView)v.findViewById(R.id.suggestedJobs);
            //suggestedJobs.setAdapter();

            if( suggestedJobs.getCount() == 0 ){
                suggestedJobs.setVisibility(View.GONE);
                noItem.setVisibility(View.VISIBLE);
            }
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
            return 2;
        }
    }
}
