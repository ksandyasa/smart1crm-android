package com.sales1crm.ekamant.sales1crm.activities.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.EditText;

import com.sales1crm.ekamant.sales1crm.activities.TaskAddActivity;
import com.sales1crm.ekamant.sales1crm.activities.databases.CategoryDao;
import com.sales1crm.ekamant.sales1crm.activities.databases.CustomerDao;
import com.sales1crm.ekamant.sales1crm.activities.databases.DBHelper;
import com.sales1crm.ekamant.sales1crm.activities.databases.ProductDao;
import com.sales1crm.ekamant.sales1crm.activities.databases.TaskDao;
import com.sales1crm.ekamant.sales1crm.activities.models.Category;
import com.sales1crm.ekamant.sales1crm.activities.models.Customer;
import com.sales1crm.ekamant.sales1crm.activities.models.Product;
import com.sales1crm.ekamant.sales1crm.activities.models.Task;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.RequestBody;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by apridosandyasa on 4/11/17.
 */

public class Smart1CrmUtils {

    public static int POST_WITH_JSON_OBJECT = 1;
    public static int POST_WITH_JSON_ARRAY = 1;

    public static int SIGNATURE_ACTIVITY = 10;

    public static Date today_date;
    public static boolean service_running = false;
    public static boolean service_taskdata_running = false;

    // Default value
    public static final int PENDINGINTENT_LATEALARM = 99990;
    public static final int PENDINGINTENT_STARTALARM = 99993;
    public static final int PENDINGINTENT_ENDALARM = 99991;
    public static final int PENDINGINTENT_LOCATION = 99999;
    public static final int PENDINGINTENT_TASKDATA = 99992;
    public static final int DEFAULT_VALUEFORCHECK_IN = 1;
    public static final int CHECKOUT_ALREADY = 0;

    // DIALOG
    public static final int TYPE_OK = 1;
    public static final int TYPE_YESNO = 2;

    public static final int CAMERA_CAPTURE = 2;
    public static final int GALLERY = 3;
    public static final int BACK_FROM_ADD = 4;
    public static final int TYPE_RECT = 0;
    public static final int TYPE_ROUND = 1;

    //	FireBase
    public static final String TOPIC_GLOBAL = "global";
    //	broadcast receiver intent filter
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    //  id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static class ConnectionUtility {

        public static boolean isNetworkConnected(Context context) {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            }
            return false;
        }

    }

    public static class PostParamUtility {

        public static RequestBody getSplashPostParam(String json) throws JSONException {
            JSONObject jsonObject = new JSONObject(json);

            RequestBody formBody = new FormEncodingBuilder()
                    .add("api_key", jsonObject.getString("api_key"))
                    .build();

            return formBody;
        }

        public static RequestBody getLoginPostParam(String json) throws JSONException {
            JSONObject jsonObject = new JSONObject(json);

            RequestBody formBody = new FormEncodingBuilder()
                    .add("passcode", jsonObject.getString("passcode"))
                    .add("device_token", jsonObject.getString("device_token"))
                    .add("device_type", jsonObject.getString("device_type"))
                    .build();

            return formBody;
        }

        public static RequestBody getLoginConfirmationParam(String json) throws JSONException {
            JSONObject jsonObject = new JSONObject(json);

            RequestBody formBody = new FormEncodingBuilder()
                    .add("api_key", jsonObject.getString("api_key"))
                    .build();

            return formBody;
        }

        public static RequestBody getCheckinParam(String json) throws JSONException {
            JSONObject jsonObject = new JSONObject(json);

            RequestBody formBody = new FormEncodingBuilder()
                    .add("api_key", jsonObject.getString("api_key"))
                    .build();

            return formBody;
        }

        public static RequestBody getLocationParam(String json) throws JSONException {
            JSONObject jsonObject = new JSONObject(json);

            RequestBody formBody = new FormEncodingBuilder()
                    .add("api_key", jsonObject.getString("api_key"))
                    .add("latitude", jsonObject.getString("latitude"))
                    .add("longitude", jsonObject.getString("longitude"))
                    .add("date", jsonObject.getString("date"))
                    .add("address", jsonObject.getString("address"))
                    .build();

            return formBody;
        }

        public static RequestBody getLogoutParam(String json) throws JSONException {
            JSONObject jsonObject = new JSONObject(json);

            RequestBody formBody = new FormEncodingBuilder()
                    .add("api_key", jsonObject.getString("api_key"))
                    .build();

            return formBody;
        }

        public static RequestBody getCategoryParam(String json) throws JSONException {
            JSONObject jsonObject = new JSONObject(json);

            RequestBody formBody = new FormEncodingBuilder()
                    .add("api_key", jsonObject.getString("api_key"))
                    .build();

            return formBody;
        }

        public static RequestBody getAccountListParam(String json) throws JSONException {
            JSONObject jsonObject = new JSONObject(json);

            RequestBody formBody = new FormEncodingBuilder()
                    .add("api_key", jsonObject.getString("api_key"))
                    .add("limit", jsonObject.getString("limit"))
                    .add("offset", jsonObject.getString("offset"))
                    .add("search", jsonObject.getString("search"))
                    .build();

            return formBody;
        }

        public static RequestBody getSendTaskDataParam(Context context, String json) throws JSONException {
            JSONObject jsonObject = new JSONObject(json);

            TaskDao taskDao = new TaskDao(new DBHelper(context), false);
            Task task = taskDao.getByCustomerId(Integer.valueOf(jsonObject.getString("account_id")));

            FormEncodingBuilder formBuilder = new FormEncodingBuilder();
            formBuilder.add("api_key", jsonObject.getString("api_key"));
            formBuilder.add("account_id", jsonObject.getString("account_id"));
            formBuilder.add("latitude", jsonObject.getString("latitude"));
            formBuilder.add("longitude", jsonObject.getString("longitude"));
            formBuilder.add("notes", jsonObject.getString("notes"));
            if (jsonObject.has("nama1")) {
                formBuilder.add("nama1", jsonObject.getString("nama1"));
                formBuilder.add("photo_1", PreferenceUtility.getInstance().loadDataString(context, PreferenceUtility.PHOTO_PATH1));
                Log.d("TAG", "nama1 " + jsonObject.getString("nama1"));
                Log.d("TAG", "foto1 " + PreferenceUtility.getInstance().loadDataString(context, PreferenceUtility.PHOTO_PATH1));
            }
            if (jsonObject.has("nama2")) {
                formBuilder.add("nama2", jsonObject.getString("nama2"));
                formBuilder.add("photo_2", PreferenceUtility.getInstance().loadDataString(context, PreferenceUtility.PHOTO_PATH2));
                Log.d("TAG", "nama2 " + jsonObject.getString("nama2"));
                Log.d("TAG", "foto2 " + PreferenceUtility.getInstance().loadDataString(context, PreferenceUtility.PHOTO_PATH2));
            }
            if (jsonObject.has("nama3")) {
                formBuilder.add("nama3", jsonObject.getString("nama3"));
                formBuilder.add("photo_3", PreferenceUtility.getInstance().loadDataString(context, PreferenceUtility.PHOTO_PATH3));
                Log.d("TAG", "nama3 " + jsonObject.getString("nama3"));
                Log.d("TAG", "foto3 " + PreferenceUtility.getInstance().loadDataString(context, PreferenceUtility.PHOTO_PATH3));
            }
            formBuilder.add("nama4", jsonObject.getString("nama4"));
            formBuilder.add("photo_4", task.getSignature());
            formBuilder.add("order_status", jsonObject.getString("order_status"));
            formBuilder.add("status_id", jsonObject.getString("status_id"));
            formBuilder.add("product", jsonObject.getString("product"));
            formBuilder.add("longitude", jsonObject.getString("longitude"));
            formBuilder.add("created_at", jsonObject.getString("created_at"));

            RequestBody formBody = formBuilder.build();

            return formBody;
        }

        public static RequestBody getKPIParam(String json) throws JSONException {
            JSONObject jsonObject = new JSONObject(json);

            RequestBody formBody = new FormEncodingBuilder()
                    .add("api_key", jsonObject.getString("api_key"))
                    .add("month", jsonObject.getString("month"))
                    .add("year", jsonObject.getString("year"))
                    .build();

            return formBody;
        }

        public static RequestBody getSendTaskDataOfflineParam(Context context, String json) throws JSONException {
            JSONObject jsonObject = new JSONObject(json);

            TaskDao taskDao = new TaskDao(new DBHelper(context));
            Task task = taskDao.getByCustomerId(Integer.valueOf(jsonObject.getString("account_id")));

            FormEncodingBuilder formBuilder = new FormEncodingBuilder();
            formBuilder.add("api_key", jsonObject.getString("api_key"));
            formBuilder.add("account_id", jsonObject.getString("account_id"));
            formBuilder.add("notes", jsonObject.getString("notes"));
            if (jsonObject.has("nama1")) {
                formBuilder.add("nama1", jsonObject.getString("nama1"));
                formBuilder.add("photo_1", task.getFoto1());
                Log.d("TAG", "nama1 " + jsonObject.getString("nama1"));
                Log.d("TAG", "foto1 " + task.getFoto1());
            }
            if (jsonObject.has("nama2")) {
                formBuilder.add("nama2", jsonObject.getString("nama2"));
                formBuilder.add("photo_2", task.getFoto2());
                Log.d("TAG", "nama2 " + jsonObject.getString("nama2"));
                Log.d("TAG", "foto2 " + task.getFoto2());
            }
            if (jsonObject.has("nama3")) {
                formBuilder.add("nama3", jsonObject.getString("nama3"));
                formBuilder.add("photo_3", task.getFoto3());
                Log.d("TAG", "nama3 " + jsonObject.getString("nama3"));
                Log.d("TAG", "foto3 " + task.getFoto3());
            }
            formBuilder.add("latitude", jsonObject.getString("latitude"));
            formBuilder.add("longitude", jsonObject.getString("longitude"));
            formBuilder.add("nama4", jsonObject.getString("nama4"));
            formBuilder.add("photo_4", task.getSignature());
            formBuilder.add("order_status", jsonObject.getString("order_status"));
            formBuilder.add("status_id", jsonObject.getString("status_id"));
            formBuilder.add("product", jsonObject.getString("product"));
            formBuilder.add("longitude", jsonObject.getString("longitude"));
            formBuilder.add("created_at", jsonObject.getString("created_at"));

            RequestBody formBody = formBuilder.build();

            return formBody;
        }

    }

    public static class JSONUtility {

        public static String getResultFromServer(String json) throws JSONException {
            String result = "";

            JSONObject jsonObject = new JSONObject(json);
            result = jsonObject.getString("result");

            return result;
        }

        public static String getApiKey(String json) throws JSONException {
            String api_key = "";

            JSONObject jsonObject = new JSONObject(json);
            api_key = jsonObject.getJSONObject("datas").getJSONObject("user").getString("api_key");

            return api_key;
        }

        public static int getStatusCheckIn(String json) throws JSONException {
            int statusCheckIn = -1;

            JSONObject jsonObject = new JSONObject(json);
            statusCheckIn = jsonObject.getJSONObject("datas").getInt("status");

            return statusCheckIn;
        }

        public static void saveLoginItemsFromJSON(Context context, String json) throws JSONException {
            JSONObject jsonObject = new JSONObject(json);

            String user_name = jsonObject.getJSONObject("datas").getJSONObject("user").getString("name");
            String user_images = jsonObject.getJSONObject("datas").getJSONObject("user").getString("images");
            String user_position = jsonObject.getJSONObject("datas").getJSONObject("user").getString("position");
            String user_company = jsonObject.getJSONObject("datas").getJSONObject("user").getString("company");
            int user_id = Integer.valueOf(jsonObject.getJSONObject("datas").getJSONObject("user").getString("user_id"));

            String start_time = jsonObject.getJSONObject("datas").getJSONObject("user").getString("working_time_start");
            String end_time = jsonObject.getJSONObject("datas").getJSONObject("user").getString("working_time_end");

            String[] array_start = start_time.split(":");
            String[] array_end = end_time.split(":");

            int type = Integer.valueOf(jsonObject.getJSONObject("datas").getJSONObject("user").getString("type"));
            PreferenceUtility.getInstance().saveData(context,
                    PreferenceUtility.DRIVER_STAFF, type);

            int start_hour, start_minute, end_hour, end_minute;
            start_hour = Integer.valueOf(array_start[0]);
            start_minute = Integer.valueOf(array_start[1]);
            end_hour = Integer.valueOf(array_end[0]);
            end_minute = Integer.valueOf(array_end[1]);

            PreferenceUtility.getInstance().saveData(context,
                    PreferenceUtility.START_HOUR, start_hour);
            PreferenceUtility.getInstance().saveData(context,
                    PreferenceUtility.START_MINUTE, start_minute);
            PreferenceUtility.getInstance().saveData(context,
                    PreferenceUtility.END_HOUR, end_hour);
            PreferenceUtility.getInstance().saveData(context,
                    PreferenceUtility.END_MINUTE, end_minute);
            PreferenceUtility.getInstance().saveData(context,
                    PreferenceUtility.USER_COMPANY, user_company);
            PreferenceUtility.getInstance().saveData(context,
                    PreferenceUtility.USER_POSITION, user_position);
            PreferenceUtility.getInstance().saveData(context,
                    PreferenceUtility.USER_IMAGES, user_images);
            PreferenceUtility.getInstance().saveData(context,
                    PreferenceUtility.USER_NAME, user_name);
            PreferenceUtility.getInstance().saveData(context,
                    PreferenceUtility.USER_ID, user_id);
        }

        public static void saveCheckInItemsFromJSON(Context context, String json) throws JSONException {
            JSONObject jsonObject = new JSONObject(json);


            if (PreferenceUtility.getInstance().loadDataString(context,
                    PreferenceUtility.URL).contains("domaintest2")) {
                if (jsonObject.getJSONObject("datas").has("user_id")) {
                    PreferenceUtility.getInstance().saveData(context,
                            PreferenceUtility.USER_ID,
                            jsonObject.getJSONObject("datas").getString("user_id"));
                }
            }
        }

        public static List<Category> getCategoryListFromJSON(Context context, String json) throws JSONException {
            List<Category> categoryList = new ArrayList<>();
            CategoryDao categoryDao = new CategoryDao(new DBHelper(context), true);
            ProductDao productDao = new ProductDao(new DBHelper(context), true);

            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONObject("datas").getJSONArray("category");
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    Category category = new Category();

                    category.setId(Integer.valueOf(jsonArray.getJSONObject(i).getString("id")));
                    category.setBranch(Integer.valueOf(jsonArray.getJSONObject(i).getString("branch")));
                    category.setName(jsonArray.getJSONObject(i).getString("name"));
                    category.setDescription(jsonArray.getJSONObject(i).getString("description"));
                    category.setStatus(jsonArray.getJSONObject(i).getString("status"));
                    category.setAccount_type(jsonArray.getJSONObject(i).getInt("account_type_id"));

                    List<Product> productList = new ArrayList<>();
                    JSONArray jsonArray1 = jsonArray.getJSONObject(i).getJSONArray("product");
                    if (jsonArray1.length() > 0) {
                        for (int j = 0; j < jsonArray1.length(); j++) {
                            Product product = new Product();

                            product.setId(Integer.valueOf(jsonArray1.getJSONObject(j).getString("id")));
                            product.setBranch_id(Integer.valueOf(jsonArray1.getJSONObject(j).getString("branch")));
                            product.setCategory_id(Integer.valueOf(jsonArray1.getJSONObject(j).getString("category_id")));
                            product.setName(jsonArray1.getJSONObject(j).getString("name"));
                            product.setProduct_code(jsonArray1.getJSONObject(j).getString("product_code"));
                            product.setDescription(jsonArray1.getJSONObject(j).getString("description"));
                            product.setStatus(jsonArray1.getJSONObject(j).getString("status"));
                            product.setCreate_date(jsonArray1.getJSONObject(j).getString("create_date"));
                            product.setUpdate_date(jsonArray1.getJSONObject(j).getString("update_date"));

                            productList.add(product);
                            productDao.insertTable(product);
                        }
                        category.setProduct(productList);
                    }
                    categoryList.add(category);
                    categoryDao.insertTable(category);
                }
            }

            return categoryList;
        }

        public static List<Customer> getCustomerListFromJSON(Context context, String json) throws JSONException {
            List<Customer> customerList = new ArrayList<>();
            CustomerDao customerDao = new CustomerDao(new DBHelper(context), true);

            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONObject("datas").getJSONArray("accounts");
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {

                    Customer customer = new Customer();
                    customer.setCustomer_id(Integer.valueOf(jsonArray.getJSONObject(i).getString("id")));
                    customer.setCustomer_name(jsonArray.getJSONObject(i).getString("name"));
                    customer.setCustomer_address(jsonArray.getJSONObject(i).getString("address"));
                    customer.setCustomer_type(jsonArray.getJSONObject(i).getInt("type_id"));
                    customer.setCustomer_code(jsonArray.getJSONObject(i).getString("account_code"));
                    customer.setCustomer_tipe(jsonArray.getJSONObject(i).getString("type"));
                    customerDao.insertTable(customer);
                    customerList.add(customer);
                }
            }

            return customerList;
        }

        public static JSONArray convertProductListToJSONArray(List<Integer> idList, List<Integer> categoryIdList, List<EditText> editTextList) throws JSONException {
            JSONArray jsonArray = new JSONArray();

            for (int i = 0; i < idList.size(); i++) {
                Log.d("TAG", "description: " + editTextList.get(i));

                JSONObject productObject = new JSONObject();

                productObject.put("product_id", String.valueOf(idList.get(i)));
                productObject.put("category_id", categoryIdList.get(i));
                productObject.put("description", editTextList.get(i).getText().toString());

                jsonArray.put(productObject);
            }
            Log.d("TAG", "saveProductdata: " + jsonArray.toString());

            return jsonArray;
        }

        public static void convertJSONArrayToProductList(Context context, List<Integer> idList, List<Integer> categoryIdList, List<EditText> editTextList, String product) throws JSONException {
            JSONArray jsonArray = new JSONArray(product);
            Log.d("TAG", "string produk " + product);
            Log.d("TAG", "jsonArray " + jsonArray.toString());

            for (int i = 0; i < jsonArray.length(); i++) {
                idList.add(jsonArray.getJSONObject(i).getInt("product_id"));
                categoryIdList.add(jsonArray.getJSONObject(i).getInt("category_id"));
                EditText editText = new EditText(context);
                editText.setText(jsonArray.getJSONObject(i).getString("description"));
                editTextList.add(editText);
            }
        }

        public static String getTotalSalesFromKPIJSON(String json) throws JSONException {
            String totalSales = "";

            JSONObject jsonObject = new JSONObject(json);
            totalSales = jsonObject.getJSONObject("datas").getString("total_sales");

            return totalSales;
        }

        public static String getTargetSalesFromKPIJSON(String json) throws JSONException {
            String targetSales = "";

            JSONObject jsonObject = new JSONObject(json);
            targetSales = jsonObject.getJSONObject("datas").getString("target_sales");

            return targetSales;
        }

    }

    public static class DisplayUtility {

        public static int dpToPx(Context context, int dp) {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dp * scale + 0.5f);
        }

        public static float convertDpToPixel(Context context, float dp) {
            Resources resources = context.getResources();
            DisplayMetrics metrics = resources.getDisplayMetrics();
            float px = dp * (metrics.densityDpi / 160f);
            return px;
        }

    }

    public static class DateUtility {

        public static int getCurrentYear(){
            Calendar calendar = Calendar.getInstance();
            return calendar.get(Calendar.YEAR);
        }

        public static String getCurrentMonth(){
            Calendar calendar=Calendar.getInstance();
            SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
            return month_date.format(calendar.getTime());
        }

    }

    public static class DatabaseUtility {

        public static List<Customer> getCustomerListFromDatabase(Context context) {
            List<Customer> customerList = new ArrayList<>();
            CustomerDao customerDao = new CustomerDao(new DBHelper(context), false);

            customerList = customerDao.getCustomerListFromDb();

            return customerList;
        }

        public static int getTaskCountByID(Context context, int customerId) {
            int count = 0;
            TaskDao taskDao = new TaskDao(new DBHelper(context), false);
            count = taskDao.checkIfTaskIsExists(customerId);

            return count;
        }

        public static Task getTaskByID(Context context, int customerId) {
            Task task = new Task();
            TaskDao taskDao = new TaskDao(new DBHelper(context), false);
            task = taskDao.getByCustomerId(customerId);

            return task;
        }

        public static void saveTaskIntoDatabase(Context context, Task task) {
            TaskDao taskDao = new TaskDao(new DBHelper(context), true);
            taskDao.insertTable(task);
        }

    }

    public static class ImageUtility {

        public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
            ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
            image.compress(compressFormat, quality, byteArrayOS);
            return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
        }

        public static Bitmap getThumbnail(String path) throws FileNotFoundException, IOException {
            File imgFile = new File(path);
            long sizeInBytes = imgFile.length() / (1024 * 1024);
            Log.d("TAG", "size image file " + sizeInBytes);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            if (sizeInBytes < 1)
                options.inSampleSize = 1;
            else if (sizeInBytes > 1 && sizeInBytes < 2)
                options.inSampleSize = 2;
            else if (sizeInBytes > 2 && sizeInBytes < 3)
                options.inSampleSize = 3;
            else if (sizeInBytes > 3 && sizeInBytes < 4)
                options.inSampleSize = 4;
            else if (sizeInBytes > 4 && sizeInBytes < 5)
                options.inSampleSize = 5;
            else if (sizeInBytes > 5 && sizeInBytes < 6)
                options.inSampleSize = 6;
            else if (sizeInBytes > 6 && sizeInBytes < 7)
                options.inSampleSize = 7;
            else if (sizeInBytes < 7 && sizeInBytes < 8)
                options.inSampleSize = 8;
            options.inJustDecodeBounds = false;
            options.inDither = true;
            options.inPurgeable = true;
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
            return bitmap;
        }

    }

    public static class LocationUtility {

        public static List<Address> getAddress(Context context, String latitude, String longitude) throws IOException {
            List<Address> addressList = new ArrayList<>();

            Geocoder geocoder = new Geocoder(context);
            addressList = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 1);

            return addressList;
        }

    }

    public static boolean getProductChecked(int productId, List<Integer> idList) {
        boolean checked = false;

        if (idList.size() > 0) {
            for (int i = 0; i < idList.size(); i++) {
                if (productId == idList.get(i))
                    checked = true;
            }
        }

        return checked;
    }

    public static String getProductDescription(int productId, List<Integer> idList, List<EditText> editTextList) {
        String description = "";

        if (idList.size() > 0) {
            for (int i = 0; i < idList.size(); i++) {
                if (productId == idList.get(i)) {
                    Log.d("TAG", "productId " + productId);
                    Log.d("TAG", "idList " + idList.get(i));
                    Log.d("TAG", "description " + editTextList.get(i));
                    description = editTextList.get(i).getText().toString();
                }
            }
        }

        return description;
    }

}
