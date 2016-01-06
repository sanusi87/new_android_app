package com.example.william.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class SelectLanguageLevel extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language_level);

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
}
