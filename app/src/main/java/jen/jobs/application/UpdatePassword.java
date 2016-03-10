package jen.jobs.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UpdatePassword extends ActionBarActivity {

    SharedPreferences sharedPref;
    static String accessToken = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        setTitle(R.string.update_password);

        sharedPref = this.getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        accessToken = sharedPref.getString("access_token", null);

        final EditText currentPassword = (EditText)findViewById(R.id.currentPassword);
        final EditText newPassword = (EditText)findViewById(R.id.newPassword);
        final EditText newPasswordRepeat = (EditText)findViewById(R.id.newPasswordRepeat);

        final Button saveButton = (Button)findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> errors = new ArrayList<>();
                if( currentPassword.getText().length() < 6 ){
                    errors.add("Current password is required, and at least 6 characters in length");
                }

                if( newPassword.getText().length() < 6 ){
                    errors.add("New password is required, and at least 6 characters in length");
                }

                if( newPasswordRepeat.getText().length() < 6 ){
                    errors.add("Repeat password is required, and at least 6 characters in length");
                }

                if( errors.size() == 0 ){
                    saveButton.setEnabled(false);
                    saveButton.setClickable(false);

                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("password", currentPassword.getText().toString());
                        obj.put("newPassword", newPassword.getText().toString());
                        obj.put("newPasswordRepeat", newPasswordRepeat.getText().toString());

                        String[] param = {Jenjobs.CHANGE_PASSWORD_URL + "?access-token=" + accessToken,obj.toString()};
                        PostRequest p = new PostRequest();
                        p.setResultListener(new PostRequest.ResultListener() {
                            @Override
                            public void processResult(JSONObject success) {
                                if( success != null ){
                                    try {
                                        Toast.makeText(getApplicationContext(), success.getString("text"), Toast.LENGTH_LONG).show();
                                        finish();
                                    } catch (JSONException e) {
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }

                                saveButton.setEnabled(true);
                                saveButton.setClickable(true);
                            }
                        });
                        p.execute(param);
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        saveButton.setEnabled(true);
                        saveButton.setClickable(true);
                    }
                }else{
                    Toast.makeText(getApplicationContext(), TextUtils.join(". ", errors), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
