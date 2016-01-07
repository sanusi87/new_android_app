package com.example.william.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class UpdateLanguage extends ActionBarActivity {

    private int SELECT_LANGUAGE = 1;
    private int SELECT_SPOKEN = 2;
    private int SELECT_WRITTEN = 3;

    TextView selectedLanguage;
    TextView selectedSpokenLevel;
    TextView selectedWrittenLevel;

    LinearLayout selectSpokenLevel;
    LinearLayout selectWrittenLevel;
    TextView nativeValueLabel;
    CheckBox nativeValue;

    TableLanguage tableLanguage;
    Language language;

    SharedPreferences sharedPref;
    String accessToken = null;
    int _viewIndex=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_language);
        sharedPref = this.getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        accessToken = sharedPref.getString("access_token", null);

        LinearLayout selectLanguage = (LinearLayout)findViewById(R.id.selectLanguage);
        selectSpokenLevel = (LinearLayout)findViewById(R.id.selectSpokenLevel);
        selectWrittenLevel = (LinearLayout)findViewById(R.id.selectWrittenLevel);

        selectedLanguage = (TextView) findViewById(R.id.selectedLanguage);
        selectedSpokenLevel = (TextView) findViewById(R.id.selectedSpokenLevel);
        selectedWrittenLevel = (TextView) findViewById(R.id.selectedWrittenLevel);
        nativeValueLabel = (TextView) findViewById(R.id.nativeValueLabel);
        nativeValue = (CheckBox) findViewById(R.id.nativeValue);

        Button okButton = (Button)findViewById(R.id.okButton);
        Button cancelButton = (Button)findViewById(R.id.cancelButton);

        String[] args = null;
        if( getIntent() != null ){
            Bundle extra = getIntent().getExtras();
            if( extra != null ){
                String language_id = extra.getString("language_id");
                _viewIndex = extra.getInt("_viewIndex");
                args = new String[]{language_id};

        tableLanguage = new TableLanguage(this);
                Cursor cl = tableLanguage.getLanguage(args);
                if( cl.moveToFirst() ){
                    HashMap _lang = Jenjobs.getLanguage();
                    String _langName = (String) _lang.get( cl.getInt(1) );
                    
                    language = new Language(cl.getInt(1), _langName);
                    language.spoken = cl.getInt( 2 );
                    language.written = cl.getInt( 3 );
                    language.isNative = cl.getInt( 4 );

                    if( language.isNative != 0 ){
                        nativeValue.setChecked(true);
                    }

                    selectedLanguage.setText( language.name );

                    HashMap _langlevel = Jenjobs.getLanguageLevel();
                    selectedSpokenLevel.setText((String) _langlevel.get(language.spoken));
                    selectedWrittenLevel.setText( (String)_langlevel.get( language.written ) );
                }
                cl.close();
            }
        }

        nativeValueLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( language != null ){
                    nativeValue.setChecked( !nativeValue.isChecked() );
                }
            }
        });

        selectLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), SelectLanguage.class);
                startActivityForResult(intent, SELECT_LANGUAGE);
            }
        });

        selectSpokenLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( language != null ){
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), SelectLanguageLevel.class);
                    startActivityForResult(intent, SELECT_SPOKEN);
                }
            }
        });

        selectWrittenLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( language != null ){
                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), SelectLanguageLevel.class);
                    startActivityForResult(intent, SELECT_WRITTEN);
                }
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> errors = new ArrayList<>();

                if( language == null ){
                    errors.add("Please select a language.");
                }

                if( ( language.spoken == 0 && language.written == 0 ) || ( language.spoken == 1 && language.written == 1 ) ){
                    errors.add("Please select speaking and writing efficiency.");
                }

                if( errors.size() == 0 ){
                    // TODO - save to database
                    ContentValues cv = new ContentValues();
                    cv.put("language_id", language.id);
                    cv.put("spoken_language_level_id", language.spoken);
                    cv.put("written_language_level_id", language.written);
                    cv.put("native", language.isNative);
                    tableLanguage.addLanguage(cv);

                    // TODO - post to server
                    String url = Jenjobs.LANGUAGE_URL+"?access-token="+accessToken;
                    JSONObject jsonString = new JSONObject();
                    try {
                        jsonString.put("language_id", language.id);
                        jsonString.put("spoken_language_level_id", language.spoken);
                        jsonString.put("written_language_level_id", language.written);
                        jsonString.put("native", language.isNative);

                        String[] param = {url, jsonString.toString()};
                        new PostRequest().execute(param);
                    } catch (JSONException e) {
                        Log.e("jsonErr", e.getMessage());
                    }

                    // TODO - submit to prev activity
                    Intent intent = new Intent();
                    intent.putExtra("language", language);
                    intent.putExtra("_viewIndex", _viewIndex);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), TextUtils.join(", ", errors), Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_LANGUAGE) {
            if (resultCode == RESULT_OK) {
                Bundle extra = data.getExtras();
                language = (Language)extra.get("language");
                if( language != null ){
                    selectedLanguage.setText(language.name);
                }

                selectSpokenLevel.setClickable(true);
                selectWrittenLevel.setClickable(true);
                nativeValueLabel.setClickable(true);
                nativeValue.setEnabled(true);

                ((TextView)selectSpokenLevel.getChildAt(0)).setTextColor(getResources().getColor(android.R.color.black));
                ((TextView)selectWrittenLevel.getChildAt(0)).setTextColor(getResources().getColor(android.R.color.black));
                nativeValueLabel.setTextColor(getResources().getColor(android.R.color.black));
            }
        }else if( requestCode == SELECT_SPOKEN ){
            if (resultCode == RESULT_OK) {
                Bundle extra = data.getExtras();
                LanguageLevel lv = (LanguageLevel)extra.get("the_text");
                if( lv != null ){
                    selectedSpokenLevel.setText( lv.name );
                    language.spoken = lv.id;
                }
            }
        }else if( requestCode == SELECT_WRITTEN ){
            if (resultCode == RESULT_OK) {
                Bundle extra = data.getExtras();
                LanguageLevel lv = (LanguageLevel)extra.get("the_text");
                if( lv != null ){
                    selectedWrittenLevel.setText( lv.name );
                    language.written = lv.id;
                }
            }
        }
    }
}
