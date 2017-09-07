package com.sales1crm.ekamant.sales1crm.activities.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sales1crm.ekamant.sales1crm.R;
import com.sales1crm.ekamant.sales1crm.activities.TaskAddActivity;
import com.sales1crm.ekamant.sales1crm.activities.adapters.AccountListDialogAdapter;
import com.sales1crm.ekamant.sales1crm.activities.callbacks.AccountListDialogCallback;
import com.sales1crm.ekamant.sales1crm.activities.callbacks.AccountListDialogPresenterCallback;
import com.sales1crm.ekamant.sales1crm.activities.models.Customer;
import com.sales1crm.ekamant.sales1crm.activities.presenters.AccountListDialogPresenter;
import com.sales1crm.ekamant.sales1crm.activities.utilities.ApiParam;
import com.sales1crm.ekamant.sales1crm.activities.widgets.CustomTextView;
import com.sales1crm.ekamant.sales1crm.activities.widgets.SearchEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by apridosandyasa on 4/13/17.
 */

public class AccountListDialog extends BaseDialogFragment implements AccountListDialogPresenterCallback {

    @InjectView(R.id.llSearchContainer)
    LinearLayout llHeader;

    @InjectView(R.id.ivSearch)
    ImageView ivSearch;

    @InjectView(R.id.etSearch)
    SearchEditText etSearch;

    @InjectView(R.id.rlIvClearSearch)
    RelativeLayout rlIvClearSearch;

    @InjectView(R.id.ivClearSearch)
    ImageView ivClearSearch;

    @InjectView(R.id.lvSearch)
    ListView lvSearch;

    @InjectView(R.id.llListContainer)
    LinearLayout llListContainer;

    @InjectView(R.id.lvAccount)
    ListView lvAccount;

    @InjectView(R.id.llsideIndex)
    LinearLayout llsideIndex;

    private Context context;
    private View view;
    private AccountListDialogCallback callback;
    private AccountListDialogPresenter accountListDialogPresenter;
    private AccountListDialogAdapter adapter;
    private GestureDetector mGestureDetector;
    private int prevSize = 0;
    private boolean isLoadMore = false;
    private boolean isMaxSize = false;
    private int sideIndexHeight;
    private static float sideIndexX;
    private static float sideIndexY;
    private int indexListSize;
    private List<Customer> customerList = new ArrayList<>();
    private List<Object[]> alphabet = new ArrayList<Object[]>();
    private HashMap<String, Integer> sections = new HashMap<String, Integer>();

    public AccountListDialog() {

    }

    @SuppressLint("ValidFragment")
    public AccountListDialog(Context context, AccountListDialogCallback listener) {
        this.context = context;
        this.callback = listener;
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
        windowParams.width = (int) (this.context.getResources().getDisplayMetrics().widthPixels * 0.99);
        windowParams.height = (int) (this.context.getResources().getDisplayMetrics().heightPixels * 0.97);
        window.setAttributes(windowParams);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreateView(inflater, container, savedInstanceState);

        this.view = inflater.inflate(R.layout.dialog_account_list, container, false);

        return this.view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);

        this.accountListDialogPresenter = new AccountListDialogPresenter(this.context, this);

        initView();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    private void initView() {
        rlIvClearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setText("");
                etSearch.clearFocus();
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);

                // SearchListData.clear();
                // searchAdapter.notifyDataSetChanged();
            }
        });

        lvAccount.setOnScrollListener(new AccountListLazyLoad());

        setTextWatcherForSearch();

        ((TaskAddActivity)this.context).showLoadingDialog();
        this.accountListDialogPresenter.setupAccountListViews(ApiParam.API_012, prevSize, etSearch.getText().toString());
    }

    private void setTextWatcherForSearch() {
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    lvAccount.setAdapter(null);
                    adapter = null;
                    isMaxSize = false;
                    prevSize = 0;
                    customerList.clear();
                    String search = etSearch.getText().toString();
                    ((TaskAddActivity)context).showLoadingDialog();
                    accountListDialogPresenter.setupAccountListViews(ApiParam.API_012, prevSize, search);
                }

                return true;
            }
        });
    }

    private void setList() {
        adapter = new AccountListDialogAdapter(context, this.customerList);
        mGestureDetector = new GestureDetector(this.context,
                new SideIndexGestureListener());
        alphabet.clear();

        List<Customer> countries = this.customerList;
        Collections.sort(countries, new Comparator<Customer>() {
            @Override public int compare(Customer  o1, Customer  o2) {
                if (o1 == o2)
                    return 0;
                if (o1 == null)
                    return -1;
                if (o2 == null)
                    return 1;

                String country1  = o1.getCustomer_name();
                String country2  = o2.getCustomer_name();

                if (country1 == country2)
                    return 0;
                if (country1 == null)
                    return -1;
                if (country2 == null)
                    return 1;
                return country1.compareTo(country2);
            }
        });

        List<AccountListDialogAdapter.Row> rows = new ArrayList<AccountListDialogAdapter.Row>();
        int start = 0;
        int end = 0;
        String previousLetter = null;
        Object[] tmpIndexItem = null;
        Pattern numberPattern = Pattern.compile("[0-9]");
        String firstLetter = "";

        for (Customer country : countries) {
            if (country.getCustomer_name().length() > 0)
                firstLetter = country.getCustomer_name().substring(0, 1)
                        .toUpperCase(Locale.UK);
            else
                firstLetter = country.getCustomer_name().toUpperCase(Locale.UK);

            // Group numbers together in the scroller
            if (numberPattern.matcher(firstLetter).matches()) {
                firstLetter = "#";
            }

            // If we've changed to a new letter, add the previous letter to the
            // alphabet scroller
            if (previousLetter != null && !firstLetter.equals(previousLetter)) {
                end = rows.size() - 1;
                tmpIndexItem = new Object[3];
                tmpIndexItem[0] = previousLetter.toUpperCase(Locale.UK);
                tmpIndexItem[1] = start;
                tmpIndexItem[2] = end;
                alphabet.add(tmpIndexItem);

                start = end + 1;
            }

            // Check if we need to add a header row
            if (!firstLetter.equals(previousLetter)) {
                rows.add(new AccountListDialogAdapter.Section(firstLetter));
                sections.put(firstLetter, start);
            }

            // Add the country to the list
            rows.add(new AccountListDialogAdapter.Item(country));
            previousLetter = firstLetter;
        }

        if (previousLetter != null) {
            // Save the last letter
            tmpIndexItem = new Object[3];
            tmpIndexItem[0] = previousLetter.toUpperCase(Locale.UK);
            tmpIndexItem[1] = start;
            tmpIndexItem[2] = rows.size() - 1;
            alphabet.add(tmpIndexItem);
        }

        adapter.setRows(rows);
        lvAccount.setAdapter(adapter);
        lvAccount.setOnItemClickListener(new AccountListDialogItemClick());

        updateList();
    }

    private void setMoreList() {
        mGestureDetector = new GestureDetector(this.context,
                new SideIndexGestureListener());
        alphabet.clear();

        List<Customer> countries = this.customerList;
        Collections.sort(countries, new Comparator<Customer>() {
            @Override public int compare(Customer  o1, Customer  o2) {
                if (o1 == o2)
                    return 0;
                if (o1 == null)
                    return -1;
                if (o2 == null)
                    return 1;

                String country1  = o1.getCustomer_name();
                String country2  = o2.getCustomer_name();

                if (country1 == country2)
                    return 0;
                if (country1 == null)
                    return -1;
                if (country2 == null)
                    return 1;
                return country1.compareTo(country2);
            }
        });

        List<AccountListDialogAdapter.Row> rows = new ArrayList<AccountListDialogAdapter.Row>();
        int start = 0;
        int end = 0;
        String previousLetter = null;
        Object[] tmpIndexItem = null;
        Pattern numberPattern = Pattern.compile("[0-9]");
        String firstLetter = "";

        for (Customer country : countries) {
            if (country.getCustomer_name().length() > 0)
                firstLetter = country.getCustomer_name().substring(0, 1)
                        .toUpperCase(Locale.UK);
            else
                firstLetter = country.getCustomer_name().toUpperCase(Locale.UK);

            // Group numbers together in the scroller
            if (numberPattern.matcher(firstLetter).matches()) {
                firstLetter = "#";
            }

            // If we've changed to a new letter, add the previous letter to the
            // alphabet scroller
            if (previousLetter != null && !firstLetter.equals(previousLetter)) {
                end = rows.size() - 1;
                tmpIndexItem = new Object[3];
                tmpIndexItem[0] = previousLetter.toUpperCase(Locale.UK);
                tmpIndexItem[1] = start;
                tmpIndexItem[2] = end;
                alphabet.add(tmpIndexItem);

                start = end + 1;
            }

            // Check if we need to add a header row
            if (!firstLetter.equals(previousLetter)) {
                rows.add(new AccountListDialogAdapter.Section(firstLetter));
                sections.put(firstLetter, start);
            }

            // Add the country to the list
            rows.add(new AccountListDialogAdapter.Item(country));
            previousLetter = firstLetter;
        }

        if (previousLetter != null) {
            // Save the last letter
            tmpIndexItem = new Object[3];
            tmpIndexItem[0] = previousLetter.toUpperCase(Locale.UK);
            tmpIndexItem[1] = start;
            tmpIndexItem[2] = rows.size() - 1;
            alphabet.add(tmpIndexItem);
        }

        adapter.setRows(rows);

        updateList();
    }

    public void updateList() {
        LinearLayout sideIndex = (LinearLayout) view.findViewById(R.id.llsideIndex);
        sideIndex.removeAllViews();
        indexListSize = alphabet.size();
        if (indexListSize < 1) {
            return;
        }

        int indexMaxSize = (int) Math.floor(sideIndex.getHeight() / 20);
        int tmpIndexListSize = indexListSize;
        while (tmpIndexListSize > indexMaxSize) {
            tmpIndexListSize = tmpIndexListSize / 2;
        }
        double delta;
        if (tmpIndexListSize > 0) {
            delta = indexListSize / tmpIndexListSize;
        } else {
            delta = 1;
        }

        CustomTextView tmpTV;
        for (double i = 1; i <= indexListSize; i = i + delta) {
            Object[] tmpIndexItem = alphabet.get((int) i - 1);
            String tmpLetter = tmpIndexItem[0].toString();

            tmpTV = new CustomTextView(this.context);
            tmpTV.setText(tmpLetter);
            tmpTV.setGravity(Gravity.CENTER);
            tmpTV.setCustomFont(context, "OpenSans-Semibold.ttf");
            tmpTV.setTextSize(15);
            tmpTV.setTextColor(context.getResources().getColor(R.color.color_title));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            tmpTV.setLayoutParams(params);
            sideIndex.addView(tmpTV);
        }

        sideIndexHeight = sideIndex.getHeight();

        sideIndex.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // now you know coordinates of touch
                sideIndexX = event.getX();
                sideIndexY = event.getY();

                // and can display a proper item it country list
                displayListItem();

                return false;
            }
        });
    }

    public void displayListItem() {
        LinearLayout sideIndex = (LinearLayout) view.findViewById(R.id.llsideIndex);
        sideIndexHeight = sideIndex.getHeight();
        // compute number of pixels for every side index item
        double pixelPerIndexItem = (double) sideIndexHeight / indexListSize;

        // compute the item index for given event position belongs to
        int itemPosition = (int) (sideIndexY / pixelPerIndexItem);

        // get the item (we can do it since we know item index)
        if (itemPosition < alphabet.size()) {
            Object[] indexItem = alphabet.get(itemPosition);
            int subitemPosition = sections.get(indexItem[0]);
            // ListView listView = (ListView) findViewById(android.R.id.list);
            lvAccount.setSelection(subitemPosition);
        }
    }

    class SideIndexGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            sideIndexX = sideIndexX - distanceX;
            sideIndexY = sideIndexY - distanceY;

            if (sideIndexX >= 0 && sideIndexY >= 0) {
                displayListItem();
            }

            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    @Override
    public void finishedSetupAccountListViews(String result, List<Customer> customerList) {
        ((TaskAddActivity)this.context).dismissLoadingDialog();
        if (result.equals("OK")) {
            Log.d("TAG", "account size " + customerList.size());
            prevSize = customerList.size();
            this.customerList = customerList;
            setList();
        }else {

        }
    }

    @Override
    public void finishedSetupMoreAccountListViews(String result, List<Customer> customerList) {
        if (result.equals("OK")) {
            for (int i = 0; i < customerList.size(); i++) {
                this.customerList.add(customerList.get(i));
            }
            Log.d("TAG", "account size " + this.customerList.size());
            if (this.prevSize == this.customerList.size())
                isMaxSize = true;
            this.prevSize = this.customerList.size();
            setMoreList();
        }else {

        }
        ((TaskAddActivity)this.context).dismissLoadingDialog();
        this.isLoadMore = false;
    }

    @Override
    public void finishedSetupAccountListViewsOffline(List<Customer> customerList) {
        ((TaskAddActivity)this.context).dismissLoadingDialog();
        prevSize = customerList.size();
        this.customerList = customerList;
        setList();
    }

    @Override
    public void finishedSetupMoreAccountListViewsOffline(List<Customer> customerList) {
        ((TaskAddActivity)this.context).dismissLoadingDialog();
    }

    private class AccountListDialogItemClick implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // TODO Auto-generated method stub
            AccountListDialogAdapter.Item item = (AccountListDialogAdapter.Item) lvAccount.getAdapter().getItem(position);
            Bundle bundle = new Bundle();
            bundle.putInt("account_id", item.text.getCustomer_id());
            bundle.putString("account_name", item.text.getCustomer_name());
            bundle.putString("account_address", item.text.getCustomer_address());
            bundle.putInt("account_type_id", item.text.getCustomer_type());
            bundle.putString("account_tipe", item.text.getCustomer_tipe());
            bundle.putString("account_code", item.text.getCustomer_code());
            callback.onAccountListDialogCallback(bundle);
            dismiss();
        }

    }

    private class AccountListLazyLoad implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
            {
                if(isLoadMore == false && isMaxSize == false)
                {
                    isLoadMore = true;
                    prevSize = customerList.size();
                    ((TaskAddActivity)context).showLoadingDialog();
                    accountListDialogPresenter.setupMoreAccountListViews(ApiParam.API_012, prevSize, etSearch.getText().toString());
                }
            }
        }
    }

}
