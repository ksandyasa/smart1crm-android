package com.sales1crm.ekamant.sales1crm.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.sales1crm.ekamant.sales1crm.R;
import com.sales1crm.ekamant.sales1crm.activities.databases.DBHelper;
import com.sales1crm.ekamant.sales1crm.activities.databases.TaskDao;
import com.sales1crm.ekamant.sales1crm.activities.models.Task;
import com.sales1crm.ekamant.sales1crm.activities.utilities.PreferenceUtility;
import com.sales1crm.ekamant.sales1crm.activities.utilities.Smart1CrmUtils;
import com.sales1crm.ekamant.sales1crm.activities.utilities.WakeAlarm;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by apridosandyasa on 4/13/17.
 */

public class TaskFinishActivity extends BaseActivity {

    @InjectView(R.id.btn_home)
    Button btn_home;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_finish);

        ButterKnife.inject(this);

        initView();
    }

    private void initView() {
        this.btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtility.getInstance().saveData(TaskFinishActivity.this, PreferenceUtility.CUSTOMER_ID, "");
                PreferenceUtility.getInstance().saveData(TaskFinishActivity.this, PreferenceUtility.CUSTOMER_NAME, "");
                PreferenceUtility.getInstance().saveData(TaskFinishActivity.this, PreferenceUtility.NOTES, "");
                PreferenceUtility.getInstance().saveData(TaskFinishActivity.this, PreferenceUtility.ORDER_STATUS, "");
                PreferenceUtility.getInstance().saveData(TaskFinishActivity.this, PreferenceUtility.LATITUDE, "");
                PreferenceUtility.getInstance().saveData(TaskFinishActivity.this, PreferenceUtility.LONGITUDE, "");
                PreferenceUtility.getInstance().saveData(TaskFinishActivity.this, PreferenceUtility.PHOTO_PATH1, "");
                PreferenceUtility.getInstance().saveData(TaskFinishActivity.this, PreferenceUtility.PHOTO_PATH2, "");
                PreferenceUtility.getInstance().saveData(TaskFinishActivity.this, PreferenceUtility.PHOTO_PATH3, "");
                PreferenceUtility.getInstance().saveData(TaskFinishActivity.this, PreferenceUtility.CREATED_AT, "");
                PreferenceUtility.getInstance().saveData(TaskFinishActivity.this, PreferenceUtility.JSON_PRODUCT, "");

                TaskFinishActivity.this.finish();
            }
        });
    }
}
