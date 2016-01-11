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

    public TableApplication(Context context) {
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

    public Cursor getApplication(int post_id){
        String strSQL = "SELECT * FROM "+TableApplication.TABLE_NAME;
        String[] args = null;
        if( post_id > 0 ){
            strSQL += " WHERE post_id=?";
            args = new String[]{String.valueOf(post_id)};
        }
        return db.rawQuery(strSQL, args);
    }

    public Long addApplication(ContentValues cv2){
        return db.insert(TableApplication.TABLE_NAME, null, cv2);
    }

    public boolean updateApplication(ContentValues cv2, int existingID){
        String[] _id = {String.valueOf(existingID)};
        int affectedRows = db.update(TableApplication.TABLE_NAME, cv2, "id=?", _id);
        return affectedRows > 0;
    }

    public boolean deleteApplication(int id){
        String _id = String.valueOf(id);
        String[] param = {_id};
        int affectedRows = db.delete(TableApplication.TABLE_NAME, "id=?", param);
        return affectedRows > 0;
    }
}

