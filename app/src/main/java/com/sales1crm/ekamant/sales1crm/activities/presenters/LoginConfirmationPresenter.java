package com.sales1crm.ekamant.sales1crm.activities.presenters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.sales1crm.ekamant.sales1crm.activities.callbacks.LoginCallback;
import com.sales1crm.ekamant.sales1crm.activities.callbacks.LoginConfirmationCallback;
import com.sales1crm.ekamant.sales1crm.activities.services.NetworkConnection;
import com.sales1crm.ekamant.sales1crm.activities.utilities.ApiParam;
import com.sales1crm.ekamant.sales1crm.activities.utilities.PreferenceUtility;
import com.sales1crm.ekamant.sales1crm.activities.utilities.Smart1CrmUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by apridosandyasa on 4/11/17.
 */

public class LoginConfirmationPresenter {

    private final String TAG = LoginConfirmationPresenter.class.getSimpleName();
    private Context context;
    private Messenger messenger;
    private Message message;
    private Bundle bundle;
    private Handler handler;
    private String failureResponse = "";
    private String[] stringResponse = {""};
    private LoginConfirmationCallback loginConfirmationCallback;
    private String resultLoginConf = "";
    private int statusLoginConf = -1;

    public LoginConfirmationPresenter(Context context, LoginConfirmationCallback listener) {
        this.context = context;
        this.loginConfirmationCallback = listener;
    }

    private void doNetworkService(String url, String params) {
        Log.d(TAG, "url login " + url);
        Intent networkIntent = new Intent(this.context, NetworkConnection.class);
        this.messenger = new Messenger(this.handler);
        networkIntent.putExtra("messenger", this.messenger);
        networkIntent.putExtra("url", url);
        networkIntent.putExtra("params", params);
        networkIntent.putExtra("from", "login_conf");
        this.context.startService(networkIntent);
    }

    public void runCheckInFromLoginConfirmation(int apiIndex) {
        if (Smart1CrmUtils.ConnectionUtility.isNetworkConnected(this.context)) {
            obtainCheckInItems(apiIndex);
        }
    }

    private void obtainCheckInItems(int apiIndex) {
        this.handler = new Handler(this.context.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                parseCheckInItemsResponse(msg);
            }

        };

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("api_key", PreferenceUtility.getInstance().loadDataString(this.context, PreferenceUtility.API_KEY));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        doNetworkService(ApiParam.urlBuilder(this.context, apiIndex), jsonObject.toString());
    }

    private void parseCheckInItemsResponse(Message message) {
        this.message = message;
        this.bundle = this.message.getData();
        this.stringResponse[0] = this.bundle.getString("network_response");
        Log.d(TAG, "responseString[0] login info " + this.stringResponse[0]);
        try {
            this.resultLoginConf = Smart1CrmUtils.JSONUtility.getResultFromServer(this.stringResponse[0]);
            this.statusLoginConf = Smart1CrmUtils.JSONUtility.getStatusCheckIn(this.stringResponse[0]);
            this.loginConfirmationCallback.finishedConfirmCheckIn(this.resultLoginConf, this.statusLoginConf);
        } catch (JSONException e) {
            Log.d(TAG, "Exception detail Response " + e.getMessage());
        }
    }

}
