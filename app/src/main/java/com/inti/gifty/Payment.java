package com.inti.gifty;

import android.graphics.drawable.Drawable;
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
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

class PaymentMethod {
    private String name;
    private Drawable icon;
    private Boolean selected;

    public PaymentMethod(String name, Drawable icon) {
        this.name = name;
        this.icon = icon;
        this.selected = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}

public class Payment extends Fragment {

    DatabaseReference dbUsers;

    ExpandableListView paymentMethodsExpandableListView;
    ArrayList<String> listGroup = new ArrayList<>();
    HashMap<String, ArrayList<PaymentMethod>> listChild = new HashMap<>();
    paymentMethodExpandableListViewAdapter adapter;
    Button payNowButton;
    ImageButton backButton;
    TextView recipientName, recipientAddress;

    Order newOrder;
    Friend friend;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null){
            newOrder = (Order) getArguments().getSerializable("order");
            friend = newOrder.getFriend();
        }
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

        paymentMethodsExpandableListView = view.findViewById(R.id.payment_method_expandable_list);
        payNowButton = view.findViewById(R.id.pay_now_button);
        recipientName = view.findViewById(R.id.payment_recipient_name);
        recipientAddress = view.findViewById(R.id.payment_recipient_address);
        backButton = view.findViewById(R.id.back_button);

        recipientName.setText(friend.getName());
        String fullAddress = friend.getAddressLine() + ", " + friend.getCity() + ", " + friend.getPostalCode() + ", " +  friend.getState();
        recipientAddress.setText(fullAddress);

        dbUsers.child("cards").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                refreshList();
                ArrayList<PaymentMethod> creditCardList = new ArrayList<>();
                if (!snapshot.exists()) {
                    creditCardList.add(new PaymentMethod("Add New Credit/Debit Card", getResources().getDrawable(R.drawable.ic_plus)));
                    listChild.put(listGroup.get(1), creditCardList);
                    fillPaymentMethods();
                    return;
                };
                for (DataSnapshot card : snapshot.getChildren()){
                    String id = card.child("id").getValue().toString();
                    String service = card.child("service").getValue().toString();
                    if (service.equals("Visa"))
                        creditCardList.add(new PaymentMethod(service + " *" + id, getResources().getDrawable(R.drawable.ic_visa)));
                    else if (service.equals("Mastercard"))
                        creditCardList.add(new PaymentMethod(service + " *" + id, getResources().getDrawable(R.drawable.ic_mastercard)));
                    else
                        creditCardList.add(new PaymentMethod( service + " *" + id, getResources().getDrawable(R.drawable.ic_card)));
                }
                creditCardList.add(new PaymentMethod("Add New Credit/Debit Card", getResources().getDrawable(R.drawable.ic_plus)));
                listChild.put(listGroup.get(1), creditCardList);
                fillPaymentMethods();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        payNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbUsers.child("orders").child(newOrder.getId()).setValue(newOrder);
                dbUsers.child("friends").child(friend.getId()).removeValue();
                sendToGiftOrdered();
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

    public void fillPaymentMethods(){
        adapter = new paymentMethodExpandableListViewAdapter(getContext(), listGroup, listChild);
        paymentMethodsExpandableListView.setAdapter(adapter);
        paymentMethodsExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (groupPosition == 1 && childPosition == listChild.get(listGroup.get(1)).size() -1){
                    sendToCardForm();
                }

                for (int i = 0; i < listChild.size(); i++){
                    for (int j = 0; j < listChild.get(listGroup.get(i)).size(); j++){
                        listChild.get(listGroup.get(i)).get(j).setSelected(false);
                    }
                }
                listChild.get(listGroup.get(groupPosition)).get(childPosition).setSelected(true);
                adapter.notifyDataSetChanged();
                payNowButton.setEnabled(true);
                return true;
            }
        });
    }

    public void refreshList(){
        if (getContext() == null) return;
        listGroup.clear();
        listGroup.add(getResources().getString(R.string.online_banking_label));
        ArrayList<PaymentMethod> onlineBankingList = new ArrayList<>();
        onlineBankingList.add(new PaymentMethod("Maybank2U", getResources().getDrawable(R.drawable.ic_maybank)));
        onlineBankingList.add(new PaymentMethod("CIMB Clicks", getResources().getDrawable(R.drawable.ic_cimb)));
        onlineBankingList.add(new PaymentMethod("Public Bank", getResources().getDrawable(R.drawable.ic_public_bank)));
        onlineBankingList.add(new PaymentMethod("RHB Now", getResources().getDrawable(R.drawable.ic_rhb)));
        onlineBankingList.add(new PaymentMethod("Hong Leong Connect", getResources().getDrawable(R.drawable.ic_hongleong)));
        onlineBankingList.add(new PaymentMethod("Ambank", getResources().getDrawable(R.drawable.ic_ambank)));
        listChild.put(listGroup.get(0), onlineBankingList);

        listGroup.add(getResources().getString(R.string.credit_debit_card_label));

        listGroup.add(getResources().getString(R.string.others_label));
        ArrayList<PaymentMethod> othersList = new ArrayList<>();
        othersList.add(new PaymentMethod("Touch 'n' Go e-Wallet", getResources().getDrawable(R.drawable.ic_tng)));
        othersList.add(new PaymentMethod("Razer Pay Wallet", getResources().getDrawable(R.drawable.ic_razer)));
        listChild.put(listGroup.get(2), othersList);
    }

    public void sendToGiftOrdered(){
        Fragment fragment = new GiftOrdered();
        AppCompatActivity activity = (AppCompatActivity) getContext();
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void sendToCardForm(){
        Fragment fragment = new CardForm();
        AppCompatActivity activity = (AppCompatActivity) getContext();
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}