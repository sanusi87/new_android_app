package jen.jobs.application;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TableAddress extends SQLiteOpenHelper{

    public static final String TABLE_NAME = "address";

    public static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TABLE_NAME
            +"' (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " + //0
            "address1 TEXT, " + //1
            "address2 TEXT, " + //2
            "postcode NUMERIC, " + //3
            "city_id NUMERIC, " + //4
            "city_name TEXT, " + //5
            "state_id NUMERIC, " + //6
            "state_name TEXT, " + //7
            "country_id NUMERIC, " + //8
            "updated_at NUMERIC);"; //9
    public static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TABLE_NAME+"'";
    public static String SQL_EMPTY_TABLE = "DELETE FROM '"+TABLE_NAME+"'";

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
        return db.insert(TABLE_NAME, null, cv);
    }

    public boolean updateAddress(ContentValues cv){
        int affectedRows = db.update(TABLE_NAME, cv, null, null);
        return affectedRows > 0;
    }

    public Cursor getAddress() {
        return db.rawQuery("SELECT * FROM "+TABLE_NAME+" JOIN "+TableCountry.TABLE_NAME
                +" ON "+TABLE_NAME+".country_id = "+TableCountry.TABLE_NAME+".id", null);
    }
}
