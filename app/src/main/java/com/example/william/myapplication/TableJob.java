package com.example.william.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TableJob extends SQLiteOpenHelper{
    public static final String TABLE_NAME = "job";
    public static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TableJob.TABLE_NAME+"' (id INTEGER, " +
            "title TEXT, " +
            "company TEXT, " +
            "job_data TEXT, " +
            "date_closed NUMERIC)";
    public static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TableJob.TABLE_NAME+"'";

    public SQLiteDatabase db;
    private Context context;

    public TableJob(Context context){
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

    public Cursor getJob(int post_id){
        String strSQL = "SELECT * FROM "+TableJob.TABLE_NAME;
        String[] args = null;
        if( post_id > 0 ){
            strSQL += " WHERE id=?";
            args = new String[]{String.valueOf(post_id)};
        }
        return db.rawQuery(strSQL, args);
    }

    public Long addJob(ContentValues cv){
        Cursor job = getJob(cv.getAsInteger("id"));
        Long insertId = (long) 0;
        if( job.getCount() == 0 ){
            insertId = db.insert(TableJob.TABLE_NAME, null, cv);
        }
        job.close();
        return insertId;
    }

    public boolean updateJob(ContentValues cv){
        int affectedRows = db.update(TableJob.TABLE_NAME, cv, null, null);
        return affectedRows > 0;
    }

    public boolean deleteJob( int post_id ){
        // check if this job is used in any other tables or not
        int used = 0;

        TableBookmark tableBookmark = new TableBookmark(context);
        TableApplication tableApplication = new TableApplication(context);
        TableInvitation tableInvitation = new TableInvitation(context);

        // check if used in bookmark
        Cursor bookmarks = tableBookmark.getBookmark(post_id);
        if( bookmarks.getCount() > 0 ){ used += 1; }

        // check if used in application
        Cursor applications = tableApplication.getApplication(post_id);
        if( applications.getCount() > 0 ){ used += 1; }

        // check if used in invitation
        Cursor invitations = tableInvitation.getInvitation(0, post_id);
        if( invitations.getCount() > 0 ){ used += 1; }

        // if not used, then deleted it
        if( used == 0 ){
            String _id = String.valueOf(post_id);
            String[] param = {_id};
            int affectedRows = db.delete(TableJob.TABLE_NAME, "id=?", param);
            return affectedRows > 0;
        }else{
            return false;
        }
    }

}
