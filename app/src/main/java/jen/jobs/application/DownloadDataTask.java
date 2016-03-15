package jen.jobs.application;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DownloadDataTask{

    SharedPreferences sharedPref;
    String accessToken;
    Context context;

    TableProfile tableProfile;
    TableJob tableJob;

    public DownloadDataTask(Context c, String accessToken){
        this.context = c;
        this.accessToken = accessToken;
        sharedPref = c.getSharedPreferences(MainActivity.JENJOBS_SHARED_PREFERENCE, Context.MODE_PRIVATE);

        tableProfile = new TableProfile(context);
        tableJob = new TableJob(context);
    }

    public void downloadProfile(final View v){
        String[] profileUrl = {Jenjobs.PROFILE_URL+"?access-token="+accessToken};
        GetRequest g = new GetRequest();
        g.execute(profileUrl);
        g.setResultListener(new GetRequest.ResultListener() {
            @Override
            public void processResultArray(JSONArray result) {
            }

            @Override
            public void processResult(JSONObject success) {
                if (success != null) {
                    int js_profile_id;
                    TableAddress tblAddress = new TableAddress(context);
                    ContentValues cv = new ContentValues();

                    try {
                        success.remove("_link"); // remove _link

                        cv.put("access_token", String.valueOf(accessToken));
                        cv.put("_id", String.valueOf(success.get("id")));
                        cv.put("email", String.valueOf(success.get("email")));
                        cv.put("username", String.valueOf(success.get("username")));
                        cv.put("name", String.valueOf(success.get("name")));
                        cv.put("ic_no", String.valueOf(success.get("ic_no")));
                        cv.put("passport_no", String.valueOf(success.get("passport_no")));
                        cv.put("mobile_no", String.valueOf(success.get("mobile_no")));
                        cv.put("dial_code", String.valueOf(success.get("dial_code")));
                        cv.put("gender", String.valueOf(success.get("gender")));
                        cv.put("dob", String.valueOf(success.get("dob")));
                        cv.put("pr", String.valueOf(success.get("pr")));
                        cv.put("resume_file", String.valueOf(success.get("resume_file")));
                        cv.put("photo_file", String.valueOf(success.get("photo_file")));
                        cv.put("access", String.valueOf(success.get("access")));
                        cv.put("status", String.valueOf(success.get("status")));
                        cv.put("country_id", String.valueOf(success.get("country_id")));
                        cv.put("driving_license", String.valueOf(success.get("driving_license")));
                        cv.put("transport", String.valueOf(success.get("transport")));
                        cv.put("js_jobseek_status_id", String.valueOf(success.get("js_jobseek_status_id")));
                        cv.put("availability", String.valueOf(success.get("availability")));
                        cv.put("availability_unit", String.valueOf(success.get("availability_unit")));
                        //cv.put("address", String.valueOf(success.get("address")));
                        cv.put("no_work_exp", String.valueOf(success.get("no_work_exp")));
                        cv.put("additional_info", String.valueOf(success.get("info")));
                        cv.put("created_at", String.valueOf(success.get("created_at")));
                        cv.put("updated_at", String.valueOf(success.get("updated_at")));

                        tableProfile.addProfile(cv);
                        js_profile_id = cv.getAsInteger("_id");
                        //Log.e("js_profile_id", String.valueOf(js_profile_id));

                        SharedPreferences.Editor spEdit = sharedPref.edit();
                        spEdit.putInt("js_profile_id", js_profile_id);
                        spEdit.apply();

                        // default address
                        ContentValues cv2 = new ContentValues();
                        cv2.put("address1", "");
                        cv2.put("address2", "");
                        cv2.put("postcode", 0);
                        cv2.put("city_id", 0);
                        cv2.put("city_name", "");
                        cv2.put("state_id", 0);
                        cv2.put("state_name", "");
                        cv2.put("country_id", 0);
                        cv2.put("updated_at", Jenjobs.date(null, null, null));
                        tblAddress.addAddress(cv2);

                        if (v != null) {
                            TextView name = (TextView) v.findViewById(R.id.fullName);
                            TextView email = (TextView) v.findViewById(R.id.email);
                            TextView mobile_no = (TextView) v.findViewById(R.id.mobile_no);
                            TextView ic_no = (TextView) v.findViewById(R.id.ic_no);
                            TextView gender = (TextView) v.findViewById(R.id.gender);
                            TextView dob = (TextView) v.findViewById(R.id.dob);
                            TextView country = (TextView) v.findViewById(R.id.country);
                            ImageView profileImage = (ImageView) v.findViewById(R.id.profile_image);

                            name.setText(String.valueOf(success.get("name")));
                            email.setText(String.valueOf(success.get("email")));
                            mobile_no.setText(String.valueOf(success.get("mobile_no")));
                            ic_no.setText(String.valueOf(success.get("ic_no")));
                            gender.setText(String.valueOf(success.get("gender")));
                            country.setText(String.valueOf(success.get("country")));

                            String _dob = String.valueOf(success.get("dob"));
                            if (_dob != null) {
                                dob.setText(Jenjobs.date(_dob, null, "yyyy-MM-dd"));
                            }

                            if (String.valueOf(success.get("photo_file")) != null) {
                                new ImageLoad(String.valueOf(success.get("photo_file")), profileImage).execute();
                            }
                        }

                        // save address
                        String address = String.valueOf(success.get("address"));
                        if (address != null && !address.equals("null")) {
                            JSONObject jsonAddr = new JSONObject(address);
                            ContentValues cv3 = new ContentValues();
                            cv3.put("address1", jsonAddr.getString("address1"));
                            cv3.put("address2", jsonAddr.getString("address2"));
                            String postCodeStr = jsonAddr.getString("postcode");
                            int postCode = 0;
                            if (postCodeStr != null && !postCodeStr.equals("null")) {
                                postCode = Integer.valueOf(postCodeStr);
                            }
                            cv3.put("postcode", postCode);
                            cv3.put("city_id", jsonAddr.getInt("city_id"));
                            cv3.put("city_name", jsonAddr.getString("city_name"));
                            cv3.put("state_id", jsonAddr.getInt("state_id"));
                            cv3.put("state_name", jsonAddr.getString("state_name"));
                            cv3.put("country_id", jsonAddr.getInt("country_id"));
                            cv3.put("updated_at", jsonAddr.getString("date_updated"));
                            tblAddress.updateAddress(cv3);
                        }
                        // end save address
                    } catch (JSONException e) {
                        Log.e("profileExcp", e.getMessage());
                    }
                }
            }
        });
    }

    public void downloadWorkExperience(){
        String[] workExperienceUrl = {Jenjobs.WORK_EXPERIENCE_URL+"?access-token="+accessToken};
        GetRequest g = new GetRequest();
        g.setResultListener(new GetRequest.ResultListener() {
            @Override
            public void processResultArray(JSONArray success) {
                ContentValues cv = new ContentValues();

                try {
                    if( success.length() > 0 ){
                        TableWorkExperience tableWorkExperience = new TableWorkExperience(context);
                        for( int i=0;i< success.length();i++ ){
                            JSONObject s = success.getJSONObject(i);

                            cv.put("_id", s.getInt("id"));
                            cv.put("position", s.optString("position"));
                            cv.put("company", s.optString("company"));

                            if( s.optString("job_type") != null && !s.optString("job_type").equals("null") ){
                                JSONObject jobType = new JSONObject(s.optString("job_type"));
                                cv.put("job_type_id", jobType.optInt("id") > 0 ? jobType.optInt("id") : 0 );
                            }

                            if( s.optString("job_spec") != null && s.optString("job_role") != null ){
                                JSONObject jobSpec = new JSONObject(s.optString("job_spec"));
                                cv.put("job_spec_id", jobSpec.optInt("id") > 0 ? jobSpec.optInt("id") : 0 );

                                JSONObject jobRole = new JSONObject(s.optString("job_role"));
                                cv.put("job_role_id", jobRole.optInt("id") > 0 ? jobRole.optInt("id") : 0 );
                            }

                            JSONObject jobLevel = new JSONObject(s.optString("job_level"));
                            cv.put("job_level_id", jobLevel.optInt("id") > 0 ? jobLevel.optInt("id") : 0 );

                            JSONObject jobIndustry = new JSONObject(s.optString("industry"));
                            cv.put("industry_id", jobIndustry.optInt("id") > 0 ? jobIndustry.optInt("id") : 0 );

                            cv.put("experience", s.optString("experience"));
                            cv.put("salary", s.optString("salary"));
                            cv.put("currency_id", s.optInt("currency_id"));
                            cv.put("started_on", s.optString("started_on"));
                            cv.put("resigned_on", s.optString("resigned_on"));
                            cv.put("update_at", "");
                            tableWorkExperience.addWorkExperience(cv);
                        }
                    }
                } catch (JSONException e) {
                    Log.e("workExpExcp", e.getMessage());
                }
            }

            @Override
            public void processResult(JSONObject success) {}
        });
        g.execute(workExperienceUrl);
    }

    public void downloadEducation(){
        String[] educationUrl = {Jenjobs.EDUCATION_URL+"?access-token="+accessToken};
        GetRequest g = new GetRequest();
        g.setResultListener(new GetRequest.ResultListener() {
            @Override
            public void processResultArray(JSONArray success) {
                ContentValues cv = new ContentValues();
                try {
                    if( success.length() > 0 ){
                        TableEducation tableEducation = new TableEducation(context);
                        for( int i=0;i< success.length();i++ ){
                            JSONObject s = success.getJSONObject(i);

                            cv.put("_id", s.getInt("id"));
                            cv.put("school", s.getString("school"));
                            cv.put("major", s.optString("major"));

                            JSONObject eduLevel = new JSONObject(s.optString("level"));
                            cv.put("edu_level_id", eduLevel.optInt("id") > 0 ? eduLevel.optInt("id") : 0 );

                            JSONObject eduField = new JSONObject(s.optString("field"));
                            cv.put("edu_field_id", eduField.optInt("id") > 0 ? eduField.optInt("id") : 0 );

                            cv.put("country_id", s.optString("country"));
                            cv.put("grade", s.optString("grade"));
                            cv.put("info", s.optString("info"));
                            cv.put("date_graduated", s.getString("date_graduated"));

                            tableEducation.addEducation(cv);
                        }
                    }

                } catch (JSONException e) {
                    Log.e("eduExcp", e.getMessage());
                }
            }

            @Override
            public void processResult(JSONObject success) {}
        });
        g.execute(educationUrl);
    }

    public void downloadApplication(){
        String[] applicationUrl = {Jenjobs.APPLICATION_URL+"?access-token="+accessToken};
        GetRequest g = new GetRequest();
        g.setResultListener(new GetRequest.ResultListener() {
            @Override
            public void processResultArray(JSONArray success) {
                try {
                    if( success.length() > 0 ){
                        ContentValues cv = new ContentValues();
                        TableApplication tableApplication = new TableApplication(context);
                        for( int i=0;i< success.length();i++ ){
                            JSONObject s = success.getJSONObject(i);
                            final int postId = s.getInt("post_id");
                            cv.put("_id", s.getInt("_id"));
                            cv.put("post_id", postId);
                            cv.put("status", s.getInt("status"));
                            cv.put("date_created", s.getString("date_created"));
                            cv.put("date_updated", s.getString("date_updated"));
                            cv.put("title", s.getString("title"));
                            cv.put("closed", s.getBoolean("closed") ? 1 : 0);
                            tableApplication.addApplication(cv);

                            // download job details and save to TableJob
                            // download the job details
                            GetRequest getRequest = new GetRequest();
                            getRequest.setResultListener(new GetRequest.ResultListener() {
                                @Override
                                public void processResultArray(JSONArray result) {}

                                @Override
                                public void processResult(JSONObject success) {
                                    if (success != null && success.toString().length() > 0) {
                                        // and save to phone database
                                        ContentValues cv2 = new ContentValues();

                                        cv2.put("id", postId);
                                        cv2.put("title", success.optString("title"));
                                        cv2.put("company", success.optString("company"));
                                        cv2.put("job_data", success.toString());
                                        cv2.put("date_closed", success.optString("date_closed"));

                                        tableJob.addJob(cv2);
                                    }
                                }
                            });
                            String[] param = {Jenjobs.JOB_DETAILS+"/"+postId};
                            getRequest.execute(param);
                        }
                    }
                } catch (JSONException e) {
                    Log.e("appExcp", e.getMessage());
                }
            }

            @Override
            public void processResult(JSONObject success) {}
        });
        g.execute(applicationUrl);
    }

    public void downloadJobPreference(){
        String[] jobPreferenceUrl = {Jenjobs.JOB_PREFERENCE_URL+"?access-token="+accessToken};
        GetRequest g = new GetRequest();
        g.setResultListener(new GetRequest.ResultListener() {
            @Override
            public void processResultArray(JSONArray result) {}

            @Override
            public void processResult(JSONObject success) {
                if( success != null ){
                    TableJobPreference tableJobPreference = new TableJobPreference(context);
                    TableJobPreferenceLocation tableJobPreferenceLocation = new TableJobPreferenceLocation(context);

                    ContentValues cv = new ContentValues();
                    try {
                        cv.put("salary", success.optString("salary"));
                        cv.put("currency_id", success.optInt("currency_id"));
                        tableJobPreference.updateJobPreference(cv);
                        cv.put("job_type_id", success.optString("job_type_id"));

                        tableJobPreferenceLocation.truncate();
                        JSONArray state = new JSONArray(success.optString("state_id"));
                        if( state.length() > 0 ){
                            for(int i=0;i<state.length();i++){
                                ContentValues cv2 = new ContentValues();
                                cv2.put("state_id", (int)state.get(i));
                                cv2.put("country_id", 127);
                                tableJobPreferenceLocation.insertJobPreference(cv2);
                            }
                        }

                        JSONArray country = new JSONArray(success.optString("country_id"));
                        if( country.length() > 0 ){
                            for(int i=0;i<country.length();i++) {
                                ContentValues cv2 = new ContentValues();
                                cv2.put("state_id", 0);
                                cv2.put("country_id", (int)country.get(i));
                                tableJobPreferenceLocation.insertJobPreference(cv2);
                            }
                        }

                    } catch (JSONException e) {
                        Log.e("jobPrefExcp", e.getMessage());
                    }
                }
            }
        });
        g.execute(jobPreferenceUrl);
    }

    public void downloadSkill(){
        String[] skillUrl = {Jenjobs.SKILL_URL+"?access-token="+accessToken};
        GetRequest g = new GetRequest();
        g.setResultListener(new GetRequest.ResultListener() {
            @Override
            public void processResultArray(JSONArray success) {
                try {
                    if( success.length() > 0 ) {
                        TableSkill tableSkill = new TableSkill(context);
                        for (int i = 0; i < success.length(); i++) {
                            JSONObject s = success.getJSONObject(i);

                            ContentValues cv = new ContentValues();
                            cv.put("_id", s.optInt("id"));
                            cv.put("name", s.optString("value"));
                            tableSkill.addSkill(cv);
                        }
                    }
                } catch (JSONException e) {
                    Log.e("skillExcp", e.getMessage());
                }
            }

            @Override
            public void processResult(JSONObject success) {}
        });
        g.execute(skillUrl);
    }

    public void downloadLanguage(){
        String[] languageUrl = {Jenjobs.LANGUAGE_URL+"?access-token="+accessToken};
        GetRequest g = new GetRequest();
        g.setResultListener(new GetRequest.ResultListener() {
            @Override
            public void processResultArray(JSONArray success) {
                if( success != null ){
                    TableLanguage tableLanguage = new TableLanguage(context);
                    try {
                        if( success.length() > 0 ) {
                            for (int i = 0; i < success.length(); i++) {
                                JSONObject s = success.getJSONObject(i);
                                ContentValues cv = new ContentValues();
                                cv.put("language_id", s.optInt("language_id"));
                                cv.put("spoken_language_level_id", s.optInt("spoken_language_level_id"));
                                cv.put("written_language_level_id", s.optInt("written_language_level_id"));
                                cv.put("native", s.optInt("native"));
                                tableLanguage.addLanguage(cv);
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("langExcp", e.getMessage());
                    }
                }
            }

            @Override
            public void processResult(JSONObject success) {

            }
        });
        g.execute(languageUrl);
    }

    public void downloadBookmark(){
        String[] bookmarkUrl = {Jenjobs.BOOKMARK_URL+"?access-token="+accessToken};
        GetRequest g = new GetRequest();
        g.setResultListener(new GetRequest.ResultListener() {
            @Override
            public void processResultArray(JSONArray success) {
                TableBookmark tableBookmark = new TableBookmark(context);
                try {
                    if( success.length() > 0 ) {
                        for (int i = 0; i < success.length(); i++) {
                            JSONObject s = success.getJSONObject(i);
                            ContentValues cv = new ContentValues();

                            final int postId = s.optInt("post_id");
                            cv.put("post_id", postId);
                            cv.put("title", s.optString("title"));
                            cv.put("date_added", s.optString("on"));
                            cv.put("date_closed", s.optString("date_closed"));
                            tableBookmark.addBookmark(cv);

                            // download job details and save to TableJob
                            GetRequest getRequest = new GetRequest();
                            getRequest.setResultListener(new GetRequest.ResultListener() {
                                @Override
                                public void processResultArray(JSONArray result) {}

                                @Override
                                public void processResult(JSONObject success) {
                                    if (success != null && success.toString().length() > 0) {
                                        ContentValues cv2 = new ContentValues();

                                        cv2.put("id", postId);
                                        cv2.put("title", success.optString("title"));
                                        cv2.put("company", success.optString("company"));
                                        cv2.put("job_data", success.toString());
                                        cv2.put("date_closed", success.optString("date_closed"));

                                        tableJob.addJob(cv2);
                                    }
                                }
                            });
                            String[] param = {Jenjobs.JOB_DETAILS+"/"+postId};
                            getRequest.execute(param);
                        }
                    }
                } catch (JSONException e) {
                    Log.e("bookExcp", e.getMessage());
                }
            }

            @Override
            public void processResult(JSONObject success) {}
        });
        g.execute(bookmarkUrl);
    }

    public void downloadSubscription(){
        String[] subscriptionUrl = {Jenjobs.SUBSCRIPTION_URL+"?access-token="+accessToken};
        GetRequest g = new GetRequest();
        g.setResultListener(new GetRequest.ResultListener() {
            @Override
            public void processResultArray(JSONArray success) {
                TableSubscription tableSubscription = new TableSubscription(context);

                try {
                    if( success.length() > 0 ) {
                        for (int i = 0; i < success.length(); i++) {
                            JSONObject s = success.getJSONObject(i);
                            ContentValues cv = new ContentValues();
                            cv.put("status", s.optBoolean("status") ? 1 : 0);
                            int subscriptionID = s.getInt("subscription_id");
                            tableSubscription.updateSubscription(cv, subscriptionID);
                        }
                    }
                } catch (JSONException e) {
                    Log.e("subExcp", e.getMessage());
                }
            }

            @Override
            public void processResult(JSONObject success) {}
        });
        g.execute(subscriptionUrl);
    }

    public void downloadInvitation(){
        String[] invitationUrl = {Jenjobs.INVITATION_URL+"?access-token="+accessToken};
        GetRequest g = new GetRequest();
        g.setResultListener(new GetRequest.ResultListener() {
            @Override
            public void processResultArray(JSONArray success) {
                TableInvitation tableInvitation = new TableInvitation(context);
                if( success != null ){
                    try {
                        if( success.length() > 0 ) {
                            for (int i = 0; i < success.length(); i++) {
                                JSONObject s = success.getJSONObject(i);
                                ContentValues cv = new ContentValues();

                                cv.put("id", s.optInt("id"));
                                cv.put("emp_profile_name", s.optString("company"));
                                cv.put("emp_profile_id", s.optInt("company_id"));
                                cv.put("status", s.optString("status"));
                                cv.put("date_added", s.optString("date_created"));
                                String dateUpdated = s.optString("date_updated");
                                if( dateUpdated != null && !dateUpdated.equals("") && !dateUpdated.equals("null") ){
                                    cv.put("date_updated", dateUpdated);
                                }

                                // this is for type "J" = Job Application Invitation
                                String post = s.getString("post");
                                if( post != null && !post.equals("null") ){
                                    JSONObject _post = new JSONObject(post);

                                    final int postId = _post.getInt("post_id");
                                    boolean isJobClosed = _post.getBoolean("closed");

                                    // for each job application invitation
                                    // if the job is still active
                                    if( !isJobClosed ){
                                        // download the job details
                                        GetRequest getRequest = new GetRequest();
                                        getRequest.setResultListener(new GetRequest.ResultListener() {
                                            @Override
                                            public void processResultArray(JSONArray result) {}

                                            @Override
                                            public void processResult(JSONObject success) {
                                                if( success != null && success.toString().length() > 0 ){
                                                    // and save to phone database
                                                    ContentValues cv2 = new ContentValues();

                                                    cv2.put("id", postId);
                                                    cv2.put("title", success.optString("title"));
                                                    cv2.put("company", success.optString("company"));
                                                    cv2.put("job_data", success.toString());
                                                    cv2.put("date_closed", success.optString("date_closed"));

                                                    tableJob.addJob(cv2);
                                                }
                                            }
                                        });
                                        String[] param = {Jenjobs.JOB_DETAILS+"/"+postId};
                                        getRequest.execute(param);
                                    }

                                    cv.put("post_id", postId);
                                    cv.put("post_title", _post.getString("post_title"));
                                    cv.put("post_closed_on", _post.getString("date_closed"));
                                }
                                tableInvitation.saveInvitation(cv, 0);
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("subExcp", e.getMessage());
                    }
                }
            }

            @Override
            public void processResult(JSONObject success) {}
        });

        g.execute(invitationUrl);
    }

    public void downloadJobmatcherProfile(){
        String[] jobmatcherUrl = {Jenjobs.SEARCH_PROFILE+"?access-token="+accessToken};
        GetRequest g = new GetRequest();
        g.setResultListener(new GetRequest.ResultListener() {
            @Override
            public void processResultArray(JSONArray success) {
                if (success != null) {
                    try {
                        if (success.length() > 0) {
                            TableJobSearchProfile tableJobSearchProfile = new TableJobSearchProfile(context);

                            for (int i = 0; i < success.length(); i++) {
                                JSONObject s = success.getJSONObject(i);
                                JSONObject queryParam = s.getJSONObject("query_param");
                                ContentValues cv = new ContentValues();

                                cv.put("id", 0);
                                cv.put("_id", (int) (s.get("id")));
                                cv.put("profile_name", s.getString("name"));
                                cv.put("parameters", queryParam.toString());
                                cv.put("notification_frequency", s.getString("frequency"));
                                cv.put("date_created", s.getString("date_added"));
                                cv.put("date_updated", s.getString("date_updated"));

                                tableJobSearchProfile.saveSearchProfile(cv);
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("djmerr", e.getMessage());
                    }
                }
            }

            @Override
            public void processResult(JSONObject success) {
            }
        });
        g.execute(jobmatcherUrl);
    }

}