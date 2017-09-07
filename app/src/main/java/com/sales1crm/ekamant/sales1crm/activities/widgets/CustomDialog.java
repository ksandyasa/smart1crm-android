/*
 * Copyright 2017 Matthew Lim
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sales1crm.ekamant.sales1crm.activities.widgets;

import android.app.Dialog;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sales1crm.ekamant.sales1crm.R;
import com.sales1crm.ekamant.sales1crm.activities.utilities.Smart1CrmUtils;

/**
 * TODO: Add a class Header comment!
 */

public class CustomDialog extends Dialog {
    private Context context;

    public CustomDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    public CustomDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public static CustomDialog createNormalDialog(Context context,
                                                  String title, String text1, String text2, int type) {
        CustomDialog dialog = new CustomDialog(context,
                R.style.CustomProgressDialog);
        dialog.setContentView(R.layout.dialog_pop_up);
        dialog.getWindow().setLayout(ViewPager.LayoutParams.MATCH_PARENT,
                ViewPager.LayoutParams.MATCH_PARENT);

        TextView tvTitle = (TextView) dialog.findViewById(R.id.tvTitle);
        TextView tvText1 = (TextView) dialog.findViewById(R.id.tvText1);
        TextView tvText2 = (TextView) dialog.findViewById(R.id.tvText2);

        LinearLayout llYesNO = (LinearLayout) dialog.findViewById(R.id.llYesNO);
        LinearLayout llOK = (LinearLayout) dialog.findViewById(R.id.llOK);

        tvTitle.setText(title);
        tvText1.setText(text1);
        tvText2.setText(text2);

        if (type == Smart1CrmUtils.TYPE_OK) {
            llYesNO.setVisibility(View.GONE);
            llOK.setVisibility(View.VISIBLE);
        } else {
            llOK.setVisibility(View.GONE);
            llYesNO.setVisibility(View.VISIBLE);
        }

        return dialog;
    }

    public static CustomDialog createDialogUniversal(Context context, int layout) {
        CustomDialog dialog = new CustomDialog(context,
                R.style.CustomProgressDialog);
        dialog.setContentView(layout); // R.layout.dialog_withdatepicker_pop_up,
        // R.layout.dialog_novisitreason
        dialog.getWindow().setLayout(ViewPager.LayoutParams.MATCH_PARENT,
                ViewPager.LayoutParams.MATCH_PARENT);

        return dialog;
    }

}