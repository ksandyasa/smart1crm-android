package com.sales1crm.ekamant.sales1crm.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sales1crm.ekamant.sales1crm.R;
import com.sales1crm.ekamant.sales1crm.activities.callbacks.MainCallback;
import com.sales1crm.ekamant.sales1crm.activities.presenters.MainPresenter;
import com.sales1crm.ekamant.sales1crm.activities.services.GpsTracker;
import com.sales1crm.ekamant.sales1crm.activities.utilities.ApiParam;
import com.sales1crm.ekamant.sales1crm.activities.utilities.PermissionCheck;
import com.sales1crm.ekamant.sales1crm.activities.utilities.Smart1CrmUtils;
import com.sales1crm.ekamant.sales1crm.activities.utilities.WakeAlarm;
import com.sales1crm.ekamant.sales1crm.activities.widgets.CustomDialog;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends BaseActivity implements MainCallback {

    @InjectView(R.id.llHeader)
    RelativeLayout llHeader;

    @InjectView(R.id.rlGotoSetting)
    LinearLayout rlGotoSetting;

    @InjectView(R.id.ivLogout)
    ImageView ivLogout;

    @InjectView(R.id.ivSetting)
    ImageView ivSetting;

    @InjectView(R.id.image_task)
    ImageView image_task;

    @InjectView(R.id.image_kpi)
    ImageView image_kpi;

    private String apiKey, longitude, latitude;
    private MainPresenter mainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        this.mainPresenter = new MainPresenter(MainActivity.this, this);

        initView();
        getLocation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionCheck.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();

                perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);

                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "Aplikasi menolak perizinan yang ingin dilakukan", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    private void initView() {
        image_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent taskAddIntent = new Intent(MainActivity.this, TaskAddActivity.class);
                startActivity(taskAddIntent);
            }
        });

        image_kpi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent kpiIntent = new Intent(MainActivity.this, KPIActivity.class);
                startActivity(kpiIntent);
            }
        });

        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CustomDialog dialog = CustomDialog.createNormalDialog(MainActivity.this,
                        "Konfirmasi", "Apakah Anda ingin logout?", "",
                        Smart1CrmUtils.TYPE_YESNO);
                LinearLayout llYesNO = (LinearLayout) dialog
                        .findViewById(R.id.llYesNO);
                (llYesNO.findViewById(R.id.tvYes)).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Log.i("TAG", "v logout: " + v.getId());
                        if (Smart1CrmUtils.ConnectionUtility.isNetworkConnected(MainActivity.this)) {
                            showLoadingDialog();
                            mainPresenter.setupDoLogout(ApiParam.API_005);
                            //doSendLocation();
                            //doOrderCreateorPaymeDeli();
                        }
                    }
                });
                (llYesNO.findViewById(R.id.tvNo)).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        ivSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void getLocation() {

        GpsTracker gpsTracker = new GpsTracker(MainActivity.this);
        if (gpsTracker.canGetLocation()) {
            latitude = String.valueOf(gpsTracker.latitude);

            longitude = String.valueOf(gpsTracker.longitude);
            Log.d("TAG", "latitude " + latitude);
            Log.d("TAG", "longitude " + longitude);

            if (!Smart1CrmUtils.service_running) {
                WakeAlarm location_alarm = new WakeAlarm();
                location_alarm.SetAlarmForGeoService(MainActivity.this);
            }
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gpsTracker.showSettingsAlert();
        }
    }

    @Override
    public void finishedSetupMainViews(String result, String json) {
        dismissLoadingDialog();
        if (result.equals("OK")) {

        }else {

        }
    }

    @Override
    public void finishedSetupDoLogout(String result) {
        dismissLoadingDialog();
        if (result.equals("OK")) {
            Intent i = new Intent(MainActivity.this, CheckInActivity.class);
            i.putExtra("is_checkout", true);
            startActivity(i);
            MainActivity.this.finish();
        }else {
            Toast.makeText(MainActivity.this, "failed to checkout...", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
