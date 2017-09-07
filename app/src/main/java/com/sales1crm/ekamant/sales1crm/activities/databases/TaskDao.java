package com.sales1crm.ekamant.sales1crm.activities.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.sales1crm.ekamant.sales1crm.activities.models.Customer;
import com.sales1crm.ekamant.sales1crm.activities.models.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apridosandyasa on 4/13/17.
 */

public class TaskDao extends BaseDao<Task> implements DBSchema.Task {

    private Cursor c;

    public TaskDao(Context c) {
        super(c, TABLE_NAME);
    }

    public TaskDao(Context c, boolean willWrite) {
        super(c, TABLE_NAME, willWrite);
    }

    public TaskDao(DBHelper db) {
        super(db, TABLE_NAME);
    }

    public TaskDao(DBHelper db, boolean willWrite) {
        super(db, TABLE_NAME, willWrite);
    }

    public Task getById(int id) {
        c = getSqliteDb().query(getTable(), null, "`" + KEY_CUSTOMER_ID + "` =?", new String[] {"" + id}, null, null, null);
        Task task = new Task();
        try {
            if(c != null && c.moveToFirst()) {
                c.moveToFirst();
                task = getByCursor(c);
            }
        } finally {
            c.close();
        }
        return task;
    }

    public Task getByCustomerId(int customerId) {
        c = getSqliteDb().query(getTable(), null, "`" + KEY_CUSTOMER_ID + "` =?", new String[] {"" + customerId}, null, null, null);
        Task task = new Task();
        try {
            if(c != null && c.moveToFirst()) {
                c.moveToFirst();
                task = getByCursor(c);
            }
        } finally {
            c.close();
        }
        return task;
    }

    public List<Task> getTaskList() {
        String query = "SELECT * FROM " + getTable();

        Cursor c = getSqliteDb().rawQuery(query, null);
        List<Task> taskList = new ArrayList<>();
        try {
            if(c != null && c.moveToFirst()) {
                taskList.add(getByCursor(c));
                while (c.moveToNext()) {
                    taskList.add(getByCursor(c));

                }
            }
        } finally {
            c.close();
        }
        return taskList;
    }

    public int checkIfTaskIsExists(int customerId) {
        String query = "SELECT * FROM " + getTable() + " WHERE " + KEY_CUSTOMER_ID + " =? LIMIT 1;";
        c = getSqliteDb().rawQuery(query, new String[] {"" + customerId});
        int count = 0;
        if(c.getCount() > 0) {
            c.moveToFirst();
            count = c.getCount();
        }
        c.close();

        return count;
    }

    @Override
    public Task getByCursor(Cursor c) {
        Task task = new Task();
        task.setCustomer_id(c.getInt(0));
        task.setCustomer_name(c.getString(1));
        task.setNotes(c.getString(2));
        task.setOrder_status("" + c.getInt(3));
        task.setLatitude(c.getString(4));
        task.setLongitude(c.getString(5));
        task.setFoto1(c.getString(6));
        task.setFoto2(c.getString(7));
        task.setFoto3(c.getString(8));
        task.setSignature(c.getString(9));
        task.setProduct(c.getString(10));
        task.setCreated_at(c.getString(11));
        return task;
    }

    @Override
    protected ContentValues upDataValues(Task task, boolean update) {
        ContentValues cv = new ContentValues();
        if (update == true) cv.put(KEY_CUSTOMER_ID, task.getCustomer_id());
        cv.put(KEY_CUSTOMER_NAME, task.getCustomer_name());
        cv.put(KEY_NOTES, task.getNotes());
        cv.put(KEY_ORDER_STATUS, task.getOrder_status());
        cv.put(KEY_LAT, task.getLatitude());
        cv.put(KEY_LONG, task.getLongitude());
        cv.put(KEY_FOTO_1, task.getFoto1());
        cv.put(KEY_FOTO_2, task.getFoto2());
        cv.put(KEY_FOTO_3, task.getFoto3());
        cv.put(KEY_SIGNATURE, task.getSignature());
        cv.put(KEY_PRODUCT, task.getProduct());
        cv.put(KEY_CREATED_AT, task.getCreated_at());
        return cv;
    }

}
