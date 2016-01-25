package com.example.william.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TableSubscription extends SQLiteOpenHelper{
    public static final String TABLE_NAME = "subscription";

    public static final int SMS_JOB_ALERT = 1;
    public static final int NEWSLETTER = 2;
    public static final int PROMOTION = 3;

    /*
    {
        "subscription_name": "SMS Job Alert",
        "subscription_id": 3,
        "status": true
    }
    */
    public static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TableSubscription.TABLE_NAME
            +"' (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            "subscription_name TEXT, " +
            "subscription_id INTEGER(4), " +
            "status INTEGER(1)," +
            "date_updated NUMERIC);";
    public static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TableSubscription.TABLE_NAME+"'";

    public SQLiteDatabase db;

    public TableSubscription(Context context){
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

    public Cursor getSubscription(){
        return db.rawQuery("SELECT * FROM "+TableSubscription.TABLE_NAME, null);
    }

    public boolean updateSubscription(ContentValues cv2, int existingID){
        String[] _id = {String.valueOf(existingID)};
        int affectedRows = db.update(TableSubscription.TABLE_NAME, cv2, "subscription_id=?", _id);
        if( affectedRows > 0 ){
            return true;
        }
        return false;
    }


}
