package jen.jobs.application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class SelectLanguage extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);
        setTitle(getText(R.string.select_language));

        final ListView listOfLanguage = (ListView)findViewById(R.id.listOfLanguage);
        final LanguageAdapter languageAdapter = new LanguageAdapter(this);
        listOfLanguage.setAdapter(languageAdapter);

        listOfLanguage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("language", (Language) languageAdapter.getItem(position));
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }
}
