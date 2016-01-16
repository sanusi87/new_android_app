package com.example.william.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class SelectPositionLevel extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_position_level);
        setTitle("Select Position Level");

        final ListView lv = (ListView)findViewById(R.id.listOfPositionLevels);
        final PositionLevelAdapter ca = new PositionLevelAdapter(getApplicationContext());

        boolean single = true;

        ArrayList selectedPositionLevels = null;
        Bundle extra = getIntent().getExtras();

        if( extra != null ){
            selectedPositionLevels = (ArrayList) extra.get("positionlevel");
            single = extra.getBoolean("single");
        }

        ca.setSingle(single);
        lv.setAdapter(ca);

        Button okButton = (Button)findViewById(R.id.okButton);
        Button cancelButton = (Button)findViewById(R.id.cancelButton);

        if( single ){
            ((ViewGroup)okButton.getParent()).setVisibility(View.GONE);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent();
                    intent.putExtra("positionlevel", (PositionLevel)ca.getItem(position));
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            });
        }else{
            if( selectedPositionLevels != null && selectedPositionLevels.size() > 0 ){
                for(int i=0; i < selectedPositionLevels.size();i++){
                    PositionLevel value = (PositionLevel) selectedPositionLevels.get(i);
                    int selectedIndex = ca.positionLevel.indexOf(value); // index of the selected value
                    if( selectedIndex != -1 ){
                        lv.setItemChecked(selectedIndex, true);
                    }
                }
            }

            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SparseBooleanArray a = lv.getCheckedItemPositions();
                    ArrayList<PositionLevel> values = new ArrayList<>();
                    for (int i = 0; i < a.size(); i++) {
                        if (a.valueAt(i) && a.keyAt(i) >= 0) {
                            PositionLevel c = (PositionLevel) ca.getItem(a.keyAt(i));
                            values.add(c);
                        }
                    }

                    Intent intent = new Intent();
                    intent.putExtra("positionlevel", values);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
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
