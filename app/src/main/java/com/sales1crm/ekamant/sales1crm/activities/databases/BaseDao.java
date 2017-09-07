package com.sales1crm.ekamant.sales1crm.activities.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by apridosandyasa on 1/22/16.
 */
public abstract class BaseDao<T> implements DBSchema.Base {

    private final static String TAG = BaseDao.class.getSimpleName();
    private final String table;
    private final DBHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;
    private Context context;

    public BaseDao(Context c, String table) {
        this(c, table, false);
        this.context = c;
    }

    public BaseDao(Context c, String table, boolean willWrite) {
        this(c, null, table, willWrite);
        this.context = c;
    }

    public BaseDao(DBHelper dbHelper) {
        this(dbHelper, null, false);
    }

    public BaseDao(DBHelper dbHelper, String table) {
        this(dbHelper, table, false);
    }

    public BaseDao(DBHelper dbHelper, String table, boolean willWrite) {
        this(null, dbHelper, table, willWrite);
    }

    public BaseDao(Context c, DBHelper db, String table, boolean willWrite) {
        this.table = table;
        if(c == null && db != null) this.dbHelper = db;
        else {
            this.context = c;
            this.dbHelper = DBHelper.getDbHelperInstance(c);
        }
        if(willWrite == true) sqLiteDatabase = dbHelper.getWritableDatabase();
        else sqLiteDatabase = dbHelper.getReadableDatabase();
    }

    public Integer insertTable(T t) {
        Long result = -1L;
        try {
            synchronized (sqLiteDatabase) {
                sqLiteDatabase = dbHelper.getWritableDatabase();
                result = sqLiteDatabase.insertWithOnConflict(table, DBSchema.Base.COL_ID, upDataValues(t, true),SQLiteDatabase.CONFLICT_REPLACE);
                //sqliteDb.insert(table, null, upDataValues(t, false));
            }
        }catch (Exception e){
            Log.d(TAG, "Exception " + e.getMessage());

        } finally {
            close();
        }
        return result.intValue();
    }

    public Integer updateTable(T t, String strId, String columnId) {
        Integer result = null;
        try {
            synchronized (sqLiteDatabase) {
                sqLiteDatabase = dbHelper.getWritableDatabase();
                result = sqLiteDatabase.update(table, upDataValues(t, true),
                        " " + columnId + " = ?", new String[]{strId});
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            close();
        }
        return result;
    }

    public void delete(Integer id, String qry) {
        try {
            synchronized(sqLiteDatabase) {
                sqLiteDatabase = dbHelper.getWritableDatabase();
                if(qry == null) sqLiteDatabase.delete(table, " ID = "+id, null);
                else sqLiteDatabase.delete(table, qry + id, null);
            }
        } finally {
            close();
        }
    }

    public int deleteAllRecord() {
        int result = -1;
        try {
            synchronized(sqLiteDatabase) {
                sqLiteDatabase = dbHelper.getWritableDatabase();
                result = sqLiteDatabase.delete(table, null, null);
            }
        } finally {
            close();
        }
        return result;
    }

    public void close() {
        if(sqLiteDatabase != null) {
            sqLiteDatabase.close();
        }
    }

    public abstract T getById(int id);
    public abstract T getByCursor(Cursor c);
    protected abstract ContentValues upDataValues(T t, boolean update);

    public String getTable() {
        return this.table;
    }

    public DBHelper getDatabase() {
        return this.dbHelper;
    }

    public SQLiteDatabase getSqliteDb() {
        return this.sqLiteDatabase;
    }

    public Context getContext() {
        return this.context;
    }

}
