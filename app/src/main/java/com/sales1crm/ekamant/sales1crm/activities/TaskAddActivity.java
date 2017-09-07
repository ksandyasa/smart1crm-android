package com.sales1crm.ekamant.sales1crm.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sales1crm.ekamant.sales1crm.R;
import com.sales1crm.ekamant.sales1crm.activities.callbacks.AccountListDialogCallback;
import com.sales1crm.ekamant.sales1crm.activities.callbacks.TaskAddCallback;
import com.sales1crm.ekamant.sales1crm.activities.databases.DBHelper;
import com.sales1crm.ekamant.sales1crm.activities.databases.TaskDao;
import com.sales1crm.ekamant.sales1crm.activities.fragments.AccountListDialog;
import com.sales1crm.ekamant.sales1crm.activities.models.Category;
import com.sales1crm.ekamant.sales1crm.activities.models.Task;
import com.sales1crm.ekamant.sales1crm.activities.presenters.TaskAddPresenter;
import com.sales1crm.ekamant.sales1crm.activities.services.GpsTracker;
import com.sales1crm.ekamant.sales1crm.activities.utilities.ApiParam;
import com.sales1crm.ekamant.sales1crm.activities.utilities.PreferenceUtility;
import com.sales1crm.ekamant.sales1crm.activities.utilities.Smart1CrmUtils;
import com.sales1crm.ekamant.sales1crm.activities.widgets.CustomTextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by apridosandyasa on 4/12/17.
 */

public class TaskAddActivity extends BaseActivity implements TaskAddCallback {

    @InjectView(R.id.btn_back)
    ImageView btn_back;

    @InjectView(R.id.text_header_title)
    CustomTextView text_header_title;

    @InjectView(R.id.text_account_name)
    CustomTextView text_account_name;

    @InjectView(R.id.text_customer_address)
    CustomTextView text_customer_address;

    @InjectView(R.id.edit_notes)
    EditText edit_notes;

    @InjectView(R.id.text_order)
    CustomTextView text_order;

    @InjectView(R.id.radio_group_order)
    RadioGroup radio_group_order;

    @InjectView(R.id.btn_take_pict1)
    Button btn_take_pict1;

    @InjectView(R.id.btn_take_pict2)
    Button btn_take_pict2;

    @InjectView(R.id.btn_take_pict3)
    Button btn_take_pict3;

    @InjectView(R.id.layout_product)
    LinearLayout layout_product;

    @InjectView(R.id.btn_submit)
    Button btn_submit;

    String myBase64Image;
    private String customerId = "", customerName = "", notes = "",
            latitude, longitude,
            createdAt, photoPath,
            foto1 = "", foto2 = "",
            foto3 = "";
    private int orderStatus = 0;
    private int REQUEST_CAMERA = 0;
    private List<Integer> idList = new ArrayList<>();
    private List<Integer> categoryIdList = new ArrayList<>();
    private List<EditText> editTextList = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();
    private TaskAddPresenter taskAddPresenter;
    private AccountListDialog accListDialog;
    private TaskDao taskDao;
    private Task task;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_add);

        ButterKnife.inject(this);

        this.taskAddPresenter = new TaskAddPresenter(TaskAddActivity.this, this);

        getLocation();
        initView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
            if (requestCode == Smart1CrmUtils.SIGNATURE_ACTIVITY) {
                startActivity(new Intent(TaskAddActivity.this, TaskFinishActivity.class));
                TaskAddActivity.this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        saveProductDataIfBackpressed();
        TaskAddActivity.this.finish();
    }

    private void initView() {
        btn_take_pict1.setVisibility(View.GONE);
        btn_take_pict2.setVisibility(View.GONE);
        btn_take_pict3.setVisibility(View.GONE);

        this.text_account_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TaskAddActivity.this, "Account click", Toast.LENGTH_SHORT).show();
                accListDialog = new AccountListDialog(TaskAddActivity.this, new AccountListDialogCallback() {
                    @Override
                    public void onAccountListDialogCallback(Bundle data) {
                        customerId = "" + data.getInt("account_id");
                        customerName = data.getString("account_name");

                        text_account_name.setText(data.getString("account_name"));
                        text_customer_address.setText(data.getString("account_address"));

                        int count = Smart1CrmUtils.DatabaseUtility.getTaskCountByID(TaskAddActivity.this, data.getInt("account_id"));
                        task = Smart1CrmUtils.DatabaseUtility.getTaskByID(TaskAddActivity.this, data.getInt("account_id"));
                        Log.d("TAG", "task count " + count);
                        Log.d("TAG", "task id " + task.getId());

                        if (count == 0) {
                            Log.d("TAG", "task is null");

                            edit_notes.setText("");

                            int radioBtnId1 = radio_group_order.getChildAt(0).getId();
                            int radioBtnId2 = radio_group_order.getChildAt(1).getId();
                            RadioButton radioButton1 = (RadioButton) findViewById(radioBtnId1);
                            RadioButton radioButton2 = (RadioButton) findViewById(radioBtnId2);
                            radioButton1.setChecked(false);
                            radioButton2.setChecked(false);

                            btn_take_pict1.setEnabled(true);
                            btn_take_pict1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            btn_take_pict2.setEnabled(true);
                            btn_take_pict2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                            btn_take_pict3.setEnabled(true);
                            btn_take_pict3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                        }else {
                            Log.d("TAG", "task is not null");
                            notes = task.getNotes();
                            edit_notes.setText(notes);
                            orderStatus = Integer.valueOf(task.getOrder_status());
                            createdAt = task.getCreated_at();

                            int radioBtnId;
                            if (orderStatus == 1) {
                                radioBtnId = radio_group_order.getChildAt(0).getId();
                                RadioButton radioButton = (RadioButton) findViewById(radioBtnId);
                                radioButton.setChecked(true);
                            }else{
                                radioBtnId = radio_group_order.getChildAt(1).getId();
                                RadioButton radioButton = (RadioButton) findViewById(radioBtnId);
                                radioButton.setChecked(true);
                            }

                            if (task.getFoto1() != null) {
                                if (!task.getFoto1().equalsIgnoreCase("")) {
                                    btn_take_pict1.setEnabled(false);
                                    btn_take_pict1.setBackgroundColor(getResources().getColor(R.color.green_600));
                                    foto1 = task.getFoto1();
                                    PreferenceUtility.getInstance().saveData(TaskAddActivity.this, PreferenceUtility.PHOTO_PATH1, foto1);
                                }else {
                                    btn_take_pict1.setEnabled(true);
                                    btn_take_pict1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                }
                            }
                            if (task.getFoto2() != null) {
                                if (task.getFoto2() != null && !task.getFoto2().equalsIgnoreCase("")) {
                                    btn_take_pict2.setEnabled(false);
                                    btn_take_pict2.setBackgroundColor(getResources().getColor(R.color.green_600));
                                    foto2 = task.getFoto2();
                                    PreferenceUtility.getInstance().saveData(TaskAddActivity.this, PreferenceUtility.PHOTO_PATH1, foto2);
                                }else {
                                    btn_take_pict2.setEnabled(true);
                                    btn_take_pict2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                }
                            }
                            if (task.getFoto3() != null) {
                                if (task.getFoto3() != null && !task.getFoto3().equalsIgnoreCase("")) {
                                    btn_take_pict3.setEnabled(false);
                                    btn_take_pict3.setBackgroundColor(getResources().getColor(R.color.green_600));
                                    foto3 = task.getFoto3();
                                    PreferenceUtility.getInstance().saveData(TaskAddActivity.this, PreferenceUtility.PHOTO_PATH1, foto3);
                                }else {
                                    btn_take_pict3.setEnabled(true);
                                    btn_take_pict3.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                }
                            }
                            try {
                                Smart1CrmUtils.JSONUtility.convertJSONArrayToProductList(TaskAddActivity.this, idList, categoryIdList, editTextList, task.getProduct());
                            } catch (JSONException e) {
                                Log.d("TAG", "Exception convert json product " + e.getMessage());
                            }
                        }

                        btn_take_pict1.setVisibility(View.VISIBLE);
                        btn_take_pict2.setVisibility(View.VISIBLE);
                        btn_take_pict3.setVisibility(View.VISIBLE);

                        setProductView(data.getInt("account_type_id"));
                    }
                });
                accListDialog.setCancelable(true);
                accListDialog.show(getSupportFragmentManager(), "accountListDialog");
            }
        });

        radio_group_order.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                int orderId = radio_group_order.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) findViewById(orderId);
                if ((radioButton.getText().toString()).equalsIgnoreCase("iya")) {
                    orderStatus = 1;
                } else if ((radioButton.getText().toString()).equalsIgnoreCase("tidak")) {
                    orderStatus = 2;
                }
                Log.i("TAG", "onCheckedChanged: " + orderStatus);
            }
        });

        this.btn_take_pict1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtility.getInstance().saveData(TaskAddActivity.this,
                        PreferenceUtility.PHOTO, "1");
                selectImage();
            }
        });

        this.btn_take_pict2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtility.getInstance().saveData(TaskAddActivity.this,
                        PreferenceUtility.PHOTO, "2");
                selectImage();
            }
        });

        this.btn_take_pict3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtility.getInstance().saveData(TaskAddActivity.this,
                        PreferenceUtility.PHOTO, "3");
                selectImage();
            }
        });

        this.btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notes = edit_notes.getText().toString();
                createdAt = getCurrentDate();

                if (customerName.equals("")) {
                    Toast.makeText(TaskAddActivity.this, "Mohon Pilih Customer!", Toast.LENGTH_SHORT)
                            .show();
                } else if (notes.equals("")) {
                    Toast.makeText(TaskAddActivity.this, "Mohon isi catatan!", Toast.LENGTH_SHORT).show();
                } else if (orderStatus == 0) {
                    Toast.makeText(TaskAddActivity.this, "Mohon Pilih order!", Toast.LENGTH_SHORT).show();
                } else if (btn_take_pict1.isEnabled() && btn_take_pict2.isEnabled()
                        && btn_take_pict3.isEnabled()) {
                    Toast.makeText(
                            TaskAddActivity.this, "Mohon ambil minimal 1 foto!", Toast.LENGTH_SHORT).show();
                } else {
                    saveProductdata();
                }
            }
        });

        this.btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProductDataIfBackpressed();
                TaskAddActivity.this.finish();
            }
        });

        showLoadingDialog();
        this.taskAddPresenter.setupCategoryViews(ApiParam.API_011);
    }

    private void setProductView(int type_id) {

        this.layout_product.removeAllViews();

        Log.d("TAG", "category size " + categoryList.size());
        Log.d("TAG", "account_type_id " + type_id);

        for (int iCategory = 0; iCategory < categoryList.size(); iCategory++) {

            Log.d("TAG", "category account_type_id " + categoryList.get(iCategory).getAccount_type());

            if (categoryList.get(iCategory).getAccount_type() == type_id) {

                //  Create LinearLayout for Category
                final LinearLayout linearCategory = new LinearLayout(TaskAddActivity.this);
                linearCategory.setPadding(Smart1CrmUtils.DisplayUtility.dpToPx(TaskAddActivity.this, 8), 0, Smart1CrmUtils.DisplayUtility.dpToPx(TaskAddActivity.this, 8), 0);
                LinearLayout.LayoutParams linearCategoryParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                linearCategoryParams.setMargins(0, 0, 0, Smart1CrmUtils.DisplayUtility.dpToPx(TaskAddActivity.this, 8));
                linearCategory.setLayoutParams(linearCategoryParams);
                linearCategory.setOrientation(LinearLayout.VERTICAL);

                //  Create RelativeLayout for Category name
                final RelativeLayout relativeCategory = new RelativeLayout(TaskAddActivity.this);
                RelativeLayout.LayoutParams relativeCategoryParams = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                relativeCategory.setPadding(0, Smart1CrmUtils.DisplayUtility.dpToPx(TaskAddActivity.this, 8), 0, Smart1CrmUtils.DisplayUtility.dpToPx(TaskAddActivity.this, 8));
                relativeCategory.setLayoutParams(relativeCategoryParams);

                //  TextView Category Name
                final TextView categoryNameView = new TextView(TaskAddActivity.this);
                RelativeLayout.LayoutParams categoryNameViewParams = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                categoryNameView.setLayoutParams(categoryNameViewParams);
                categoryNameView.setText(categoryList.get(iCategory).getName());
                categoryNameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                categoryNameView.setTextColor(
                        ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null));
                categoryNameViewParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                relativeCategory.addView(categoryNameView);

                //  Icon Down Arrow
                final ImageView showView = new ImageView(TaskAddActivity.this);
                RelativeLayout.LayoutParams showViewParams = new RelativeLayout.LayoutParams(
                        Smart1CrmUtils.DisplayUtility.dpToPx(TaskAddActivity.this, 16), Smart1CrmUtils.DisplayUtility.dpToPx(TaskAddActivity.this, 16));
                showView.setLayoutParams(showViewParams);
                showView.setImageResource(R.drawable.ic_arrow_down);
                showViewParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                relativeCategory.addView(showView);

                //  Icon Up Arrow
                final ImageView hideView = new ImageView(TaskAddActivity.this);
                RelativeLayout.LayoutParams hideViewParams = new RelativeLayout.LayoutParams(
                        Smart1CrmUtils.DisplayUtility.dpToPx(TaskAddActivity.this, 24), Smart1CrmUtils.DisplayUtility.dpToPx(TaskAddActivity.this, 24));
                hideView.setLayoutParams(showViewParams);
                hideView.setImageResource(R.drawable.ic_arrow_up);
                hideView.setVisibility(View.GONE);
                hideViewParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                relativeCategory.addView(hideView);


                linearCategory.addView(relativeCategory);

                //  Get Product List based on Category Id

                final LinearLayout linearProduct = new LinearLayout(TaskAddActivity.this);
                LinearLayout.LayoutParams linearProductParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                linearProduct.setLayoutParams(linearProductParams);
                linearProduct.setOrientation(LinearLayout.VERTICAL);
                linearProduct.setVisibility(View.GONE);

                for (int iProduct = 0; iProduct < categoryList.get(iCategory).getProduct().size(); iProduct++) {


                    final LinearLayout linearProduct2 = new LinearLayout(TaskAddActivity.this);
                    LinearLayout.LayoutParams linearProduct2Params = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    linearProduct2.setLayoutParams(linearProduct2Params);
                    linearProduct2.setOrientation(LinearLayout.HORIZONTAL);
                    linearProduct2.setWeightSum(8.0f);

                    //  Checkbox Product
                    final CheckBox checkProductView = new CheckBox(TaskAddActivity.this);
                    LinearLayout.LayoutParams checkProductParams = new LinearLayout.LayoutParams(
                            0, ViewGroup.LayoutParams.MATCH_PARENT, 0.75f);
                    checkProductView.setLayoutParams(checkProductParams);
                    checkProductView.setId(categoryList.get(iCategory).getProduct().get(iProduct).getId());
                    checkProductView.setChecked(Smart1CrmUtils.getProductChecked(categoryList.get(iCategory).getProduct().get(iProduct).getId(), idList));
                    linearProduct2.addView(checkProductView);

                    //  TextView Product Name
                    final TextView productNameView = new TextView(TaskAddActivity.this);
                    LinearLayout.LayoutParams productNameViewParams = new LinearLayout.LayoutParams(
                            0, ViewGroup.LayoutParams.MATCH_PARENT, 2.25f);
                    productNameView.setLayoutParams(productNameViewParams);
                    productNameView.setText(categoryList.get(iCategory).getProduct().get(iProduct).getName());
                    productNameView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    productNameView.setTextColor(
                            ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null));
                    productNameView.setGravity(Gravity.CENTER_VERTICAL);
                    linearProduct2.addView(productNameView);

                    //  EditText Description
                    final EditText editProductView = new EditText(TaskAddActivity.this);
                    LinearLayout.LayoutParams editProductViewParams = new LinearLayout.LayoutParams(
                            0, ViewGroup.LayoutParams.WRAP_CONTENT, 5.0f);
                    editProductViewParams.setMargins(0, Smart1CrmUtils.DisplayUtility.dpToPx(TaskAddActivity.this, 4), 0, Smart1CrmUtils.DisplayUtility.dpToPx(TaskAddActivity.this, 4));
                    editProductView.setLayoutParams(editProductViewParams);
                    editProductView.setId(categoryList.get(iCategory).getProduct().get(iProduct).getId());
                    editProductView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                    editProductView.setVisibility(Smart1CrmUtils.getProductDescription(categoryList.get(iCategory).getProduct().get(iProduct).getId(), idList, editTextList).equals("") ? View.GONE : View.VISIBLE);
                    editProductView.setHint("Note");
                    editProductView.setText(Smart1CrmUtils.getProductDescription(categoryList.get(iCategory).getProduct().get(iProduct).getId(), idList, editTextList));
                    editProductView.setHintTextColor(
                            ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null));
                    editProductView.setPadding(Smart1CrmUtils.DisplayUtility.dpToPx(TaskAddActivity.this, 8),
                            Smart1CrmUtils.DisplayUtility.dpToPx(TaskAddActivity.this, 8),
                            Smart1CrmUtils.DisplayUtility.dpToPx(TaskAddActivity.this, 8),
                            Smart1CrmUtils.DisplayUtility.dpToPx(TaskAddActivity.this, 8));
                    editProductView.setTextColor(
                            ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null));
                    editProductView.setBackgroundColor(
                            ResourcesCompat.getColor(getResources(), R.color.white, null));
                    editProductView.setGravity(Gravity.CENTER_VERTICAL);
                    linearProduct2.addView(editProductView);

                    final EditText editCategoryId = new EditText(TaskAddActivity.this);
                    LinearLayout.LayoutParams editCategoryIdParams = new LinearLayout.LayoutParams(
                            0, ViewGroup.LayoutParams.WRAP_CONTENT, 5.0f);
                    editCategoryIdParams.setMargins(0, Smart1CrmUtils.DisplayUtility.dpToPx(TaskAddActivity.this, 4), 0, Smart1CrmUtils.DisplayUtility.dpToPx(TaskAddActivity.this, 4));
                    editCategoryId.setLayoutParams(editCategoryIdParams);
                    editCategoryId.setId(categoryList.get(iCategory).getProduct().get(iProduct).getId());
                    editCategoryId.setText("" + categoryList.get(iCategory).getProduct().get(iProduct).getCategory_id());
                    editCategoryId.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                    editCategoryId.setVisibility(View.GONE);
                    linearProduct2.addView(editCategoryId);

                    linearProduct.addView(linearProduct2);

                    checkProductView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (checkProductView.isChecked()) {
                                editProductView.setVisibility(View.VISIBLE);
                                idList.add(checkProductView.getId());
                                categoryIdList.add(Integer.valueOf(editCategoryId.getText().toString()));
                                Log.d("TAG", "description add: " + editProductView.getText().toString());
                                editTextList.add(editProductView);
                                editProductView.requestFocus();
                            } else {
                                editProductView.setVisibility(View.GONE);
                                deleteFromIdList(checkProductView.getId());
                                deleteFromCategoryIdList(editProductView.getId());
                                deleteFromEditTextList();
                            }
                        }
                    });

                }
                linearCategory.addView(linearProduct);

                relativeCategory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (showView.isShown()) {
                            showView.setVisibility(View.GONE);
                            hideView.setVisibility(View.VISIBLE);
                            linearProduct.setVisibility(View.VISIBLE);
                        } else {
                            showView.setVisibility(View.VISIBLE);
                            hideView.setVisibility(View.GONE);
                            linearProduct.setVisibility(View.GONE);
                        }
                    }
                });

                layout_product.addView(linearCategory);

            }
        }

    }

    private void deleteFromIdList(int id) {
        for (int i = 0; i < idList.size(); i++) {
            if (idList.get(i) == id) {
                idList.remove(i);
            }
        }
    }

    private void deleteFromCategoryIdList(int categoryId) {
        for (int i = 0; i < categoryIdList.size(); i++) {
            if (categoryIdList.get(i) == categoryId) {
                categoryIdList.remove(i);
            }
        }
    }

    private void deleteFromEditTextList() {
        editTextList.remove(editTextList.size() - 1);
    }

    private void selectImage() {
        final CharSequence[] items = {"Ambil Foto", "Batal"};

        AlertDialog.Builder builder = new AlertDialog.Builder(TaskAddActivity.this);
        builder.setTitle("Tambah Foto!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Ambil Foto")) {
                    cameraIntent();

                } else if (items[item].equals("Batal")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        try {
            Uri photoURI = FileProvider.getUriForFile(TaskAddActivity.this, getApplicationContext().getPackageName() + ".provider", createImageFile());
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(intent, REQUEST_CAMERA);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "testphoto.jpg";
        File storageDir = new File(Environment.getExternalStorageDirectory().getPath(), imageFileName);
        photoPath = storageDir.getPath();
        Log.d("TAG", storageDir.getPath());

        return storageDir;
    }

    private void onCaptureImageResult(Intent data) {
        //Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        try {
            Bitmap thumbnail = Smart1CrmUtils.ImageUtility.getThumbnail(photoPath);

            myBase64Image = Smart1CrmUtils.ImageUtility.encodeToBase64(thumbnail, Bitmap.CompressFormat.JPEG, 100);

            String photo = PreferenceUtility.getInstance().loadDataString(TaskAddActivity.this,
                    PreferenceUtility.PHOTO);

            if (photo.equals("1")) {
                btn_take_pict1.setEnabled(false);
                btn_take_pict1.setBackgroundColor(getResources().getColor(R.color.green_600));
                foto1 = myBase64Image;
                PreferenceUtility.getInstance().saveData(TaskAddActivity.this, PreferenceUtility.PHOTO_PATH1, myBase64Image);
                Log.d("TAG", "foto1 " + PreferenceUtility.getInstance().loadDataString(TaskAddActivity.this, PreferenceUtility.PHOTO_PATH1));
            } else if (photo.equals("2")) {
                btn_take_pict2.setEnabled(false);
                btn_take_pict2.setBackgroundColor(getResources().getColor(R.color.green_600));
                foto2 = myBase64Image;
                PreferenceUtility.getInstance().saveData(TaskAddActivity.this, PreferenceUtility.PHOTO_PATH2, myBase64Image);
                Log.d("TAG", "foto2 " + PreferenceUtility.getInstance().loadDataString(TaskAddActivity.this, PreferenceUtility.PHOTO_PATH2));
            } else if (photo.equals("3")) {
                btn_take_pict3.setEnabled(false);
                btn_take_pict3.setBackgroundColor(getResources().getColor(R.color.green_600));
                foto3 = myBase64Image;
                PreferenceUtility.getInstance().saveData(TaskAddActivity.this, PreferenceUtility.PHOTO_PATH3, myBase64Image);
                Log.d("TAG", "foto3 " + PreferenceUtility.getInstance().loadDataString(TaskAddActivity.this, PreferenceUtility.PHOTO_PATH3));
            }

            File imgFile = new File(photoPath);
            imgFile.delete();
            photoPath = "";

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("TAG", e.getMessage());
        }
    }

    private String getCurrentDate() {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(c.getTime());
    }

    private void saveProductDataIfBackpressed() {
        try {
            JSONArray productArray = Smart1CrmUtils.JSONUtility.convertProductListToJSONArray(this.idList, this.categoryIdList, this.editTextList);
            saveDataToSharedPrefIfBackpressed(productArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveProductdata() {
        try {
            JSONArray productArray = Smart1CrmUtils.JSONUtility.convertProductListToJSONArray(this.idList, this.categoryIdList, this.editTextList);
            saveDataToSharedPref(productArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveDataToSharedPref(JSONArray jsonArray) {
        if (!customerId.equals("")) {
            this.task.setCustomer_id(Integer.valueOf(customerId));
            this.task.setCustomer_name(customerName);
            this.task.setNotes(edit_notes.getText().toString());
            this.task.setOrder_status("" + orderStatus);
            this.task.setLatitude(latitude);
            this.task.setLongitude(longitude);
            this.task.setFoto1(foto1);
            this.task.setFoto2(foto2);
            this.task.setFoto3(foto3);
            this.task.setProduct(jsonArray.toString());
            this.task.setCreated_at(createdAt);
            Smart1CrmUtils.DatabaseUtility.saveTaskIntoDatabase(TaskAddActivity.this, this.task);

            PreferenceUtility.getInstance().saveData(TaskAddActivity.this,
                    PreferenceUtility.CUSTOMER_ID, customerId);
            PreferenceUtility.getInstance().saveData(TaskAddActivity.this,
                    PreferenceUtility.CUSTOMER_NAME, customerName);
            PreferenceUtility.getInstance().saveData(TaskAddActivity.this,
                    PreferenceUtility.NOTES, notes);
            PreferenceUtility.getInstance().saveData(TaskAddActivity.this,
                    PreferenceUtility.ORDER_STATUS, String.valueOf(orderStatus));
            PreferenceUtility.getInstance().saveData(TaskAddActivity.this,
                    PreferenceUtility.LATITUDE, latitude);
            PreferenceUtility.getInstance().saveData(TaskAddActivity.this,
                    PreferenceUtility.LONGITUDE, longitude);
            PreferenceUtility.getInstance().saveData(TaskAddActivity.this,
                    PreferenceUtility.CREATED_AT, createdAt);
            PreferenceUtility.getInstance().saveData(TaskAddActivity.this,
                    PreferenceUtility.JSON_PRODUCT, jsonArray.toString());

            startActivityForResult(new Intent(TaskAddActivity.this, SignatureActivity.class), Smart1CrmUtils.SIGNATURE_ACTIVITY);
        }
    }

    private void saveDataToSharedPrefIfBackpressed(JSONArray jsonArray) {
        if (!customerId.equals("")) {
            this.task.setCustomer_id(Integer.valueOf(customerId));
            this.task.setCustomer_name(customerName);
            this.task.setNotes(edit_notes.getText().toString());
            this.task.setOrder_status("" + orderStatus);
            this.task.setLatitude(latitude);
            this.task.setLongitude(longitude);
            this.task.setFoto1(foto1);
            this.task.setFoto2(foto2);
            this.task.setFoto3(foto3);
            this.task.setProduct(jsonArray.toString());
            this.task.setCreated_at(createdAt);
            Smart1CrmUtils.DatabaseUtility.saveTaskIntoDatabase(TaskAddActivity.this, this.task);

            PreferenceUtility.getInstance().saveData(TaskAddActivity.this,
                    PreferenceUtility.CUSTOMER_ID, customerId);
            PreferenceUtility.getInstance().saveData(TaskAddActivity.this,
                    PreferenceUtility.CUSTOMER_NAME, customerName);
            PreferenceUtility.getInstance().saveData(TaskAddActivity.this,
                    PreferenceUtility.NOTES, notes);
            PreferenceUtility.getInstance().saveData(TaskAddActivity.this,
                    PreferenceUtility.ORDER_STATUS, String.valueOf(orderStatus));
            PreferenceUtility.getInstance().saveData(TaskAddActivity.this,
                    PreferenceUtility.LATITUDE, latitude);
            PreferenceUtility.getInstance().saveData(TaskAddActivity.this,
                    PreferenceUtility.LONGITUDE, longitude);
            PreferenceUtility.getInstance().saveData(TaskAddActivity.this,
                    PreferenceUtility.CREATED_AT, createdAt);
            PreferenceUtility.getInstance().saveData(TaskAddActivity.this,
                    PreferenceUtility.JSON_PRODUCT, jsonArray.toString());
        }
    }

    private void getLocation() {

        GpsTracker gpsTracker = new GpsTracker(TaskAddActivity.this);
        if (gpsTracker.canGetLocation()) {
            latitude = String.valueOf(gpsTracker.latitude);

            longitude = String.valueOf(gpsTracker.longitude);
            Log.d("TAG", "latitude " + latitude);
            Log.d("TAG", "longitude " + longitude);

        }
    }

    @Override
    public void finishedSetupCategory(String result, String json) {
        dismissLoadingDialog();
        if (result.equals("OK")) {
            Log.d("TAG", "category list " + json);
            try {
                this.categoryList = Smart1CrmUtils.JSONUtility.getCategoryListFromJSON(TaskAddActivity.this, json);
            } catch (JSONException e) {
                Log.d("TAG", "Exception task add category " + e.getMessage());
            }
        }else {

        }
    }

    @Override
    public void finishedSetupCategoryOffline(List<Category> categoryList) {
        dismissLoadingDialog();
        this.categoryList = categoryList;
    }
}
