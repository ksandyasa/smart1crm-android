package com.sales1crm.ekamant.sales1crm.activities.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.sales1crm.ekamant.sales1crm.R;

/**
 * Created by apridosandyasa on 4/13/17.
 */

public class CustomLoadingDialog extends DialogFragment {

    private Context context;
    private View view;

    public CustomLoadingDialog() {

    }

    @SuppressLint("ValidFragment")
    public CustomLoadingDialog(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.width = (int) (this.context.getResources().getDisplayMetrics().widthPixels);
        windowParams.height = (int) (this.context.getResources().getDisplayMetrics().heightPixels);
        windowParams.dimAmount = 0.75f;
        window.setAttributes(windowParams);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreateView(inflater, container, savedInstanceState);

        this.view = inflater.inflate(R.layout.loading_screen, container, false);

        return this.view;
    }

}
