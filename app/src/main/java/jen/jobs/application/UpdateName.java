package jen.jobs.application;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class UpdateName extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_name);

        Bundle extra = getIntent().getExtras();
        String currentText = extra.getString("the_text");

        // set this if u wish to submit the text entered
        final String url = extra.getString("url");
        final String jsonString = extra.getString("json");

        // enable multiline?
        boolean multiline = extra.getBoolean("multiline");

        String theTitle = extra.getString("the_title");
        setTitle(theTitle);

        final EditText t = (EditText)findViewById(R.id.theText);
        t.setText(currentText);
        if( multiline ){
            t.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            t.setLines(5);
            t.setSingleLine(false);
        }

        Button okButton = (Button)findViewById(R.id.okButton);
        Button cancelButton = (Button)findViewById(R.id.cancelButton);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String theText = t.getText().toString();

                if( url != null && jsonString != null ){
                    String[] param = {url, jsonString};
                    new PostRequest().execute(param);
                }

                // finish the job
                Intent intent = new Intent();
                intent.putExtra("the_text", theText);
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
