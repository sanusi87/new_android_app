package com.example.william.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class TableJobSearchMatched extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "job_matched";
    public static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TABLE_NAME+"' (id INTEGER, " +
            "jm_profile_id INTEGER, " +
            "title TEXT, " +
            "company TEXT, " +
            "job_data TEXT, " +
            "date_closed NUMERIC," +
            "date_added NUMERIC)";
    public static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TABLE_NAME+"'";

    public SQLiteDatabase db;

    public TableJobSearchMatched(Context context){
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

    public Long addJob(ContentValues cv){
        return db.insert(TABLE_NAME, null, cv);
    }

    public Cursor getJob( int jm_profile_id, String dateAdded ){
        String strSQL = "SELECT * FROM "+TABLE_NAME+" WHERE jm_profile_id=? AND date(date_added)=?";
        String[] args = {String.valueOf(jm_profile_id),dateAdded};
        return db.rawQuery(strSQL, args);
    }

    /*
    * has today jobmatcher been downloaded?
    * */
    public boolean alreadyDownloaded( int jm_profile_id ){
        String strSQL = "SELECT COUNT(*) FROM "+TABLE_NAME+" WHERE jm_profile_id=? AND date(date_added) = date('now')";
        String[] args = {String.valueOf(jm_profile_id)};
        Cursor c = db.rawQuery(strSQL, args);
        c.moveToFirst();
        int hasData = c.getInt(0);
        c.close();
        return hasData > 0;
    }

    /*
    * count the total number of matched jobs(max=20)
    * */
    public int countJobs( int jm_profile_id, String dateAdded ){
        String strSQL = "SELECT COUNT(*) FROM "+TABLE_NAME+" WHERE jm_profile_id=? AND date(date_added)=?";
        ArrayList<String> args = new ArrayList<>();
        args.add(String.valueOf(jm_profile_id));
        args.add(dateAdded);

        Cursor c = db.rawQuery(strSQL, args.toArray(new String[args.size()]));
        c.moveToFirst();
        int hasData = c.getInt(0);
        c.close();
        return hasData;
    }

    /*
    * empty this table
    * */
    public void truncateTable() {
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }

    /*
    * get a list of dates from job matcher
    * */
    public String[] getDateAdded(){
        String today = Jenjobs.date(null, "yyyy-MM-dd", null);
        String strSQL = "SELECT distinct date(date_added) FROM job_matched";
        Cursor c = db.rawQuery(strSQL, null);
        ArrayList<String> s = new ArrayList<>();
        if( c.moveToFirst() ){
            while( !c.isAfterLast() ){
                String theDate = c.getString(0);
                s.add(theDate);
                c.moveToNext();
            }
        }

        if( !s.contains( today ) ){
            s.add( today );
        }

        c.close();
        return s.toArray(new String[s.size()]);
    }

    /*
    * delete closed jobs
    * */
    public int deleteClosedJob(){
        return db.delete(TABLE_NAME, "date_closed < date('now')", null);
    }
}
