package com.example.william.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TableJobPreference extends SQLiteOpenHelper{
    public static final String TABLE_NAME = "job_preference";
    public static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TableJobPreference.TABLE_NAME+"' (salary INTEGER, " +
            "currency_id INTEGER, " +
            "job_type_id TEXT, " + // saved as json string
            "date_updated NUMERIC)";
    public static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TableJobPreference.TABLE_NAME+"'";

    public SQLiteDatabase db;

    public TableJobPreference(Context context){
        super(context, Jenjobs.DATABASE_NAME , null, Jenjobs.DATABASE_VERSION);
        db = this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL(SQL_DELETE_ENTRIES);
        //onCreate(db);
    }

    public boolean updateJobPreference(ContentValues cv){
        int affectedRows = db.update(TableJobPreference.TABLE_NAME, cv, null, null);
        if( affectedRows > 0 ){
            return true;
        }
        return false;
    }

    public Cursor getJobPreference() {
        Cursor c = db.rawQuery("SELECT * FROM "+TableJobPreference.TABLE_NAME, null);
        return c;
    }
}
