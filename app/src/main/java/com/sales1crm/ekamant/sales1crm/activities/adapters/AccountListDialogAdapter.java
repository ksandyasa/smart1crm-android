package com.sales1crm.ekamant.sales1crm.activities.adapters;

import java.util.ArrayList;
import java.util.List;

import com.sales1crm.ekamant.sales1crm.R;
import com.sales1crm.ekamant.sales1crm.activities.models.Customer;
import com.sales1crm.ekamant.sales1crm.activities.utilities.PreferenceUtility;
import com.sales1crm.ekamant.sales1crm.activities.widgets.CustomTextView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AccountListDialogAdapter extends BaseAdapter {
	
	private Context context;
	private Bitmap icon;
	private String url;
	private List<Customer> AccListData;
	
	public AccountListDialogAdapter(Context context, List<Customer> objects) {
		this.context = context;
		this.AccListData = objects;
		icon = BitmapFactory.decodeResource(this.context.getResources(),
				R.drawable.account_avatar);
		url = PreferenceUtility.getInstance().loadDataString(this.context,
				PreferenceUtility.URL);
	}

	public static abstract class Row {
	}

	// This is for making Section, ex: A B C D etc
	public static final class Section extends Row {
		public final String text;

		public Section(String text) {
			this.text = text;
		}
	}

	// This is for making Row, ex: A B C D etc
	public static final class Item extends Row {
		public final Customer text;

		public Item(Customer text) {
			this.text = text;
		}

		public Customer getItem() {
			return text;
		}
	}

	private List<Row> rows;

	public void setRows(List<Row> rows) {
		this.rows = rows;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return rows.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return rows.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		if (getItem(position) instanceof Section) {
			return 1;
		} else {
			return 0;
		}
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		if (getItemViewType(position) == 0) { // Item
			Item item = (Item) getItem(position);
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) parent.getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = (LinearLayout) inflater.inflate(
						R.layout.row_account_list, parent, false);
				holder = new ViewHolder();
				holder.tvAccountName = (CustomTextView) view
						.findViewById(R.id.tvAccountName);
				holder.tvStatus = (CustomTextView) view.findViewById(R.id.tvStatus);
				holder.tvEmail = (CustomTextView) view.findViewById(R.id.tvEmail);
				holder.tvIndustry = (CustomTextView) view
						.findViewById(R.id.tvIndustry);
				holder.tvPhone = (CustomTextView) view.findViewById(R.id.tvPhone);
				holder.llDetailStatus = (LinearLayout) view
						.findViewById(R.id.llDetailStatus);

				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			// 1:analyst, 2:competitor, 3:customer, 4:integrator, 5:investor,
			// 6:partner, 7:press, 8:prospect, 9:reseller, 10:other

			if (item.text.getCustomer_type() == 2) {
				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
					holder.llDetailStatus.setBackground(context.getResources()
							.getDrawable(R.drawable.competitor_bg));
				} else {
					holder.llDetailStatus.setBackgroundDrawable(context
							.getResources().getDrawable(
									R.drawable.competitor_bg));
				}
			} else {
				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
					holder.llDetailStatus.setBackground(context.getResources()
							.getDrawable(R.drawable.customer_bg));
				} else {
					holder.llDetailStatus.setBackgroundDrawable(context
							.getResources().getDrawable(
									R.drawable.customer_bg));
				}
			}
			holder.tvStatus.setText(item.text.getCustomer_tipe());
			holder.tvIndustry.setText(item.text.getCustomer_code());
			holder.tvPhone.setText(item.text.getCustomer_name());
			holder.tvEmail.setText(item.text.getCustomer_address());

		} else { // Section
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) parent.getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = (LinearLayout) inflater.inflate(R.layout.section_child,
						parent, false);
			}
			view.setClickable(false);
			view.setTag("section");
			Section section = (Section) getItem(position);
			TextView textView = (TextView) view.findViewById(R.id.tvSection);
			textView.setText(section.text);
		}

		return view;
	}

	private static class ViewHolder {
		CustomTextView tvAccountName, tvIndustry, tvPhone, tvStatus, tvEmail;
		ImageView ivAccountPict;
		LinearLayout llDetailStatus;
	}

}
