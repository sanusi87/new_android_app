package jen.jobs.application;

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
            "name TEXT," +
            "date_added NUMERIC);";
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
        return db.insert(TableSkill.TABLE_NAME, null, cv2);
    }

    public boolean updateSkill(ContentValues cv2, int existingID){
        String[] _id = {String.valueOf(existingID)};
        int affectedRows = db.update(TableSkill.TABLE_NAME, cv2, "id=?", _id);
        return affectedRows > 0;
    }

    public boolean deleteSkill(int id){
        String _id = String.valueOf(id);
        String[] param = {_id};
        int affectedRows = db.delete(TableSkill.TABLE_NAME, "id=?", param);
        return affectedRows > 0;
    }

    public String[] findSkillById(int skillId) {
        String[] s = null;
        Cursor c = db.rawQuery("SELECT * FROM "+TableSkill.TABLE_NAME+" WHERE id="+skillId, null);
        if( c.moveToFirst() ){
            /*
            * 0 => savedId
            * 1 => actualId = id in remote server
            * 2 => skill name
            * */

            s = new String[]{c.getString(0),c.getString(1),c.getString(2)};

            c.moveToNext();
        }
        c.close();

        return s;
    }
}
