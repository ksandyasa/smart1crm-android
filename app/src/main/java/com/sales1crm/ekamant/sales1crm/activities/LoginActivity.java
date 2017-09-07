package com.sales1crm.ekamant.sales1crm.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.sales1crm.ekamant.sales1crm.R;
import com.sales1crm.ekamant.sales1crm.activities.callbacks.LoginCallback;
import com.sales1crm.ekamant.sales1crm.activities.presenters.LoginPresenter;
import com.sales1crm.ekamant.sales1crm.activities.utilities.ApiParam;
import com.sales1crm.ekamant.sales1crm.activities.utilities.PermissionCheck;
import com.sales1crm.ekamant.sales1crm.activities.utilities.PreferenceUtility;
import com.sales1crm.ekamant.sales1crm.activities.utilities.Smart1CrmUtils;
import com.sales1crm.ekamant.sales1crm.activities.widgets.CustomTextView;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by apridosandyasa on 4/11/17.
 */

public class LoginActivity extends BaseActivity implements LoginCallback {

    @InjectView(R.id.etPassword)
    EditText etPassword;

    @InjectView(R.id.ivClear)
    ImageView ivClear;

    @InjectView(R.id.tvLogin)
    CustomTextView tvLogin;

    private static final String TAG = LoginActivity.class.getSimpleName();

    private String mAction = null;
    private Intent mIntent = null;
    private String rawPasscode = "", passCode = "", reg_id = "", api_key = "";
    private LoginPresenter loginPresenter;
    private PermissionCheck permissionCheck;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.inject(this);

        this.loginPresenter = new LoginPresenter(LoginActivity.this, this);

        this.permissionCheck = new PermissionCheck();
        this.permissionCheck.checkPermission(LoginActivity.this);

        initView();
        getDataFromLoginUrl();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        getDataFromLoginUrl();
    }

    private void initView() {


        ivClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etPassword.setText("");
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etPassword.getText().toString().trim().length() > 0) {
                    // DECODE the password entered on the field
                    showLoadingDialog();
                    String toDecode = etPassword.getText().toString();
                    byte[] data = Base64.decode(toDecode, Base64.NO_PADDING);
                    try {
                        rawPasscode = new String(data, "UTF-8");
                        Log.i(TAG, "decoded text : " + rawPasscode);
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (rawPasscode.contains("http://")) {
                        String[] ProcessingPasscode = rawPasscode.split("/");
                        for (int i = 0; i < ProcessingPasscode.length; i++) {
                            Log.i(TAG, "split : " + ProcessingPasscode[i]);
                        }
                        String url = "http://"
                                + ProcessingPasscode[2].toString();
                        Log.i(TAG, "url : " + url);
                        PreferenceUtility.getInstance().saveData(LoginActivity.this,
                                PreferenceUtility.URL, url);
                        passCode = ProcessingPasscode[3].substring(1);
                        Log.i(TAG, "passCode : " + passCode);
                        PreferenceUtility.getInstance().saveData(LoginActivity.this,
                                PreferenceUtility.PASSCODE, passCode);

                        loginPresenter.setupLoginToServer(ApiParam.API_001, passCode);
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid passcode",
                                Toast.LENGTH_SHORT).show();
                        dismissLoadingDialog();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Input your passcode",
                            Toast.LENGTH_SHORT).show();
//                    tvWrongLogin.setText("EMPTY PASSCODE");
//                    tvWrongLogin.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void getDataFromLoginUrl() {

        mIntent = getIntent();
        mAction = mIntent.getAction();
        PreferenceUtility.getInstance().saveData(LoginActivity.this, PreferenceUtility.URL_INTENT_DATA, getIntent().toUri(0));

        Log.i(TAG, "getDataFromLoginUrl : " + mAction + mIntent.getData());
        if (Intent.ACTION_VIEW.equals(mAction)) {
            String newToDecode = null;
            String toDecode = mIntent.getData().getEncodedQuery();

            try {
                newToDecode = java.net.URLDecoder.decode(toDecode, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String decodeResult = newToDecode.substring(2, newToDecode.length());
            PreferenceUtility.getInstance().saveData(LoginActivity.this,
                    PreferenceUtility.getInstance().ENCODED_PASSCODE, decodeResult);
            passCodeCheck();
        } else {
            passCodeCheck();
        }
    }

    private void passCodeCheck() {
        String encodedPasscode = PreferenceUtility.getInstance().loadDataString(LoginActivity.this,
                PreferenceUtility.getInstance().ENCODED_PASSCODE);
        etPassword.setText(encodedPasscode);
    }

    @Override
    public void finishedLogin(String result, String json) {
        dismissLoadingDialog();
        if (result.equals("OK")) {
            try {
                this.api_key = Smart1CrmUtils.JSONUtility.getApiKey(json);
                PreferenceUtility.getInstance().saveData(LoginActivity.this, PreferenceUtility.API_KEY, this.api_key);
                Smart1CrmUtils.JSONUtility.saveLoginItemsFromJSON(LoginActivity.this, json);
                Intent loginConfIntent = new Intent(LoginActivity.this, LoginConfirmationActivity.class);
                startActivity(loginConfIntent);
                LoginActivity.this.finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else {

        }
    }
}
