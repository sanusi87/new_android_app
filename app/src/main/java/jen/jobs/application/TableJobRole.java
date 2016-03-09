package jen.jobs.application;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.util.ArrayList;

public class TableJobRole extends SQLiteOpenHelper{
    public static final String TABLE_NAME = "job_role";
    public static String SQL_CREATE_ENTRIES = "CREATE TABLE '"+TABLE_NAME+"' (id INTEGER(4), " +
            "job_spec_id INTEGER(4), " +
            "role_name TEXT);";
    public static String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS '"+TABLE_NAME+"'";

    public SQLiteDatabase db;
    public TableJobRole(Context context){
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

    public Cursor getAllJobRole(int jobSpecId){
        String[] _id = {String.valueOf(jobSpecId)};
        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE job_spec_id=?", _id);
        return c;
    }

    public JobRole findById( int id ){
        String[] _id = {String.valueOf(id)};
        Cursor c = db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE id=?", _id);
        if( c.moveToFirst() ){
            JobRole r = new JobRole(c.getInt(0), c.getInt(1), c.getString(2));
            c.close();
            return r;
        }
        return null;
    }

    public ArrayList<JobRole> findByJobSpec(String jobSpecId, ArrayList<Integer> jobRoleId){
        ArrayList<JobRole> myJobRole = new ArrayList<>();
        if( jobRoleId != null && jobRoleId.size() > 0 ){
            ArrayList<String> params = new ArrayList<>();
            params.add(jobSpecId);

            ArrayList<String> placeHolder = new ArrayList<>();
            for( Integer roleId : jobRoleId ){
                placeHolder.add("?");
                params.add(String.valueOf(roleId));
            }

            String strSQL = "SELECT * FROM (SELECT * FROM "+TABLE_NAME+" WHERE job_spec_id=?) A WHERE id IN("+ TextUtils.join(",", placeHolder)+")";
            Cursor c = db.rawQuery(strSQL, params.toArray(new String[params.size()]));

            if(c.moveToFirst()){
                while(!c.isAfterLast()){
                    //c.getString(0);
                    //c.getString(1);
                    //c.getString(2);
                    //Log.e("jobRole", c.getString(2));
                    myJobRole.add(new JobRole(c.getInt(0),c.getInt(1),c.getString(2)));
                    c.moveToNext();
                }
            }else{
                //Log.e("c.moveToFirst", "false");
            }
            c.close();
        }else{
            //Log.e("jobRoleId.size", "0");
        }
        return myJobRole;
    }

    public void clearAll() {
        db.execSQL("DELETE FROM "+TABLE_NAME);
    }

    public int addJobRole( ContentValues cv ){
        Long insertId = db.insert(TABLE_NAME, null, cv);
        return insertId.intValue();
    }
}
