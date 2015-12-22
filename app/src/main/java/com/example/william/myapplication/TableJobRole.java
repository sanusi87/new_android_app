package com.example.william.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TableJobRole extends SQLiteOpenHelper{
    public static final String TABLE_NAME = "job_role";
    public static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TableJobRole.TABLE_NAME+"' (id INTEGER(4), role_name TEXT);";
    public static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TableJobRole.TABLE_NAME+"'";

    public SQLiteDatabase db;
    public TableJobRole(Context context){
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

    public JobRole findById( int id ){
        String[] _id = {String.valueOf(id)};
        Cursor c = db.rawQuery("SELECT * FROM "+TableJobRole.TABLE_NAME+" WHERE id=?", _id);
        if( c.moveToFirst() ){
            return new JobRole(c.getInt(0), c.getString(1));
        }
        return null;
    }

}
