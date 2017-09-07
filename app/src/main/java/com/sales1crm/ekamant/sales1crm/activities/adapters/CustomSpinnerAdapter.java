package com.sales1crm.ekamant.sales1crm.activities.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.sales1crm.ekamant.sales1crm.R;
import com.sales1crm.ekamant.sales1crm.activities.utilities.Smart1CrmUtils;

/**
 * Created by MatthewLim on 2/22/17.
 */

public class CustomSpinnerAdapter extends ArrayAdapter<String>{

    private Context context;
    private int resource;
    private String[] list;

    public CustomSpinnerAdapter(Context context, int resource,
                                String[] objects) {
        super(context, resource, objects);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.resource = resource;
        this.list = objects;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        // TODO Auto-generated method stub
        return getCustomView(position, convertView, parent);
    }

//	  @Override
//	  public View getView(int position, View convertView, ViewGroup parent) {
//	   // TODO Auto-generated method stub
//	   return getCustomView(position, convertView, parent);
//	  }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        //return super.getView(position, convertView, parent);

        View row= LayoutInflater.from(context).inflate(resource, parent, false);
        TextView label=(TextView)row.findViewById(R.id.tvSpinner);
        label.setText(list[position]);
        label.setHeight((int) Smart1CrmUtils.DisplayUtility.convertDpToPixel(context, 40));
        label.setTextSize(16);
        if(position == 0){
            label.setHeight(0);
            label.setVisibility(View.GONE);
        }

        // Hide scroll bar because it appears sometimes unnecessarily, this does not prevent scrolling
        parent.setVerticalScrollBarEnabled(false);

        return row;
    }

}
