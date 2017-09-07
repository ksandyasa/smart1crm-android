package com.sales1crm.ekamant.sales1crm.activities.presenters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.sales1crm.ekamant.sales1crm.activities.callbacks.SignatureCallback;
import com.sales1crm.ekamant.sales1crm.activities.callbacks.TaskAddCallback;
import com.sales1crm.ekamant.sales1crm.activities.databases.DBHelper;
import com.sales1crm.ekamant.sales1crm.activities.databases.TaskDao;
import com.sales1crm.ekamant.sales1crm.activities.models.Task;
import com.sales1crm.ekamant.sales1crm.activities.services.NetworkConnection;
import com.sales1crm.ekamant.sales1crm.activities.utilities.ApiParam;
import com.sales1crm.ekamant.sales1crm.activities.utilities.PreferenceUtility;
import com.sales1crm.ekamant.sales1crm.activities.utilities.Smart1CrmUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by apridosandyasa on 4/13/17.
 */

public class SignaturePresenter {

    private final String TAG = SignaturePresenter.class.getSimpleName();
    private Context context;
    private Messenger messenger;
    private Message message;
    private Bundle bundle;
    private Handler handler;
    private String failureResponse = "";
    private String[] stringResponse = {""};
    private SignatureCallback signatureCallback;
    private String resultSignature = "";

    public SignaturePresenter(Context context, SignatureCallback listener) {
        this.context = context;
        this.signatureCallback = listener;
    }

    private void doNetworkService(String url, String params, String from) {
        Log.d(TAG, "url login " + url);
        Intent networkIntent = new Intent(this.context, NetworkConnection.class);
        this.messenger = new Messenger(this.handler);
        networkIntent.putExtra("messenger", this.messenger);
        networkIntent.putExtra("url", url);
        networkIntent.putExtra("params", params);
        networkIntent.putExtra("from", from);
        this.context.startService(networkIntent);
    }

    public void setupSendTaskData(int apiIndex, Task task) {
        if (Smart1CrmUtils.ConnectionUtility.isNetworkConnected(this.context)) {
            obtainSendTaskDataItems(apiIndex, task);
        }else {
            this.signatureCallback.finishedSetupSendTaskDataOffine();
        }
    }

    private void obtainSendTaskDataItems(int apiIndex, Task task) {
        this.handler = new Handler(this.context.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                parseSendTaskDataItemsResponse(msg);
            }

        };

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("api_key", PreferenceUtility.getInstance().loadDataString(this.context, PreferenceUtility.API_KEY));
            jsonObject.put("account_id", PreferenceUtility.getInstance().loadDataString(this.context, PreferenceUtility.CUSTOMER_ID));
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
            e.printStackTrace();
        }

        doNetworkService(ApiParam.urlBuilder(this.context, apiIndex), jsonObject.toString(), "sendTaskData");
    }

    private void parseSendTaskDataItemsResponse(Message message) {
        this.message = message;
        this.bundle = this.message.getData();
        this.failureResponse = this.bundle.getString("network_failure");
        if (this.failureResponse.equals("yes")) {
            this.signatureCallback.failureSetupSendTaskData();
        }else{
            this.stringResponse[0] = this.bundle.getString("network_response");
            Log.d(TAG, "responseString[0] category info " + this.stringResponse[0]);
            try {
                this.resultSignature = Smart1CrmUtils.JSONUtility.getResultFromServer(this.stringResponse[0]);
                this.signatureCallback.finishedSetupSendTaskData(this.resultSignature);
            } catch (JSONException e) {
                Log.d(TAG, "Exception category Response " + e.getMessage());
            }
        }
    }

}
