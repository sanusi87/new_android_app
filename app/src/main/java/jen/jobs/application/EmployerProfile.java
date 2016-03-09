package jen.jobs.application;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONObject;

public class EmployerProfile extends ActionBarActivity {

    static JSONObject companyDetails;
    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_profile);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        context = this;
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
            return 4;
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
                case 3:
                    return "SECTION 4";
            }
            return null;
        }
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
            int SECTION_OVERVIEW = 1;
            int SECTION_ADDITIONAL_INFO = 2;
            int SECTION_COMPANY_DETAILS = 3;
            int SECTION_COMPANY_LOCATION = 4;

            if( sectionNumber == SECTION_OVERVIEW){
                rootView = inflater.inflate(R.layout.company_info, container, false);
                setupOverview(rootView);
            }else if( sectionNumber == SECTION_ADDITIONAL_INFO){
                rootView = inflater.inflate(R.layout.company_info, container, false);
                setupAdditionalInfo(rootView);
            }else if( sectionNumber == SECTION_COMPANY_DETAILS){
                rootView = inflater.inflate(R.layout.company_details, container, false);
                setupCompanyDetails(rootView);
            }else if( sectionNumber == SECTION_COMPANY_LOCATION){
                //rootView = inflater.inflate(R.layout.company_location, container, false);
                setupCompanyLocation(rootView);
            }

            return rootView;
        }
    }


    public static void setupOverview(View v) {
        if( companyDetails != null ){
            ((TextView)v.findViewById(R.id.itemTitle)).setText(context.getText(R.string.overview));
            ((TextView)v.findViewById(R.id.itemContent)).setText(Html.fromHtml(companyDetails.optString("overview")));
        }
    }

    public static void setupAdditionalInfo(View v) {
        if( companyDetails != null ){
            ((TextView)v.findViewById(R.id.itemTitle)).setText(context.getText(R.string.additional_info));
            ((TextView)v.findViewById(R.id.itemContent)).setText(Html.fromHtml(companyDetails.optString("info")));
        }
    }

    public static void setupCompanyDetails(View v) {
        if( companyDetails != null ){
            ((TextView)v.findViewById(R.id.itemTitle)).setText(context.getText(R.string.company_info));

        }
    }

    public static void setupCompanyLocation(View v) {
        if( companyDetails != null ){
            ((TextView)v.findViewById(R.id.itemTitle)).setText(context.getText(R.string.company_location));

        }
    }
}
