package com.inti.gifty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class wishlistSelectorListViewAdapter extends ArrayAdapter<Friend> {

    public wishlistSelectorListViewAdapter(Context context, ArrayList<Friend> friends){
        super(context, 0, friends);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Friend friend = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.wishlist_selection_item, parent, false);
        }

        TextView wishlistName = convertView.findViewById(R.id.wishlist_text);
        TextView cartItemsCount = convertView.findViewById(R.id.cart_items_count_text);

        wishlistName.setText(friend.getName() + "\'s " + friend.getOccasion());
        cartItemsCount.setText(String.valueOf(friend.getWishlist().size()));

        return  convertView;
    }
}
