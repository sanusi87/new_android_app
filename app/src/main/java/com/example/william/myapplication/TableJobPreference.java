package com.example.william.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TableJobPreference extends SQLiteOpenHelper{
    public static final String TABLE_NAME = "job_preference";
    private static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TableJobPreference.TABLE_NAME+"' (salary INTEGER, " +
            "currency_id INTEGER, " +
            "state_id TEXT, " +
            "country_id TEXT, " +
            "job_type_id TEXT)";
    private static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TableJobPreference.TABLE_NAME+"'";

    public SQLiteDatabase db;

    public TableJobPreference(Context context){
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

    public boolean updateJobPreference(ContentValues cv){
        int affectedRows = db.update(TableJobPreference.TABLE_NAME, cv, null, null);
        if( affectedRows > 0 ){
            return true;
        }
        return false;
    }
}
