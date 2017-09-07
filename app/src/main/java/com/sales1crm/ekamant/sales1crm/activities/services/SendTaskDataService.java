package com.sales1crm.ekamant.sales1crm.activities.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import com.sales1crm.ekamant.sales1crm.activities.SignatureActivity;
import com.sales1crm.ekamant.sales1crm.activities.databases.DBHelper;
import com.sales1crm.ekamant.sales1crm.activities.databases.TaskDao;
import com.sales1crm.ekamant.sales1crm.activities.models.Task;
import com.sales1crm.ekamant.sales1crm.activities.utilities.ApiParam;
import com.sales1crm.ekamant.sales1crm.activities.utilities.PreferenceUtility;
import com.sales1crm.ekamant.sales1crm.activities.utilities.Smart1CrmUtils;
import com.sales1crm.ekamant.sales1crm.activities.utilities.WakeAlarm;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by apridosandyasa on 4/2/17.
 */

public class SendTaskDataService extends Service {

    public static final String TAG = SendTaskDataService.class.getSimpleName();

    private Context context;
    private List<Task> taskList = new ArrayList<>();
    private String apiKey, customerId;
    private TaskDao taskDao;

    private Messenger messenger;
    private Message message;
    private Bundle bundle;
    private Handler handler;
    private int taskIndex = 0;
    private String failureResponse = "";
    private String[] stringResponse = {""};

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        if (intent != null) {
            if (intent.getExtras().containsKey("send_taskdata")) {
                if (intent.getExtras().getBoolean("send_taskdata")) {
                    Log.i("AAA", "service JALAN");
                    customerId = intent.getExtras().getString("customerId");
                    Log.d(TAG, "customerId " + customerId);
                    taskDao = new TaskDao(new DBHelper(this));
                    taskList = taskDao.getTaskList();
//                    task = taskDao.getByCustomerId(Integer.valueOf(customerId));

                    apiKey = PreferenceUtility.getInstance().loadDataString(this,
                            PreferenceUtility.API_KEY);

                    Log.d(TAG, "task: " + taskList.get(0).getCustomer_id() + " " + taskList.get(0).getCustomer_name() + " " + taskList.get(0)
                            .getNotes() + "| " + taskList.get(0).getFoto1() + "\n" + taskList.get(0).getFoto2() + "\n" +
                            taskList.get(0).getFoto3());

                    Log.d(TAG, "task: " + taskList.get(0).getLatitude() + " " + taskList.get(0).getLongitude() + " " + taskList.get(0)
                            .getOrder_status() + "| " + taskList.get(0).getCreated_at() + "\n" + taskList.get(0).getSignature() + "\n" +
                            taskList.get(0).getProduct());

                    if (Smart1CrmUtils.ConnectionUtility.isNetworkConnected(this)) {
                        postSendDataTask(apiKey, taskList.get(taskIndex), taskIndex, taskList.size());
                    }
                }
            }
        }
        // boolean send_location =
        // intent.getExtras().getBoolean("send_location");
        // Log.i("AAA", "service SENDLOCATION : "+send_location);
        Log.i("AAA", "service SENDTASKDATA");
        return START_STICKY;
    }

    private void doNetworkService(String url, String params) {
        Log.d(TAG, "url login " + url);
        Intent networkIntent = new Intent(getApplicationContext(), NetworkConnection.class);
        this.messenger = new Messenger(this.handler);
        networkIntent.putExtra("messenger", this.messenger);
        networkIntent.putExtra("url", url);
        networkIntent.putExtra("params", params);
        networkIntent.putExtra("from", "sendTaskDataOffline");
        startService(networkIntent);
    }

    private void postSendDataTask(final String api_key, Task task, final int taskIndex, final int taskListSize) {
        this.handler = new Handler(getApplicationContext().getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                parseSendDataTaskResponse(msg, taskIndex, taskListSize);
            }

        };

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("api_key", api_key);
            jsonObject.put("account_id", task.getCustomer_id());
            jsonObject.put("latitude", task.getLatitude());
            jsonObject.put("longitude", task.getLongitude());
            jsonObject.put("notes", task.getNotes());

            if (!task.getFoto1().equalsIgnoreCase("")) {
                jsonObject.put("nama1", "photo1_" + ".jpg");
            }
            if (!task.getFoto2().equalsIgnoreCase("")) {
                jsonObject.put("nama2", "photo2_" + ".jpg");
            }
            if (!task.getFoto3().equalsIgnoreCase("")) {
                jsonObject.put("nama3", "photo3_" + ".jpg");
            }
            jsonObject.put("nama4", "sign_" + ".jpg");
            jsonObject.put("order_status", task.getOrder_status());
            jsonObject.put("status_id", "1");
            jsonObject.put("product", task.getProduct());
            jsonObject.put("created_at", task.getCreated_at());
        } catch (JSONException e) {
            Log.d(TAG, "exception " + e.getMessage());
        }

        doNetworkService(ApiParam.urlBuilder(this, ApiParam.API_035), jsonObject.toString());
    }

    private void parseSendDataTaskResponse(Message message, int taskIndex, int taskListSize) {
        this.message = message;
        this.bundle = this.message.getData();
        this.failureResponse = this.bundle.getString("network_failure");
        if (this.failureResponse.equals("yes")) {
            WakeAlarm wakeAlarm = new WakeAlarm();
            wakeAlarm.stopSendTaskData(this);
            stopSelf();
            Toast.makeText(this, "Laporan akan terkirim jika sinyal bagus", Toast.LENGTH_SHORT).show();
        }else{
            if (taskIndex < taskListSize) {
                taskIndex++;
                postSendDataTask(apiKey, taskList.get(taskIndex), taskIndex, taskList.size());
            }else {
                this.stringResponse[0] = this.bundle.getString("network_response");
                Log.d(TAG, "responseString[0] send data task info " + this.stringResponse[0]);
                taskDao = new TaskDao(new DBHelper(this), true);
                taskDao.deleteAllRecord();
                PreferenceUtility.getInstance().saveData(this, PreferenceUtility.CUSTOMER_ID, "");
                PreferenceUtility.getInstance().saveData(this, PreferenceUtility.CUSTOMER_NAME, "");
                PreferenceUtility.getInstance().saveData(this, PreferenceUtility.NOTES, "");
                PreferenceUtility.getInstance().saveData(this, PreferenceUtility.ORDER_STATUS, "");
                PreferenceUtility.getInstance().saveData(this, PreferenceUtility.LATITUDE, "");
                PreferenceUtility.getInstance().saveData(this, PreferenceUtility.LONGITUDE, "");
                PreferenceUtility.getInstance().saveData(this, PreferenceUtility.PHOTO_PATH1, "");
                PreferenceUtility.getInstance().saveData(this, PreferenceUtility.PHOTO_PATH2, "");
                PreferenceUtility.getInstance().saveData(this, PreferenceUtility.PHOTO_PATH3, "");
                PreferenceUtility.getInstance().saveData(this, PreferenceUtility.CREATED_AT, "");
                PreferenceUtility.getInstance().saveData(this, PreferenceUtility.JSON_PRODUCT, "");
                WakeAlarm wakeAlarm = new WakeAlarm();
                wakeAlarm.stopSendTaskData(this);
                stopSelf();
            }
        }
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.i("AAA", "oncreate service SENDTASKDATA");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onDestroy() {
        Log.i("AAA", "ondestroy service SENDTASKDATA");
        super.onDestroy();
    }
}
