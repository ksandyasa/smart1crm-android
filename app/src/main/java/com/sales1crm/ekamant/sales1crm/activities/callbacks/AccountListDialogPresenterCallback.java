package com.sales1crm.ekamant.sales1crm.activities.callbacks;

import com.sales1crm.ekamant.sales1crm.activities.models.Customer;

import java.util.List;

/**
 * Created by apridosandyasa on 4/13/17.
 */

public interface AccountListDialogPresenterCallback {
    void finishedSetupAccountListViews(String result, List<Customer> customerList);
    void finishedSetupMoreAccountListViews(String result, List<Customer> customerList);
    void finishedSetupAccountListViewsOffline(List<Customer> customerList);
    void finishedSetupMoreAccountListViewsOffline(List<Customer> customerList);
}
