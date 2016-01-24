package com.example.william.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TableInvitation extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "invitation";

    public static int STATUS_ALLOWED = 1;
    public static int STATUS_REJECTED = 2;
    public static int STATUS_APPLIED = 1;
    public static int STATUS_NOT_INTERESTED = 2;

    public static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TABLE_NAME+"' (id INTEGER, " +
            "post_id INTEGER, " +
            "post_title TEXT, " +
            "emp_profile_id INTEGER, " +
            "emp_profile_name TEXT, " +
            "status INTEGER, " + //1=
            "date_added NUMERIC, " +
            "date_updated NUMERIC);";
    public static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TABLE_NAME+"'";

    public SQLiteDatabase db;
    private Context context;

    public TableInvitation(Context context){
        super(context, Jenjobs.DATABASE_NAME , null, Jenjobs.DATABASE_VERSION);
        db = this.getReadableDatabase();
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public Cursor getInvitation( int post_id ){
        String strSQL = "SELECT * FROM "+TABLE_NAME;
        String[] args = null;
        if( post_id > 0 ){
            strSQL += " WHERE post_id=?";
            args = new String[]{String.valueOf(post_id)};
        }
        strSQL += " ORDER BY id DESC";
        return db.rawQuery(strSQL, args);
    }

    public void saveInvitation( ContentValues cv2, int existingID ){
        String strSQL = "SELECT * FROM "+TABLE_NAME+" WHERE id=?";
        String[] param = {String.valueOf(existingID)};
        Cursor c = db.rawQuery(strSQL, param);

        if( c.getCount() > 0 ){
            db.update(TABLE_NAME, cv2, "id=?", param);
        }else{
            db.insert(TABLE_NAME, null, cv2);
        }
    }

    public boolean deleteInvitation( int existingID ){
        String _id = String.valueOf(existingID);
        String[] param = {_id};
        int affectedRows = db.delete(TABLE_NAME, "id=?", param);
        return affectedRows > 0;
    }

    public boolean updateInvitation(ContentValues cv, int invitationID) {
        int affectedRows = db.update(TABLE_NAME, cv, "id=?", new String[]{String.valueOf(invitationID)});
        return affectedRows > 0;
    }
}
