package com.example.william.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TableJobRole extends SQLiteOpenHelper{
    public static final String TABLE_NAME = "job_role";
    private static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TableJobRole.TABLE_NAME+"' (id INTEGER(4), role_name TEXT);";
    private static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TableJobRole.TABLE_NAME+"'";

    public SQLiteDatabase db;
    public TableJobRole(Context context){
        super(context, Jenjobs.DATABASE_NAME , null, 1);
        db = this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);

        // add job specs, and roles
        insertRecord();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    private void insertRecord() {
        // insert spec

        //insert role

    }
}
