package com.sales1crm.ekamant.sales1crm.activities.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by dedepradana on 1/21/15.
 * at time 16:21.
 */
@SuppressWarnings({"FieldCanBeLocal", "JavaDoc", "unused"})
public class PreferenceUtility {

    private final String SP_COMMON = "SMART1CRM_PREFS";
    public static final String API_KEY = "API_KEY";
    public static final String USER_ID = "USER_ID";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_IMAGES = "USER_IMAGES";
    public static final String USER_POSITION = "USER_POSITION";
    public static final String USER_COMPANY = "USER_COMPANY";

    public static final String URL = "URL";
    public static final String URL_INTENT_DATA = "URL_INTENT_DATA";
    public static final String ENCODED_PASSCODE = "ENCODED_PASSCODE";
    public static final String FROM_DASHBOARD = "FROM_DASHBOARD";
    public static final String PASSCODE = "PASSCODE";
    public static final String CHECKIN = "CHECKIN";
    public static final String TASKLIST_TYPE = "TASKLIST_TYPE";
    public static final String PREFERENCES_PROPERTY_REG_ID = "PREFERENCES_PROPERTY_REG_ID";
    public static final String PREFERENCES_DEVICE_TOKEN = "PREFERENCES_DEVICE_TOKEN";
    public static final String TASK_ID = "TASK_ID";

    //  TASK PREF
    public static final String CUSTOMER_ID = "CUSTOMER_ID";
    public static final String CUSTOMER_NAME = "CUSTOMER_NAME";
    public static final String NOTES = "NOTES";
    public static final String ORDER_STATUS = "ORDER_STATUS";
    public static final String LATITUDE = "LATITUDE";
    public static final String LONGITUDE = "LONGITUDE";
    public static final String CREATED_AT = "CREATED_AT";
    public static final String PHOTO = "PHOTO";
    public static final String PHOTO_STRING1 = "PHOTO_STRING1";
    public static final String PHOTO_STRING2 = "PHOTO_STRING2";
    public static final String PHOTO_STRING3 = "PHOTO_STRING3";
    public static final String PHOTO_PATH1 = "PHOTO_PATH1";
    public static final String PHOTO_PATH2 = "PHOTO_PATH2";
    public static final String PHOTO_PATH3 = "PHOTO_PATH3";
    public static final String JSON_PRODUCT = "JSON_PRODUCT";


    //START AND END WORKING HOUR
    public static final String START_HOUR = "START_HOUR";
    public static final String START_MINUTE = "START_MINUTE";
    public static final String END_HOUR = "END_HOUR";
    public static final String END_MINUTE = "END_MINUTE";

    public static final String DRIVER_STAFF = "DRIVER_STAFF";//1 staff, 2 driver


    public static final String REMINDME = "REMINDME";
    public static final String WORK_START_NOTIF = "WORK_START_NOTIF";
    public static final String WORK_END_NOTIF = "WORK_END_NOTIF";

    /**
     * Singleton instance
     */
    private static PreferenceUtility instance = null;

    /**
     * Private constructor to avoid instantiation outside class
     */
    protected PreferenceUtility() {
    }

    /**
     * Get the singleton instance
     *
     * @return the PreferenceUtility instance
     */
    public static PreferenceUtility getInstance() {
        if (instance == null) {
            return new PreferenceUtility();
        }
        return instance;
    }

    /**
     * Set the singleton instance
     *
     * @param instance the PreferenceUtility instance
     */
    public static synchronized void setInstance(PreferenceUtility instance) {
        PreferenceUtility.instance = instance;
    }

    /**
     * private constructor, not to be instantiated with new keyword
     *
     * @param context application context
     * @return SharedPreference object of current application
     */
    public SharedPreferences sharedPreferences(Context context) {
        return context.getSharedPreferences(SP_COMMON, Context.MODE_PRIVATE);
    }

    /**
     * Save data base on key given, and the value is on integer format
     *
     * @param context
     * @param key
     * @param value
     */
    public void saveData(Context context, String key, int value) {
        saveData(context, key, String.valueOf(value));
    }

    /**
     * Save data base on key given, and the value is on string format
     *
     * @param context
     * @param key
     * @param value
     */
    public void saveData(Context context, String key, String value) {
        sharedPreferences(context).edit().putString(key, value).commit();
    }

    public void saveData(Context context, String key, Set<String> value) {
        sharedPreferences(context).edit().putStringSet(key, value).commit();
    }

    /**
     * Save data base on key given, and the value is on string format
     *
     * @param context
     * @param key
     * @param value
     */
    public void saveData(Context context, String key, Boolean value) {
        sharedPreferences(context).edit().putBoolean(key, value).commit();
    }

    /**
     * Save data base on key given, and the value is on long format
     *
     * @param context
     * @param key
     * @param value
     */
    public void saveData(Context context, String key, long value) {
        sharedPreferences(context).edit().putLong(key, value).commit();
    }

    /**
     * get data base on key given, and the value is on integer format
     *
     * @param context
     * @param key
     * @return
     */
    public int loadDataInt(Context context, String key) {
        return Integer.parseInt(sharedPreferences(context).getString(key, "0"));
    }

    /**
     * get data base on key given, and the value is on String format
     *
     * @param context
     * @param key
     * @return
     */
    public String loadDataString(Context context, String key) {
        return sharedPreferences(context).getString(key, "");
    }

    public Set<String> loadDataStringSet(Context context, String key) {
        Set<String> stringSet = new HashSet<>();
        return sharedPreferences(context).getStringSet(key, stringSet);
    }

    /**
     * get data base on key given, and the value is on String format
     *
     * @param context
     * @param key
     * @return
     */
    public Boolean loadDataBoolean(Context context, String key) {
        return loadDataBoolean(context, key, true);
    }

    /**
     * get data base on key given, and the value is on String format
     *
     * @param context
     * @param key
     * @return
     */
    public Boolean loadDataBoolean(Context context, String key, boolean defaultValue) {
        return sharedPreferences(context).getBoolean(key, defaultValue);
    }

    /**
     * get data base on key given, and the value is on long format
     *
     * @param context
     * @param key
     * @return
     */
    public long loadDataOfLong(Context context, String key) {
        return sharedPreferences(context).getLong(key, 0);
    }

    /**
     * Save data base on key given, and the value is on string format
     *
     * @param context
     * @param key
     */
    public void delete(Context context, String key) {
        sharedPreferences(context).edit().remove(key).commit();
    }

    /**
     * Delete data base for all existing key, and based on context
     *
     * @param context
     */
    public void deleteAll(Context context) {
        sharedPreferences(context).edit().clear().commit();
    }

}
