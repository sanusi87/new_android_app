package jen.jobs.application;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TableWorkExperience  extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "work_experience";

    public static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TableWorkExperience.TABLE_NAME
            +"' (" +
            "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
            "_id INTEGER, "+
            "position TEXT, " +
            "company TEXT, " +
            "job_spec_id INTEGER(4), " +
            "job_role_id INTEGER(4), " +
            "job_type_id INTEGER(4), " +
            "job_level_id INTEGER(4), " +
            "industry_id INTEGER(4), " +
            "experience TEXT, " +
            "salary INTEGER, " +
            "currency_id INTEGER, " +
            "started_on NUMERIC, " +
            "resigned_on NUMERIC," +
            "update_at NUMERIC);";
    public static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TableWorkExperience.TABLE_NAME+"'";

    public SQLiteDatabase db;

    public TableWorkExperience(Context context) {
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

    public Cursor getWorkExperience(){
        return db.rawQuery("SELECT * FROM "+TableWorkExperience.TABLE_NAME, null);
    }

    public Long addWorkExperience(ContentValues cv2){
        return db.insert(TableWorkExperience.TABLE_NAME, null, cv2);
    }

    public boolean updateWorkExperience(ContentValues cv2, int existingID){
        if( existingID > 0 ){
            String[] _id = {String.valueOf(existingID)};
            int affectedRows = db.update(TableWorkExperience.TABLE_NAME, cv2, "id=?", _id);
            if( affectedRows > 0 ){
                return true;
            }
        }
        return false;
    }

    public boolean deleteWorkExperience(int id){
        String _id = String.valueOf(id);
        String[] param = {_id};
        int affectedRows = db.delete(TableWorkExperience.TABLE_NAME, "id=?", param);
        return affectedRows > 0;
    }

    public Cursor getWorkExperienceById(int currentId) {
        String[] _id = {String.valueOf(currentId)};
        return db.rawQuery("SELECT * FROM "+TableWorkExperience.TABLE_NAME+" WHERE id=?", _id);
    }
}
