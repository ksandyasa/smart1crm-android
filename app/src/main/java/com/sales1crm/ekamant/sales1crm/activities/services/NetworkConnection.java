package com.sales1crm.ekamant.sales1crm.activities.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.StrictMode;
import android.util.Log;

import com.sales1crm.ekamant.sales1crm.activities.utilities.Smart1CrmUtils;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.ConnectionPool;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by apridosandyasa on 6/19/16.
 */
public class NetworkConnection extends IntentService {
    private final String TAG = NetworkConnection.class.getSimpleName();
    private String[] responseString = {""};
    private StringBuilder builder;
    private Messenger messenger;
    private Message message;
    private String url;
    private String params;
    private String from = "";
    private RequestBody requestBody;

    public NetworkConnection() {
        super("");
    }

    public void doObtainDataFromServer(String url) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

//        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(10, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(10, TimeUnit.SECONDS);

        try {
            if (from.equals("login"))
                this.requestBody = Smart1CrmUtils.PostParamUtility.getLoginPostParam(params);
            else if (from.equals("login_conf")) {
                this.requestBody = Smart1CrmUtils.PostParamUtility.getLoginConfirmationParam(params);
            }else if (from.equals("splash")) {
                this.requestBody = Smart1CrmUtils.PostParamUtility.getSplashPostParam(params);
            }else if (from.equals("checkin")) {
                this.requestBody = Smart1CrmUtils.PostParamUtility.getCheckinParam(params);
            }else if (from.equals("locationservice")) {
                this.requestBody = Smart1CrmUtils.PostParamUtility.getLocationParam(params);
            }else if (from.equals("locationservice")) {
                this.requestBody = Smart1CrmUtils.PostParamUtility.getLocationParam(params);
            }else if (from.equals("mainLogout")) {
                this.requestBody = Smart1CrmUtils.PostParamUtility.getLogoutParam(params);
            }else if (from.equals("taskAddCategory")) {
                this.requestBody = Smart1CrmUtils.PostParamUtility.getCategoryParam(params);
            }else if (from.equals("accountListDialog")) {
                this.requestBody = Smart1CrmUtils.PostParamUtility.getAccountListParam(params);
            }else if (from.equals("sendTaskData")) {
                this.requestBody = Smart1CrmUtils.PostParamUtility.getSendTaskDataParam(this, params);
            }else if (from.equals("kpi")) {
                this.requestBody = Smart1CrmUtils.PostParamUtility.getKPIParam(params);
            }else if (from.equals("sendTaskDataOffline")) {
                this.requestBody = Smart1CrmUtils.PostParamUtility.getSendTaskDataOfflineParam(this, params);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Content-Encoding", "application/gzip")
                .addHeader("Connection", "Keep-Alive")
                .addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.45 Safari/535.19")
                .post(this.requestBody)
                .build();
        Log.d(TAG, url);

        Call call = okHttpClient.newCall(request);
        call.enqueue(new NetworkConnectionCallback());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        this.messenger = (Messenger) intent.getParcelableExtra("messenger");
        this.url = intent.getStringExtra("url");
        this.params = intent.getStringExtra("params");
        this.from = intent.getStringExtra("from");
        this.message = Message.obtain();
        Log.d(TAG, "params " + params);
        doObtainDataFromServer(this.url);
    }

    private class NetworkConnectionCallback implements Callback {

        @Override
        public void onFailure(Request request, IOException e) {
            if (e.getMessage() != null) {
                Log.d(TAG, "failure " + e.getMessage());
            }
            onFailureInMainThread();
        }

        @Override
        public void onResponse(final Response response) throws IOException {
            try {
                if (response.isSuccessful()) {
                    onResponseInMainThread(response);
                }else {
                    onFailureInMainThread();
                }
            } catch (IOException e) {
                Log.d(TAG, "Exception " + e.getMessage());
            }
        }
    }

    public void onFailureInMainThread() {
        Log.d(TAG, "failure connection");
        Bundle bundle = new Bundle();
        bundle.putString("network_response", this.responseString[0]);
        bundle.putString("network_failure", "yes");
        this.message.setData(bundle);
        try {
            this.messenger.send(this.message);
        } catch (RemoteException e) {
            Log.d(TAG, "Exception" + e.getMessage());
        }
    }

    public void onResponseInMainThread(Response response) throws IOException {
        this.responseString[0] = response.body().string();
        Log.d(TAG, "responseString[0] " + this.responseString[0]);
        Log.d(TAG, "message.what" + this.message.what);
        Bundle bundle = new Bundle();
        bundle.putString("network_response", this.responseString[0]);
        bundle.putString("network_failure", "no");
        this.message.setData(bundle);
        try {
            this.messenger.send(this.message);
        } catch (RemoteException e) {
            Log.d(TAG, "Exception" + e.getMessage());
        }
    }

}
