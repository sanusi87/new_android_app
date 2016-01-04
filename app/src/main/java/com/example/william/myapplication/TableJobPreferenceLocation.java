package com.example.william.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TableJobPreferenceLocation extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "job_preference_location";
    public static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TableJobPreferenceLocation.TABLE_NAME+"' (city_id NUMERIC, " +
            "city_name TEXT, " +
            "state_id NUMERIC, " +
            "state_name TEXT, " +
            "country_id NUMERIC, " +
            "country_name TEXT, " +
            "keyword TEXT, " +
            "date_updated NUMERIC)";
    public static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TableJobPreferenceLocation.TABLE_NAME+"'";

    public SQLiteDatabase db;

    public TableJobPreferenceLocation(Context context){
        super(context, Jenjobs.DATABASE_NAME , null, Jenjobs.DATABASE_VERSION);
        db = this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean updateJobPreference(ContentValues cv){
        int affectedRows = db.update(TableJobPreferenceLocation.TABLE_NAME, cv, null, null);
        if( affectedRows > 0 ){
            return true;
        }
        return false;
    }

    public Cursor getJobPreference() {
        Cursor c = db.rawQuery("SELECT * FROM "+TableJobPreferenceLocation.TABLE_NAME, null);
        return c;
    }
}
