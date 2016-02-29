package com.example.william.myapplication;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

public class RedirectActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redirect);

        Bundle extras = getIntent().getExtras();
        if( extras != null ){
            String redirectUrl = extras.getString("redirect");
            WebView wv = (WebView)findViewById(R.id.website_content);
            wv.loadUrl(redirectUrl);
            WebSettings ws = wv.getSettings();
            ws.setJavaScriptEnabled(true);
        }else{
            Toast.makeText(getApplicationContext(), getString(R.string.no_url_provided), Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
