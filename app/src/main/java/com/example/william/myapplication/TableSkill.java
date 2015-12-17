package com.example.william.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TableSkill extends SQLiteOpenHelper{
    public static final String TABLE_NAME = "skill";

    public static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TableSkill.TABLE_NAME+"' (" +
            "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            "_id INTEGER, "+
            "name TEXT);";
    public static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TableSkill.TABLE_NAME+"'";

    public SQLiteDatabase db;
    public TableSkill(Context context){
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

    public Cursor getSkill(){
        return db.rawQuery("SELECT * FROM "+TableSkill.TABLE_NAME, null);
    }

    public Long addSkill(ContentValues cv2){
        Long newID = db.insert(TableSkill.TABLE_NAME, null, cv2);
        return newID;
    }

    public boolean updateSkill(ContentValues cv2, int existingID){
        String[] _id = {String.valueOf(existingID)};
        int affectedRows = db.update(TableSkill.TABLE_NAME, cv2, "id=?", _id);
        if( affectedRows > 0 ){
            return true;
        }
        return false;
    }

    public boolean deleteSkill(int id){
        String _id = String.valueOf(id);
        String[] param = {_id};
        int affectedRows = db.delete(TableSkill.TABLE_NAME, "id=?", param);
        if( affectedRows > 0 ){
            return true;
        }
        return false;
    }
}
