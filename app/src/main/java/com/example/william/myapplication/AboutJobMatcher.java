package com.example.william.myapplication;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

public class AboutJobMatcher extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_job_matcher);
        setTitle(getText(R.string.about_job_matcher));
    }
}
