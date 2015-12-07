package com.example.william.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class SelectPositionLevel extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_position_level);

        final ListView lv = (ListView)findViewById(R.id.listOfPositionLevels);
        final PositionLevelAdapter ca = new PositionLevelAdapter(getApplicationContext());
        lv.setAdapter(ca);

        Bundle extra = getIntent().getExtras();
        // checked selected index
        if( extra != null ){
            ArrayList<PositionLevel> selectedPositionLevels = (ArrayList<PositionLevel>) extra.get("positionlevel");
            if( selectedPositionLevels != null && selectedPositionLevels.size() > 0 ){
                for(int i=0; i < selectedPositionLevels.size();i++){
                    PositionLevel value = selectedPositionLevels.get(i);
                    int selectedIndex = ca.positionLevel.indexOf(value); // index of the selected value
                    if( selectedIndex != -1 ){
                        lv.setItemChecked(selectedIndex, true);
                    }
                }
            }
        }

        Button okButton = (Button)findViewById(R.id.okButton);
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

        Button cancelButton = (Button)findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }
}
