package com.sales1crm.ekamant.sales1crm.activities.callbacks;

/**
 * Created by apridosandyasa on 4/12/17.
 */

public interface CheckInCallback {
    void finishedDoCheckIn(String result, String json);
    void finishedDoLogout(String result, String json);
}
