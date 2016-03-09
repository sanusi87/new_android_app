package jen.jobs.application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class SelectJobNotice  extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_job_notice);

        final TextView durationPeriod = (TextView) findViewById(R.id.durationPeriod);

        final ListView lv = (ListView) findViewById(R.id.listOfPeriod);
        String[] items = {"Day(s)", "Week(s)", "Month(s)", "Immedietly"};
        lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, items));

        final Button okButton = (Button)findViewById(R.id.okButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedItem = lv.getSelectedItem().toString();
                Intent intent = new Intent();
                intent.putExtra("availability", selectedItem);
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
