package jen.jobs.application;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TableSettings  extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "settings";

    public static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TABLE_NAME
            +"' (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            "setting_key TEXT, " +
            "setting_value TEXT, " +
            "updated_at NUMERIC);";
    public static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TABLE_NAME+"'";

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
        Long newId = db.insert(TABLE_NAME, null, cv);
        return newId.intValue() > 0;
    }

    public void updateSettings(String key, String value){
        ContentValues cv = new ContentValues();
        cv.put("setting_key", key);
        cv.put("setting_value", value);
        cv.put("updated_at", Jenjobs.date(null, null,null));
        db.update(TABLE_NAME, cv, "setting_key=?", new String[]{key});
    }

    public String getSetting(String key){
        String strSQL = "SELECT setting_value FROM "+TABLE_NAME+" WHERE setting_key=?";
        String[] args = {key};

        String value = null;
        Cursor c = db.rawQuery(strSQL, args);
        if( c.moveToFirst() ){
            value = c.getString(0);
            c.close();
        }
        return value;
    }
}
