package com.example.william.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPasswordActivity extends ActionBarActivity {
    Button forgotButton;
    TableForgotPassword tableForgotPassword;
    TableSettings tableSettings;
    boolean isOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
        setTitle("Forgot Password?");
        if( getSupportActionBar() != null ){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        isOnline = Jenjobs.isOnline(getApplicationContext());
        final EditText emailView = (EditText)findViewById(R.id.forgot_email_address);

        forgotButton = (Button) findViewById(R.id.request_password_reset_button);
        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( isOnline ){
                    String emailAddress = emailView.getText().toString();
                    String[] params = {Jenjobs.FORGOT_PASSWORD_URL, emailAddress};
                    //new ForgotPasswordTask().execute(params);
                    PostRequest p = new PostRequest();
                    p.setResultListener(new PostRequest.ResultListener() {
                        @Override
                        public void processResult(JSONObject result) {
                            if( result != null ){
                                try {
                                    String statusText = result.getString("status_text");
                                    Toast.makeText(getApplicationContext(), statusText, Toast.LENGTH_SHORT).show();

                                    // log request for counting
                                    tableForgotPassword.logRequest();
                                } catch (JSONException e) {
                                    Toast.makeText(getApplicationContext(), "Response error!", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(getApplicationContext(), "No response received!", Toast.LENGTH_SHORT).show();
                            }

                            // enable button after 5s
                            (new Handler()).postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    forgotButton.setEnabled(true);
                                    forgotButton.setClickable(true);
                                }
                            }, 5000);
                        }
                    });
                    p.execute(params);

                    forgotButton.setEnabled(false);
                    forgotButton.setClickable(false);
                }else{
                    Toast.makeText(getApplicationContext(), R.string.offline_notification, Toast.LENGTH_LONG).show();
                }
            }
        });

        // get max request allowed from database
        tableSettings = new TableSettings(this);
        Integer maxRequest = Integer.valueOf(tableSettings.getSetting("max_password_reset_request"));

        // count and compare request done for today
        tableForgotPassword = new TableForgotPassword(this);
        int todayTotalRequest = tableForgotPassword.countTodayRequest();
        Log.e("requestCount", ""+todayTotalRequest);

        // disable button id exceed
        if( todayTotalRequest > maxRequest ){
            forgotButton.setEnabled(false);
            forgotButton.setClickable(false);
            Toast.makeText(getApplicationContext(), "Total number of password reset request reached!", Toast.LENGTH_SHORT).show();
        }
    }
}
