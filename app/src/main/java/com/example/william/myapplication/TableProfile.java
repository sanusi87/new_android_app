package com.example.william.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class TableProfile extends SQLiteOpenHelper{

    public static final String TABLE_NAME = "profile";
	
    public static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TableProfile.TABLE_NAME+"' (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + //0
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
            "availability INTEGER(2), " + //21
            "availability_unit TEXT, " + //22
            //"address TEXT, " + //23
            "no_work_exp TEXT, " + //1=no work, 0=got work //24
            "additional_info TEXT, " + //25
            "created_at NUMERIC, " + //26
            "updated_at NUMERIC);"; //27
    public static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TableProfile.TABLE_NAME+"'";

    public SQLiteDatabase db;

    public TableProfile(Context context){
        super(context, Jenjobs.DATABASE_NAME , null, Jenjobs.DATABASE_VERSION);
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

        // insert subscription
        ContentValues cv1 = new ContentValues();
        ContentValues cv2 = new ContentValues();
        ContentValues cv3 = new ContentValues();
        cv1.put("subscription_name", "Emails of newsletter, promotion, career advise from JenJOBS");
        cv1.put("subscription_id", "1");
        cv1.put("status", TableSubscription.NEWSLETTER);
        cv2.put("subscription_name", "Promotion emails from JenJOBS partners and affiliates");
        cv2.put("subscription_id", "2");
        cv2.put("status", TableSubscription.PROMOTION);
        cv3.put("subscription_name", "SMS Job Alert");
        cv3.put("subscription_id", "3");
        cv3.put("status", TableSubscription.SMS_JOB_ALERT);
        db.insert(TableSubscription.TABLE_NAME, null, cv1);
        db.insert(TableSubscription.TABLE_NAME, null, cv2);
        db.insert(TableSubscription.TABLE_NAME, null, cv3);
        // end insert subscription

        // insert settings
        String[] ss = {"notification_alert"};
        for( int i=0;i<ss.length;i++ ){
            ContentValues cv8 = new ContentValues();
            cv8.put("setting_key", ss[i]);
            cv8.put("setting_value", 1);
            db.insert(TableSettings.TABLE_NAME, null, cv8);
        }

        String[] s = {"workexp", "education", "profile", "jobseek", "jobpref", "attachment", "language"};
        for( int i=0;i<s.length;i++ ){
            ContentValues cv8 = new ContentValues();
            cv8.put("setting_key", "completeness_"+s[i]);
            cv8.put("setting_value", false);
            db.insert(TableSettings.TABLE_NAME, null, cv8);
        }
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
        cv.put("state_id", "");
        cv.put("country_id", "");
        cv.put("job_type_id", "");
        db.insert(TableJobPreference.TABLE_NAME, null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TableProfile.SQL_DELETE_ENTRIES);
        db.execSQL(TableApplication.SQL_DELETE_ENTRIES);
        db.execSQL(TableBookmark.SQL_DELETE_ENTRIES);
        db.execSQL(TableEducation.SQL_DELETE_ENTRIES);
        db.execSQL(TableJob.SQL_DELETE_ENTRIES);
        db.execSQL(TableJobPreference.SQL_DELETE_ENTRIES);
        db.execSQL(TableJobRole.SQL_DELETE_ENTRIES);
        db.execSQL(TableJobSpec.SQL_DELETE_ENTRIES);
        db.execSQL(TableLanguage.SQL_DELETE_ENTRIES);
        db.execSQL(TableSettings.SQL_DELETE_ENTRIES);
        db.execSQL(TableSkill.SQL_DELETE_ENTRIES);
        db.execSQL(TableSubscription.SQL_DELETE_ENTRIES);
        db.execSQL(TableWorkExperience.SQL_DELETE_ENTRIES);
        db.execSQL(TableCountry.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void truncate() {
        db.execSQL(TableProfile.SQL_DELETE_ENTRIES);
        db.execSQL(TableApplication.SQL_DELETE_ENTRIES);
        db.execSQL(TableBookmark.SQL_DELETE_ENTRIES);
        db.execSQL(TableEducation.SQL_DELETE_ENTRIES);
        db.execSQL(TableJob.SQL_DELETE_ENTRIES);
        db.execSQL(TableJobPreference.SQL_DELETE_ENTRIES);
        db.execSQL(TableJobRole.SQL_DELETE_ENTRIES);
        db.execSQL(TableJobSpec.SQL_DELETE_ENTRIES);
        db.execSQL(TableLanguage.SQL_DELETE_ENTRIES);
        db.execSQL(TableSettings.SQL_DELETE_ENTRIES);
        db.execSQL(TableSkill.SQL_DELETE_ENTRIES);
        db.execSQL(TableSubscription.SQL_DELETE_ENTRIES);
        db.execSQL(TableWorkExperience.SQL_DELETE_ENTRIES);
        db.execSQL(TableCountry.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public Profile getProfile(){
        Profile profile = new Profile();

        Cursor c = db.rawQuery("SELECT * FROM "+TableProfile.TABLE_NAME, null);
        c.moveToFirst();
        while( !c.isAfterLast() ){
            String name = c.getString(4);
            String email = c.getString(2);

            Log.e("name", name);
            Log.e("email", email);

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
            profile.photo_file = c.getString(13);
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
        return profile;
    }

    public Long addProfile(ContentValues cv){
        return db.insert(TableProfile.TABLE_NAME, null, cv);
    }

    public boolean updateProfile(ContentValues cv, int existingID){
        String[] _id = {String.valueOf(existingID)};
        int affectedRows = db.update(TableProfile.TABLE_NAME, cv, "id=?", _id);
        if( affectedRows > 0 ){
            return true;
        }
        return false;
    }

    public boolean deleteProfile(int id){
        String _id = String.valueOf(id);
        String[] param = {_id};
        int affectedRows = db.delete(TableProfile.TABLE_NAME, "id=?", param);
        if( affectedRows > 0 ){
            return true;
        }
        return false;
    }


}
