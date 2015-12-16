package com.example.william.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TableEducation extends SQLiteOpenHelper{
    public static final String TABLE_NAME = "education";

    /*
    "id": 778017,
    "school": "aaaa",
    "major": "bbbb",
    "edu_level_id": 2,
    "edu_field_id": 2,
    "country_id": 0,
    "grade": "cccc",
    "date_graduated": "0000-00-00",
    "info": "dddd"
    * */
    private static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TableEducation.TABLE_NAME
            +"' (" +
            "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            "_id INTEGER, "+
            "school TEXT, " +
            "major TEXT, " +
            "edu_level_id INTEGER(4), " +
            "edu_field_id INTEGER(4), " +
            "country_id INTEGER(4), " +
            "grade TEXT, " +
            "info TEXT, " +
            "date_graduated NUMERIC);";
    private static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TableEducation.TABLE_NAME+"'";

    public SQLiteDatabase db;

    public TableEducation(Context context){
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

    public void truncate() {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public Cursor getEducation(){
        return db.rawQuery("SELECT * FROM "+TableEducation.TABLE_NAME, null);
    }

    public Long addEducation(ContentValues cv2){
        Long newID = db.insert(TableEducation.TABLE_NAME, null, cv2);
        return newID;
    }

    public boolean updateEducation(ContentValues cv2, int existingID){
        String[] _id = {String.valueOf(existingID)};
        int affectedRows = db.update(TableEducation.TABLE_NAME, cv2, "id=?", _id);
        if( affectedRows > 0 ){
            return true;
        }
        return false;
    }

    public boolean deleteEducation(int id){
        String _id = String.valueOf(id);
        String[] param = {_id};
        int affectedRows = db.delete(TableEducation.TABLE_NAME, "id=?", param);
        if( affectedRows > 0 ){
            return true;
        }
        return false;
    }
}
