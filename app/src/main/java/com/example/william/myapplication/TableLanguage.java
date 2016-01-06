package com.example.william.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TableLanguage extends SQLiteOpenHelper{
    public static final String TABLE_NAME = "language";

    public static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TableLanguage.TABLE_NAME+"' (" +
            "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + //0
            "language_id INTEGER, "+ //1
            "spoken_language_level_id INTEGER, "+ //2
            "written_language_level_id INTEGER, "+ //3
            "native INTEGER(1));"; //4 0/1
    public static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TableLanguage.TABLE_NAME+"'";

    public SQLiteDatabase db;
    public TableLanguage(Context context){
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

    public Cursor getLanguage(String[] args){
        String strSQL = "SELECT * FROM "+TableLanguage.TABLE_NAME;
        if( args != null && args.length > 0 ){
            strSQL += " WHERE language_id=?";
        }
        return db.rawQuery(strSQL,args);
    }

    public Long addLanguage(ContentValues cv2){
        return db.insert(TableLanguage.TABLE_NAME, null, cv2);
    }

    public boolean updateLanguage(ContentValues cv2, int existingID){
        String[] _id = {String.valueOf(existingID)};
        int affectedRows = db.update(TableLanguage.TABLE_NAME, cv2, "id=?", _id);
        return affectedRows > 0;
    }

    public boolean deleteLanguage(int id){
        String _id = String.valueOf(id);
        String[] param = {_id};
        int affectedRows = db.delete(TableLanguage.TABLE_NAME, "id=?", param);
        return affectedRows > 0;
    }
}
