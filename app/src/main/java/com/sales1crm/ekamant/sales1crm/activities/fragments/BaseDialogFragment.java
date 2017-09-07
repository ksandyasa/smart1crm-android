package com.sales1crm.ekamant.sales1crm.activities.fragments;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.view.Window;

import com.sales1crm.ekamant.sales1crm.R;

/**
 * Created by apridosandyasa on 4/13/17.
 */

public class BaseDialogFragment extends DialogFragment {

    private Dialog dialog;

    public void showLoadingDialog() {
        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loading_screen);
        dialog.show();
    }

    public void dismissLoadingDialog() {
        if (null == dialog) return;
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

}
