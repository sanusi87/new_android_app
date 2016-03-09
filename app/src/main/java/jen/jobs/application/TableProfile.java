package jen.jobs.application;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class TableProfile extends SQLiteOpenHelper{

    public static final String TABLE_NAME = "profile";
	
    public static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TABLE_NAME+"' (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + //0
            "_id INTEGER, " + //1
            "email TEXT, " + //2
            "username TEXT, " + //3
            "name TEXT, " + //4
            "ic_no TEXT, " + //5
            "passport_no TEXT, " + //6
            "dial_code TEXT, " + //7
            "mobile_no TEXT, " + //8
            "gender TEXT, " + //9
            "dob TEXT, " + //10
            "pr INTEGER(2), " + //11
            "resume_file TEXT, " + //12
            "photo_file TEXT, " + //13
            "access TEXT, " + //14
            "access_token TEXT, " + //15
            "status TEXT, " + //16
            "country_id INTEGER(4), " + //17
            "driving_license INTEGER(2), " + //18
            "transport INTEGER(2), " + //19
            "js_jobseek_status_id INTEGER(2), " + //20
            "availability INTEGER(4), " + //21
            "availability_unit TEXT, " + //22
            //"address TEXT, " + //23
            "no_work_exp TEXT, " + //1=no work, 0=got work //24
            "additional_info TEXT, " + //25
            "created_at NUMERIC, " + //26
            "updated_at NUMERIC," + //27
            "resume_uri TEXT," +
            "photo_uri TEXT);";
    public static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TABLE_NAME+"'";
    public static String SQL_EMPTY_TABLE = "DELETE FROM '"+TABLE_NAME+"'";

    public SQLiteDatabase db;
    private Context context;

    public TableProfile(Context context){
        super(context, Jenjobs.DATABASE_NAME , null, Jenjobs.DATABASE_VERSION);
        this.context = context;
        db = this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TableProfile.SQL_CREATE_ENTRIES);
        db.execSQL(TableApplication.SQL_CREATE_ENTRIES);
        db.execSQL(TableBookmark.SQL_CREATE_ENTRIES);
        db.execSQL(TableEducation.SQL_CREATE_ENTRIES);
        db.execSQL(TableJob.SQL_CREATE_ENTRIES);
        db.execSQL(TableJobPreference.SQL_CREATE_ENTRIES);
        db.execSQL(TableJobRole.SQL_CREATE_ENTRIES);
        db.execSQL(TableJobSpec.SQL_CREATE_ENTRIES);
        db.execSQL(TableLanguage.SQL_CREATE_ENTRIES);
        db.execSQL(TableSettings.SQL_CREATE_ENTRIES);
        db.execSQL(TableSkill.SQL_CREATE_ENTRIES);
        db.execSQL(TableSubscription.SQL_CREATE_ENTRIES);
        db.execSQL(TableWorkExperience.SQL_CREATE_ENTRIES);
		db.execSQL(TableCountry.SQL_CREATE_ENTRIES);
		db.execSQL(TableAddress.SQL_CREATE_ENTRIES);
        db.execSQL(TableJobPreferenceLocation.SQL_CREATE_ENTRIES);
        db.execSQL(TableInvitation.SQL_CREATE_ENTRIES);
        db.execSQL(TableForgotPassword.SQL_CREATE_ENTRIES);
        db.execSQL(TableJobSearchProfile.SQL_CREATE_ENTRIES);
        db.execSQL(TableJobSearchMatched.SQL_CREATE_ENTRIES);

        // insert subscription
        ContentValues cv1 = new ContentValues();
        ContentValues cv2 = new ContentValues();
        ContentValues cv3 = new ContentValues();
        cv1.put("subscription_name", "Emails of newsletter, promotion, career advise from JenJOBS");
        cv1.put("subscription_id", TableSubscription.NEWSLETTER);
        cv1.put("status", "1");
        cv2.put("subscription_name", "Promotion emails from JenJOBS partners and affiliates");
        cv2.put("subscription_id", TableSubscription.PROMOTION);
        cv2.put("status", "1");
        cv3.put("subscription_name", "SMS Job Alert");
        cv3.put("subscription_id", TableSubscription.SMS_JOB_ALERT);
        cv3.put("status", "1");
        db.insert(TableSubscription.TABLE_NAME, null, cv1);
        db.insert(TableSubscription.TABLE_NAME, null, cv2);
        db.insert(TableSubscription.TABLE_NAME, null, cv3);
        // end insert subscription

        // insert settings
        ContentValues cv88 = new ContentValues();
        cv88.put("setting_key", "notification_alert");
        cv88.put("setting_value", 1);
        db.insert(TableSettings.TABLE_NAME, null, cv88);

        ContentValues cv888 = new ContentValues();
        cv888.put("setting_key", "max_password_reset_request");
        cv888.put("setting_value", 4);
        db.insert(TableSettings.TABLE_NAME, null, cv888);

        //String[] s = {"workexp", "education", "profile", "jobseek", "jobpref", "attachment", "language"};
        //for (String value : s) {
        //    ContentValues cv8 = new ContentValues();
        //    cv8.put("setting_key", "completeness_" + value);
        //    cv8.put("setting_value", false);
        //    db.insert(TableSettings.TABLE_NAME, null, cv8);
        //}
        // end insert settings

        ArrayList<String[]> countries = TableCountry.initialise();
        for( int i=0;i< countries.size(); i++ ){
            ContentValues cv8 = new ContentValues();
            cv8.put("id", countries.get(i)[0]);
            cv8.put("name", countries.get(i)[1]);
            cv8.put("dial_code", countries.get(i)[2]);
            db.insert(TableCountry.TABLE_NAME, null, cv8);
        }

        // job preference
        ContentValues cv = new ContentValues();
        cv.put("salary", 0);
        cv.put("currency_id", 6);
        cv.put("job_type_id", "");
        db.insert(TableJobPreference.TABLE_NAME, null, cv);

        ContentValues cv9 = new ContentValues();
        cv9.put("state_id", "");
        cv9.put("country_id", "");
        db.insert(TableJobPreferenceLocation.TABLE_NAME, null, cv9);

        // job spec and roles
        //String[] url = {Jenjobs.JOB_SPEC_URL};
        //new DownloadDataTask(1).execute(url);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TableProfile.SQL_DELETE_ENTRIES);
        db.execSQL(TableApplication.SQL_DELETE_ENTRIES);
        db.execSQL(TableBookmark.SQL_DELETE_ENTRIES);
        db.execSQL(TableEducation.SQL_DELETE_ENTRIES);
        db.execSQL(TableJob.SQL_DELETE_ENTRIES);
        db.execSQL(TableJobPreference.SQL_DELETE_ENTRIES);
        db.execSQL(TableJobPreferenceLocation.SQL_DELETE_ENTRIES);
        db.execSQL(TableJobRole.SQL_DELETE_ENTRIES);
        db.execSQL(TableJobSpec.SQL_DELETE_ENTRIES);
        db.execSQL(TableLanguage.SQL_DELETE_ENTRIES);
        db.execSQL(TableSettings.SQL_DELETE_ENTRIES);
        db.execSQL(TableSkill.SQL_DELETE_ENTRIES);
        db.execSQL(TableSubscription.SQL_DELETE_ENTRIES);
        db.execSQL(TableWorkExperience.SQL_DELETE_ENTRIES);
        db.execSQL(TableCountry.SQL_DELETE_ENTRIES);
        db.execSQL(TableAddress.SQL_DELETE_ENTRIES);
        db.execSQL(TableInvitation.SQL_DELETE_ENTRIES);
        db.execSQL(TableForgotPassword.SQL_DELETE_ENTRIES);
        db.execSQL(TableJobSearchProfile.SQL_DELETE_ENTRIES);
        db.execSQL(TableJobSearchMatched.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void truncate() {
        db.execSQL(TableProfile.SQL_DELETE_ENTRIES);
        db.execSQL(TableApplication.SQL_DELETE_ENTRIES);
        db.execSQL(TableBookmark.SQL_DELETE_ENTRIES);
        db.execSQL(TableEducation.SQL_DELETE_ENTRIES);
        db.execSQL(TableJob.SQL_DELETE_ENTRIES);
        db.execSQL(TableJobPreference.SQL_DELETE_ENTRIES);
        db.execSQL(TableJobPreferenceLocation.SQL_DELETE_ENTRIES);
        db.execSQL(TableJobRole.SQL_DELETE_ENTRIES);
        db.execSQL(TableJobSpec.SQL_DELETE_ENTRIES);
        db.execSQL(TableLanguage.SQL_DELETE_ENTRIES);
        db.execSQL(TableSettings.SQL_DELETE_ENTRIES);
        db.execSQL(TableSkill.SQL_DELETE_ENTRIES);
        db.execSQL(TableSubscription.SQL_DELETE_ENTRIES);
        db.execSQL(TableWorkExperience.SQL_DELETE_ENTRIES);
        db.execSQL(TableCountry.SQL_DELETE_ENTRIES);
        db.execSQL(TableAddress.SQL_DELETE_ENTRIES);
        db.execSQL(TableInvitation.SQL_DELETE_ENTRIES);
        db.execSQL(TableForgotPassword.SQL_DELETE_ENTRIES);
        db.execSQL(TableJobSearchProfile.SQL_DELETE_ENTRIES);
        db.execSQL(TableJobSearchMatched.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public Profile getProfile(){
        Profile profile = new Profile();

        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME, null);
        c.moveToFirst();
        while( !c.isAfterLast() ){
            String name = c.getString(4);
            String email = c.getString(2);

            //Log.e("name", name);
            //Log.e("email", email);

            profile.email = email;
            profile.username = c.getString(3);
            profile.name = name;
            profile._id = c.getInt(1);
            profile.ic = c.getString(5);
            profile.passport = c.getString(6);
            profile.dial_code = c.getString(7);
            profile.mobile_no = c.getString(8);
            profile.gender = c.getString(9); //Male, Female
            profile.dob = c.getString(10);
            profile.pr = c.getInt(11) > 0;
            profile.resume_file = c.getString(12);
            profile.photo_file = c.getString(c.getColumnIndex("photo_file"));
            profile.photo_uri = c.getString(c.getColumnIndex("photo_uri"));
            profile.access = c.getString(14); //Open, Limited, Hidden
            //profile.access_token = c.getString(15);
            profile.status = c.getString(16);
            profile.country_id = c.getInt(17);
            profile.driving_license = c.getInt(18) > 0;
            profile.transport = c.getInt(19) > 0;
            profile.js_jobseek_status_id = c.getInt(20);
            profile.availability = c.getInt(21);
            profile.availability_unit = c.getString(22);
            profile.no_work_exp = c.getInt(23) > 0;
            profile.additional_info = c.getString(24);
            profile.created_at = c.getString(25);
            profile.updated_at = c.getString(26);

            c.moveToNext();
        }
        c.close();
        return profile;
    }

    public Long addProfile(ContentValues cv){
        return db.insert(TABLE_NAME, null, cv);
    }

    public boolean updateProfile(ContentValues cv, int existingID){
        String[] _id = {String.valueOf(existingID)};
        int affectedRows = db.update(TABLE_NAME, cv, "_id=?", _id);
        return affectedRows > 0;
    }

    public ArrayList<String> isProfileComplete(){
        ArrayList<String> errors = new ArrayList<>();
        TableWorkExperience tableWorkExperience = new TableWorkExperience(context);
        TableEducation tableEducation = new TableEducation(context);
        TableSkill tableSkill = new TableSkill(context);
        TableLanguage tableLanguage = new TableLanguage(context);

        Profile profile = getProfile();

        // if got resume file, send application without checking anything
        if( profile.resume_file != null && profile.resume_file.length() > 0 ){
            return errors;
        }else{
            if( profile.country_id == 0 ){
                errors.add("Please set your nationality!");
            }

            if( profile.dob == null ){
                errors.add("Please set your date of birth!");
            }

            if( profile.gender == null ){
                errors.add("Please set gender!");
            }

            if( profile.mobile_no == null ){
                errors.add("Please set mobile number!");
            }

            if( profile.access == null ){
                errors.add("Please update your resume visibility!");
            }

            if( profile.js_jobseek_status_id == 0 ){
                errors.add("Please update your jobseeking infomation!");
            }

            // work exp
            if( !profile.no_work_exp ){ // if no_work_exp == false // got work experience
                // check for entered work exp
                Cursor works = tableWorkExperience.getWorkExperience();
                if( works.getCount() == 0 ){
                    errors.add("Please add your work experience!");
                }
                works.close();
            }

            // education
            Cursor edus = tableEducation.getEducation();
            if( edus.getCount() == 0 ){
                errors.add("Please add your qualification!");
            }
            edus.close();

            // skills
            Cursor skills = tableSkill.getSkill();
            if( skills.getCount() == 0 ){
                errors.add("Please add your skills!");
            }
            skills.close();

            // languages
            Cursor languages = tableLanguage.getLanguage(null);
            if( languages.getCount() == 0 ){
                errors.add("Please add your language proficiencies!");
            }
            languages.close();
        }

        return errors;
    }

}
