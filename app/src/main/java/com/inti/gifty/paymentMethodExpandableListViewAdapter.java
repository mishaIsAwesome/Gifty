package com.inti.gifty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class paymentMethodExpandableListViewAdapter extends BaseExpandableListAdapter {

    Context context;
    ArrayList<String> listGroup;
    HashMap<String, ArrayList<PaymentMethod>> listChild;

    public paymentMethodExpandableListViewAdapter (Context context, ArrayList<String> listGroup, HashMap<String, ArrayList<PaymentMethod>> listChild){
        this.context = context;
        this.listGroup = listGroup;
        this.listChild = listChild;
    }

    @Override
    public int getGroupCount() {
        return listGroup.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listChild.get(listGroup.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listGroup.get(groupPosition);
    }

    @Override
    public PaymentMethod getChild(int groupPosition, int childPosition) {
        return listChild.get(listGroup.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.payment_expandable_header, null);
        }
        TextView headerTitle = convertView.findViewById(R.id.payment_method_option_header);
        ImageView arrowImage = convertView.findViewById(R.id.expandable_arrow);
        headerTitle.setText(String.valueOf(getGroup(groupPosition)));

        if (isExpanded) {
            arrowImage.setImageResource(R.drawable.ic_arrow_up);
        } else {
            arrowImage.setImageResource(R.drawable.ic_arrow_down);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.payment_expandable_item, null);
        }
        TextView optionText = convertView.findViewById(R.id.payment_option_text);
        ImageView optionIcon = convertView.findViewById(R.id.payment_option_image);
        ImageView optionChecked = convertView.findViewById(R.id.payment_option_checked);
        optionText.setText(getChild(groupPosition, childPosition).getName());
        optionIcon.setImageDrawable(getChild(groupPosition, childPosition).getIcon());
        if (getChild(groupPosition, childPosition).getSelected()){
            optionChecked.setVisibility(View.VISIBLE);
        } else
            optionChecked.setVisibility(View.INVISIBLE);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
