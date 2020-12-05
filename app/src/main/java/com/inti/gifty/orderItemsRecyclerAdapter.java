package com.inti.gifty;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class orderItemsRecyclerAdapter extends RecyclerView.Adapter<orderItemsRecyclerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CartItem> items;

    public orderItemsRecyclerAdapter(Context context, ArrayList<CartItem> items) {
        this.context = context;
        this.items = items;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView orderItemImage;
        TextView orderItemName, orderItemShop, orderItemPrice, orderItemQuantity;
        CardView orderCard;
        ViewHolder(View view) {
            super(view);
            orderItemImage = view.findViewById(R.id.order_item_image);
            orderItemName = view.findViewById(R.id.order_item_name);
            orderItemShop = view.findViewById(R.id.order_item_shop);
            orderItemPrice = view.findViewById(R.id.order_item_price);
            orderItemQuantity = view.findViewById(R.id.order_item_quantity);
            orderCard = view.findViewById(R.id.order_item_card_view);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item_recycler_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = items.get(position);
        holder.orderItemName.setText(item.getName());
        holder.orderItemShop.setText(item.getShop());
        holder.orderItemPrice.setText("RM " + String.format("%.2f", item.getPrice()));
        holder.orderItemQuantity.setText("x" + item.getQuantity());
        Glide.with(context).load(item.getImage()).placeholder(R.color.light_gray).centerCrop().into(holder.orderItemImage);

        switch (position % 3){
            case 0: holder.orderCard.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimarySuperLight)); break;
            case 1: holder.orderCard.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimaryLight)); break;
            case 2: holder.orderCard.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimary)); break;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
