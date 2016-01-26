package com.example.william.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
* {
    "id": 5389402,
    "post_id": 320159,
    "_id": 320159,
    "status": 0,
    "date_created": "2015-03-16 13:25:54",
    "date_updated": "2015-03-16 13:25:54",
    "title": "PHP Guru",
    "closed": true
  },
* */

public class TableApplication extends SQLiteOpenHelper {
    public static final int STATUS_UNPROCESSED = 0;
    public static final int STATUS_SHORTLISTED = 1;
    public static final int STATUS_INTERVIEW = 2;
    public static final int STATUS_REJECTED = 4;
    public static final int STATUS_CLOSED = 5;
    public static final int STATUS_KIV = 6;
    public static final int STATUS_PRESCREENED = 9;
    public static final int STATUS_WITHDRAWN = 10;
    public static final int STATUS_HIRED = 11;

    public static final String TABLE_NAME = "application";
    public static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TableApplication.TABLE_NAME
            +"' (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            "_id INTEGER, " +
            "post_id INTEGER, " +
            "status INTEGER(3), " +
            "date_created NUMERIC," +
            "date_updated NUMERIC," +
            "title TEXT," +
            "closed INTEGER(1));";
    public static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TableApplication.TABLE_NAME+"'";

    public SQLiteDatabase db;
    private Context context;

    public TableApplication(Context context) {
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

    public Cursor getApplication(int post_id){
        String strSQL = "SELECT * FROM "+TableApplication.TABLE_NAME;
        String[] args = null;
        if( post_id > 0 ){
            strSQL += " WHERE post_id=?";
            args = new String[]{String.valueOf(post_id)};
        }
        return db.rawQuery(strSQL, args);
    }

    public Cursor getActiveApplication(){
        String strSQL = "SELECT * FROM "+TableApplication.TABLE_NAME+" WHERE (status != ? AND status != ?) AND closed=0";
        String[] args = {String.valueOf(STATUS_WITHDRAWN), String.valueOf(STATUS_HIRED)};
        return db.rawQuery(strSQL, args);
    }

    public boolean isDifferentApplicationStatus(int postId, int newStatus){
        String string = "SELECT status,post_id FROM "+TableApplication.TABLE_NAME+" WHERE post_id=?";
        String[] args = {String.valueOf(postId)};
        Cursor c = db.rawQuery(string, args);

        if( c.moveToFirst() ){
            int _status = c.getInt(0);
            c.close();
            if( _status != newStatus ){
                return true;
            }
        }
        return false;
    }

    public Long addApplication(ContentValues cv2){
        return db.insert(TableApplication.TABLE_NAME, null, cv2);
    }

    public boolean updateApplication(ContentValues cv2, int existingID){
        String[] _id = {String.valueOf(existingID)};
        int affectedRows = db.update(TableApplication.TABLE_NAME, cv2, "post_id=?", _id);
        return affectedRows > 0;
    }

    public boolean deleteApplication(int id){
        String _id = String.valueOf(id);
        String[] param = {_id};
        int affectedRows = db.delete(TableApplication.TABLE_NAME, "post_id=?", param);
        return affectedRows > 0;
    }
}

