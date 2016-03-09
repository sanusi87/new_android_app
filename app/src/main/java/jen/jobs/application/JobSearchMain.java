package jen.jobs.application;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class JobSearchMain extends ActionBarActivity {
    SharedPreferences sharedPref;
    private final Handler mHideHandler = new Handler();
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    Button loginButton;
    Button registerButton;
    Button forgotButton;
    Button jobSearch;
    TextView enteredKeyword;
    Spinner keywordFilter;

    private TableJobSpec tableJobSpec;
    private TableJobRole tableJobRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_search_main);

        sharedPref = this.getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        TableProfile tableProfile = new TableProfile(getApplicationContext());
        tableJobSpec = new TableJobSpec(getApplicationContext());
        tableJobRole = new TableJobRole(getApplicationContext());

        loginButton = (Button) findViewById(R.id.login_button);
        registerButton = (Button) findViewById(R.id.register_button);
        forgotButton = (Button) findViewById(R.id.forgot_button);
        jobSearch = (Button) findViewById(R.id.job_search);

        enteredKeyword = (TextView)findViewById(R.id.keyword);

        KeywordFilterAdapter kwa = new KeywordFilterAdapter(getApplicationContext());
        keywordFilter = (Spinner)findViewById(R.id.keyword_filter);
        keywordFilter.setAdapter(kwa);
        keywordFilter.setSelection(0);

        // handle register button click
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        // handle forgot password button click
        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        jobSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), JobSearchActivity.class);

                KeywordFilter kf = (KeywordFilter) keywordFilter.getSelectedItem();
                intent.putExtra("keyword_filter", kf);
                if( enteredKeyword.getText().toString().length() > 0 ){
                    intent.putExtra("keyword", enteredKeyword.getText().toString());
                }

                startActivity(intent);
            }
        });


        // check table content
        Cursor c = tableJobSpec.getAllJobSpec();
        if( c.getCount() == 0 ){
            tableJobSpec.clearAll();
            tableJobRole.clearAll();

            GetRequest g = new GetRequest();
            g.setResultListener(new GetRequest.ResultListener() {
                @Override
                public void processResultArray(JSONArray result) {}

                @Override
                public void processResult(JSONObject success) {
                    if (success != null) {
                        Iterator i = success.keys();
                        while (i.hasNext()) {
                            String jobSpecId = (String) i.next();
                            JSONObject jobSpec;
                            try {
                                jobSpec = success.getJSONObject(jobSpecId);
                                ContentValues cv = new ContentValues();
                                cv.put("id", jobSpecId);
                                cv.put("spec_name", jobSpec.getString("name"));
                                tableJobSpec.addJobSpec(cv);

                                JSONObject jobRole = jobSpec.getJSONObject("roles");
                                Iterator r = jobRole.keys();
                                while (r.hasNext()) {
                                    String jobRoleId = (String) r.next();
                                    String jobRoleName = jobRole.getString(jobRoleId);

                                    ContentValues cv2 = new ContentValues();
                                    cv2.put("id", jobRoleId);
                                    cv2.put("job_spec_id", jobSpecId);
                                    cv2.put("role_name", jobRoleName);
                                    tableJobRole.addJobRole(cv2);
                                }
                            } catch (JSONException e) {
                                Toast.makeText(getApplicationContext(), R.string.unknown_error, Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.empty_response, Toast.LENGTH_LONG).show();
                    }
                }
            });
            String[] jobSpecUrl = {Jenjobs.JOB_SPEC_URL};
            g.execute(jobSpecUrl);
        }
        c.close();

        // check for sharedPreference data
        String accessToken = sharedPref.getString("access_token", null);
        if( accessToken != null ){
            loginButton.setText(R.string.my_profile);
            registerButton.setVisibility(View.GONE);
            forgotButton.setVisibility(View.GONE);
        }

        /*
        if( accessToken != null ){
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
            finish();
        }
        */
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
