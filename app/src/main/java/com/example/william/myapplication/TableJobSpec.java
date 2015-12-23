package com.example.william.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TableJobSpec extends SQLiteOpenHelper{
    public static final String TABLE_NAME = "job_spec";
    public static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TableJobSpec.TABLE_NAME+"' (id INTEGER(4), spec_name TEXT);";
    public static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TableJobSpec.TABLE_NAME+"'";

    public SQLiteDatabase db;
    public TableJobSpec(Context context){
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

    public Cursor getAllJobSpec(){
        Cursor c = db.rawQuery("SELECT * FROM "+TableJobSpec.TABLE_NAME, null);
        return c;
    }

    public JobSpec findById( int id ){
        String[] _id = {String.valueOf(id)};
        Cursor c = db.rawQuery("SELECT * FROM "+TableJobSpec.TABLE_NAME+" WHERE id=?", _id);
        if( c.moveToFirst() ){
            JobSpec s = new JobSpec(c.getInt(0), c.getString(1));
            c.close();
            return s;
        }
        return null;
    }

}
