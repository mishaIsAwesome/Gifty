package com.inti.gifty;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Wishlist extends Fragment {

    DatabaseReference dbUsers;

    ImageView friendImage;
    TextView friendName, friendDate, friendAddress, friendNotes, itemsLabel, emptyListMessage;
    ImageButton backButton, editButton;
    Button checkoutButton;
    ListView cartItemsListView;

    String friendID;
    Friend friend;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);
        if (getArguments() != null)
            friendID =  getArguments().getString("friendID");

        friendName = view.findViewById(R.id.friend_name);
        friendDate = view.findViewById(R.id.friend_date);
        friendAddress = view.findViewById(R.id.friend_address);
        friendNotes = view.findViewById(R.id.friend_notes);
        friendImage = view.findViewById(R.id.friend_image);
        itemsLabel = view.findViewById(R.id.items_label);
        emptyListMessage = view.findViewById(R.id.list_empty_text);
        backButton = view.findViewById(R.id.back_button);
        editButton = view.findViewById(R.id.edit_button);
        checkoutButton = view.findViewById(R.id.checkout_button);

        cartItemsListView = view.findViewById(R.id.cart_items_list_view);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("friends").child(friendID);
        dbUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;
                String id = snapshot.getKey();
                String name = snapshot.child("name").getValue().toString();
                String image = snapshot.child("image").getValue().toString();
                String occasion = snapshot.child("occasion").getValue().toString();
                String date = snapshot.child("date").getValue().toString();
                String occurrence = snapshot.child("occurrence").getValue().toString();
                int reminderId = snapshot.child("reminderId").getValue(Integer.class);
                String addressLine = snapshot.child("addressLine").getValue().toString();
                String state = snapshot.child("state").getValue().toString();
                String city = snapshot.child("city").getValue().toString();
                String postalCode = snapshot.child("postalCode").getValue().toString();
                String notes = snapshot.child("notes").getValue().toString();
                Boolean priority = Boolean.parseBoolean(snapshot.child("priority").getValue().toString());

                ArrayList<CartItem> wishlist = new ArrayList<>();
                for (DataSnapshot item : snapshot.child("wishlist").getChildren()){
                    String itemId = item.getKey();
                    String itemName = item.child("name").getValue().toString();
                    String itemShop = item.child("shop").getValue().toString();
                    double itemPrice = item.child("price").getValue(Integer.class);
                    String itemImage = item.child("image").getValue().toString();
                    String itemDesc = item.child("desc").getValue().toString();
                    double itemRating = item.child("rating").getValue(Integer.class);
                    int itemQuantity = item.child("quantity").getValue(Integer.class);
                    double itemAmount = item.child("amount").getValue(Integer.class);
                    CartItem newItem = new CartItem(itemId, itemName, itemShop, itemImage, itemPrice, itemDesc, itemRating, itemQuantity, itemAmount);
                    wishlist.add(newItem);
                }
                Friend newFriend = new Friend(id, name, image, occasion, date, occurrence, reminderId, addressLine, state, city, postalCode, notes, priority, wishlist);
                friend = newFriend;
                fillFriendDetails();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new WishlistForm();
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle args = new Bundle();
                args.putSerializable("friend", friend);
                fragment.setArguments(args);
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStackImmediate();
            }
        });

        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToMyCart();
            }
        });

        return view;
    }

    public void fillFriendDetails() {
        if (getContext() != null) {
            Glide.with(getContext()).load(friend.getImage()).placeholder(R.drawable.ic_profile_fill_super_light).circleCrop().into(friendImage);
            friendName.setText(friend.getName());
            friendAddress.setText(friend.getAddressLine());
            friendDate.setText(friend.getDate());
            if (friend.getNotes().isEmpty()) {
                friendNotes.setText("-");
            } else
                friendNotes.setText(friend.getNotes());
            itemsLabel.setText("Items (" + friend.getWishlist().size() + ")");

            if (friend.getWishlist().isEmpty()) {
                emptyListMessage.setVisibility(View.VISIBLE);
                checkoutButton.setEnabled(false);
            } else {
                emptyListMessage.setVisibility(View.GONE);
                checkoutButton.setEnabled(true);
                fillWishlist();
            }
        }
    }

    public void fillWishlist() {
        if (getActivity() != null) {
            cartItemsListView.setAdapter(null);
            final cartItemListViewAdapter adapter = new cartItemListViewAdapter(getActivity(), friend.getWishlist(), friend.getId());
            cartItemsListView.setAdapter(adapter);
        }
    }

    public void sendToMyCart(){
        Fragment fragment = new Cart();
        AppCompatActivity activity = (AppCompatActivity) getContext();
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putString("friendID", friend.getId());
        fragment.setArguments(args);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}