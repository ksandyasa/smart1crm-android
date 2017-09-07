package com.sales1crm.ekamant.sales1crm.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.sales1crm.ekamant.sales1crm.R;
import com.sales1crm.ekamant.sales1crm.activities.callbacks.SignatureCallback;
import com.sales1crm.ekamant.sales1crm.activities.databases.DBHelper;
import com.sales1crm.ekamant.sales1crm.activities.databases.TaskDao;
import com.sales1crm.ekamant.sales1crm.activities.models.Task;
import com.sales1crm.ekamant.sales1crm.activities.presenters.SignaturePresenter;
import com.sales1crm.ekamant.sales1crm.activities.utilities.ApiParam;
import com.sales1crm.ekamant.sales1crm.activities.utilities.PreferenceUtility;
import com.sales1crm.ekamant.sales1crm.activities.utilities.Smart1CrmUtils;
import com.sales1crm.ekamant.sales1crm.activities.utilities.WakeAlarm;
import com.sales1crm.ekamant.sales1crm.activities.widgets.CustomTextView;

import java.io.ByteArrayOutputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by apridosandyasa on 4/13/17.
 */

public class SignatureActivity extends BaseActivity implements SignatureCallback {

    @InjectView(R.id.rlBack)
    RelativeLayout rlBack;

    @InjectView(R.id.cancel)
    CustomTextView cancel;

    @InjectView(R.id.clear)
    CustomTextView clear;

    @InjectView(R.id.getsign)
    CustomTextView getsign;

    @InjectView(R.id.signature_pad)
    SignaturePad signature_pad;

    private String sign;
    private SignaturePresenter signaturePresenter;
    private Task task;
    private TaskDao taskDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);

        ButterKnife.inject(this);

        this.signaturePresenter = new SignaturePresenter(SignatureActivity.this, this);

        initView();
    }

    private void initView() {
        this.signature_pad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }

            @Override
            public void onSigned() {
                getsign.setEnabled(true);
                clear.setEnabled(true);
            }

            @Override
            public void onClear() {
                getsign.setEnabled(false);
                clear.setEnabled(false);
            }
        });

        this.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignatureActivity.this.finish();
            }
        });

        this.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signature_pad.clear();
                getsign.setEnabled(false);
            }
        });

        this.getsign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap signatureBitmap = signature_pad.getSignatureBitmap();
                sign = encodeToBase64(signatureBitmap, Bitmap.CompressFormat.JPEG, 100);

                saveTaskData();

                postData();
            }
        });
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    private void postData() {
        showLoadingDialog();
        this.signaturePresenter.setupSendTaskData(ApiParam.API_035, this.task);
    }

    private void saveTaskData() {
        this.taskDao = new TaskDao(new DBHelper(SignatureActivity.this), true);

        this.task = new Task();
        this.task.setCustomer_id(Integer.valueOf(PreferenceUtility.getInstance().loadDataString(SignatureActivity.this, PreferenceUtility.CUSTOMER_ID)));
        this.task.setCustomer_name(PreferenceUtility.getInstance().loadDataString(SignatureActivity.this, PreferenceUtility.CUSTOMER_NAME));
        this.task.setNotes(PreferenceUtility.getInstance().loadDataString(SignatureActivity.this, PreferenceUtility.NOTES));
        this.task.setOrder_status(PreferenceUtility.getInstance().loadDataString(SignatureActivity.this, PreferenceUtility.ORDER_STATUS));
        this.task.setLatitude(PreferenceUtility.getInstance().loadDataString(SignatureActivity.this, PreferenceUtility.LATITUDE));
        this.task.setLongitude(PreferenceUtility.getInstance().loadDataString(SignatureActivity.this, PreferenceUtility.LONGITUDE));
        this.task.setFoto1(PreferenceUtility.getInstance().loadDataString(SignatureActivity.this, PreferenceUtility.PHOTO_PATH1));
        this.task.setFoto2(PreferenceUtility.getInstance().loadDataString(SignatureActivity.this, PreferenceUtility.PHOTO_PATH2));
        this.task.setFoto3(PreferenceUtility.getInstance().loadDataString(SignatureActivity.this, PreferenceUtility.PHOTO_PATH3));
        this.task.setSignature(this.sign);
        this.task.setCreated_at(PreferenceUtility.getInstance().loadDataString(SignatureActivity.this, PreferenceUtility.CREATED_AT));
        this.task.setProduct(PreferenceUtility.getInstance().loadDataString(SignatureActivity.this, PreferenceUtility.JSON_PRODUCT));

        this.taskDao.insertTable(this.task);
    }

    @Override
    public void failureSetupSendTaskData() {
        Toast.makeText(SignatureActivity.this, "Laporan akan terkirim jika sinyal bagus", Toast.LENGTH_SHORT).show();
        dismissLoadingDialog();
        WakeAlarm location_alarm = new WakeAlarm();
        location_alarm.sendDataIfConnected(SignatureActivity.this, PreferenceUtility.getInstance().loadDataString(SignatureActivity.this, PreferenceUtility.CUSTOMER_ID));
        setResult(RESULT_OK);
        SignatureActivity.this.finish();
    }

    @Override
    public void finishedSetupSendTaskData(String result) {
        dismissLoadingDialog();
        if (result.equals("OK")) {
            this.taskDao = new TaskDao(new DBHelper(SignatureActivity.this), true);
            this.taskDao.deleteAllRecord();

            setResult(RESULT_OK);
            SignatureActivity.this.finish();
        }else {

        }
    }

    @Override
    public void finishedSetupSendTaskDataOffine() {
        dismissLoadingDialog();
        WakeAlarm location_alarm = new WakeAlarm();
        location_alarm.sendDataIfConnected(SignatureActivity.this, PreferenceUtility.getInstance().loadDataString(SignatureActivity.this, PreferenceUtility.CUSTOMER_ID));
        setResult(RESULT_OK);
        SignatureActivity.this.finish();
    }
}
