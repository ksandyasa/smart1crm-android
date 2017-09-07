package com.sales1crm.ekamant.sales1crm.activities.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.sales1crm.ekamant.sales1crm.activities.utilities.ApiParam;
import com.sales1crm.ekamant.sales1crm.activities.utilities.PreferenceUtility;
import com.sales1crm.ekamant.sales1crm.activities.utilities.Smart1CrmUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationSendService extends Service {

    public static final String TAG = LocationSendService.class.getSimpleName();

    private Context context;
    private String stringLatitude, stringLongitude, date;
    private int year, month, day, hour, minute, second;

    private String apiKey;
    private Messenger messenger;
    private Message message;
    private Bundle bundle;
    private Handler handler;
    private String failureResponse = "";
    private String[] stringResponse = {""};

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        if (intent != null) {
            if (intent.getExtras().containsKey("send_location")) {
                if (intent.getExtras().getBoolean("send_location")) {
                    Log.i("AAA", "service JALAN");
                    GpsTracker gpsTracker = new GpsTracker(this);
                    if (gpsTracker.canGetLocation()) {
                        final Calendar c = Calendar.getInstance();
                        year = c.get(Calendar.YEAR);
                        month = c.get(Calendar.MONTH) + 1;
                        day = c.get(Calendar.DAY_OF_MONTH);
                        hour = c.get(Calendar.HOUR_OF_DAY);
                        minute = c.get(Calendar.MINUTE);
                        second = c.get(Calendar.SECOND);
                        date = year + "-" + month + "-" + day + " " + hour
                                + ":" + minute + ":" + second;

//                        stringLatitude = "-6.316540";//String.valueOf(gpsTracker.latitude);
                        stringLatitude = "-6.227029";//String.valueOf(gpsTracker.latitude);
//                        stringLatitude = String.valueOf(gpsTracker.latitude);

//                        stringLongitude = "106.961701";//String.valueOf(gpsTracker.longitude);
                        stringLongitude = "106.852706";//String.valueOf(gpsTracker.longitude);
//                        stringLongitude = String.valueOf(gpsTracker.longitude);

                        try {
                            List<Address> addresses = Smart1CrmUtils.LocationUtility.getAddress(this, stringLatitude, stringLongitude);

                            StringBuilder stringBuilder = new StringBuilder();
                            if (addresses.size() > 0) {
                                Address returnAddress = addresses.get(0);
                                stringBuilder.append(returnAddress.getAddressLine(0) + ", " + returnAddress.getLocality() + " " + returnAddress.getAdminArea() +
                                        ", " + returnAddress.getCountryName() + " " + returnAddress.getPostalCode());
                            } else {
                                stringBuilder.append("-");
                            }
                            Log.d("AAA", stringBuilder.toString());

                            Log.i("TAG", "longitude : " + stringLongitude
                                    + ", latituded : " + stringLatitude
                                    + ", date : " + date
                                    + ", address : " + stringBuilder.toString());

                            apiKey = PreferenceUtility.getInstance().loadDataString(this,
                                    PreferenceUtility.API_KEY);

                            postLocation(apiKey, date, stringLatitude, stringLongitude, stringBuilder.toString());

                        } catch (IOException e) {
                            Log.d("AAA", "Exception " + e.getMessage());
                        }

                    } else {
                        // can't get location
                        // GPS or Network is not enabled
                        // Ask user to enable GPS/network in settings, not
                        // possible on service...
//						Toast.makeText(getApplicationContext(),
//								"Please turn on your GPS", Toast.LENGTH_SHORT)
//								.show();
                    }
                }
            }
        }
        // boolean send_location =
        // intent.getExtras().getBoolean("send_location");
        // Log.i("AAA", "service SENDLOCATION : "+send_location);
        Log.i("AAA", "service SENDLOCATION");
        return START_STICKY;
    }

    private void doNetworkService(String url, String params) {
        Log.d(TAG, "url login " + url);
        Intent networkIntent = new Intent(getApplicationContext(), NetworkConnection.class);
        this.messenger = new Messenger(this.handler);
        networkIntent.putExtra("messenger", this.messenger);
        networkIntent.putExtra("url", url);
        networkIntent.putExtra("params", params);
        networkIntent.putExtra("from", "locationservice");
        startService(networkIntent);
    }

    private void postLocation(final String api_key, final String date,
                              final String latitude, final String longitude, final String address) {

        this.handler = new Handler(getApplicationContext().getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                parseLocationResponse(msg);
            }

        };

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("api_key", api_key);
            jsonObject.put("latitude", latitude);
            jsonObject.put("longitude", longitude);
            jsonObject.put("date", date);
            jsonObject.put("address", address);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        doNetworkService(ApiParam.urlBuilder(getApplicationContext(), ApiParam.API_045), jsonObject.toString());
    }

    private void parseLocationResponse(Message message) {
        this.message = message;
        this.bundle = this.message.getData();
        this.stringResponse[0] = this.bundle.getString("network_response");
        Log.d(TAG, "responseString[0] login info " + this.stringResponse[0]);
    }


    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Log.i("AAA", "oncreate service SENDLOCATION");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onDestroy() {
        Log.i("AAA", "location service destroy");
        Smart1CrmUtils.service_running = false;
        super.onDestroy();
    }

}
