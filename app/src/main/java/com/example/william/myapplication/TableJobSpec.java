package com.example.william.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TableJobSpec extends SQLiteOpenHelper{
    public static final String TABLE_NAME = "job_spec";
    private static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TableJobSpec.TABLE_NAME+"' (id INTEGER(4), spec_name TEXT);";
    private static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TableJobSpec.TABLE_NAME+"'";

    public SQLiteDatabase db;
    public TableJobSpec(Context context){
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
}
