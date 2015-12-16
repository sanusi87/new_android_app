package com.example.william.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;

public class TableProfile extends SQLiteOpenHelper{

    public static final String TABLE_NAME = "profile";

    /*
{
  "id": 1000001,
  "email": "",
  "username": "sanusi@my.jenjobs.com",
  "name": "Mohd Sanusi Yaakub",
  "ic_no": "870101295389",
  "passport_no": null,
  "mobile_no": "(+60)132621641",
  "gender": "Male",
  "dob": "1987-01-01",
  "pr": 0,
  "resume_file": "http://files.jenjobs.com/jj2upload/jobseeker/resume/1000001/resume_1444276053.docx",
  "photo_file": "",
  "access": "Open",
  "status": "Active",
  "country_id": 127,
  "country": "Malaysia",
  "driving_license": 0,
  "transport": 1,
  "js_jobseek_status_id": 4,
  "availability": 1,
  "availability_unit": "W",
  "address": {
    "country_id": 127,
    "state_id": 60,
    "city_id": 0,
    "address1": null,
    "address2": null,
    "postcode": null,
    "city_name": null,
    "state_name": null,
    "date_updated": "2015-10-08 13:40:27"
  },
  "created_at": "2015-09-28 14:02:24",
  "info": "xxx yyy",
  "no_work_exp": 0,
  "updated_at": "2015-10-21 11:39:25",
  "_links": {
    "self": {
      "href": "http://api.jenjobs.local/jobseekers/1000001"
    }
  }
}
    */
    private static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TableProfile.TABLE_NAME
            +"' (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            "_id INTEGER, " +
            "email TEXT, " +
            "username TEXT, " +
            "name TEXT, " +
            "ic_no TEXT, " +
            "passport_no TEXT, " +
            "mobile_no TEXT, " +
            "gender TEXT, " +
            "dob TEXT, " +
            "pr INTEGER(2), " +
            "resume_file TEXT, " +
            "photo_file TEXT, " +
            "access TEXT, " +
            "access_token TEXT, " +
            "status TEXT, " +
            "country_id INTEGER(4), " +
            "driving_license INTEGER(2), " +
            "transport INTEGER(2), " +
            "js_jobseek_status_id INTEGER(2), " +
            "availability INTEGER(2), " +
            "availability_unit TEXT, " +
            "address TEXT, " +
            "no_work_exp TEXT, " + //1=no work, 0=got work
            "additional_info TEXT, " +
            "created_at NUMERIC, " +
            "updated_at NUMERIC);";
    private static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TableProfile.TABLE_NAME+"'";

    public SQLiteDatabase db;

    public TableProfile(Context context){
        super(context, Jenjobs.DATABASE_NAME , null, 1);
        db = this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void truncate() {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public Profile getProfile(){
        Profile profile = new Profile();

        Cursor c = db.rawQuery("SELECT * FROM "+TableProfile.TABLE_NAME, null);
        Bundle extras = c.getExtras();


        if( extras.size() > 0 ){
            String name = extras.getString("name");
            String email = extras.getString("email");

            Log.e("name", name);
            Log.e("email", email);

            profile.email = email;
            profile.name = name;

            return profile;
        }

        return null;
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
