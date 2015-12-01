package com.example.william.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TableLanguage extends SQLiteOpenHelper{
    public static final String TABLE_NAME = "language";

    private static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TableLanguage.TABLE_NAME+"' (" +
            "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            "language_id INTEGER, "+
            "spoken_language_level_id INTEGER, "+
            "written_language_level_id INTEGER, "+
            "native INTEGER(1));";
    private static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TableLanguage.TABLE_NAME+"'";

    public SQLiteDatabase db;
    public TableLanguage(Context context){
        super(context, Jenjobs.DATABASE_NAME , null, 1);
        db = this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public Cursor getLanguage(){
        return db.rawQuery("SELECT * FROM "+TableLanguage.TABLE_NAME, null);
    }

    public Long addLanguage(ContentValues cv2){
        return db.insert(TableLanguage.TABLE_NAME, null, cv2);
    }

    public boolean updateLanguage(ContentValues cv2, int existingID){
        String[] _id = {String.valueOf(existingID)};
        int affectedRows = db.update(TableLanguage.TABLE_NAME, cv2, "id=?", _id);
        if( affectedRows > 0 ){
            return true;
        }
        return false;
    }

    public boolean deleteLanguage(int id){
        String _id = String.valueOf(id);
        String[] param = {_id};
        int affectedRows = db.delete(TableLanguage.TABLE_NAME, "id=?", param);
        if( affectedRows > 0 ){
            return true;
        }
        return false;
    }
}
