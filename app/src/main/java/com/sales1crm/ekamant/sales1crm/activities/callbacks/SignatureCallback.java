package com.sales1crm.ekamant.sales1crm.activities.callbacks;

/**
 * Created by apridosandyasa on 4/13/17.
 */

public interface SignatureCallback {
    void failureSetupSendTaskData();
    void finishedSetupSendTaskData(String result);
    void finishedSetupSendTaskDataOffine();
}
