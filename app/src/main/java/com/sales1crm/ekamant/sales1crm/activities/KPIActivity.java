package com.sales1crm.ekamant.sales1crm.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.sales1crm.ekamant.sales1crm.R;
import com.sales1crm.ekamant.sales1crm.activities.adapters.CustomSpinnerAdapter;
import com.sales1crm.ekamant.sales1crm.activities.callbacks.KPICallback;
import com.sales1crm.ekamant.sales1crm.activities.presenters.KPIPresenter;
import com.sales1crm.ekamant.sales1crm.activities.utilities.ApiParam;
import com.sales1crm.ekamant.sales1crm.activities.utilities.Smart1CrmUtils;
import com.sales1crm.ekamant.sales1crm.activities.widgets.CustomTextView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by apridosandyasa on 4/13/17.
 */

public class KPIActivity extends BaseActivity implements KPICallback {

    @InjectView(R.id.ivBackKPI)
    ImageView ivBackKPI;

    @InjectView(R.id.spinner_month)
    Spinner spinner_month;

    @InjectView(R.id.spinner_year)
    Spinner spinner_year;

    @InjectView(R.id.btn_update)
    Button btn_update;

    @InjectView(R.id.text_kpi_desc)
    CustomTextView text_kpi_desc;

    @InjectView(R.id.text_kpi_current)
    CustomTextView text_kpi_current;

    @InjectView(R.id.text_kpi_target)
    CustomTextView text_kpi_target;

    private String month, year;
    private String[] monthArr, yearArr;
    private KPIPresenter kpiPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kpi);

        ButterKnife.inject(this);

        this.kpiPresenter = new KPIPresenter(KPIActivity.this, this);

        initView();
    }

    private void initView() {
        this.monthArr = new String[]{"Pilih bulan", "January", "February", "March", "April"
                , "May", "June", "July", "August"
                , "September", "October", "November", "December"};

        this.yearArr = new String[]{"Pilih tahun", "2016", "2017", "2018", "2019", "2020"};

        this.month = Smart1CrmUtils.DateUtility.getCurrentMonth();
        this.year = "" + Smart1CrmUtils.DateUtility.getCurrentYear();

        CustomSpinnerAdapter monthAdapter = new CustomSpinnerAdapter(this, R.layout.spinner_month, monthArr);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinner_month.setAdapter(monthAdapter);
        this.spinner_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                month = monthArr[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        CustomSpinnerAdapter yearAdapter = new CustomSpinnerAdapter(this, R.layout.spinner_year, yearArr);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spinner_year.setAdapter(yearAdapter);
        this.spinner_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                year = yearArr[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        for (int j = 0; j < yearArr.length; j++) {
            if (year.equals(yearArr[j])) {
                this.spinner_year.setSelection(j);
            }
        }

        for (int i = 0; i < monthArr.length; i++) {
            if (month.equals(monthArr[i]) ) {
                this.spinner_month.setSelection(i);
            }
        }

        this.ivBackKPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KPIActivity.this.finish();
            }
        });

        this.btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLoadingDialog();
                kpiPresenter.setupKPIViews(ApiParam.API_013, month, "" + year);
            }
        });

        showLoadingDialog();
        this.kpiPresenter.setupKPIViews(ApiParam.API_013, month, "" + year);

    }

    @Override
    public void finishedSetupKPIViews(String result, String totalSales, String targetSales) {
        dismissLoadingDialog();
        if (result.equals("OK")) {
            text_kpi_desc.setText(
                    String.format(getResources().getString(R.string.desc_kpi), month, year));
            text_kpi_current.setText(totalSales);
            text_kpi_target.setText(targetSales);
        }
    }
}
