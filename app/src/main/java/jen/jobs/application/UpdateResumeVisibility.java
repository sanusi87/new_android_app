package jen.jobs.application;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;

public class UpdateResumeVisibility extends Activity {

    SharedPreferences sharedPref;
    boolean isOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_resume_visibility);
        setTitle(getText(R.string.resume_visibility));

        sharedPref = this.getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        isOnline = Jenjobs.isOnline(getApplicationContext());

        ListView listOFResumeVisibility = (ListView) findViewById(R.id.listOfResumeVisibility);
        final ResumeVisibilityAdapter rvAdapter = new ResumeVisibilityAdapter(getApplicationContext());
        listOFResumeVisibility.setAdapter(rvAdapter);

        listOFResumeVisibility.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if( isOnline ){
                    /*
                    * save visibility first before finishing activity
                    * local
                    * */
                    TableProfile tableProfile = new TableProfile(getApplicationContext());
                    ContentValues cv = new ContentValues();
                    cv.put("access", rvAdapter.visibility[position]);
                    tableProfile.updateProfile(cv, sharedPref.getInt("js_profile_id", 1));

                    /*
                    * remote
                    * */
                    PostRequest p = new PostRequest();
                    p.setResultListener(new PostRequest.ResultListener() {
                        @Override
                        public void processResult(JSONObject success) {
                        /*
                        if( success != null ){
                            Log.e("success", success.toString());

                            try {
                                int status_code = Integer.valueOf(success.get("status_code").toString());
                                Log.e("status_code", ""+status_code);
                            } catch (JSONException e) {
                                Log.e("JSONException", e.getMessage());
                            }
                        }
                        */
                        }
                    });
                    String accessToken = sharedPref.getString("access_token", null);
                    String[] s = {Jenjobs.ACCESS_LEVEL+"?access-token="+accessToken,rvAdapter.visibility[position]};
                    p.execute(s);

                    /*
                    * only then send the result back to the previous page
                    * */
                    Intent intent = new Intent();
                    intent.putExtra("selectedvisibility", rvAdapter.visibility[position]);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
