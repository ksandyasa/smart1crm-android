package com.sales1crm.ekamant.sales1crm.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.sales1crm.ekamant.sales1crm.R;
import com.sales1crm.ekamant.sales1crm.activities.callbacks.SplashCallback;
import com.sales1crm.ekamant.sales1crm.activities.firebases.FirebaseNotification;
import com.sales1crm.ekamant.sales1crm.activities.presenters.LoginPresenter;
import com.sales1crm.ekamant.sales1crm.activities.presenters.SplashPresenter;
import com.sales1crm.ekamant.sales1crm.activities.utilities.ApiParam;
import com.sales1crm.ekamant.sales1crm.activities.utilities.PreferenceUtility;
import com.sales1crm.ekamant.sales1crm.activities.utilities.Smart1CrmUtils;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

/**
 * Created by apridosandyasa on 4/11/17.
 */

public class SplashActivity extends BaseActivity implements SplashCallback {

    private final String TAG = SplashActivity.class.getSimpleName();
    private SplashPresenter splashPresenter;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private String apiKey = "", rawPasscode = "", toDecode = "";
    private int checkIn = 1;
    private String mAction = null;
    private Intent mIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Smart1CrmUtils.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Smart1CrmUtils.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Smart1CrmUtils.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                }
            }
        };

        this.splashPresenter = new SplashPresenter(SplashActivity.this, this);

        getDataFromURL();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Smart1CrmUtils.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Smart1CrmUtils.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        FirebaseNotification.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setupCheckIn() {
        showLoadingDialog();
        this.splashPresenter.runCheckIn(ApiParam.API_003);
    }

    private void displayFirebaseRegId() {
        Log.d(TAG, "Firebase reg id: "
                + PreferenceUtility.getInstance().loadDataString(
                SplashActivity.this, PreferenceUtility.getInstance().PREFERENCES_DEVICE_TOKEN));
    }

    private void getDataFromURL() {
        try {
            mIntent = Intent.parseUri(PreferenceUtility.getInstance().loadDataString(SplashActivity.this, PreferenceUtility.URL_INTENT_DATA), 0);
            mAction = mIntent.getAction();
            if (mIntent.getData() != null) {
                if (Intent.ACTION_VIEW.equals(mAction)) {
                    this.toDecode = mIntent.getData().getEncodedQuery();
                    Log.d(TAG, "toDecode " + toDecode);
                    if (this.toDecode == null) {
                        Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        SplashActivity.this.finish();
                    }else {
                        String[] split = toDecode.split("=");
                        toDecode = split[1];
                        PreferenceUtility.getInstance().saveData(SplashActivity.this,
                                PreferenceUtility.ENCODED_PASSCODE, toDecode);
                        Log.i(TAG, "todecode : " + toDecode);

                        byte[] data = Base64.decode(toDecode, Base64.NO_PADDING);
                        try {
                            rawPasscode = new String(data, "UTF-8");
                            Log.i(TAG, "decoded text : " + rawPasscode);
                        } catch (UnsupportedEncodingException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        if (rawPasscode.contains("http://")) {
                            String[] ProcessingPasscode = rawPasscode.split("/");
                            for (int i = 0; i < ProcessingPasscode.length; i++) {
                                Log.i(TAG, "split : " + ProcessingPasscode[i]);
                            }
                            String url = "http://"
                                    + ProcessingPasscode[2].toString();
                            Log.i(TAG, "url : " + url);
                            PreferenceUtility.getInstance().saveData(SplashActivity.this,
                                    PreferenceUtility.URL, url);
                        }

                        setupCheckIn();
                    }
                }
            }else{
                Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                SplashActivity.this.finish();
            }
            Log.i(TAG, "onrestart : " + mAction + mIntent);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finishedSetupCheckIn(String result, String json) {
        dismissLoadingDialog();
        if (result.equals("OK")) {
            try {
                this.checkIn = Smart1CrmUtils.JSONUtility.getStatusCheckIn(json);
                PreferenceUtility.getInstance().saveData(SplashActivity.this, PreferenceUtility.CHECKIN, checkIn);

                if (this.checkIn == 1) {
                    // there is api_key, but not check in yet, go to check in, skip
                    // tutor and login
                    Intent i = new Intent(SplashActivity.this,
                            CheckInActivity.class);
                    i.putExtra("is_checkout", false);
                    startActivity(i);
                } else if (this.checkIn == 2) {
                    // already check in, have apikey so get to work, go to dashboard
                    Intent i = new Intent(SplashActivity.this,
                            MainActivity.class);
                    startActivity(i);
                } else if (this.checkIn == 0) {
                    Intent i = new Intent(SplashActivity.this,
                            CheckInActivity.class);
                    i.putExtra("is_checkout", true);
                    startActivity(i);
                }
                // close this activity
                SplashActivity.this.finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {
            Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            SplashActivity.this.finish();
        }
    }
}
