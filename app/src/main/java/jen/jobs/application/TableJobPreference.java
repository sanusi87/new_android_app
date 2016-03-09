package jen.jobs.application;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TableJobPreference extends SQLiteOpenHelper{
    public static final String TABLE_NAME = "job_preference";
    public static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TABLE_NAME+"' (salary INTEGER, " +
            "currency_id INTEGER, " +
            "job_type_id TEXT, " + // saved as json string
            "date_updated NUMERIC)";
    public static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TABLE_NAME+"'";
    public static String SQL_EMPTY_TABLE = "DELETE FROM '"+TABLE_NAME+"'";

    public SQLiteDatabase db;

    public TableJobPreference(Context context){
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

    public boolean updateJobPreference(ContentValues cv){
        int affectedRows = db.update(TABLE_NAME, cv, null, null);
        return affectedRows > 0;
    }

    public Cursor getJobPreference() {
        return db.rawQuery("SELECT * FROM "+TABLE_NAME, null);
    }
}
