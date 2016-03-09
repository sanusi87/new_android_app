package jen.jobs.application;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutJobSearch extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_job_search);
        setTitle(getText(R.string.about_job_posting));

        if( getSupportActionBar() != null ){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TextView tv = (TextView)findViewById(R.id.aboutUs);
        tv.setText(Html.fromHtml(getString(R.string.about_us)));

        TextView tv2 = (TextView)findViewById(R.id.whoWeAre);
        tv2.setText(Html.fromHtml(getString(R.string.who_we_are_text)));

        TextView tv3 = (TextView)findViewById(R.id.whatWeDo);
        tv3.setText(Html.fromHtml(getString(R.string.what_we_do_text)));
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
