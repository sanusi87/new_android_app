package com.example.william.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TableBookmark extends SQLiteOpenHelper{

    public static final String TABLE_NAME = "bookmark";

    /*
    {
        "post_id": 320246,
        "on": "2015-10-20 09:14:17",
        "date_closed": "2016-01-27 13:57:42",
        "title": "Artisan"
    }
    */
    public static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TableBookmark.TABLE_NAME
            +"' (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            "title TEXT, " +
            "post_id INTEGER, " +
            "date_added NUMERIC," +
            "date_closed NUMERIC);";
    public static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TableBookmark.TABLE_NAME+"'";

    public SQLiteDatabase db;
    private Context context;

    public TableBookmark(Context context){
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

    /*
    public Cursor getBookmark(){}
    public Long addBookmark(){}
    public int updateBookmark(){}
    public int deleteBookmark(){}
    */

    public Cursor getBookmark(int post_id){
        String strSQL = "SELECT * FROM "+TableBookmark.TABLE_NAME;
        String[] args = null;
        if( post_id > 0 ){
            strSQL += " WHERE post_id=?";
            args = new String[]{String.valueOf(post_id)};
        }
        return db.rawQuery(strSQL, args);
    }

    public Long addBookmark(ContentValues cv){
        return db.insert(TableBookmark.TABLE_NAME, null, cv);
    }

    public boolean updateBookmark(ContentValues cv, int existingID){
        String[] _id = {String.valueOf(existingID)};
        int affectedRows = db.update(TableBookmark.TABLE_NAME, cv, "id=?", _id);
        if( affectedRows > 0 ){
            return true;
        }
        return false;
    }

    public boolean deleteBookmark(int id, int post_id){
        String _id = String.valueOf(id);
        String[] param = {_id};

        // delete bookmark
        int affectedRows = db.delete(TableBookmark.TABLE_NAME, "id=?", param);
        if( affectedRows > 0 ){
            // and delete job
            TableJob tableJob = new TableJob(context);
            tableJob.deleteJob(post_id);

            return true;
        }
        return false;
    }
}
