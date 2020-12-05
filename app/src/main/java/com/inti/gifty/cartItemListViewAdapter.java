package com.inti.gifty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class cartItemListViewAdapter extends ArrayAdapter<CartItem> {

    private DatabaseReference dbUsers;
    private ArrayList<CartItem> cartItems;
    private String friendID;

    public cartItemListViewAdapter(Context context, ArrayList<CartItem> cartItems, String friendID){
        super(context, 0, cartItems);
        this.cartItems = cartItems;
        this.friendID = friendID;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final CartItem item = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.cart_item, parent, false);
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("friends").child(friendID).child("wishlist");

        TextView cartItemName = convertView.findViewById(R.id.cart_item_name_text);
        TextView cartItemShop = convertView.findViewById(R.id.cart_item_shop_text);
        TextView cartItemPrice = convertView.findViewById(R.id.cart_item_price_text);
        final TextView cartItemQuantity = convertView.findViewById(R.id.cart_item_quantity);
        ImageView cartItemImage = convertView.findViewById(R.id.cart_item_image);
        ImageButton deleteCartItemButton = convertView.findViewById(R.id.cart_item_delete_button);
        Button plusButton = convertView.findViewById(R.id.cart_item_plus_button);
        Button minusButton = convertView.findViewById(R.id.cart_item_minus_button);

        Glide.with(getContext()).load(item.getImage()).placeholder(R.color.light_gray).centerCrop().into(cartItemImage);
        cartItemName.setText(item.getName());
        cartItemShop.setText(item.getShop());
        cartItemPrice.setText(String.valueOf(item.getPrice()));
        cartItemQuantity.setText(String.valueOf(item.getQuantity()));

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.getQuantity() < 50){
                    item.setQuantity(item.getQuantity() + 1);
                    dbUsers.child(item.getId()).child("quantity").setValue(item.getQuantity());
                    dbUsers.child(item.getId()).child("amount").setValue(item.getQuantity() * item.getPrice());
                    cartItemQuantity.setText(String.valueOf(item.getQuantity()));
                }
            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.getQuantity() > 1){
                    item.setQuantity(item.getQuantity() - 1);
                    dbUsers.child(item.getId()).child("quantity").setValue(item.getQuantity());
                    dbUsers.child(item.getId()).child("amount").setValue(item.getQuantity() * item.getPrice());
                    cartItemQuantity.setText(String.valueOf(item.getQuantity()));
                }
            }
        });

        deleteCartItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(item);
                dbUsers.removeValue();
                for (int i = 0; i < cartItems.size(); i++){
                    dbUsers.child("ct" + String.format("%03d", (i + 1))).setValue(cartItems.get(i));
                }
            }
        });

        return convertView;
    }

    @Override
    public void remove(@Nullable CartItem object) {
        super.remove(object);
    }
}
