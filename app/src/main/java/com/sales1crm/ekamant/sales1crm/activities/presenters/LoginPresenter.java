package com.sales1crm.ekamant.sales1crm.activities.presenters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.sales1crm.ekamant.sales1crm.activities.callbacks.LoginCallback;
import com.sales1crm.ekamant.sales1crm.activities.services.NetworkConnection;
import com.sales1crm.ekamant.sales1crm.activities.utilities.ApiParam;
import com.sales1crm.ekamant.sales1crm.activities.utilities.PreferenceUtility;
import com.sales1crm.ekamant.sales1crm.activities.utilities.Smart1CrmUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by apridosandyasa on 4/11/17.
 */

public class LoginPresenter {

    private final String TAG = LoginPresenter.class.getSimpleName();
    private Context context;
    private Messenger messenger;
    private Message message;
    private Bundle bundle;
    private Handler handler;
    private String failureResponse = "";
    private String[] stringResponse = {""};
    private LoginCallback loginCallback;
    private String resultLogin = "";

    public LoginPresenter(Context context, LoginCallback listener) {
        this.context = context;
        this.loginCallback = listener;
    }

    private void doNetworkService(String url, String params) {
        Log.d(TAG, "url login " + url);
        Intent networkIntent = new Intent(this.context, NetworkConnection.class);
        this.messenger = new Messenger(this.handler);
        networkIntent.putExtra("messenger", this.messenger);
        networkIntent.putExtra("url", url);
        networkIntent.putExtra("params", params);
        networkIntent.putExtra("from", "login");
        this.context.startService(networkIntent);
    }

    public void setupLoginToServer(int apiIndex, String passCode) {
        if (Smart1CrmUtils.ConnectionUtility.isNetworkConnected(this.context)) {
            obtainLoginItems(apiIndex, passCode);
        }
    }

    public void obtainLoginItems(int apiIndex, String passCode) {
        this.handler = new Handler(this.context.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                parseLoginItemsResponse(msg);
            }

        };

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("passcode", passCode);
            jsonObject.put("device_token", PreferenceUtility.getInstance().loadDataString(this.context, PreferenceUtility.PREFERENCES_PROPERTY_REG_ID));
            jsonObject.put("device_type", "2");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        doNetworkService(ApiParam.urlBuilder(this.context, apiIndex), jsonObject.toString());
    }

    private void parseLoginItemsResponse(Message message) {
        this.message = message;
        this.bundle = this.message.getData();
        this.stringResponse[0] = this.bundle.getString("network_response");
        Log.d(TAG, "responseString[0] login info " + this.stringResponse[0]);
        try {
            this.resultLogin = Smart1CrmUtils.JSONUtility.getResultFromServer(this.stringResponse[0]);
            this.loginCallback.finishedLogin(this.resultLogin, this.stringResponse[0]);
        } catch (JSONException e) {
            Log.d(TAG, "Exception detail Response " + e.getMessage());
        }
    }

}
