package jen.jobs.application;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ProfileActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * list of fragment numbers
     */
    public static final int PROFILE_FRAGMENT = 1;
    public static final int ONLINE_RESUME_FRAGMENT = 2;
    public static final int JOB_FRAGMENT = 3;
    public static final int APPLICATION_FRAGMENT = 4;
    public static final int INVITATION_AND_REQUEST = 5;
    public static final int BOOKMARK_FRAGMENT = 6;
    public static final int JOB_SUGGESTION = 7;
    public static final int SETTINGS_FRAGMENT = 8;
    public static final int LOG_OUT_FRAGMENT = 9;

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
    private int DOWNLOAD_INVITATION = 10;
    private int DOWNLOAD_JOBMATCHER_PROFILE = 11;

    /*
    * job search
    * */
    //public static final int FETCH_FILTER_PARAM = 1;

    /*
    * online resume
    * */
    public static final int ADD_WORK_EXP = 10;
    //public static final int UPDATE_WORK_EXP = 11;
    public static final int ADD_EDU = 12;
    //public static final int UPDATE_EDU = 13;
    public static final int UPDATE_RESUME_VISIBILITY = 14;
    public static final int UPDATE_JOB_SEEKING = 15;
    public static final int UPDATE_JOB_PREFERENCE = 16;
    public static final int ADD_SKILL = 17;
    //public static final int UPDATE_SKILL = 18;
    public static final int ADD_LANGUAGE = 19;
    //public static final int UPDATE_LANGUAGE = 20;
    //public static final int UPDATE_ATTACHED_RESUME = 21;
    public static final int UPDATE_ADDITIONAL_INFO = 22;
    public static final int UPDATE_PROFILE = 23;
    public static final int TAKE_A_PHOTO = 24;

    private static TextView resumeVisibility;
    private static TextView jobSeeking;
    private static TextView jobPreference;
    private static TextView attachedResume;
    private static LinearLayout skill;
    private static LinearLayout language;
    private static TextView additionalInfo;
    private static LinearLayout listOfWorkExp;
    private static LinearLayout listOfEducation;
    private static LinearLayout workExpQuestion;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    SharedPreferences sharedPref;
    static String accessToken = null;

    private static Context context;
    private static Activity activity;
    static ImageView profileImage;
    static String mCurrentPhotoPath;

    /*
    * list of used tables
    * */
    static TableSkill tableSkill;
    static TableWorkExperience tableWorkExperience;
    static TableEducation tableEducation;
    static TableJobPreference tableJobPreference;
    static TableLanguage tableLanguage;
    static TableJob tableJob;
    static TableProfile tableProfile;

    // if this activity was opened by notification, set the default fragment to be opened,
    // then updated it depends on the notification
    int defaultPage = 0;
    static int jsProfileId = 0;
    static boolean isOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        mNavigationDrawerFragment.setHasOptionsMenu(true);

        sharedPref = this.getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        accessToken = sharedPref.getString("access_token", null);
        jsProfileId = sharedPref.getInt("js_profile_id", 0);
        context = getApplicationContext();
        isOnline = Jenjobs.isOnline(context);
        activity = this;

        // redirect to login if no access token found
        if (accessToken == null) {
            Intent intent2 = new Intent();
            intent2.setClass(getApplicationContext(), MainActivity.class);
            startActivity(intent2);
            finish();
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getBoolean("downloadData") && isOnline) {
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

                String[] invitationUrl = {Jenjobs.INVITATION_URL};
                new DownloadDataTask(DOWNLOAD_INVITATION).execute(invitationUrl);

                String[] jobmatcherUrl = {Jenjobs.SEARCH_PROFILE};
                new DownloadDataTask(DOWNLOAD_JOBMATCHER_PROFILE).execute(jobmatcherUrl);

                extras.clear();
            }else{
                if( !isOnline ){
                    Toast.makeText(context, R.string.network_error, Toast.LENGTH_LONG).show();
                }
            }

            defaultPage = extras.getInt("defaultPage", defaultPage);
            if( defaultPage > 0 ){
                FragmentManager fragmentManager = getSupportFragmentManager();
                if( defaultPage == APPLICATION_FRAGMENT ){
                    fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(APPLICATION_FRAGMENT)).commit();
                }else if( defaultPage == JOB_SUGGESTION ){
                    fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(JOB_SUGGESTION)).commit();
                }else if( defaultPage == INVITATION_AND_REQUEST ){
                    fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(INVITATION_AND_REQUEST)).commit();
                }
            }
        }

        tableProfile = new TableProfile(context);
        tableSkill = new TableSkill(context);
        tableWorkExperience = new TableWorkExperience(context);
        tableEducation = new TableEducation(context);
        tableJobPreference = new TableJobPreference(context);
        tableLanguage = new TableLanguage(context);
        tableJob = new TableJob(getApplicationContext());

        Intent svc = new Intent(this, MainService.class);
        startService(svc);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        final int selectedItem = position+1;
        if( selectedItem == JOB_SUGGESTION ){
            Intent intent = new Intent(this, JobSuggestion.class);
            startActivity(intent);
            //finish(); // -- do not set this if you wanted to return
        }else if( selectedItem == JOB_FRAGMENT ){
            Intent intent = new Intent(this, JobSearchMain.class);
            startActivity(intent);
        }else{
            // update the main content by replacing fragments
            final FragmentManager fragmentManager = getSupportFragmentManager();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(selectedItem)).commit();
                }
            }, 0);
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case PROFILE_FRAGMENT:
                mTitle = getString(R.string.my_profile);
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
            case JOB_SUGGESTION:
                mTitle = getString(R.string.job_suggestion);
                break;
            case BOOKMARK_FRAGMENT:
                mTitle = getString(R.string.bookmark);
                break;
            case INVITATION_AND_REQUEST:
                mTitle = getString(R.string.invitation_and_request);
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
    private static LinearLayout profileLayout = null;

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

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
                case APPLICATION_FRAGMENT:
                    rootView = inflater.inflate(R.layout.application_layout, container, false);
                    setupApplicationFragment(rootView);
                    break;
                case INVITATION_AND_REQUEST:
                    rootView = inflater.inflate(R.layout.invitation_and_request_layout, container, false);
                    setupInvitationAndRequestFragment(rootView);
                    break;
                case JOB_SUGGESTION:
                    //rootView = inflater.inflate(R.layout.job_suggestion, container, false);
                    //setupJobSuggestionFragment(rootView);
                    break;
                case BOOKMARK_FRAGMENT:
                    rootView = inflater.inflate(R.layout.bookmark_layout, container, false);
                    setupBookmarkFragment(rootView);
                    break;
                case SETTINGS_FRAGMENT:
                    rootView = inflater.inflate(R.layout.settings_layout, container, false);
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

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            tableProfile.truncate();
                        }
                    }).start();

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
            final Profile theProfile = tableProfile.getProfile();
            TextView name = (TextView) profileLayout.findViewById(R.id.fullName);
            TextView email = (TextView) profileLayout.findViewById(R.id.email);
            TextView mobile_no = (TextView) profileLayout.findViewById(R.id.mobile_no);
            TextView ic_no = (TextView) profileLayout.findViewById(R.id.ic_no);
            TextView gender = (TextView) profileLayout.findViewById(R.id.gender);
            TextView dob = (TextView) profileLayout.findViewById(R.id.dob);
            TextView country = (TextView) profileLayout.findViewById(R.id.country);

            name.setText( theProfile.name );
            email.setText( theProfile.email );

            if( theProfile.mobile_no != null && !theProfile.mobile_no.equals("null") ){
                mobile_no.setText(theProfile.mobile_no);
            }

            if( theProfile.ic != null && !theProfile.ic.equals("null") ){
                ic_no.setText( theProfile.ic );
            }

            gender.setText(theProfile.gender);

            if( theProfile.country_id > 0 ){
                TableCountry tableCountry = new TableCountry(getActivity());
                Country c = tableCountry.findCountryById( theProfile.country_id );
                if( c != null ){
                    country.setText( c.name );
                }
            }

            String _dob = theProfile.dob;
            if( _dob != null && !_dob.equals("null") ){
                dob.setText( Jenjobs.date(_dob, null, "yyyy-MM-dd") );
            }

            profileImage = (ImageView) rootView.findViewById(R.id.profile_image);

            // check photo URL
            if( theProfile.photo_file != null ){
                final String fileName = theProfile.photo_file.substring(theProfile.photo_file.lastIndexOf("/")+1, theProfile.photo_file.length());

                // create the file to local internal storage
                File file = new File(context.getFilesDir(), fileName);
                if( file.exists() ){
                    profileImage.setImageDrawable(Drawable.createFromPath(file.getPath()));
                }else{
                    ImageLoad img = new ImageLoad(theProfile.photo_file, profileImage);
                    img.setResultListener(new ImageLoad.ResultListener() {
                        @Override
                        public void processResult(final Bitmap result) {
                            if( result != null ){
                                try {
                                    // done downloading the file
                                    FileOutputStream outputStream = context.openFileOutput(fileName, MODE_PRIVATE);
                                    result.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                                    outputStream.flush();
                                    outputStream.close();

                                    profileImage.setImageBitmap(result);

                                } catch (IOException e) {
                                    Log.e("fileErr", e.getMessage());
                                }
                            }
                        }
                    });
                    img.execute();

                }

                // done loading photo URL, check for photo URI, if equals, then the file has already been uploaded
                // if not, then upload the file, and update view
                if( theProfile.photo_uri != null ){
                    String urlFn = theProfile.photo_file.substring(theProfile.photo_file.lastIndexOf("/")+1, theProfile.photo_file.length());
                    String uriFn = theProfile.photo_uri.substring(theProfile.photo_uri.lastIndexOf("/")+1, theProfile.photo_uri.length());

                    if(!urlFn.equals(uriFn)){
                        File _file = new File(theProfile.photo_uri);
                        Log.e("onPhotoFileNotEqual", ""+file);
                        uploadPhoto(_file);
                    }
                }
            }else{
                // if photo_uri exists, it means that this file needs to be uploaded
                // and tableProfile needs to be updated
                if( theProfile.photo_uri != null ){
                    File file = new File(theProfile.photo_uri);
                    Log.e("onPhotoFileNull", ""+file);
                    uploadPhoto(file);
                }
            }


            rootView.findViewById(R.id.upload_a_photo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if( isOnline ){
                        String[] allowedExtension = {"jpg", "png", "gif", "jpeg", "bmp"};

                        FileChooser fileChooser = new FileChooser(getActivity());
                        fileChooser.setAdapter(new ImageAdapter());
                        fileChooser.setExtension(allowedExtension);
                        fileChooser.setFileListener(new FileChooser.FileSelectedListener() {
                            @Override
                            public void fileSelected(final File file) {
                                Log.e("onUpload", ""+file);
                                uploadPhoto(file);
                            }
                        });
                        fileChooser.showDialog();
                    }else{
                        Toast.makeText(context, R.string.offline_notification, Toast.LENGTH_LONG).show();
                    }
                }
            });

            rootView.findViewById(R.id.take_a_photo).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // check for intent availability
                    if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
                        // Create the File where the photo should go
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                            getActivity().startActivityForResult(takePictureIntent, TAKE_A_PHOTO);
                        }
                    }else{
                        Toast.makeText(getActivity(), R.string.camera_unavailable, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

        private File createImageFile() throws IOException {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = image.getAbsolutePath();
            return image;
        }

        private void uploadPhoto(final File file){
            if( file != null && file.length() > 0 ){
                try {
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bmOptions.inJustDecodeBounds = true; // get bounds only(Width, Height)
                    BitmapFactory.decodeFile(file.getPath(), bmOptions); // return the bounds

                    int targetW = profileImage.getWidth();
                    int targetH = profileImage.getHeight();
                    int photoW = bmOptions.outWidth;
                    int photoH = bmOptions.outHeight;

                    bmOptions.inSampleSize = Math.min(photoW/targetW, photoH/targetH); // scale factor
                    bmOptions.inJustDecodeBounds = false;
                    final Bitmap compressedPhoto = BitmapFactory.decodeFile(file.getPath(), bmOptions);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedPhoto.compress(Bitmap.CompressFormat.JPEG, 80, baos);

                    byte[] byteArray = baos.toByteArray();
                    String encodedFile = Base64.encodeToString(byteArray, Base64.DEFAULT);

                    JSONObject fileParam = new JSONObject();
                    fileParam.put("name", file.getName());
                    fileParam.put("type", "photo_file");
                    fileParam.put("attachment", encodedFile);

                    String[] params = { Jenjobs.ATTACH_RESUME + "?access-token=" + accessToken, fileParam.toString() };
                    Toast.makeText(context, "uploading photo...", Toast.LENGTH_SHORT).show();

                    ContentValues cv = new ContentValues();
                    cv.put("photo_uri", file.getPath());
                    tableProfile.updateProfile(cv, jsProfileId);

                    PostRequest postRequest = new PostRequest();
                    postRequest.setResultListener(new PostRequest.ResultListener() {
                        @Override
                        public void processResult(JSONObject result) {
                            if (result != null) {
                                // if successful
                                if (result.optInt("status_code") == 1) {
                                    ContentValues cv = new ContentValues();
                                    try {
                                        String _fUrl = result.getString("photo_url");
                                        cv.put("photo_file", _fUrl);
                                        profileImage.setImageBitmap(compressedPhoto);

                                        // save newly downloaded image file to local
                                        String _fn = _fUrl.substring(_fUrl.lastIndexOf("/")+1, _fUrl.length());
                                        File _f = new File(context.getFilesDir(), _fn);
                                        cv.put("photo_uri", _f.getPath());
                                        tableProfile.updateProfile(cv, jsProfileId);
                                    } catch (JSONException e) {
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                Toast.makeText(context, result.optString("status_text"), Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context, R.string.empty_response, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    postRequest.execute(params);
                } catch (JSONException e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(context, "File is null!", Toast.LENGTH_LONG).show();
            }
        }

        private void setupApplicationFragment(View rootView) {
            ListView lv = (ListView)rootView.findViewById(R.id.listOfApplication);
            ApplicationAdapter applicationAdapter = new ApplicationAdapter(context, accessToken);
            applicationAdapter.setActivity(getActivity());
            lv.setAdapter(applicationAdapter);

            int totalApplication = lv.getChildCount();
            if( totalApplication == 0 && applicationAdapter.getCount() == 0 ){
                LinearLayout ll = (LinearLayout)rootView.findViewById(R.id.no_item);
                ll.setVisibility(View.VISIBLE);
                ((TextView)ll.findViewById(R.id.noticeText)).setText(R.string.no_active_application);
            }
        }

        private void setupSettingsFragment(View rootView) {
            SettingsSection settingsSection = new SettingsSection(context, rootView);
            settingsSection.setAccessToken(accessToken);
            settingsSection.setActivity(activity);
            settingsSection.setIsOnline(isOnline);
            settingsSection.render();
        }

        private void setupOnlineResumeFragment(View rootView) {
            final Profile theProfile = tableProfile.getProfile();

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

            workExpQuestion = (LinearLayout)rootView.findViewById(R.id.workExpQuestion);
            listOfWorkExp = (LinearLayout)rootView.findViewById(R.id.listOfWorkExperience);
            Cursor cw = tableWorkExperience.getWorkExperience();
            if( cw.moveToFirst() ){
                listOfWorkExp.removeAllViews();

                while( !cw.isAfterLast() ){
                    final int savedId = cw.getInt(0);
                    final int actualId = cw.getInt(1);
                    String positionTitle = cw.getString(2);
                    String companyName = cw.getString(3);
                    String dateStart = cw.getString(12);
                    String dateResign = cw.getString(13);

                    String durationRange = Jenjobs.date(dateStart, null, "yyyy-MM-dd")+" - ";
                    String durationCount;

                    if( dateResign.length() == 0 || dateResign.equals("null") || dateResign.matches("^0000\\-00\\-00$") ){
                        durationRange += "Present";
                        durationCount = Jenjobs.calculateDuration( dateStart, Jenjobs.date(null, null, "yyyy-MM-dd") );
                    }else{
                        durationRange += Jenjobs.date(dateResign, null, "yyyy-MM-dd");
                        durationCount = Jenjobs.calculateDuration(dateStart, dateResign );
                    }

                    final View v = getActivity().getLayoutInflater().inflate(R.layout.each_work_experience, listOfWorkExp, false);
                    listOfWorkExp.addView(v);

                    ((TextView)v.findViewById(R.id.positionTitle)).setText( positionTitle );
                    ((TextView)v.findViewById(R.id.companyName)).setText( companyName );
                    ((TextView)v.findViewById(R.id.startedOn)).setText( durationRange );
                    ((TextView)v.findViewById(R.id.workDuration)).setText( durationCount );

                    // final int selectedWork = cw.getPosition();
                    LinearLayout updateWorkExp = (LinearLayout)v.findViewById(R.id.updateWorkExperience);
                    updateWorkExp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View _v) {
                            Intent intent = new Intent();
                            intent.setClass(getActivity(),  UpdateWorkExperience.class);
                            intent.putExtra("id", savedId);
                            intent.putExtra("selectedWork", listOfWorkExp.indexOfChild(v));
                            getActivity().startActivityForResult(intent, ADD_WORK_EXP);
                        }
                    });

                    Button deleteButton = (Button)v.findViewById(R.id.deleteButton);
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View _v) {
                            Confirmation confirmation = new Confirmation(activity);
                            confirmation.setConfirmationText(null);
                            confirmation.setStatusListener(new Confirmation.ConfirmationListener() {
                                @Override
                                public void statusSelected(boolean status) {
                                    if( status ){
                                        if( isOnline ){
                                            listOfWorkExp.removeView(v);
                                            String[] param = {Jenjobs.WORK_EXPERIENCE_URL+"/"+actualId+"?access-token="+accessToken};
                                            new DeleteRequest().execute(param);
                                            tableWorkExperience.deleteWorkExperience(savedId);

                                            // get list of work exp left, if none, then show workExpQuestion
                                            if( listOfWorkExp.getChildCount() == 0 ){
                                                workExpQuestion.setVisibility(View.VISIBLE);
                                            }
                                        }else{
                                            Toast.makeText(context, R.string.offline_notification, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            }).showDialog();
                        }
                    });
                    cw.moveToNext();
                }
            }else{
                // if no work experience found + no_work_exp = 0
                if( !theProfile.no_work_exp ){
                    workExpQuestion.setVisibility(View.VISIBLE);
                }
            }

            Button haveWorkExp = (Button)rootView.findViewById(R.id.haveWorkExp);
            Button noWorkExp = (Button)rootView.findViewById(R.id.noWorkExp);

            haveWorkExp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(),  UpdateWorkExperience.class);
                    getActivity().startActivityForResult(intent, ADD_WORK_EXP);
                }
            });

            noWorkExp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _v) {
                    if( isOnline ){
                        // save
                        ContentValues cv = new ContentValues();
                        cv.put("no_work_exp", 1);
                        tableProfile.updateProfile(cv, theProfile._id);

                        // hide layout
                        workExpQuestion.setVisibility(View.GONE);

                        // post no_work_exp to server
                        String[] param = {Jenjobs.PROFILE_URL+"?access-token="+accessToken,"{}"};
                        new PostRequest().execute(param);
                    }else{
                        Toast.makeText(context, R.string.offline_notification, Toast.LENGTH_LONG).show();
                    }
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

            listOfEducation = (LinearLayout)rootView.findViewById(R.id.listOfEducation);
            Cursor ce = tableEducation.getEducation();
            if( ce.moveToFirst() ){
                listOfEducation.removeAllViews();

                HashMap eduLv = Jenjobs.getEducationLevel();

                while( !ce.isAfterLast() ){
                    final int savedId = ce.getInt(0);
                    final int actualId = ce.getInt(1);
                    String school = ce.getString(2);
                    String graduationYear = Jenjobs.date(ce.getString(9), "yyyy", "yyyy-MM-dd");
                    String eduLevel = (String) eduLv.get(ce.getInt(4));

                    final View v = getActivity().getLayoutInflater().inflate(R.layout.each_education, listOfEducation, false);
                    listOfEducation.addView(v);

                    ((TextView)v.findViewById(R.id.educationLevel)).setText( eduLevel );
                    ((TextView)v.findViewById(R.id.school)).setText(school);
                    ((TextView)v.findViewById(R.id.graduationYear)).setText( graduationYear );

                    //final int selectedEdu = ce.getPosition(); // the view index based on the listOfEducation
                    LinearLayout updateEducation = (LinearLayout)v.findViewById(R.id.updateEducation);
                    updateEducation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View _v) {
                            Intent intent = new Intent();
                            intent.setClass(getActivity(),  UpdateEducation.class);
                            intent.putExtra("id", savedId);
                            intent.putExtra("currentViewPosition", listOfEducation.indexOfChild(v));
                            getActivity().startActivityForResult(intent, ADD_EDU);
                        }
                    });

                    Button deleteButton = (Button)v.findViewById(R.id.deleteButton);
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View _v) {
                            Confirmation confirmation = new Confirmation(activity);
                            confirmation.setConfirmationText(null);
                            confirmation.setStatusListener(new Confirmation.ConfirmationListener() {
                                @Override
                                public void statusSelected(boolean status) {
                                    if( status ){
                                        if( isOnline ){
                                            listOfEducation.removeView(v);
                                            String[] param = {Jenjobs.EDUCATION_URL+"/"+actualId+"?access-token="+accessToken};
                                            new DeleteRequest().execute(param);
                                            tableEducation.deleteEducation(savedId);
                                        }else{
                                            Toast.makeText(context, R.string.offline_notification, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            }).showDialog();
                        }
                    });

                    ce.moveToNext();
                }
            }

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
            resumeVisibility.setText(theProfile.access);

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

            HashMap _jobseekingStatus = Jenjobs.getJobSeekingStatus();
            Object jobseekingStatusText = _jobseekingStatus.get(theProfile.js_jobseek_status_id);
            if( jobseekingStatusText != null ){
                jobSeeking.setText(jobseekingStatusText.toString());
            }else{
                jobSeeking.setText(getText(R.string.no_value));
            }

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

            Cursor tjp = tableJobPreference.getJobPreference();
            if( tjp.moveToFirst() ){
                String savedSalary = tjp.getString(0);
                int savedCurrency = tjp.getInt(1);
                HashMap currencies = Jenjobs.getCurrency();
                String _savedCurrency = (String) currencies.get(savedCurrency);

                String summary = _savedCurrency+" "+savedSalary+" per month";
                if( savedSalary == null || savedSalary.equals("null") ){
                    summary = getText(R.string.no_value).toString();
                }
                jobPreference.setText(summary);
            }
            tjp.close();

            /*
            * skills
            * */
            skill = (LinearLayout) rootView.findViewById(R.id.listOfSkill);

            //final TableSkill tableSkill = new TableSkill(getActivity());
            Cursor c = tableSkill.getSkill();

            if( c.moveToFirst() && c.getCount() > 0 ){
                skill.removeView(skill.findViewById(R.id.emptyText));

                while( !c.isAfterLast() ){
                    final int savedId = c.getInt(0); //id
                    final int actualId = c.getInt(1); //_id
                    String skillName = c.getString(2); //name

                    final View v = getActivity().getLayoutInflater().inflate(R.layout.each_skill, skill, false);
                    skill.addView(v);

                    v.findViewById(R.id.deleteSkillButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View vv) {
                            Confirmation confirmation = new Confirmation(activity);
                            confirmation.setConfirmationText(null);
                            confirmation.setStatusListener(new Confirmation.ConfirmationListener() {
                                @Override
                                public void statusSelected(boolean status) {
                                    if( status ){
                                        if( isOnline ){
                                            skill.removeView(v);

                                            // delete from server
                                            String[] param = {Jenjobs.SKILL_URL+"/"+actualId+"?access-token="+accessToken};
                                            new DeleteRequest().execute(param);

                                            // delete from local
                                            tableSkill.deleteSkill(savedId);
                                        }else{
                                            Toast.makeText(context, R.string.offline_notification, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            }).showDialog();
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
            language = (LinearLayout) rootView.findViewById(R.id.listOfLanguage);

            final Cursor cl = tableLanguage.getLanguage(null);
            if( cl.moveToFirst() && cl.getCount() > 0 ) {
                language.removeView(language.findViewById(R.id.emptyLanguageText));

                HashMap _languageLevel = Jenjobs.getLanguageLevel();
                HashMap _language = Jenjobs.getLanguage();

                while( !cl.isAfterLast() ){
                    //final int lang_saved_id = cl.getInt(0);
                    final int lang_id = cl.getInt(1);
                    int lang_spoken = cl.getInt(2);
                    int lang_written = cl.getInt(3);
                    int lang_native = cl.getInt(4);

                    final View v = getActivity().getLayoutInflater().inflate(R.layout.each_language, language, false);
                    language.addView(v);

                    if( lang_native > 0 ){ v.setBackgroundColor(getResources().getColor(R.color.white)); }

                    // update language
                    v.findViewById(R.id.languageContainer).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View _v) {
                            int __viewIndex = language.indexOfChild(v);
                            Intent intent = new Intent();
                            intent.setClass(getActivity(), UpdateLanguage.class);
                            intent.putExtra("_viewIndex", __viewIndex);
                            intent.putExtra("language_id", lang_id);
                            getActivity().startActivityForResult(intent, ADD_LANGUAGE);
                        }
                    });

                    // delete language
                    v.findViewById(R.id.deleteLanguageButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View _v) {
                            Confirmation confirmation = new Confirmation(activity);
                            confirmation.setConfirmationText(null);
                            confirmation.setStatusListener(new Confirmation.ConfirmationListener() {
                                @Override
                                public void statusSelected(boolean status) {
                                    if( status ){
                                        if( isOnline ){
                                            language.removeView(v);
                                            // delete from server
                                            String[] param = {Jenjobs.LANGUAGE_URL + "/" + lang_id + "?access-token=" + accessToken};
                                            new DeleteRequest().execute(param);
                                            // delete from local
                                            tableLanguage.deleteLanguage(lang_id);
                                        }else{
                                            Toast.makeText(context, R.string.offline_notification, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                            }).showDialog();
                        }
                    });
                    ((TextView)v.findViewById(R.id.languageName)).setText((String) _language.get(lang_id));
                    ((TextView)v.findViewById(R.id.spokenLanguageLevel)).setText((String) _languageLevel.get(lang_spoken));
                    ((TextView)v.findViewById(R.id.writtenLanguageLevel)).setText( (String) _languageLevel.get( lang_written ) );
                    cl.moveToNext();
                }
            }
            cl.close();

            Button addLanguageButton = (Button)rootView.findViewById(R.id.add_language);
            addLanguageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), UpdateLanguage.class);
                    getActivity().startActivityForResult(intent, ADD_LANGUAGE);
                }
            });

            /*
            * attached resume
            * */
            LinearLayout updateAttachedResume = (LinearLayout) rootView.findViewById(R.id.updateAttachedResume);
            attachedResume = (TextView)rootView.findViewById(R.id.attachedResume);
            if( theProfile.resume_file != null && theProfile.resume_file.length() > 0 ){
                attachedResume.setText( theProfile.resume_file.substring( theProfile.resume_file.lastIndexOf("/")+1 , theProfile.resume_file.length() ) );
            }

            final ProgressBar progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar2);
            updateAttachedResume.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View _v) {
                    String[] allowedExtension = {"doc", "docx", "odt", "pdf", "ppt", "txt"};
                    if( isOnline ){
                        FileChooser fileChooser = new FileChooser(getActivity());
                        fileChooser.setExtension(allowedExtension);
                        fileChooser.setFileListener(new FileChooser.FileSelectedListener() {
                            @Override
                            public void fileSelected(final File file) {
                                // file.getAbsolutePath() === /storage/emulated/0/Download/CG_resume.pdf
                                // file.getName() === CG_resume.pdf

                                try {
                                    byte[] buffer = new byte[8192];
                                    int bytesRead;

                                    InputStream inputStream = new FileInputStream(file);
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                                        baos.write(buffer, 0, bytesRead);
                                    }
                                    byte[] byteArray = baos.toByteArray();
                                    String encodedFile = Base64.encodeToString(byteArray, Base64.DEFAULT);

                                    JSONObject fileParam = new JSONObject();
                                    fileParam.put("name", file.getName());
                                    fileParam.put("type", "resume_file");
                                    fileParam.put("attachment", encodedFile);

                                    String[] params = {
                                            Jenjobs.ATTACH_RESUME + "?access-token=" + accessToken,
                                            fileParam.toString()
                                    };

                                    attachedResume.setText("");
                                    progressBar.setVisibility(View.VISIBLE);

                                    PostRequest postRequest = new PostRequest();
                                    postRequest.setResultListener(new PostRequest.ResultListener() {
                                        @Override
                                        public void processResult(JSONObject result) {
                                            if( result != null ){
                                                // if successul
                                                if( result.optInt( "status_code" ) == 1 ){
                                                    attachedResume.setText(result.optString("resume") );

                                                    ContentValues cv = new ContentValues();
                                                    cv.put("resume_file", result.optString("resume_url"));
                                                    cv.put("resume_uri", file.getPath());
                                                    tableProfile.updateProfile(cv, jsProfileId);
                                                }
                                                Toast.makeText(context, result.optString("status_text"), Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                    postRequest.execute(params);

                                } catch (FileNotFoundException e) {
                                    Toast.makeText(context, "File not found!", Toast.LENGTH_LONG).show();
                                } catch (IOException | JSONException e) {
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        fileChooser.showDialog();
                    }else{
                        Toast.makeText(context, R.string.offline_notification, Toast.LENGTH_LONG).show();
                    }
                }
            });

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

        private void setupBookmarkFragment(View rootView){
            LinearLayout ll = (LinearLayout)rootView.findViewById(R.id.no_item);
            ((TextView)ll.findViewById(R.id.noticeText)).setText(R.string.no_bookmark);

            ListView lv = (ListView)rootView.findViewById(R.id.listOfBookmark);
            BookmarkAdapter bookmarkAdapter = new BookmarkAdapter(context, accessToken);
            bookmarkAdapter.setActivity(getActivity());
            bookmarkAdapter.setNoItemView(ll);
            lv.setAdapter(bookmarkAdapter);

            if( bookmarkAdapter.getCount() == 0 ){
                ll.setVisibility(View.VISIBLE);
            }
        }

        private void setupInvitationAndRequestFragment(View rootView){
            ListView lv = (ListView)rootView.findViewById(R.id.listOfInvitation);
            InvitationAdapter invitationAdapter = new InvitationAdapter(context);
            invitationAdapter.setActivity(getActivity());
            invitationAdapter.setAccessToken(accessToken);
            lv.setAdapter(invitationAdapter);

            if( lv.getCount() == 0 && invitationAdapter.getCount() == 0 ){
                LinearLayout ll = (LinearLayout)rootView.findViewById(R.id.no_item);
                ll.setVisibility(View.VISIBLE);
                ((TextView)ll.findViewById(R.id.noticeText)).setText(R.string.no_request);
            }
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
                if( clickedItem == R.id.action_update_profile ){
                    Intent intent = new Intent(getApplicationContext(), UpdateProfile.class);
                    startActivityForResult(intent, UPDATE_PROFILE);
                    return true;
                }else{
                    return super.onOptionsItemSelected(item);
                }
            case APPLICATION_FRAGMENT:
            case SETTINGS_FRAGMENT:
            case ONLINE_RESUME_FRAGMENT:
            case JOB_SUGGESTION:
            case BOOKMARK_FRAGMENT:
            case INVITATION_AND_REQUEST:
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
            case APPLICATION_FRAGMENT:
            case INVITATION_AND_REQUEST:
            case JOB_SUGGESTION:
            case BOOKMARK_FRAGMENT:
            case SETTINGS_FRAGMENT:
            case ONLINE_RESUME_FRAGMENT:
                break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_WORK_EXP) {
                Bundle extra = data.getExtras();
                final int id = extra.getInt("id");
                int prevWork = extra.getInt("selectedWork");

                final View v;
                if (prevWork >= 0) {
                    v = listOfWorkExp.getChildAt(prevWork);
                } else {
                    v = getLayoutInflater().inflate(R.layout.each_work_experience, listOfWorkExp, false);
                    listOfWorkExp.addView(v);
                }

                Cursor c = tableWorkExperience.getWorkExperienceById(id);
                if (c.moveToFirst()) {
                    final int actualId = c.getInt(1);
                    final String positionTitle = c.getString(2);
                    String companyName = c.getString(3);
                    String dateStart = c.getString(12);
                    String dateResign = c.getString(13);

                    ((TextView) v.findViewById(R.id.positionTitle)).setText(positionTitle);
                    ((TextView) v.findViewById(R.id.companyName)).setText(companyName);

                    String durationRange = Jenjobs.date(dateStart, "MMM yyyy", "yyyy-MM-dd") + " - ";
                    String durationCount;

                    if ( dateResign.length() == 0 || dateResign.equals("null") || dateResign.matches("^0000\\-00\\-00$") ) {
                        durationRange += "Present";
                        durationCount = Jenjobs.calculateDuration(dateStart, Jenjobs.date(null, null, "yyyy-MM-dd"));
                    } else {
                        durationRange += Jenjobs.date(dateResign, "MMM yyyy", "yyyy-MM-dd");
                        durationCount = Jenjobs.calculateDuration(dateStart, dateResign);
                    }
                    ((TextView) v.findViewById(R.id.startedOn)).setText(durationRange);
                    ((TextView) v.findViewById(R.id.workDuration)).setText(durationCount);

                    if (prevWork < 0) {
                        final int selectedWork = listOfWorkExp.getChildCount() - 1;
                        LinearLayout updateWorkExp = (LinearLayout) v.findViewById(R.id.updateWorkExperience);
                        updateWorkExp.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View _v) {
                                Intent intent = new Intent();
                                intent.setClass(context, UpdateWorkExperience.class);
                                intent.putExtra("id", id);
                                intent.putExtra("selectedWork", selectedWork);
                                startActivityForResult(intent, ADD_WORK_EXP);
                            }
                        });

                        Button deleteButton = (Button) v.findViewById(R.id.deleteButton);
                        deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View _v) {
                                if (isOnline) {
                                    listOfWorkExp.removeView(v);
                                    String[] param = {Jenjobs.WORK_EXPERIENCE_URL + "/" + actualId + "?access-token=" + accessToken};
                                    new DeleteRequest().execute(param);
                                    tableWorkExperience.deleteWorkExperience(id);
                                } else {
                                    Toast.makeText(context, R.string.offline_notification, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }

                    c.close();
                }
                workExpQuestion.setVisibility(View.GONE);
            } else if (requestCode == ADD_EDU) {
                Bundle extra = data.getExtras();
                final int id = extra.getInt("id");
                // TODO: the index might changed one we have added or removed a child
                int prevEdu = extra.getInt("currentViewPosition");
                final View v;
                if (prevEdu >= 0) {
                    v = listOfEducation.getChildAt(prevEdu);
                } else {
                    v = getLayoutInflater().inflate(R.layout.each_education, listOfEducation, false);
                    listOfEducation.addView(v);
                }

                Cursor c = tableEducation.getEducationById(id);
                HashMap eduLv = Jenjobs.getEducationLevel();
                if (c.moveToFirst()) {
                    final int actualId = c.getInt(1);
                    String school = c.getString(2);
                    String graduationYear = c.getString(9).substring(0, 4);
                    String eduLevel = (String) eduLv.get(c.getInt(4));

                    ((TextView) v.findViewById(R.id.educationLevel)).setText(eduLevel);
                    ((TextView) v.findViewById(R.id.school)).setText(school);
                    ((TextView) v.findViewById(R.id.graduationYear)).setText(graduationYear);

                    if (prevEdu < 0) {
                        // bind events to new element
                        final int selectedEdu = listOfEducation.getChildCount() - 1;
                        LinearLayout updateWorkExp = (LinearLayout) v.findViewById(R.id.updateEducation);
                        updateWorkExp.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View _v) {
                                Intent intent = new Intent();
                                intent.setClass(context, UpdateEducation.class);
                                intent.putExtra("id", id);
                                intent.putExtra("selectedEdu", selectedEdu);
                                startActivityForResult(intent, ADD_EDU);
                            }
                        });

                        Button deleteButton = (Button) v.findViewById(R.id.deleteButton);
                        deleteButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View _v) {
                                if (isOnline) {
                                    // delete view
                                    listOfWorkExp.removeView(v);

                                    // delete from server
                                    String[] param = {Jenjobs.EDUCATION_URL + "/" + actualId + "?access-token=" + accessToken};
                                    new DeleteRequest().execute(param);

                                    // delete record
                                    tableEducation.deleteEducation(id);
                                } else {
                                    Toast.makeText(context, R.string.offline_notification, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }

                    c.close();
                }
            } else if (requestCode == UPDATE_RESUME_VISIBILITY) {
                Bundle extra = data.getExtras();
                resumeVisibility.setText(extra.getString("selectedvisibility"));
            } else if (requestCode == UPDATE_JOB_SEEKING) {
                Bundle extra = data.getExtras();
                String summary = extra.getString("summary");
                jobSeeking.setText(summary);
            } else if (requestCode == UPDATE_JOB_PREFERENCE) {
                Bundle extra = data.getExtras();
                String response = extra.getString("summary");
                jobPreference.setText(response);
            } else if (requestCode == ADD_SKILL) {
                Bundle extra = data.getExtras();
                String skillName = extra.getString("skill_name");
                final int skillId = extra.getInt("skill_id");

                if (skill.findViewById(R.id.emptyText) != null) {
                    skill.removeView(skill.findViewById(R.id.emptyText));
                }

                final View v = getLayoutInflater().inflate(R.layout.each_skill, skill, false);
                skill.addView(v);

                v.findViewById(R.id.deleteSkillButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View _v) {
                        if (isOnline) {
                            String[] _skill = tableSkill.findSkillById(skillId);
                            skill.removeView(v);

                            // delete from server
                            String[] param = {Jenjobs.SKILL_URL + "/" + _skill[1] + "?access-token=" + accessToken};
                            new DeleteRequest().execute(param);

                            // delete from local
                            tableSkill.deleteSkill(skillId);
                        } else {
                            Toast.makeText(context, R.string.offline_notification, Toast.LENGTH_LONG).show();
                        }
                    }
                });
                ((TextView) v.findViewById(R.id.skillText)).setText(skillName);

            } else if (requestCode == ADD_LANGUAGE) {
                Bundle extra = data.getExtras();
                final Language _lang = (Language) extra.get("language");
                int _viewIndex = extra.getInt("_viewIndex");

                if (_lang != null) {
                    HashMap _languageLevel = Jenjobs.getLanguageLevel();
                    HashMap _language = Jenjobs.getLanguage();
                    if (_viewIndex != -1) {
                        View __v = language.getChildAt(_viewIndex);
                        if (__v != null) {
                            __v.clearFocus();
                            language.removeView(__v);
                        }
                    }

                    final View v = getLayoutInflater().inflate(R.layout.each_language, language, false);
                    language.addView(v);

                    if (_lang.isNative > 0) {
                        v.setBackgroundColor(getResources().getColor(R.color.white));
                    }

                    // update language
                    v.findViewById(R.id.languageContainer).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View _v) {
                            int __viewIndex = language.indexOfChild(v);
                            Intent intent = new Intent();
                            intent.setClass(getApplicationContext(), UpdateLanguage.class);
                            intent.putExtra("_viewIndex", __viewIndex);
                            intent.putExtra("language_id", _lang.id);
                            startActivityForResult(intent, ADD_LANGUAGE);
                        }
                    });

                    // delete language
                    v.findViewById(R.id.deleteLanguageButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View _v) {
                            if (isOnline) {
                                language.removeView(v);
                                // delete from server
                                String[] param = {Jenjobs.LANGUAGE_URL + "/" + _lang.id + "?access-token=" + accessToken};
                                new DeleteRequest().execute(param);
                                // delete from local
                                tableLanguage.deleteLanguage(_lang.id);
                            } else {
                                Toast.makeText(context, R.string.offline_notification, Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    ((TextView) v.findViewById(R.id.languageName)).setText((String) _language.get(_lang.id));
                    ((TextView) v.findViewById(R.id.spokenLanguageLevel)).setText((String) _languageLevel.get(_lang.spoken));
                    ((TextView) v.findViewById(R.id.writtenLanguageLevel)).setText((String) _languageLevel.get(_lang.written));
                }
            } else if (requestCode == UPDATE_ADDITIONAL_INFO) {
                Bundle extra = data.getExtras();
                additionalInfo.setText(extra.getString("info"));
            } else if (requestCode == UPDATE_PROFILE) {
                //Bundle extra = data.getExtras();
                // refresh profile
                profileLayout = (LinearLayout) findViewById(R.id.profileLayout);
                Profile theProfile = tableProfile.getProfile();
                TextView name = (TextView) profileLayout.findViewById(R.id.fullName);
                TextView email = (TextView) profileLayout.findViewById(R.id.email);
                TextView mobile_no = (TextView) profileLayout.findViewById(R.id.mobile_no);
                TextView ic_no = (TextView) profileLayout.findViewById(R.id.ic_no);
                TextView gender = (TextView) profileLayout.findViewById(R.id.gender);
                TextView dob = (TextView) profileLayout.findViewById(R.id.dob);
                TextView country = (TextView) profileLayout.findViewById(R.id.country);

                name.setText(theProfile.name);
                email.setText(theProfile.email);
                mobile_no.setText(theProfile.mobile_no);
                ic_no.setText(theProfile.ic);
                gender.setText(theProfile.gender);

                if (theProfile.country_id > 0) {
                    TableCountry tableCountry = new TableCountry(getApplicationContext());
                    Country c = tableCountry.findCountryById(theProfile.country_id);
                    if (c != null) {
                        country.setText(c.name);
                    }
                }

                String _dob = theProfile.dob;
                if (_dob != null) {
                    dob.setText(Jenjobs.date(_dob, null, "yyyy-M-d"));
                }
                // refresh profile
            } else if (requestCode == TAKE_A_PHOTO) {
                /*
                * TODO - test camera intent
                * if NO FILE provided to the CAMERA intent, only thumbnail is returned
                * provide FILE to the camera intent if you want to save the file
                * */

                BitmapFactory.Options bmOptions = new BitmapFactory.Options();

                // Get the dimensions of the View
                int targetW = profileImage.getWidth();
                int targetH = profileImage.getHeight();

                // Get the dimensions of the bitmap
                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;
                //Log.e("outWidth", ""+photoW); // 640 - output
                //Log.e("outHeight", ""+photoH); // 480 - output
                //Log.e("targetWidth", ""+targetW); // 1370
                //Log.e("targetHeight", ""+targetH); // 525

                // Determine how much to scale down the image
                int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
                //Log.e("scaleFactor", ""+scaleFactor);

                // Decode the image file into a Bitmap sized to fill the View
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;
                //bmOptions.inPurgeable = true;

                Bitmap cameraImage = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
                profileImage.setImageBitmap(cameraImage);

                // start upload camera image
                if( isOnline ){
                    //byte[] buffer = new byte[8192];
                    //int bytesRead;

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    final File file = new File(mCurrentPhotoPath);
                    try {
                        //final InputStream inputStream = new FileInputStream(file);
                        //while ((bytesRead = inputStream.read(buffer)) != -1) {
                        //    baos.write(buffer, 0, bytesRead);
                        //}

                        cameraImage.compress(Bitmap.CompressFormat.JPEG, 80, baos);

                        byte[] byteArray = baos.toByteArray();
                        String encodedFile = Base64.encodeToString(byteArray, Base64.DEFAULT);

                        JSONObject fileParam = new JSONObject();
                        fileParam.put("name", file.getName());
                        fileParam.put("type", "photo_file");
                        fileParam.put("attachment", encodedFile);
                        String[] params = {Jenjobs.ATTACH_RESUME + "?access-token=" + accessToken,fileParam.toString()};

                        Toast.makeText(context, "uploading photo...", Toast.LENGTH_LONG).show();

                        // save the file to local
                        ContentValues cv = new ContentValues();
                        cv.put("photo_uri", file.getPath());
                        tableProfile.updateProfile(cv, jsProfileId);

                        // then upload
                        PostRequest postRequest = new PostRequest();
                        postRequest.setResultListener(new PostRequest.ResultListener() {
                            @Override
                            public void processResult(JSONObject result) {
                                if (result != null) {
                                    // if successful, update profile photo url
                                    if (result.optInt("status_code") == 1) {
                                        try {
                                            String _fUrl = result.getString("photo_url");
                                            ContentValues cv = new ContentValues();
                                            cv.put("photo_file", _fUrl);
                                            tableProfile.updateProfile(cv, jsProfileId);

                                            String _fn = _fUrl.substring(_fUrl.lastIndexOf("/")+1, _fUrl.length());
                                            File _f = new File(context.getFilesDir(), _fn);
                                            cv.put("photo_uri", _f.getPath());
                                            tableProfile.updateProfile(cv, jsProfileId);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    Toast.makeText(context, result.optString("status_text"), Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(context, R.string.empty_response, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        postRequest.execute(params);
                    } catch (JSONException e) {
                        //Log.e("exception", e.getMessage());
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(context, R.string.offline_notification, Toast.LENGTH_SHORT).show();
                }
                // end upload
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

        @Override
        protected String doInBackground(String... params) {
            String _response = null;

            accessToken = sharedPref.getString("access_token", null);
            String url = params[0]+"?access-token="+accessToken;

            final HttpClient httpclient = new DefaultHttpClient();
            final HttpGet httpget = new HttpGet( url );
            httpget.addHeader("Content-Type", "application/json");
            httpget.addHeader("Accept", "application/json");

            try {
                HttpResponse _http_response = httpclient.execute(httpget);
                HttpEntity _entity = _http_response.getEntity();
                InputStream is = _entity.getContent();
                _response = JenHttpRequest.readInputStreamAsString(is);
                //_response = JenHttpRequest.decodeJsonObjectString(responseString);
                //_response = responseString;
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
                if( downloadSection == DOWNLOAD_PROFILE ){
                    JSONObject success;
                    int js_profile_id;

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
                        cv.put("dial_code", String.valueOf(success.get("dial_code")));
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

                        tableProfile.addProfile(cv);
                        js_profile_id = cv.getAsInteger("_id");
                        //Log.e("js_profile_id", String.valueOf(js_profile_id));

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

                            //ImageView profileImage = (ImageView) profileLayout.findViewById(R.id.profile_image);
                            if( String.valueOf(success.get("photo_file")) != null ){
                                new ImageLoad(String.valueOf(success.get("photo_file")), profileImage).execute();
                            }
                        }

                        // save address
                        String address = String.valueOf(success.get("address"));

                        if( address != null && !address.equals("null") ){
                            JSONObject jsonAddr = new JSONObject(address);

                            ContentValues cv3 = new ContentValues();
                            cv3.put("address1", jsonAddr.getString("address1"));
                            cv3.put("address2", jsonAddr.getString("address2"));

                            String postCodeStr = jsonAddr.getString("postcode");
                            int postCode = 0;
                            if( postCodeStr != null && !postCodeStr.equals("null") ){
                                postCode = Integer.valueOf(postCodeStr);
                            }
                            cv3.put("postcode", postCode);
                            cv3.put("city_id", jsonAddr.getInt("city_id"));
                            cv3.put("city_name", jsonAddr.getString("city_name"));
                            cv3.put("state_id", jsonAddr.getInt("state_id"));
                            cv3.put("state_name", jsonAddr.getString("state_name"));
                            cv3.put("country_id", jsonAddr.getInt("country_id"));
                            cv3.put("updated_at", jsonAddr.getString("date_updated"));
                            tblAddress.updateAddress(cv3);
                        }
                        // end save address
                    } catch (JSONException e) {
                        Log.e("profileExcp", e.getMessage());
                    }

                }else if( downloadSection == DOWNLOAD_APPLICATION ){
                    TableApplication tableApplication = new TableApplication(getApplicationContext());
                    ContentValues cv = new ContentValues();

                    try {
                        JSONArray success = new JSONArray(nsuccess);
                        if( success.length() > 0 ){
                            for( int i=0;i< success.length();i++ ){
                                JSONObject s = success.getJSONObject(i);
                                final int postId = s.getInt("post_id");
                                cv.put("_id", s.getInt("_id"));
                                cv.put("post_id", postId);
                                cv.put("status", s.getInt("status"));
                                cv.put("date_created", s.getString("date_created"));
                                cv.put("date_updated", s.getString("date_updated"));
                                cv.put("title", s.getString("title"));
                                cv.put("closed", s.getBoolean("closed") ? 1 : 0);
                                tableApplication.addApplication(cv);

                                // download job details and save to TableJob
                                // download the job details
                                GetRequest getRequest = new GetRequest();
                                getRequest.setResultListener(new GetRequest.ResultListener() {
                                    @Override
                                    public void processResultArray(JSONArray result) {
                                    }

                                    @Override
                                    public void processResult(JSONObject success) {
                                        if (success != null && success.toString().length() > 0) {
                                            // and save to phone database
                                            ContentValues cv2 = new ContentValues();

                                            cv2.put("id", postId);
                                            cv2.put("title", success.optString("title"));
                                            cv2.put("company", success.optString("company"));
                                            cv2.put("job_data", success.toString());
                                            cv2.put("date_closed", success.optString("date_closed"));

                                            tableJob.addJob(cv2);
                                        }
                                    }
                                });
                                String[] param = {Jenjobs.JOB_DETAILS+"/"+postId};
                                getRequest.execute(param);
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("appExcp", e.getMessage());
                    }

                }else if( downloadSection == DOWNLOAD_WORK_EXPERIENCE ){
                    ContentValues cv = new ContentValues();
                    JSONArray success;

                    try {
                        success = new JSONArray(nsuccess);
                        if( success.length() > 0 ){
                            for( int i=0;i< success.length();i++ ){
                                JSONObject s = success.getJSONObject(i);

                                cv.put("_id", s.getInt("id"));
                                cv.put("position", s.optString("position"));
                                cv.put("company", s.optString("company"));

                                //Log.e("job_type", ""+s.optString("job_type"));
                                if( s.optString("job_type") != null && !s.optString("job_type").equals("null") ){
                                    JSONObject jobType = new JSONObject(s.optString("job_type"));
                                    //Log.e("job_type2", ""+jobType);
                                    cv.put("job_type_id", jobType.optInt("id") > 0 ? jobType.optInt("id") : 0 );
                                }

                                if( s.optString("job_spec") != null && s.optString("job_role") != null ){
                                    JSONObject jobSpec = new JSONObject(s.optString("job_spec"));
                                    cv.put("job_spec_id", jobSpec.optInt("id") > 0 ? jobSpec.optInt("id") : 0 );

                                    JSONObject jobRole = new JSONObject(s.optString("job_role"));
                                    cv.put("job_role_id", jobRole.optInt("id") > 0 ? jobRole.optInt("id") : 0 );
                                }

                                JSONObject jobLevel = new JSONObject(s.optString("job_level"));
                                cv.put("job_level_id", jobLevel.optInt("id") > 0 ? jobLevel.optInt("id") : 0 );

                                JSONObject jobIndustry = new JSONObject(s.optString("industry"));
                                cv.put("industry_id", jobIndustry.optInt("id") > 0 ? jobIndustry.optInt("id") : 0 );

                                cv.put("experience", s.optString("experience"));
                                cv.put("salary", s.optString("salary"));
                                cv.put("currency_id", s.optInt("currency_id"));
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
                    ContentValues cv = new ContentValues();
                    JSONArray success;

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
                    TableJobPreferenceLocation tableJobPreferenceLocation = new TableJobPreferenceLocation(getApplicationContext());

                    ContentValues cv = new ContentValues();
                    JSONObject success;
                    try {
                        success = new JSONObject(nsuccess);

                        cv.put("salary", success.optString("salary"));
                        cv.put("currency_id", success.optInt("currency_id"));
                        tableJobPreference.updateJobPreference(cv);
                        cv.put("job_type_id", success.optString("job_type_id"));

                        tableJobPreferenceLocation.truncate();
                        JSONArray state = new JSONArray(success.optString("state_id"));
                        if( state.length() > 0 ){
                           for(int i=0;i<state.length();i++){
                               ContentValues cv2 = new ContentValues();
                               cv2.put("state_id", (int)state.get(i));
                               cv2.put("country_id", 127);
                               tableJobPreferenceLocation.insertJobPreference(cv2);
                           }
                        }

                        JSONArray country = new JSONArray(success.optString("country_id"));
                        if( country.length() > 0 ){
                            for(int i=0;i<country.length();i++) {
                                ContentValues cv2 = new ContentValues();
                                cv2.put("state_id", 0);
                                cv2.put("country_id", (int)country.get(i));
                                tableJobPreferenceLocation.insertJobPreference(cv2);
                            }
                        }

                    } catch (JSONException e) {
                        Log.e("jobPrefExcp", e.getMessage());
                    }

                }else if( downloadSection == DOWNLOAD_SKILL ){
                    JSONArray success;
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
                    JSONArray success;
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
                    JSONArray success;
                    try {
                        success = new JSONArray(nsuccess);
                        if( success.length() > 0 ) {
                            for (int i = 0; i < success.length(); i++) {
                                JSONObject s = success.getJSONObject(i);
                                ContentValues cv = new ContentValues();

                                final int postId = s.optInt("post_id");
                                cv.put("post_id", postId);
                                cv.put("title", s.optString("title"));
                                cv.put("date_added", s.optString("on"));
                                cv.put("date_closed", s.optString("date_closed"));
                                tableBookmark.addBookmark(cv);

                                // download job details and save to TableJob
                                GetRequest getRequest = new GetRequest();
                                getRequest.setResultListener(new GetRequest.ResultListener() {
                                    @Override
                                    public void processResultArray(JSONArray result) {
                                    }

                                    @Override
                                    public void processResult(JSONObject success) {
                                        if (success != null && success.toString().length() > 0) {
                                            // and save to phone database
                                            ContentValues cv2 = new ContentValues();

                                            cv2.put("id", postId);
                                            cv2.put("title", success.optString("title"));
                                            cv2.put("company", success.optString("company"));
                                            cv2.put("job_data", success.toString());
                                            cv2.put("date_closed", success.optString("date_closed"));

                                            tableJob.addJob(cv2);
                                        }
                                    }
                                });
                                String[] param = {Jenjobs.JOB_DETAILS+"/"+postId};
                                getRequest.execute(param);
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("bookExcp", e.getMessage());
                    }

                }else if( downloadSection == DOWNLOAD_SUBSCRIPTION ){

                    TableSubscription tableSubscription = new TableSubscription(getApplicationContext());
                    JSONArray success;

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

                }else if( downloadSection == DOWNLOAD_INVITATION ){
                    TableInvitation tableInvitation = new TableInvitation(getApplicationContext());

                    JSONArray success;
                    try {
                        success = new JSONArray(nsuccess);
                        if( success.length() > 0 ) {
                            for (int i = 0; i < success.length(); i++) {
                                JSONObject s = success.getJSONObject(i);
                                ContentValues cv = new ContentValues();

                                cv.put("id", s.optInt("id"));
                                cv.put("emp_profile_name", s.optString("company"));
                                cv.put("emp_profile_id", s.optInt("company_id"));
                                cv.put("status", s.optString("status"));
                                cv.put("date_added", s.optString("date_created"));
                                String dateUpdated = s.optString("date_updated");
                                if( dateUpdated != null && !dateUpdated.equals("") && !dateUpdated.equals("null") ){
                                    cv.put("date_updated", dateUpdated);
                                }

                                // this is for type "J" = Job Application Invitation
                                String post = s.getString("post");
                                if( post != null && !post.equals("null") ){
                                    JSONObject _post = new JSONObject(post);

                                    final int postId = _post.getInt("post_id");
                                    boolean isJobClosed = _post.getBoolean("closed");

                                    // for each job application invitation
                                    // if the job is still active
                                    if( !isJobClosed ){
                                        // download the job details
                                        GetRequest getRequest = new GetRequest();
                                        getRequest.setResultListener(new GetRequest.ResultListener() {
                                            @Override
                                            public void processResultArray(JSONArray result) {}

                                            @Override
                                            public void processResult(JSONObject success) {
                                                if( success != null && success.toString().length() > 0 ){
                                                    // and save to phone database
                                                    ContentValues cv2 = new ContentValues();

                                                    cv2.put("id", postId);
                                                    cv2.put("title", success.optString("title"));
                                                    cv2.put("company", success.optString("company"));
                                                    cv2.put("job_data", success.toString());
                                                    cv2.put("date_closed", success.optString("date_closed"));

                                                    tableJob.addJob(cv2);
                                                }
                                            }
                                        });
                                        String[] param = {Jenjobs.JOB_DETAILS+"/"+postId};
                                        getRequest.execute(param);
                                    }

                                    cv.put("post_id", postId);
                                    cv.put("post_title", _post.getString("post_title"));
                                    cv.put("post_closed_on", _post.getString("date_closed"));
                                }
                                tableInvitation.saveInvitation(cv, 0);
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("subExcp", e.getMessage());
                    }
                }else  if( downloadSection == DOWNLOAD_JOBMATCHER_PROFILE ){
                    TableJobSearchProfile tableProfile = new TableJobSearchProfile(getApplicationContext());

                    JSONArray success;
                    try {
                        success = new JSONArray(nsuccess);
                        if (success.length() > 0) {
                            for (int i = 0; i < success.length(); i++) {
                                JSONObject s = success.getJSONObject(i);
                                JSONObject queryParam = s.getJSONObject("query_param");
                                ContentValues cv = new ContentValues();

                                cv.put("id", 0);
                                cv.put("_id", (int)(s.get("id")));
                                cv.put("profile_name", s.getString("name"));
                                cv.put("parameters", queryParam.toString());
                                cv.put("notification_frequency", s.getString("frequency"));
                                cv.put("date_created", s.getString("date_added"));
                                cv.put("date_updated", s.getString("date_updated"));

                                tableProfile.saveSearchProfile(cv);
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("djmerr", e.getMessage());
                    }
                }
            }else{
                Log.e("success", "null");
            }
        }
    }
}
