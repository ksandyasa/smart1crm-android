package com.sales1crm.ekamant.sales1crm.activities.presenters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.sales1crm.ekamant.sales1crm.activities.callbacks.AccountListDialogPresenterCallback;
import com.sales1crm.ekamant.sales1crm.activities.callbacks.TaskAddCallback;
import com.sales1crm.ekamant.sales1crm.activities.databases.CustomerDao;
import com.sales1crm.ekamant.sales1crm.activities.models.Customer;
import com.sales1crm.ekamant.sales1crm.activities.services.NetworkConnection;
import com.sales1crm.ekamant.sales1crm.activities.utilities.ApiParam;
import com.sales1crm.ekamant.sales1crm.activities.utilities.PreferenceUtility;
import com.sales1crm.ekamant.sales1crm.activities.utilities.Smart1CrmUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apridosandyasa on 4/13/17.
 */

public class AccountListDialogPresenter {

    private final String TAG = AccountListDialogPresenter.class.getSimpleName();
    private Context context;
    private Messenger messenger;
    private Message message;
    private Bundle bundle;
    private Handler handler;
    private String failureResponse = "";
    private String[] stringResponse = {""};
    private AccountListDialogPresenterCallback callback;
    private String resultTaskAdd = "";
    private List<Customer> customerList = new ArrayList<>();

    public AccountListDialogPresenter(Context context, AccountListDialogPresenterCallback listener) {
        this.context = context;
        this.callback = listener;
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

    public void setupAccountListViews(int apiIndex, int offset, String word) {
        if (Smart1CrmUtils.ConnectionUtility.isNetworkConnected(this.context)) {
            obtainAccountListItems(apiIndex, offset, word);
        }else{
            obtainAccountListFromDB();
        }
    }

    private void obtainAccountListItems(int apiIndex, int offset, String word) {
        this.handler = new Handler(this.context.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                parseAccountListItemsResponse(msg);
            }

        };

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("api_key", PreferenceUtility.getInstance().loadDataString(this.context, PreferenceUtility.API_KEY));
            jsonObject.put("limit", "20");
            jsonObject.put("offset", "" + offset);
            jsonObject.put("search", word);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        doNetworkService(ApiParam.urlBuilder(this.context, apiIndex), jsonObject.toString(), "accountListDialog");
    }

    private void parseAccountListItemsResponse(Message message) {
        this.message = message;
        this.bundle = this.message.getData();
        this.stringResponse[0] = this.bundle.getString("network_response");
        Log.d(TAG, "responseString[0] category info " + this.stringResponse[0]);
        try {
            this.resultTaskAdd = Smart1CrmUtils.JSONUtility.getResultFromServer(this.stringResponse[0]);
            this.customerList = Smart1CrmUtils.JSONUtility.getCustomerListFromJSON(this.context, this.stringResponse[0]);
            this.callback.finishedSetupAccountListViews(this.resultTaskAdd, this.customerList);
        } catch (JSONException e) {
            Log.d(TAG, "Exception category Response " + e.getMessage());
        }
    }

    public void setupMoreAccountListViews(int apiIndex, int offset, String word) {
        if (Smart1CrmUtils.ConnectionUtility.isNetworkConnected(this.context)) {
            obtainMoreAccountListItems(apiIndex, offset, word);
        }else {
            obtainMoreAccountListFromDB();
        }
    }

    private void obtainMoreAccountListItems(int apiIndex, int offset, String word) {
        this.handler = new Handler(this.context.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                parseMoreAccountListItemsResponse(msg);
            }

        };

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("api_key", PreferenceUtility.getInstance().loadDataString(this.context, PreferenceUtility.API_KEY));
            jsonObject.put("limit", "20");
            jsonObject.put("offset", "" + offset);
            jsonObject.put("search", word);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        doNetworkService(ApiParam.urlBuilder(this.context, apiIndex), jsonObject.toString(), "accountListDialog");
    }

    private void parseMoreAccountListItemsResponse(Message message) {
        this.message = message;
        this.bundle = this.message.getData();
        this.stringResponse[0] = this.bundle.getString("network_response");
        Log.d(TAG, "responseString[0] category info " + this.stringResponse[0]);
        try {
            this.resultTaskAdd = Smart1CrmUtils.JSONUtility.getResultFromServer(this.stringResponse[0]);
            this.customerList = Smart1CrmUtils.JSONUtility.getCustomerListFromJSON(this.context, this.stringResponse[0]);
            this.callback.finishedSetupMoreAccountListViews(this.resultTaskAdd, this.customerList);
        } catch (JSONException e) {
            Log.d(TAG, "Exception category Response " + e.getMessage());
        }
    }

    private void obtainAccountListFromDB() {
        this.customerList = Smart1CrmUtils.DatabaseUtility.getCustomerListFromDatabase(this.context);
        this.callback.finishedSetupAccountListViewsOffline(this.customerList);
    }

    private void obtainMoreAccountListFromDB() {
        this.customerList = Smart1CrmUtils.DatabaseUtility.getCustomerListFromDatabase(this.context);
        this.callback.finishedSetupMoreAccountListViewsOffline(this.customerList);
    }

}
