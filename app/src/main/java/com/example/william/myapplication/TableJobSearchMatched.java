package com.example.william.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TableJobSearchMatched extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "job_matched";
    public static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TABLE_NAME+"' (id INTEGER, " +
            "title TEXT, " +
            "company TEXT, " +
            "job_data TEXT, " +
            "date_closed NUMERIC)";
    public static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TABLE_NAME+"'";

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

    public Long addJob(ContentValues cv){
        return db.insert(TABLE_NAME, null, cv);
    }

    public Cursor getJob(){
        String strSQL = "SELECT * FROM "+TABLE_NAME;
        return db.rawQuery(strSQL, null);
    }

    public void truncateTable(){
        db.execSQL("DELETE FROM "+TABLE_NAME);
    }
}
