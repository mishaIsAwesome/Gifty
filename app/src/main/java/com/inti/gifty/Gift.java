package com.inti.gifty;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class Gift extends Fragment {

    DatabaseReference dbUsers;

    ImageView giftImage;
    TextView giftName, giftShop, giftPrice, giftDesc, giftQuantity;
    Button backButton, plusButton, minusButton, addToWishlistButton, heartButton;

    Item gift;
    int quantity = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gift, container, false);
        if (getArguments() != null)
            gift = (Item) getArguments().getSerializable("item");

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Favourites");

        giftImage = view.findViewById(R.id.gift_image);
        giftName = view.findViewById(R.id.gift_name);
        giftShop = view.findViewById(R.id.gift_shop);
        giftPrice = view.findViewById(R.id.gift_price);
        giftDesc = view.findViewById(R.id.gift_desc);
        backButton = view.findViewById(R.id.back_button);
        giftQuantity = view.findViewById(R.id.gift_quantity);
        plusButton = view.findViewById(R.id.plus_button);
        minusButton = view.findViewById(R.id.minus_button);
        heartButton = view.findViewById(R.id.heart_button);
        addToWishlistButton = view.findViewById(R.id.add_to_wishlist_button);

        dbUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item: snapshot.getChildren()){
                    if (item.getKey().equals(gift.getId()))
                        heartButton.setActivated(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if (getContext() != null)
            Glide.with(getContext()).load(gift.getImage()).centerCrop().into(giftImage);
        giftName.setText(gift.getName());
        giftShop.setText(gift.getShop());
        giftPrice.setText("RM " + gift.getPrice());
        giftDesc.setText(gift.getDesc());

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity < 50){
                    quantity ++;
                    updateQuantity();
                }
            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 1){
                    quantity --;
                    updateQuantity();
                }
            }
        });

        heartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean newState = !heartButton.isActivated();
                heartButton.setActivated(newState);
                if (newState)
                    dbUsers.child(gift.getId()).setValue(gift);
                else
                    dbUsers.child(gift.getId()).removeValue();
            }
        });

        addToWishlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartItem cartItem = new CartItem(gift.getId(), gift.getName(), gift.getShop(), gift.getImage(), gift.getPrice(), gift.getDesc(), gift.getRating(), quantity, quantity*gift.getPrice());
                WishlistSelectorBottomFragment wishlistSelector = new WishlistSelectorBottomFragment();
                Bundle args = new Bundle();
                args.putSerializable("cartItem", cartItem);
                wishlistSelector.setArguments(args);
                wishlistSelector.show(getActivity().getSupportFragmentManager(), "WISHLIST_SELECTOR");
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStackImmediate();
            }
        });

        return view;
    }

    public void updateQuantity(){
        giftQuantity.setText(String.valueOf(quantity));
    }
}