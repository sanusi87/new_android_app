package com.example.william.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class SelectState extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_state);
        setTitle(getText(R.string.select_state));

        final ListView lv = (ListView)findViewById(R.id.listOfStates);
        final StateAdapter ca = new StateAdapter(getApplicationContext());

        Bundle extra = getIntent().getExtras();
        boolean single = false;
        // checked selected index
        if( extra != null ){
            single = extra.getBoolean("single");
            ca.setLayoutSingle(single);
            lv.setAdapter(ca);

            ArrayList selectedStates = (ArrayList) extra.get("state");
            if( selectedStates != null && selectedStates.size() > 0 ){
                for(int i=0; i < selectedStates.size();i++){
                    State value = (State) selectedStates.get(i);
                    int selectedIndex = ca.listOfStates.indexOf(value); // index of the selected value
                    if( selectedIndex != -1 ){
                        lv.setItemChecked(selectedIndex, true);
                    }
                }
            }
        }else{
            lv.setAdapter(ca);
        }

        Button okButton = (Button)findViewById(R.id.okButton);
        Button cancelButton = (Button)findViewById(R.id.cancelButton);

        if( !single ){
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
                    // when multiple, return ArrayList
                    intent.putExtra("state", values);
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
        }else{
            ((ViewGroup)okButton.getParent()).setVisibility(View.GONE);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    State state = (State) ca.getItem(position);
                    Intent intent = new Intent();
                    // when single, returned State
                    intent.putExtra("state", state);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            });
        }
    }
}
