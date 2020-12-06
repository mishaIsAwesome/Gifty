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
                    Order newOrder = order.getValue(Order.class);
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