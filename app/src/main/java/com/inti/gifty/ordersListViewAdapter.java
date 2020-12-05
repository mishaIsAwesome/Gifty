package com.inti.gifty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ordersListViewAdapter extends ArrayAdapter<Order> {

    private Context context;

    public ordersListViewAdapter(Context context, ArrayList<Order> orderItems){
        super(context, 0, orderItems);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Order order = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.orders_list_item, parent, false);
        }

        TextView orderOccasion = convertView.findViewById(R.id.order_occasion);
        ImageView orderImage = convertView.findViewById(R.id.order_item_image);
        TextView orderNumber = convertView.findViewById(R.id.order_number);
        TextView orderDate = convertView.findViewById(R.id.order_date);
        TextView orderPrice = convertView.findViewById(R.id.order_total_price);
        TextView orderStatus = convertView.findViewById(R.id.order_status);

        orderOccasion.setText(order.getFriend().getName() + "'s " + order.getFriend().getOccasion());
        String url = order.getFriend().getWishlist().get(0).getImage();
        Glide.with(context).load(url).placeholder(R.color.light_gray).centerCrop().into(orderImage);
        orderNumber.setText("Order Number: ");
        orderDate.setText("Order Date: ");
        orderPrice.setText("Total: ");
        orderNumber.append(order.getId());
        orderDate.append(order.getDateOrdered());
        orderPrice.append("RM " + String.format("%.2f", order.getTotalAmount()));
        orderStatus.setText(order.getStatus());

        return convertView;
    }
}
