package com.sales1crm.ekamant.sales1crm.activities.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.sales1crm.ekamant.sales1crm.activities.models.Category;
import com.sales1crm.ekamant.sales1crm.activities.models.Product;
import com.sales1crm.ekamant.sales1crm.activities.models.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apridosandyasa on 4/17/17.
 */

public class CategoryDao extends BaseDao<Category> implements DBSchema.Category {

    public CategoryDao(Context c) {
        super(c, TABLE_NAME);
    }

    public CategoryDao(Context c, boolean willWrite) {
        super(c, TABLE_NAME, willWrite);
    }

    public CategoryDao(DBHelper db) {
        super(db, TABLE_NAME);
    }

    public CategoryDao(DBHelper db, boolean willWrite) {
        super(db, TABLE_NAME, willWrite);
    }

    public Category getById(int id) {
        Cursor c = getSqliteDb().query(getTable(), null, COL_ID + "=?", new String[] {"" + id}, null, null, null);;
        Category category = new Category();
        try {
            if(c != null && c.moveToFirst()) {
                c.moveToFirst();
                category = getByCursor(c);
            }
        } finally {
            c.close();
        }
        return category;
    }

    public List<Category> getCategoryListFromDb() {
        String query = "SELECT * FROM " + getTable();
        Cursor c = getSqliteDb().rawQuery(query, null);
        List<Category> categoryList = new ArrayList<>();
        try {
            if(c != null && c.moveToFirst()) {
                categoryList.add(getByCursor(c));
                while (c.moveToNext()) {
                    categoryList.add(getByCursor(c));

                }
            }
        } finally {
            c.close();
        }
        return categoryList;
    }

    @Override
    public Category getByCursor(Cursor c) {
        Category category = new Category();
        category.setId(c.getInt(0));
        category.setBranch(c.getInt(1));
        category.setName(c.getString(2));
        category.setDescription(c.getString(3));
        category.setStatus(c.getString(4));
        category.setAccount_type(c.getInt(6));
        return category;
    }

    @Override
    protected ContentValues upDataValues(Category category, boolean update) {
        ContentValues cv = new ContentValues();
        if (update == true) cv.put(COL_ID, category.getId());
        cv.put(KEY_BRANCH, category.getBranch());
        cv.put(KEY_NAME, category.getName());
        cv.put(KEY_DESCRIPTION, category.getDescription());
        cv.put(KEY_STATUS, category.getStatus());
        cv.put(KEY_PRODUCT, "");
        cv.put(KEY_ACCOUNT_TYPE_ID, category.getAccount_type());
        return cv;
    }

}
