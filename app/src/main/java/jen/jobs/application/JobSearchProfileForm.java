package jen.jobs.application;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JobSearchProfileForm extends ActionBarActivity {

    private Context context;

    Bundle searchParameter;
    JSONObject _searchParameter;
    SharedPreferences sharedPref;
    static String accessToken = null;

    int id = 0;
    int _id = 0;
    String frequency = "Daily";
    TextView selectedFrequency;
    TableJobSearchProfile tableJobSearchProfile;

    private int SELECT_NOTIFICATION_FREQUENCY = 1;
    static boolean isOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_search_profile_form);
        setTitle(R.string.create_search_profile);

        context = getApplicationContext();
        _searchParameter = new JSONObject();
        sharedPref = this.getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);
        accessToken = sharedPref.getString("access_token", null);
        tableJobSearchProfile = new TableJobSearchProfile(context);
        isOnline = Jenjobs.isOnline(context);

//        final CheckBox notificationAlert = (CheckBox)findViewById(R.id.notification_checkbox);
//        notificationAlert.setChecked(true);
//        findViewById(R.id.notification_alert).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                notificationAlert.setChecked(!notificationAlert.isChecked());
//            }
//        });

        LinearLayout selectFrequency = (LinearLayout)findViewById(R.id.selectFrequency);
        selectFrequency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SelectNotificationFrequency.class);
                startActivityForResult(intent, SELECT_NOTIFICATION_FREQUENCY);
            }
        });
        selectedFrequency = (TextView)findViewById(R.id.selectedFrequency);

        Bundle extra = getIntent().getExtras();
        if( extra != null ){
            searchParameter = extra.getBundle("searchParameter");

            // id passed from job search profiles list
            if( extra.getInt("id") > 0 ){
                id = extra.getInt("id");
                setTitle(R.string.update_search_profile);
                Cursor c = tableJobSearchProfile.getSearchProfile(id);
                c.moveToFirst();

                /*
                id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "_id NUMERIC, " + // ID from JenJOBS database
                "profile_name TEXT, " +
                "parameters TEXT, " +
                "notification_frequency TEXT, "+
                "date_created NUMERIC," +
                "date_updated NUMERIC
                */

                ((EditText) findViewById(R.id.profile_name)).setText(c.getString(c.getColumnIndex("profile_name")));

                String f = c.getString(c.getColumnIndex("notification_frequency"));
                if( f.equals("D") ){
                    frequency = getString(R.string.daily);
                }else if( f.equals("W") ){
                    frequency = getString(R.string.weekly);
                }else{
                    frequency = getString(R.string.unsubscribe);
                }
                _id = c.getInt(c.getColumnIndex("_id"));

                try {
                    _searchParameter = new JSONObject(c.getString(c.getColumnIndex("parameters")));
                } catch (JSONException e) {
                    Log.e("decodeFailed", e.getMessage());
                }

                c.close();
            }

            // bundle passed from job search
            if( searchParameter != null ) {
                try {
                    // TODO - save to _searchParameter
                    if (searchParameter.get("keyword_filter") != null) {
                        KeywordFilter keywordFilter = (KeywordFilter) searchParameter.get("keyword_filter");
                        String keyword = searchParameter.getString("keyword");
                        String salary_min = searchParameter.getString("salary_min");
                        String salary_max = searchParameter.getString("salary_max");
                        ArrayList<Integer> job_spec = searchParameter.getIntegerArrayList("job_spec");
                        ArrayList<Integer> job_role = searchParameter.getIntegerArrayList("job_role");
                        ArrayList<Integer> job_type = searchParameter.getIntegerArrayList("job_type");
                        ArrayList<Integer> position_level = searchParameter.getIntegerArrayList("position_level");
                        ArrayList<Integer> country = searchParameter.getIntegerArrayList("country");
                        ArrayList<Integer> state = searchParameter.getIntegerArrayList("state");
                        Boolean directEmployer = searchParameter.getBoolean("direct_employer");
                        Boolean recruitmentAgency = searchParameter.getBoolean("recruitment_agency");

                        if( keywordFilter != null ){ _searchParameter.put("search_by", keywordFilter.id); }
                        if( keyword != null ){ _searchParameter.put("keyword", keyword); }
                        if( salary_min != null ){ _searchParameter.put("salary_min", salary_min); }
                        if( salary_max != null ){ _searchParameter.put("salary_max", salary_max); }

                        if( job_spec != null && job_spec.size() > 0 ){
                            TableJobRole tableJobRole = new TableJobRole(context);
                            //_searchParameter.put("job_spec_id", (new JSONArray(job_spec)).toString()); // job_spec_id=[1,2,3...]

                            JSONArray objArr = new JSONArray();
                            for( Integer _job_spec : job_spec ){
                                JSONObject obj = new JSONObject();
                                obj.put("id", _job_spec);

                                JSONArray jobRoleArr = new JSONArray();
                                if( job_role != null && job_role.size() > 0){
                                    ArrayList<JobRole> _job_roles = tableJobRole.findByJobSpec(String.valueOf(_job_spec), job_role);
                                    if( _job_roles.size() > 0 ){
                                        for( JobRole _job_role : _job_roles ){
                                            jobRoleArr.put(_job_role.id);
                                        }
                                    }
                                }
                                obj.put("job_role_id", jobRoleArr);

                                objArr.put(obj);
                            }
                            _searchParameter.put("job_spec_id", objArr.toString());
                        }

                        //if( job_role != null && job_role.size() > 0 ){
                        //    _searchParameter.put("job_role_id", (new JSONArray(job_role)).toString()); // job_role_id=[1,2,3...]
                        //}

                        if( job_type != null && job_type.size() > 0 ){
                            _searchParameter.put("job_type_id", (new JSONArray(job_type)).toString()); // job_type_id=[1,2,3...]
                        }
                        if( position_level != null && position_level.size() > 0 ){
                            _searchParameter.put("job_level_id", (new JSONArray(position_level)).toString()); // job_level_id=[1,2,3...]
                        }
                        if( country != null && country.size() > 0 ){
                            _searchParameter.put("country_id", (new JSONArray(country)).toString()); // country_id=[1,2,3...]
                        }
                        if( state != null && state.size() > 0 ){
                            _searchParameter.put("state_id", (new JSONArray(state)).toString()); // state_id=[1,2,3...]
                        }

                        _searchParameter.put("direct_employer", directEmployer);
                        _searchParameter.put("advertiser", recruitmentAgency);
                    }
                } catch (JSONException e) {
                    Log.e("err", e.getMessage());
                }
            }
        }

        selectedFrequency.setText(frequency);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int clickedItem = item.getItemId();
        if( clickedItem == R.id.save ){
            if( isOnline ){
                String profileName = ((EditText)findViewById(R.id.profile_name)).getText().toString();
                if( profileName.length() == 0 ){
                    Toast.makeText(context, R.string.profile_name_required, Toast.LENGTH_LONG).show();
                }else{
                    ContentValues cv = new ContentValues();

                    if( _searchParameter.length() > 0 ){
                        cv.put("id", id);
                        cv.put("_id", _id);
                        cv.put("profile_name", profileName);
                        cv.put("parameters", _searchParameter.toString());
                        cv.put("notification_frequency", frequency.substring(0, 1));
                        if( id > 0 ){
                            cv.put("date_updated", Jenjobs.date(null,"yyyy-MM-dd hh:mm:ss",null));
                        }else{
                            cv.put("date_created", Jenjobs.date(null,"yyyy-MM-dd hh:mm:ss",null));
                        }
                        id = tableJobSearchProfile.saveSearchProfile(cv);

                        JSONObject postedData = _searchParameter; // copy content to other variable
                        try {
                            final String notificationFreq = cv.getAsString("notification_frequency");
                            postedData.put("name", profileName);
                            postedData.put("frequency", notificationFreq);

                            // TODO - post to server
                            // - get returned ID and update the row
                            // - then check this row to make sure that it is online
                            PostRequest p = new PostRequest();
                            p.setResultListener(new PostRequest.ResultListener() {
                                @Override
                                public void processResult(JSONObject success) {
                                    if( success != null ){
                                        Log.e("success", success.toString());
                                        if( success.optString("status_text") != null ){
                                            Toast.makeText(context, success.optString("status_text"), Toast.LENGTH_LONG).show();

                                            if( success.optInt("new_id") > 0 ){
                                                _id = success.optInt("new_id");
                                                ContentValues cv = new ContentValues();
                                                cv.put("id", id);
                                                cv.put("_id", _id);
                                                tableJobSearchProfile.saveSearchProfile(cv);
                                            }

                                            finish();
                                        }else{
                                            Toast.makeText(context, R.string.unknown_error, Toast.LENGTH_LONG).show();
                                        }
                                    }else{
                                        Toast.makeText(context, R.string.empty_response, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                            String url = Jenjobs.SEARCH_PROFILE;
                            if( _id > 0 ){ url += "/"+_id; }
                            url += "?access-token="+accessToken;
                            String[] param = {url, postedData.toString()};
                            p.execute(param);
                        } catch (JSONException e) {
                            Log.e("err", e.getMessage());
                        }
                    }else{
                        Toast.makeText(context, R.string.empty_search_parameter, Toast.LENGTH_LONG).show();
                    }
                }
            }else{
                Toast.makeText(context, R.string.offline_notification, Toast.LENGTH_LONG).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_NOTIFICATION_FREQUENCY) {
            if (resultCode == RESULT_OK) {
                Bundle extra = data.getExtras();
                frequency = extra.getString("frequency");
                selectedFrequency.setText(frequency);
            }
        }
    }
}
