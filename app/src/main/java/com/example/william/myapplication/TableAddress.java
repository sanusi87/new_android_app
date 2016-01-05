package com.example.william.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TableAddress extends SQLiteOpenHelper{

    public static final String TABLE_NAME = "address";

    public static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TableAddress.TABLE_NAME
            +"' (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + //1
            "address1 TEXT, " + //2
            "address2 TEXT, " + //3
            "postcode NUMERIC, " + //4
            "city_id NUMERIC, " + //5
            "city_name TEXT, " + //6
            "state_id NUMERIC, " + //7
            "state_name TEXT, " + //8
            "country_id NUMERIC, " + //9
            "updated_at NUMERIC);"; //10
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
        return affectedRows > 0;
    }

    public Cursor getAddress() {
        return db.rawQuery("SELECT * FROM "+TableAddress.TABLE_NAME+" JOIN "+TableCountry.TABLE_NAME
                +" ON "+TableAddress.TABLE_NAME+".country_id = "+TableCountry.TABLE_NAME+".id", null);
    }
}
