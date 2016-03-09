package jen.jobs.application;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TableForgotPassword extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "forgot_password";
    public static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TABLE_NAME
            +"' (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, date_added NUMERIC);";
    public static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TABLE_NAME+"'";

    public SQLiteDatabase db;
    private Context context;

    public TableForgotPassword(Context context) {
        super(context, Jenjobs.DATABASE_NAME , null, Jenjobs.DATABASE_VERSION);
        db = this.getReadableDatabase();
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public void logRequest(){
        ContentValues cv = new ContentValues();
        cv.put("date_added", Jenjobs.date(null ,"yyyy-MM-dd hh:mm:ss", null));
        db.insert(TABLE_NAME, null, cv);
    }

    public int countTodayRequest(){
        String strSQL = "SELECT COUNT(*) FROM "+TABLE_NAME+" WHERE date(date_added)=?";
        String[] args = {Jenjobs.date(null,"yyyy-MM-dd",null)};

        Cursor c = db.rawQuery(strSQL, args);

        int totalItem = 0;
        if( c.moveToFirst() ){
            totalItem = c.getInt(0);
            c.close();
        }
        return totalItem;
    }
}
