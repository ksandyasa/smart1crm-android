package com.sales1crm.ekamant.sales1crm.activities.databases;

/**
 * Created by apridosandyasa on 1/22/16.
 */
public interface DBSchema {

    public interface Base {
        public static final String COL_ID = "_id";
    }

    public interface Task {
        public static final String TABLE_NAME = "TASK";
        public static final String KEY_CUSTOMER_ID = "account_id"; //1
        public static final String KEY_CUSTOMER_NAME = "account_name"; // as subject - 2
        public static final String KEY_NOTES = "notes"; // 3
        public static final String KEY_ORDER_STATUS = "order_status"; // 4
        public static final String KEY_LAT = "latitude"; // 5
        public static final String KEY_LONG = "longitude"; // 6
        public static final String KEY_FOTO_1 = "foto1"; // 7
        public static final String KEY_FOTO_2 = "foto2"; // 8
        public static final String KEY_FOTO_3 = "foto3"; // 9
        public static final String KEY_CREATED_AT = "created_at"; // 10
        public static final String KEY_SIGNATURE = "signature"; // 10
        public static final String KEY_PRODUCT = "product"; // 10
    }

    public interface Customer {
        public static final String TABLE_NAME = "CUSTOMER";
        public static final String KEY_CUSTOMER_NAME = "customer_name";
        public static final String KEY_CUSTOMER_ADDRESS = "customer_address";
        public static final String KEY_TYPE_ID = "type_id";
        public static final String KEY_TIPE_ID = "tipe";
        public static final String KEY_CUSTOMER_CODE = "customer_code";
    }

    public interface Category {
        public static final String TABLE_NAME = "CATEGORY";
        public static final String KEY_BRANCH = "branch";
        public static final String KEY_NAME = "name";
        public static final String KEY_DESCRIPTION = "description";
        public static final String KEY_STATUS = "status";
        public static final String KEY_PRODUCT = "product";
        public static final String KEY_ACCOUNT_TYPE_ID = "account_type";
    }

    public interface Product {
        public static final String TABLE_NAME = "PRODUCT";
        public static final String KEY_BRANCH = "branch";
        public static final String KEY_PRODUCT_CODE = "product_code";
        public static final String KEY_CATEGORY_ID = "category_id";
        public static final String KEY_NAME = "name";
        public static final String KEY_DESCRIPTION = "description";
        public static final String KEY_STATUS = "status";
        public static final String KEY_CREATE_DATE = "create_date";
        public static final String KEY_UPDATE_DATE = "update_date";
    }

}
