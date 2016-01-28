package com.example.william.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TableJobSearchProfile extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "job_search_profile";

    public static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TABLE_NAME
            +"' (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            "profile_name TEXT, " +
            "parameters TEXT, " +
            "date_created NUMERIC," +
            "date_updated NUMERIC);";
    public static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TABLE_NAME+"'";

    public SQLiteDatabase db;

    public TableJobSearchProfile(Context context) {
        super(context, Jenjobs.DATABASE_NAME , null, Jenjobs.DATABASE_VERSION);
        db = this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public Cursor getSearchProfile(int id){
        String strSQL = "SELECT * FROM "+TABLE_NAME;
        String[] args = null;
        if( id > 0 ){
            strSQL += " WHERE id=?";
            args = new String[]{String.valueOf(id)};
        }
        return db.rawQuery(strSQL, args);
    }

    public int saveSearchProfile(ContentValues cv){
        if( cv.size() == 0 ){
            return 0;
        }

        int id = cv.getAsInteger("id");
        if( id > 0 ){
            db.update(TABLE_NAME, cv, "id=?", new String[]{String.valueOf(id)});
        }else{
            Long insertedId = db.insert(TABLE_NAME, null, cv);
            id = insertedId.intValue();
        }
        return id;
    }

    public boolean deleteSearchProfile(int id){
        String _id = String.valueOf(id);
        String[] param = {_id};
        int affectedRows = db.delete(TABLE_NAME, "id=?", param);
        return affectedRows > 0;
    }

}
