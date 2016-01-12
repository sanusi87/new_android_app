package com.example.william.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TableInvitation extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "invitation";
    public static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TableInvitation.TABLE_NAME
            +"' (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            "post_id INTEGER, " +
            "date_added NUMERIC);";
    public static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TableInvitation.TABLE_NAME+"'";

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
        String strSQL = "SELECT * FROM "+TableInvitation.TABLE_NAME;
        String[] args = null;
        if( post_id > 0 ){
            strSQL += " WHERE post_id=?";
            args = new String[]{String.valueOf(post_id)};
        }
        return db.rawQuery(strSQL, args);
    }
}
