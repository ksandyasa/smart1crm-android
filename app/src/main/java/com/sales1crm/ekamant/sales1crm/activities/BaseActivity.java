package com.sales1crm.ekamant.sales1crm.activities;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.sales1crm.ekamant.sales1crm.R;
import com.sales1crm.ekamant.sales1crm.activities.widgets.CustomLoadingDialog;

/**
 * Created by apridosandyasa on 4/11/17.
 */

public class BaseActivity extends AppCompatActivity {

    private CustomLoadingDialog dialog;

    public void showLoadingDialog() {
        dialog = new CustomLoadingDialog(this);
        dialog.show(this.getSupportFragmentManager(), "customLoadingDialog");
    }

    public void dismissLoadingDialog() {
        if (null == dialog) return;
        dialog.dismiss();
    }

}
