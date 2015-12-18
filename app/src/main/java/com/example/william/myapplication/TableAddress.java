package com.example.william.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TableAddress extends SQLiteOpenHelper{

    public static final String TABLE_NAME = "settings";

    public static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TableAddress.TABLE_NAME
            +"' (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            "address1 TEXT, " +
            "address2 TEXT, " +
            "postcode NUMERIC, " +
            "city_id NUMERIC, " +
            "city_name TEXT, " +
            "state_id NUMERIC, " +
            "state_name TEXT, " +
            "country_id NUMERIC, " +
            "updated_at NUMERIC);";
    public static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TableAddress.TABLE_NAME+"'";

    public SQLiteDatabase db;

    public TableAddress(Context context) {
        super(context, Jenjobs.DATABASE_NAME , null, Jenjobs.DATABASE_VERSION);
        db = this.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Long addAddress(ContentValues cv){
        return db.insert(TableAddress.TABLE_NAME, null, cv);
    }

    public boolean updateAddress(ContentValues cv){
        int affectedRows = db.update(TableAddress.TABLE_NAME, cv, null, null);
        if( affectedRows > 0 ){
            return true;
        }
        return false;
    }

}
