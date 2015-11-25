package com.example.william.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ProfileActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * list of fragment numbers
    * */
    public static final int PROFILE_FRAGMENT = 1;
    public static final int ONLINE_RESUME_FRAGMENT = 5;
    public static final int JOB_FRAGMENT = 2;
    public static final int APPLICATION_FRAGMENT = 3;
    public static final int SETTINGS_FRAGMENT = 4;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    SharedPreferences sharedPref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp( R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        sharedPref = this.getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if( extras != null ){
            if( extras.getString("downloadData") != null ){
                Log.e("start", "downloading data.");
            }
        }

        String accessToken = sharedPref.getString("access_token", null);
        //Log.e("accessToken", accessToken);
        if( accessToken == null ){
            Intent intent2 = new Intent();
            intent2.setClass(getApplicationContext(), MainActivity.class);
            startActivity(intent2);
            finish();
        }
    }



    @Override
    public void onNavigationDrawerItemSelected(int position) {
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
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private TextView sectionLabel;
        private int sectionNumber;
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

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = null;

            switch (sectionNumber) {
                case PROFILE_FRAGMENT:
                    rootView = inflater.inflate(R.layout.profile_layout, container, false);
                    setupProfileFragment(rootView);
                    break;
                case JOB_FRAGMENT:
                    rootView = inflater.inflate(R.layout.fragment_profile, container, false);
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

        }

        private void setupJobFragment(View rootView) {
            sectionLabel = (TextView) rootView.findViewById(R.id.section_label);
            if( sectionLabel != null ){
                sectionLabel.setText("xxx="+sectionNumber);
            }


        }

        private void setupApplicationFragment(View rootView) {
            sectionLabel = (TextView) rootView.findViewById(R.id.section_label);
            if( sectionLabel != null ){
                sectionLabel.setText("xxx="+sectionNumber);
            }


        }

        private void setupSettingsFragment(View rootView) {
            sectionLabel = (TextView) rootView.findViewById(R.id.section_label);
            if( sectionLabel != null ){
                sectionLabel.setText("xxx="+sectionNumber);
            }


        }

        private void setupOnlineResumeFragment(View rootView){

        }

    }

}
