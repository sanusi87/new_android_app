package com.example.william.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class JobSuggestion extends ActionBarActivity{
    //private LinearLayout tabButton;
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private static Context context;
    private static Activity activity;

    static JobSearchProfileAdapter adapter;
    private static final int SELECT_MATCHED_DATE = 1;
    private static String matchedOn;
    static TextView tMatchedOn;

    Menu menu;
    String mTitle = "Matched Jobs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_suggestion);
        setTitle(mTitle);

        context = getApplicationContext();
        activity = this;

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
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

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if( position == 0 ){
                    mTitle = "Matched Jobs";
                    menu.getItem(0).setVisible(true);
                    menu.getItem(1).setVisible(true);
                }else if( position == 1 ){
                    menu.getItem(0).setVisible(false);
                    menu.getItem(1).setVisible(false);
                    mTitle = "Suggested Jobs";
                }
                restoreActionBar();
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        // set today's date
        matchedOn = Jenjobs.date(null, "yyyy-MM-dd", null);
    }

    @Override
    public void onResume() {
        super.onResume();
        if( adapter != null ){
            adapter.loadItem(true);
            adapter.notifyDataSetChanged();
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if( actionBar != null ){
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.job_suggestion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int clickedItem = item.getItemId();
        if( clickedItem == R.id.job_search_profile_list ){
            Intent intent = new Intent(getApplicationContext(), JobSearchProfile.class);
            startActivity(intent);
            return true;
        }else if( clickedItem == R.id.about_job_matcher ){
            Intent intent = new Intent(getApplicationContext(), AboutJobMatcher.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
            LinearLayout noItem = (LinearLayout)v.findViewById(R.id.no_item);
            ((TextView)noItem.findViewById(R.id.noticeText)).setText(R.string.no_job_search_profile);

            // use job search profile adapter
            adapter = new JobSearchProfileAdapter(context);
            // customize adapter
            adapter.showDeleteButton(false);
            adapter.setActivity(activity);
            // by default, pass today's date to adapter
            adapter.setMatchedOn(matchedOn);

            // list the job matcher profile, show the total number of matched jobs for today(changeable)
            ListView lv = (ListView)v.findViewById(R.id.listOfJobSearchProfile);
            lv.setAdapter(adapter);

            tMatchedOn = (TextView)v.findViewById(R.id.selectedDate);
            tMatchedOn.setText(matchedOn);

            (v.findViewById(R.id.selectDate)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SelectJobMatcherDate.class);
                    activity.startActivityForResult(intent, SELECT_MATCHED_DATE);
                }
            });
        }

        private void setupSuggestedJobs(View v){
            // TODO - setup suggested jobs
            LinearLayout noItem = (LinearLayout)v.findViewById(R.id.no_item);
            ((TextView)noItem.findViewById(R.id.noticeText)).setText(R.string.no_suggested_jobs);

            ListView suggestedJobs = (ListView)v.findViewById(R.id.suggestedJobs);
            SuggestedJobsAdapter suggestedJobsAdapter = new SuggestedJobsAdapter(context);
            suggestedJobsAdapter.setActivity(activity);
            suggestedJobs.setAdapter(suggestedJobsAdapter);

            if( suggestedJobs.getCount() == 0 ){
                suggestedJobs.setVisibility(View.GONE);
                noItem.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_MATCHED_DATE) {
                Bundle extra = data.getExtras();
                // update selected date which has been previously assigned to adapter
                matchedOn = extra.getString("date_added");
                adapter.setMatchedOn(matchedOn);
                adapter.notifyDataSetChanged();
                tMatchedOn.setText(matchedOn);
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
