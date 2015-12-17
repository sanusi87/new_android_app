package com.example.william.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TableSettings  extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "settings";

    public static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TableSettings.TABLE_NAME
            +"' (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            "setting_key TEXT, " +
            "setting_value TEXT, " +
            "updated_at NUMERIC);";
    public static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TableSettings.TABLE_NAME+"'";

    public SQLiteDatabase db;

    public TableSettings(Context context) {
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

    public boolean addSetting(String key, String value){
        ContentValues cv = new ContentValues();
        cv.put("setting_key", key);
        cv.put("setting_value", value);
        Long newId = db.insert(TableSettings.TABLE_NAME, null, cv);
        if( newId != null ){
            return true;
        }
        return false;
    }

    public void updateSettings(String key, String value){
        ContentValues cv = new ContentValues();
        cv.put("setting_key", key);
        cv.put("setting_value", value);
        cv.put("updated_at", Jenjobs.date(null, null));
        db.update(TableSettings.TABLE_NAME, cv, "setting_key=?", new String[]{key});
    }
}
