package com.inti.gifty;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Orders extends Fragment {

    private DatabaseReference dbUsers;

    private TextView emptyListMessage;
    private ListView ordersListView;
    private ArrayList<Order> ordersList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        ordersListView = view.findViewById(R.id.orders_list_view);
        emptyListMessage = view.findViewById(R.id.list_empty_text);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("orders");
        dbUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    emptyListMessage.setVisibility(View.VISIBLE);
                    return;
                };
                emptyListMessage.setVisibility(View.INVISIBLE);
                ordersList.clear();

                for (DataSnapshot order : snapshot.getChildren()){
                    String orderID = order.getKey();
                    String dateOrdered = order.child("dateOrdered").getValue().toString();
                    String dateETA = order.child("dateETA").getValue().toString();
                    String dateSent = order.child("dateSent").getValue().toString();
                    String dateReceived = order.child("dateReceived").getValue().toString();
                    double totalAmount = order.child("totalAmount").getValue(Integer.class);
                    String status = order.child("status").getValue().toString();

                    String id = order.child("friend").child("id").getValue().toString();
                    String name = order.child("friend").child("name").getValue().toString();
                    String image = order.child("friend").child("image").getValue().toString();
                    String occasion = order.child("friend").child("occasion").getValue().toString();
                    String date = order.child("friend").child("date").getValue().toString();
                    String occurrence = order.child("friend").child("occurrence").getValue().toString();
                    int reminderId = order.child("friend").child("reminderId").getValue(Integer.class);
                    String addressLine = order.child("friend").child("addressLine").getValue().toString();
                    String state = order.child("friend").child("state").getValue().toString();
                    String city = order.child("friend").child("city").getValue().toString();
                    String postalCode = order.child("friend").child("postalCode").getValue().toString();
                    String notes = order.child("friend").child("notes").getValue().toString();
                    Boolean priority = Boolean.parseBoolean(order.child("friend").child("priority").getValue().toString());

                    ArrayList<CartItem> wishlist = new ArrayList<>();
                    for (DataSnapshot item : order.child("friend").child("wishlist").getChildren()){
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
                    Order newOrder = new Order(orderID, newFriend, dateOrdered, dateETA, dateSent, dateReceived, totalAmount, status);
                    ordersList.add(newOrder);
                }
                fillList(ordersList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    public void fillList(final ArrayList<Order> ordersList){
        if (getActivity() != null){
            ordersListView.setAdapter(null);
            ordersListViewAdapter adapter = new ordersListViewAdapter(getActivity(), ordersList);
            ordersListView.setAdapter(adapter);
            ordersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    sendToOrderDetails(ordersList.get(position));
                }
            });
        }
    }

    public void sendToOrderDetails(Order order) {
        Fragment fragment = new OrderDetails();
        AppCompatActivity activity = (AppCompatActivity) getContext();
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putSerializable("order", order);
        fragment.setArguments(args);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}