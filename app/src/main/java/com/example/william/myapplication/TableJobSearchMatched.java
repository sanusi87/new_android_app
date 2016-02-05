package com.example.william.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TableJobSearchMatched extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "job";
    public static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TableJob.TABLE_NAME+"' (id INTEGER, " +
            "title TEXT, " +
            "company TEXT, " +
            "job_data TEXT, " +
            "date_closed NUMERIC)";
    public static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TableJob.TABLE_NAME+"'";

    public SQLiteDatabase db;
    private Context context;

    public TableJobSearchMatched(Context context){
        super(context, Jenjobs.DATABASE_NAME , null, Jenjobs.DATABASE_VERSION);
        db = this.getReadableDatabase();
        this.context = context;
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
}
