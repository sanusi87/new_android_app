package jen.jobs.application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

public class SelectLanguageLevel extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language_level);
        setTitle(getText(R.string.language_level));

        Bundle extra = getIntent().getExtras();
        if( extra != null ){
            int type = extra.getInt("type");
            if( type == 1 ){
                setTitle(getText(R.string.spoken_language_level));
            }else{
                setTitle(getText(R.string.written_language_level));
            }
        }

        ListView lv = (ListView)findViewById(R.id.listOfLanguageLevel);
        final LanguageLevelAdapter languageLevelAdapter = new LanguageLevelAdapter(this);
        lv.setAdapter(languageLevelAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("the_text", (LanguageLevel) languageLevelAdapter.getItem(position));
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // In order to not be too narrow, set the window size based on the screen resolution:
        final int screen_width = getResources().getDisplayMetrics().widthPixels;
        final int new_window_width = screen_width * 90 / 100;
        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.width = Math.max(layout.width, new_window_width);
        getWindow().setAttributes(layout);
    }
}
