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

public class wishlistSelectorSpinnerAdapter extends ArrayAdapter<Friend> {

    private ArrayList<Friend> wishlists;
    private Context context;

    public wishlistSelectorSpinnerAdapter(Context context, ArrayList<Friend> wishlists){
        super(context, 0, wishlists);
        this.wishlists = wishlists;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.wishlist_selection_spinner_item, parent, false);
        }

        Friend wishlist = getItem(position);

        ImageView friendImage = convertView.findViewById(R.id.friend_image);
        TextView occasionName = convertView.findViewById(R.id.occasion_text);
        TextView itemsCount = convertView.findViewById(R.id.cart_items_count_text);

        Glide.with(context).load(wishlist.getImage()).placeholder(R.drawable.ic_profile_fill_super_light).circleCrop().into(friendImage);
        occasionName.setText(wishlist.getName() + "\'s " + wishlist.getOccasion());
        itemsCount.setText("(" + wishlist.getWishlist().size() + ")");


        return convertView;
    }

}
