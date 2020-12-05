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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

public class Cart extends Fragment {

    DatabaseReference dbUsers;

    Spinner wishlistSelectorSpinner;
    ListView checkoutItemsListView;
    TextView subtotalText, shippingFeesText, totalText;
    Button payNowButton;
    ImageButton backButton;

    ArrayList<Friend> friendsList = new ArrayList<Friend>();
    String selectedFriendID;
    double subTotalPrice = 0;
    double totalPrice = 0;
    double shippingFee = 4.00;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null)
            selectedFriendID = getArguments().getString("friendID");

        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        wishlistSelectorSpinner = view.findViewById(R.id.wishlist_selection_spinner);
        checkoutItemsListView = view.findViewById(R.id.checkout_items_list_view);
        subtotalText = view.findViewById(R.id.checkout_subtotal_text);;
        shippingFeesText = view.findViewById(R.id.checkout_shipping_fee_text);
        totalText = view.findViewById(R.id.checkout_total_text);
        payNowButton = view.findViewById(R.id.pay_now_button);
        backButton = view.findViewById(R.id.back_button);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("friends");
        dbUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendsList.clear();

                for (DataSnapshot friend : snapshot.getChildren()) {
                    String id = friend.getKey();
                    String name = friend.child("name").getValue().toString();
                    String image = friend.child("image").getValue().toString();
                    String occasion = friend.child("occasion").getValue().toString();
                    String date = friend.child("date").getValue().toString();
                    String occurrence = friend.child("occurrence").getValue().toString();
                    int reminderId = friend.child("reminderId").getValue(Integer.class);
                    String addressLine = friend.child("addressLine").getValue().toString();
                    String state = friend.child("state").getValue().toString();
                    String city = friend.child("city").getValue().toString();
                    String postalCode = friend.child("postalCode").getValue().toString();
                    String notes = friend.child("notes").getValue().toString();
                    Boolean priority = Boolean.parseBoolean(friend.child("priority").getValue().toString());

                    ArrayList<CartItem> wishlist = new ArrayList<>();
                    for (DataSnapshot item : friend.child("wishlist").getChildren()){
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
                    friendsList.add(newFriend);
                }
                fillWishlistSpinner(sortFriends(friendsList));
                fillInReceipt();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        payNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToPayment();
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

    public void fillWishlistSpinner(ArrayList<Friend> friends){
        if (getActivity() != null){
            wishlistSelectorSpinner.setAdapter(null);
            wishlistSelectorSpinnerAdapter adapter = new wishlistSelectorSpinnerAdapter(getActivity(), friends);
            wishlistSelectorSpinner.setAdapter(adapter);
            wishlistSelectorSpinner.setSelection(0);
            for (Friend friend : friendsList){
                if (friend.getId().equals(selectedFriendID))
                    wishlistSelectorSpinner.setSelection(friendsList.indexOf(friend));
            }
            wishlistSelectorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    selectedFriendID = friendsList.get(position).getId();
                    fillCartItems(friendsList.get(position));
                    fillInReceipt();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    public void fillCartItems(Friend friend) {
        if (getActivity() != null) {
            checkoutItemsListView.setAdapter(null);
            final cartItemListViewAdapter adapter = new cartItemListViewAdapter(getActivity(), friend.getWishlist(), friend.getId());
            checkoutItemsListView.setAdapter(adapter);
        }
    }

    public void fillInReceipt() {
        totalPrice = 0;
        subTotalPrice = 0;
        for (CartItem item : friendsList.get(wishlistSelectorSpinner.getSelectedItemPosition()).getWishlist()){
            subTotalPrice += item.getAmount();
        }
        if (subTotalPrice == 0){
            subtotalText.setText("RM " + String.format("%.2f", subTotalPrice));
            shippingFeesText.setText("RM 0.00");
        } else {
        totalPrice += subTotalPrice + shippingFee;
        subtotalText.setText("RM " + String.format("%.2f", subTotalPrice));
        shippingFeesText.setText("RM " + String.format("%.2f", shippingFee));
        }
        totalText.setText("RM " + String.format("%.2f", totalPrice));
    }

    public ArrayList<Friend> sortFriends (ArrayList<Friend> friends){
        ArrayList<Friend> sortedList = new ArrayList<>();
        Iterator<Friend> friendIterator = friends.iterator();
        while (friendIterator.hasNext()){
            Friend current = friendIterator.next();
            if (current.getPriority()){
                sortedList.add(current);
                friendIterator.remove();
            }
        }
        Collections.sort(friends, new Comparator<Friend>() {
            @Override
            public int compare(Friend o1, Friend o2) {
                return convertDate(o1.getDate()).compareTo(convertDate(o2.getDate()));
            }
        });
        sortedList.addAll(friends);
        friendsList = sortedList;
        return sortedList;
    }

    public Date convertDate(String strDate){
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        Date newDate = new Date();
        try {
            newDate = sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newDate;
    }

    public void sendToPayment(){
        String orderID = "o" + dbUsers.push().getKey();
        Calendar today = Calendar.getInstance();
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String dateCreated = sdf.format(today.getTime());
        Friend friendToGive = friendsList.get(wishlistSelectorSpinner.getSelectedItemPosition());
        Order newOrder = new Order(orderID, friendToGive, dateCreated, "TBA", "TBA", "TBA", totalPrice, "Order Received");

        Fragment fragment = new Customise();
        AppCompatActivity activity = (AppCompatActivity) getContext();
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putSerializable("order", newOrder);
        fragment.setArguments(args);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}