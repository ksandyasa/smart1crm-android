package com.sales1crm.ekamant.sales1crm.activities.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.sales1crm.ekamant.sales1crm.BuildConfig.DB_NAME;
import static com.sales1crm.ekamant.sales1crm.BuildConfig.DB_VERSION;

/**
 * Created by apridosandyasa on 1/22/16.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static DBHelper dbHelperInstance;
    private Context mContext;

    public static DBHelper getDbHelperInstance(Context c) {
        if (dbHelperInstance == null) {
            dbHelperInstance = new DBHelper(c);
        }
        return dbHelperInstance;
    }

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        setupTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    private void setupTables(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `" + DBSchema.Task.TABLE_NAME + "` (\n" +
                "`" + DBSchema.Task.KEY_CUSTOMER_ID + "` INTEGER PRIMARY KEY,\n" +
                "`" + DBSchema.Task.KEY_CUSTOMER_NAME + "` TEXT,\n" +
                "`" + DBSchema.Task.KEY_NOTES + "` TEXT,\n" +
                "`" + DBSchema.Task.KEY_ORDER_STATUS + "` INTEGER,\n" +
                "`" + DBSchema.Task.KEY_LAT + "` TEXT,\n" +
                "`" + DBSchema.Task.KEY_LONG + "` TEXT,\n" +
                "`" + DBSchema.Task.KEY_FOTO_1 + "` TEXT,\n" +
                "`" + DBSchema.Task.KEY_FOTO_2 + "` TEXT,\n" +
                "`" + DBSchema.Task.KEY_FOTO_3 + "` TEXT,\n" +
                "`" + DBSchema.Task.KEY_SIGNATURE + "` TEXT,\n" +
                "`" + DBSchema.Task.KEY_PRODUCT + "` TEXT,\n" +
                "`" + DBSchema.Task.KEY_CREATED_AT + "` TEXT" +
                ");");

        db.execSQL("CREATE TABLE IF NOT EXISTS `" + DBSchema.Customer.TABLE_NAME +"` (\n" +
                "`" + DBSchema.Base.COL_ID + "` INTEGER PRIMARY KEY,\n" +
                "`" + DBSchema.Customer.KEY_CUSTOMER_NAME + "` TEXT,\n" +
                "`" + DBSchema.Customer.KEY_CUSTOMER_ADDRESS + "` TEXT,\n" +
                "`" + DBSchema.Customer.KEY_TYPE_ID + "` INTEGER,\n" +
                "`" + DBSchema.Customer.KEY_TIPE_ID + "` TEXT,\n" +
                "`" + DBSchema.Customer.KEY_CUSTOMER_CODE + "` TEXT" +
                ");");

        db.execSQL("CREATE TABLE IF NOT EXISTS `" + DBSchema.Category.TABLE_NAME +"` (\n" +
                "`" + DBSchema.Base.COL_ID + "` INTEGER PRIMARY KEY,\n" +
                "`" + DBSchema.Category.KEY_BRANCH + "` INTEGER,\n" +
                "`" + DBSchema.Category.KEY_NAME + "` TEXT,\n" +
                "`" + DBSchema.Category.KEY_DESCRIPTION + "` TEXT,\n" +
                "`" + DBSchema.Category.KEY_STATUS + "` TEXT,\n" +
                "`" + DBSchema.Category.KEY_PRODUCT + "` TEXT,\n" +
                "`" + DBSchema.Category.KEY_ACCOUNT_TYPE_ID + "` INTEGER" +
                ");");

        db.execSQL("CREATE TABLE IF NOT EXISTS `" + DBSchema.Product.TABLE_NAME + "` (\n" +
                "`" + DBSchema.Base.COL_ID + "` INTEGER PRIMARY KEY,\n" +
                "`" + DBSchema.Product.KEY_BRANCH + "` INTEGER,\n" +
                "`" + DBSchema.Product.KEY_PRODUCT_CODE + "` TEXT,\n" +
                "`" + DBSchema.Product.KEY_CATEGORY_ID + "` INTEGER,\n" +
                "`" + DBSchema.Product.KEY_NAME + "` TEXT,\n" +
                "`" + DBSchema.Product.KEY_DESCRIPTION + "` TEXT,\n" +
                "`" + DBSchema.Product.KEY_STATUS + "` TEXT,\n" +
                "`" + DBSchema.Product.KEY_CREATE_DATE + "` TEXT,\n" +
                "`" + DBSchema.Product.KEY_UPDATE_DATE + "` TEXT" +
                ");");
    }

}
