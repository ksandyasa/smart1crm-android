package com.sales1crm.ekamant.sales1crm.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sales1crm.ekamant.sales1crm.R;
import com.sales1crm.ekamant.sales1crm.activities.callbacks.LoginConfirmationCallback;
import com.sales1crm.ekamant.sales1crm.activities.presenters.LoginConfirmationPresenter;
import com.sales1crm.ekamant.sales1crm.activities.utilities.ApiParam;
import com.sales1crm.ekamant.sales1crm.activities.utilities.PreferenceUtility;
import com.sales1crm.ekamant.sales1crm.activities.utilities.Smart1CrmUtils;
import com.sales1crm.ekamant.sales1crm.activities.widgets.CustomDialog;
import com.sales1crm.ekamant.sales1crm.activities.widgets.CustomTextView;
import com.sales1crm.ekamant.sales1crm.activities.widgets.RoundedImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by apridosandyasa on 4/11/17.
 */

public class LoginConfirmationActivity extends BaseActivity implements LoginConfirmationCallback {

    @InjectView(R.id.text_hello)
    CustomTextView text_hello;

    @InjectView(R.id.image_prof_pict)
    RoundedImageView image_prof_pict;

    @InjectView(R.id.text_company)
    CustomTextView text_company;

    @InjectView(R.id.text_company_name)
    CustomTextView text_company_name;

    @InjectView(R.id.text_name)
    CustomTextView text_name;

    @InjectView(R.id.text_full_name)
    CustomTextView text_full_name;

    @InjectView(R.id.text_position)
    CustomTextView text_position;

    @InjectView(R.id.text_position_name)
    CustomTextView text_position_name;

    @InjectView(R.id.btn_ok)
    Button btn_ok;

    @InjectView(R.id.btn_no)
    Button btn_no;

    private CustomDialog mDialog;
    private LoginConfirmationPresenter loginConfirmationPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_confirmation);

        ButterKnife.inject(this);

        PreferenceUtility.getInstance().saveData(LoginConfirmationActivity.this,
                PreferenceUtility.WORK_START_NOTIF, false);
        PreferenceUtility.getInstance().saveData(LoginConfirmationActivity.this,
                PreferenceUtility.WORK_END_NOTIF, false);
        PreferenceUtility.getInstance().saveData(LoginConfirmationActivity.this,
                PreferenceUtility.REMINDME,false);

        this.loginConfirmationPresenter = new LoginConfirmationPresenter(LoginConfirmationActivity.this,
                this);

        initView();
    }

    private void initView() {
        String name = PreferenceUtility.getInstance().loadDataString(LoginConfirmationActivity.this,
                PreferenceUtility.USER_NAME);
        String position = PreferenceUtility.getInstance().loadDataString(LoginConfirmationActivity.this,
                PreferenceUtility.USER_POSITION);
        String company = PreferenceUtility.getInstance().loadDataString(LoginConfirmationActivity.this,
                PreferenceUtility.USER_COMPANY);

        this.text_name.setText("Hello " + (name.split(" "))[0] + "!");
        this.text_full_name.setText(name);
        this.text_position_name.setText(position);
        this.text_company_name.setText(company);

        this.btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();
                loginConfirmationPresenter.runCheckInFromLoginConfirmation(ApiParam.API_003);
            }
        });

        this.btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog = CustomDialog.createNormalDialog(LoginConfirmationActivity.this,
                        "CONFIRMATION", "Are you sure you want to", "logout?",
                        Smart1CrmUtils.TYPE_YESNO);

                CustomTextView tvTitle = (CustomTextView) mDialog.findViewById(R.id.tvTitle);
                CustomTextView tvText1 = (CustomTextView) mDialog.findViewById(R.id.tvText1);
                CustomTextView tvText2 = (CustomTextView) mDialog.findViewById(R.id.tvText2);
                LinearLayout llYesNO = (LinearLayout) mDialog
                        .findViewById(R.id.llYesNO);
                LinearLayout llOK = (LinearLayout) mDialog
                        .findViewById(R.id.llOK);
                CustomTextView tvYes = (CustomTextView) mDialog.findViewById(R.id.tvYes);
                CustomTextView tvNo = (CustomTextView) mDialog.findViewById(R.id.tvNo);

                tvTitle.setText("CONFIRMATION");
                tvText1.setText("Are you sure you want to");
                tvText2.setText("logout?");

                llOK.setVisibility(View.GONE);
                llYesNO.setVisibility(View.VISIBLE);

                tvYes.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        mDialog.dismiss();
                        PreferenceUtility.getInstance().saveData(LoginConfirmationActivity.this,
                                PreferenceUtility.API_KEY, "");
                        Intent i = new Intent(LoginConfirmationActivity.this,
                                LoginActivity.class);
                        startActivity(i);
                        finish();
                    }
                });
                tvNo.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        mDialog.dismiss();
                    }
                });
                mDialog.show();
            }
        });
    }

    @Override
    public void finishedConfirmCheckIn(String result, int statusCheckIn) {
        dismissLoadingDialog();
        if (result.equals("OK")) {
            PreferenceUtility.getInstance().saveData(LoginConfirmationActivity.this,
                    PreferenceUtility.CHECKIN, statusCheckIn);
            if (statusCheckIn == 0) {
                Intent i = new Intent(LoginConfirmationActivity.this,
                        CheckInActivity.class);
                i.putExtra("is_checkout", true);
                startActivity(i);
            }else if (statusCheckIn == 1) {
                Intent i = new Intent(LoginConfirmationActivity.this,
                        CheckInActivity.class);
                i.putExtra("is_checkout", false);
                startActivity(i);
            }else if (statusCheckIn == 2) {
                Intent i = new Intent(LoginConfirmationActivity.this,
                        MainActivity.class);
                startActivity(i);
            }
            LoginConfirmationActivity.this.finish();
        }else {
            if (PreferenceUtility.getInstance().loadDataInt(LoginConfirmationActivity.this,
                    PreferenceUtility.CHECKIN) == 1
                    || PreferenceUtility.getInstance().loadDataInt(LoginConfirmationActivity.this,
                    PreferenceUtility.CHECKIN) == 0) {
                Intent i = new Intent(LoginConfirmationActivity.this,
                        CheckInActivity.class);
                startActivity(i);
            } else {
                Intent i = new Intent(LoginConfirmationActivity.this,
                        MainActivity.class);
                startActivity(i);
            }
            LoginConfirmationActivity.this.finish();
        }
    }
}
