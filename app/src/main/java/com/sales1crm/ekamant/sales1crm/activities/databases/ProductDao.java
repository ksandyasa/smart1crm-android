package com.sales1crm.ekamant.sales1crm.activities.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.sales1crm.ekamant.sales1crm.activities.models.Customer;
import com.sales1crm.ekamant.sales1crm.activities.models.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apridosandyasa on 4/17/17.
 */

public class ProductDao extends BaseDao<Product> implements DBSchema.Product {

    public ProductDao(Context c) {
        super(c, TABLE_NAME);
    }

    public ProductDao(Context c, boolean willWrite) {
        super(c, TABLE_NAME, willWrite);
    }

    public ProductDao(DBHelper db) {
        super(db, TABLE_NAME);
    }

    public ProductDao(DBHelper db, boolean willWrite) {
        super(db, TABLE_NAME, willWrite);
    }

    public Product getById(int id) {
        Cursor c = getSqliteDb().query(getTable(), null, COL_ID + "=?", new String[] {"" + id}, null, null, null);;
        Product product = new Product();
        try {
            if(c != null && c.moveToFirst()) {
                product = getByCursor(c);
            }
        } finally {
            c.close();
        }
        return product;
    }

    public List<Product> getProductListFromDb(int categoryId) {
        Cursor c = getSqliteDb().query(getTable(), null, KEY_CATEGORY_ID + "=?", new String[] {"" + categoryId}, null, null, null);
        List<Product> productList = new ArrayList<>();
        try {
            if(c != null && c.moveToFirst()) {
                productList.add(getByCursor(c));
                while (c.moveToNext()) {
                    productList.add(getByCursor(c));

                }
            }
        } finally {
            c.close();
        }
        return productList;
    }

    @Override
    public Product getByCursor(Cursor c) {
        Product product = new Product();
        product.setId(c.getInt(0));
        product.setBranch_id(c.getInt(1));
        product.setProduct_code(c.getString(2));
        product.setCategory_id(c.getInt(3));
        product.setName(c.getString(4));
        product.setDescription(c.getString(5));
        product.setStatus(c.getString(6));
        product.setCreate_date(c.getString(7));
        product.setUpdate_date(c.getString(8));
        return product;
    }

    @Override
    protected ContentValues upDataValues(Product product, boolean update) {
        ContentValues cv = new ContentValues();
        if (update == true) cv.put(COL_ID, product.getId());
        cv.put(KEY_BRANCH, product.getBranch_id());
        cv.put(KEY_PRODUCT_CODE, product.getProduct_code());
        cv.put(KEY_CATEGORY_ID, product.getCategory_id());
        cv.put(KEY_NAME, product.getName());
        cv.put(KEY_DESCRIPTION, product.getDescription());
        cv.put(KEY_STATUS, product.getStatus());
        cv.put(KEY_CREATE_DATE, product.getCreate_date());
        cv.put(KEY_UPDATE_DATE, product.getUpdate_date());
        return cv;
    }

}
