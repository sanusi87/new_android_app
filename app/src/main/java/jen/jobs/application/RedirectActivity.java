package jen.jobs.application;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

public class RedirectActivity extends ActionBarActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redirect);
        setTitle(R.string.jenjobs_partner);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
