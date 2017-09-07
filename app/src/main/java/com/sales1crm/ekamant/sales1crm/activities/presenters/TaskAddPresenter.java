package com.sales1crm.ekamant.sales1crm.activities.presenters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.sales1crm.ekamant.sales1crm.activities.callbacks.TaskAddCallback;
import com.sales1crm.ekamant.sales1crm.activities.databases.CategoryDao;
import com.sales1crm.ekamant.sales1crm.activities.databases.DBHelper;
import com.sales1crm.ekamant.sales1crm.activities.databases.ProductDao;
import com.sales1crm.ekamant.sales1crm.activities.models.Category;
import com.sales1crm.ekamant.sales1crm.activities.models.Product;
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

public class TaskAddPresenter {

    private final String TAG = TaskAddPresenter.class.getSimpleName();
    private Context context;
    private Messenger messenger;
    private Message message;
    private Bundle bundle;
    private Handler handler;
    private String failureResponse = "";
    private String[] stringResponse = {""};
    private TaskAddCallback taskAddCallback;
    private String resultTaskAdd = "";
    private List<Category> categoryList = new ArrayList<>();

    public TaskAddPresenter(Context context, TaskAddCallback listener) {
        this.context = context;
        this.taskAddCallback = listener;
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

    public void setupCategoryViews(int apiIndex) {
        if (Smart1CrmUtils.ConnectionUtility.isNetworkConnected(this.context)) {
            obtainCategoryItems(apiIndex);
        }else {
            obtainCategoryItemsFromDatabase();
        }
    }

    private void obtainCategoryItems(int apiIndex) {
        this.handler = new Handler(this.context.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                parseCategoryItemsResponse(msg);
            }

        };

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("api_key", PreferenceUtility.getInstance().loadDataString(this.context, PreferenceUtility.API_KEY));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        doNetworkService(ApiParam.urlBuilder(this.context, apiIndex), jsonObject.toString(), "taskAddCategory");
    }

    private void parseCategoryItemsResponse(Message message) {
        this.message = message;
        this.bundle = this.message.getData();
        this.stringResponse[0] = this.bundle.getString("network_response");
        Log.d(TAG, "responseString[0] category info " + this.stringResponse[0]);
        try {
            this.resultTaskAdd = Smart1CrmUtils.JSONUtility.getResultFromServer(this.stringResponse[0]);
            this.taskAddCallback.finishedSetupCategory(this.resultTaskAdd, this.stringResponse[0]);
        } catch (JSONException e) {
            Log.d(TAG, "Exception category Response " + e.getMessage());
        }
    }

    private void obtainCategoryItemsFromDatabase() {
        CategoryDao categoryDao = new CategoryDao(new DBHelper(this.context), false);
        this.categoryList = categoryDao.getCategoryListFromDb();
        for (int i = 0; i < this.categoryList.size(); i++) {
            List<Product> productList = new ArrayList<>();
            ProductDao productDao = new ProductDao(new DBHelper(this.context), false);
            productList = productDao.getProductListFromDb(this.categoryList.get(i).getId());
            this.categoryList.get(i).setProduct(productList);
        }
        this.taskAddCallback.finishedSetupCategoryOffline(this.categoryList);
    }

}
