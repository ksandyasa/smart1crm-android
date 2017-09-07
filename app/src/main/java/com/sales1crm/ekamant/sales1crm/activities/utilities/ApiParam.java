package com.sales1crm.ekamant.sales1crm.activities.utilities;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by apridosandyasa on 4/11/17.
 */

public class ApiParam {

    private final static String TAG = ApiParam.class.getSimpleName();

    public final static int API_OK = 1;
    public final static int API_NG = 2;

    /**
     * Login
     */
    public final static int API_001 = 1;
    /**
     * Logout
     */
    public final static int API_002 = 2;
    /**
     * Check for checkin
     */
    public final static int API_003 = 3;
    /**
     * Check in
     */
    public final static int API_004 = 4;
    /**
     * Check out
     */
    public final static int API_005 = 5;
    /**
     * Category & Product List
     */
    public final static int API_011 = 11;
    /**
     * Account List
     */
    public final static int API_012 = 12;
    /**
     * KPI User
     */
    public static final int API_013 = 13;
    /**
     * Submit Report
     */
    public final static int API_035 = 35;
    /**
     * Location send
     */
    public final static int API_045 = 45;

    public static String urlBuilder(Context context, int apiIndex) {
        String url = PreferenceUtility.getInstance().loadDataString(context,
                PreferenceUtility.URL);
        url += getApiUrl(apiIndex);

        return url;
    }

    public static String getApiUrl(int apiIndex) {
        String apiUrl = "";
        switch (apiIndex) {
            case API_001:
                apiUrl = "/api/user/login";
                break;
            case API_002:
                apiUrl = "/api/user/logout";
                break;
            case API_003:
                apiUrl = "/api/user/get-status-checkin";
                break;
            case API_004:
                apiUrl = "/api/user/checkin";
                break;
            case API_005:
                apiUrl = "/api/user/checkout";
                break;
            case API_011:
                apiUrl = "/api/category/list";
                break;
            case API_012:
                apiUrl = "/api/account/list";
                break;
            case API_013:
                apiUrl = "/api/user/kpi-user";
                break;
            case API_035:
                apiUrl = "/api/crm-task/create";
                break;
            case API_045:
                apiUrl = "/api/user/location";
                break;
        }
        return apiUrl;
    }

}
