package com.sales1crm.ekamant.sales1crm.activities.callbacks;

import com.sales1crm.ekamant.sales1crm.activities.models.Category;

import java.util.List;

/**
 * Created by apridosandyasa on 4/13/17.
 */

public interface TaskAddCallback {
    void finishedSetupCategory(String result, String json);
    void finishedSetupCategoryOffline(List<Category> categoryList);
}
