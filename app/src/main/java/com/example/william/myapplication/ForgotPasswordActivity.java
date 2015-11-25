package com.example.william.myapplication;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.json.JSONObject;

public class ForgotPasswordActivity extends Activity {

    private ProgressBar progressBar = null;
    private String emailAddress;
    private ForgotPasswordTask mForgotTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        final EditText emailView = (EditText)findViewById(R.id.forgot_email_address);

        Button forgotButton = (Button) findViewById(R.id.request_password_reset_button);
        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                emailAddress = String.valueOf(emailView.getText());

                mForgotTask = new ForgotPasswordTask(emailAddress);
            }
        });
    }


    public class ForgotPasswordTask extends AsyncTask<Void, Void, JSONObject> {

        private final String mEmail;

        ForgotPasswordTask(String email) {
            mEmail = email;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPostExecute(final JSONObject success) {

            //if( success.optString() ){

           // }

            mForgotTask = null;
        }
    }
}
