package com.example.william.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class SelectState extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_state);
        setTitle("Select State");

        final ListView lv = (ListView)findViewById(R.id.listOfStates);
        final StateAdapter ca = new StateAdapter(getApplicationContext());
        lv.setAdapter(ca);

        Bundle extra = getIntent().getExtras();
        boolean single = false;
        // checked selected index
        if( extra != null ){
            ArrayList<State> selectedStates = (ArrayList<State>) extra.get("state");
            if( selectedStates != null && selectedStates.size() > 0 ){
                for(int i=0; i < selectedStates.size();i++){
                    State value = selectedStates.get(i);
                    int selectedIndex = ca.listOfStates.indexOf(value); // index of the selected value
                    if( selectedIndex != -1 ){
                        lv.setItemChecked(selectedIndex, true);
                    }
                }
            }
        }else{

        }

        if( !single ){
            Button okButton = (Button)findViewById(R.id.okButton);
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SparseBooleanArray a = lv.getCheckedItemPositions();
                    ArrayList<State> values = new ArrayList<>();
                    for (int i = 0; i < a.size(); i++) {
                        if (a.valueAt(i) && a.keyAt(i) >= 0) {
                            State c = (State) ca.getItem(a.keyAt(i));
                            values.add(c);
                        }
                    }

                    Intent intent = new Intent();
                    intent.putExtra("state", values);
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
        }else{
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    State state = (State) ca.getItem(position);
                    Intent intent = new Intent();
                    intent.putExtra("state", state);
                    setResult(Activity.RESULT_OK, intent);
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
