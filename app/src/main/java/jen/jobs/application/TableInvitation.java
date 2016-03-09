package jen.jobs.application;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TableInvitation extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "invitation";

    public static String STATUS_APPROVED = "A";
    public static String STATUS_REJECTED = "R";
    public static String STATUS_APPLIED = "A"; // applied is assumed as approved
    public static String STATUS_NOT_INTERESTED = "R"; // not interested is assumed as rejected

    public static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TABLE_NAME+"' (id INTEGER, " + //0
            "emp_profile_id INTEGER, " + //1
            "emp_profile_name TEXT, " + //2
            "status TEXT, " + //3
            "post_id INTEGER, " + //4
            "post_title TEXT, " + //5
            "post_closed_on NUMERIC, " + //6
            "date_added NUMERIC, " + //7
            "date_updated NUMERIC);"; //8
    public static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TABLE_NAME+"'";

    public SQLiteDatabase db;
    private Context context;

    public TableInvitation(Context context){
        super(context, Jenjobs.DATABASE_NAME , null, Jenjobs.DATABASE_VERSION);
        db = this.getReadableDatabase();
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {}

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    public Cursor getInvitation( int invitation_id, int post_id ){
        String strSQL = "SELECT * FROM "+TABLE_NAME;
        String[] args = null;
        if( post_id > 0 ){
            strSQL += " WHERE post_id=?";
            args = new String[]{String.valueOf(post_id)};
        }else{
            if( invitation_id > 0 ){
                strSQL += " WHERE id=?";
                args = new String[]{String.valueOf(invitation_id)};
            }
        }
        strSQL += " ORDER BY id DESC";
        return db.rawQuery(strSQL, args);
    }

    public boolean saveInvitation( ContentValues cv2, int existingID ){
        boolean status = false;
        if( existingID == 0 ){
            Long insertID = db.insert(TABLE_NAME, null, cv2);
            status = insertID.intValue() > 0;
        }else{
            String strSQL = "SELECT * FROM "+TABLE_NAME+" WHERE id=?";
            String[] param = {String.valueOf(existingID)};
            Cursor c = db.rawQuery(strSQL, param);

            if( c.getCount() > 0 ){
                int updatedRow = db.update(TABLE_NAME, cv2, "id=?", param);
                status = updatedRow > 0;
            }else{
                Long insertID = db.insert(TABLE_NAME, null, cv2);
                status = insertID.intValue() > 0;
            }
        }
        return status;
    }

    public boolean deleteInvitation( int existingID ){
        String _id = String.valueOf(existingID);
        String[] param = {_id};
        int affectedRows = db.delete(TABLE_NAME, "id=?", param);
        return affectedRows > 0;
    }

    public boolean updateInvitation(ContentValues cv, int invitationID) {
        int affectedRows = db.update(TABLE_NAME, cv, "id=?", new String[]{String.valueOf(invitationID)});
        return affectedRows > 0;
    }
}
