package com.example.william.myapplication;

import android.app.Activity;
import android.content.Intent;
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

    Language language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_language);

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
                args = new String[]{language_id};
            }
        }

        TableLanguage tableLanguage = new TableLanguage(this);
        Cursor cl = tableLanguage.getLanguage(args);
        if( cl.moveToFirst() ){
            HashMap _lang = Jenjobs.getLanguage();
            String _langName = (String) _lang.get( cl.getInt(1) );
            Log.e("name", ""+_langName);
            Log.e("native", ""+cl.getInt( 4 ));
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

        nativeValueLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nativeValue.setChecked( !nativeValue.isChecked() );
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
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), SelectLanguageLevel.class);
                startActivityForResult(intent, SELECT_SPOKEN);
            }
        });

        selectWrittenLevel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), SelectLanguageLevel.class);
                startActivityForResult(intent, SELECT_WRITTEN);
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> errors = new ArrayList<>();

                if( language == null ){

                }

                if( errors.size() == 0 ){
                    // TODO - save to database

                    // TODO - post to server

                    // TODO - submit to prev activity
                    Intent intent = new Intent();
                    intent.putExtra("", "");
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
