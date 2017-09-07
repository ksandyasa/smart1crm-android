package com.sales1crm.ekamant.sales1crm.activities.presenters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.sales1crm.ekamant.sales1crm.activities.callbacks.KPICallback;
import com.sales1crm.ekamant.sales1crm.activities.callbacks.TaskAddCallback;
import com.sales1crm.ekamant.sales1crm.activities.services.NetworkConnection;
import com.sales1crm.ekamant.sales1crm.activities.utilities.ApiParam;
import com.sales1crm.ekamant.sales1crm.activities.utilities.PreferenceUtility;
import com.sales1crm.ekamant.sales1crm.activities.utilities.Smart1CrmUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by apridosandyasa on 4/13/17.
 */

public class KPIPresenter {

    private final String TAG = KPIPresenter.class.getSimpleName();
    private Context context;
    private Messenger messenger;
    private Message message;
    private Bundle bundle;
    private Handler handler;
    private String failureResponse = "";
    private String[] stringResponse = {""};
    private KPICallback kpiCallback;
    private String resultKPI = "";
    private String totalSales = "";
    private String targetSales = "";

    public KPIPresenter(Context context, KPICallback listener) {
        this.context = context;
        this.kpiCallback = listener;
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

    public void setupKPIViews(int apiIndex, String month, String year) {
        if (Smart1CrmUtils.ConnectionUtility.isNetworkConnected(this.context)) {
            obtainKPIItems(apiIndex, month, year);
        }
    }

    private void obtainKPIItems(int apiIndex, String month, String year) {
        this.handler = new Handler(this.context.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                parseKPIItemsResponse(msg);
            }

        };

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("api_key", PreferenceUtility.getInstance().loadDataString(this.context, PreferenceUtility.API_KEY));
            jsonObject.put("month", month);
            jsonObject.put("year", year);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        doNetworkService(ApiParam.urlBuilder(this.context, apiIndex), jsonObject.toString(), "kpi");
    }

    private void parseKPIItemsResponse(Message message) {
        this.message = message;
        this.bundle = this.message.getData();
        this.stringResponse[0] = this.bundle.getString("network_response");
        Log.d(TAG, "responseString[0] category info " + this.stringResponse[0]);
        try {
            this.resultKPI = Smart1CrmUtils.JSONUtility.getResultFromServer(this.stringResponse[0]);
            this.totalSales = Smart1CrmUtils.JSONUtility.getTotalSalesFromKPIJSON(this.stringResponse[0]);
            this.targetSales = Smart1CrmUtils.JSONUtility.getTargetSalesFromKPIJSON(this.stringResponse[0]);
            this.kpiCallback.finishedSetupKPIViews(this.resultKPI, this.totalSales, this.targetSales);
        } catch (JSONException e) {
            Log.d(TAG, "Exception category Response " + e.getMessage());
        }
    }

}
