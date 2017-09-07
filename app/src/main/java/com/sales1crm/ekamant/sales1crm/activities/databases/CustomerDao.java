package com.sales1crm.ekamant.sales1crm.activities.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.sales1crm.ekamant.sales1crm.activities.models.Customer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apridosandyasa on 4/13/17.
 */

public class CustomerDao extends BaseDao<Customer> implements DBSchema.Customer {

    public CustomerDao(Context c) {
        super(c, TABLE_NAME);
    }

    public CustomerDao(Context c, boolean willWrite) {
        super(c, TABLE_NAME, willWrite);
    }

    public CustomerDao(DBHelper db) {
        super(db, TABLE_NAME);
    }

    public CustomerDao(DBHelper db, boolean willWrite) {
        super(db, TABLE_NAME, willWrite);
    }

    public Customer getById(int id) {
        String qry = "SELECT * FROM " + getTable() + " WHERE " + COL_ID + " = " + id;
        Cursor c = getSqliteDb().rawQuery(qry, null);
        Customer customer = new Customer();
        try {
            if(c != null && c.moveToFirst()) {
                customer = getByCursor(c);
            }
        } finally {
            c.close();
        }
        return customer;
    }

    public List<Customer> getCustomerListFromDb() {
        String query = "SELECT * FROM " + getTable();

        Cursor c = getSqliteDb().rawQuery(query, null);
        List<Customer> customerList = new ArrayList<>();
        try {
            if(c != null && c.moveToFirst()) {
                customerList.add(getByCursor(c));
                while (c.moveToNext()) {
                    customerList.add(getByCursor(c));

                }
            }
        } finally {
            c.close();
        }
        return customerList;
    }

    @Override
    public Customer getByCursor(Cursor c) {
        Customer customer = new Customer();
        customer.setCustomer_id(c.getInt(0));
        customer.setCustomer_name(c.getString(1));
        customer.setCustomer_address(c.getString(2));
        customer.setCustomer_type(c.getInt(3));
        customer.setCustomer_tipe(c.getString(4));
        customer.setCustomer_code(c.getString(5));
        return customer;
    }

    @Override
    protected ContentValues upDataValues(Customer customer, boolean update) {
        ContentValues cv = new ContentValues();
        if (update == true) cv.put(COL_ID, customer.getCustomer_id());
        cv.put(KEY_CUSTOMER_NAME, customer.getCustomer_name());
        cv.put(KEY_CUSTOMER_ADDRESS, customer.getCustomer_address());
        cv.put(KEY_TYPE_ID, customer.getCustomer_type());
        cv.put(KEY_TIPE_ID, customer.getCustomer_tipe());
        cv.put(KEY_CUSTOMER_CODE, customer.getCustomer_code());
        return cv;
    }

}
