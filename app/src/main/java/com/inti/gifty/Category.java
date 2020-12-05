package com.inti.gifty;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Category extends Fragment {

    DatabaseReference dbRef;
    itemListViewAdapter itemsAdapter;

    ImageButton backButton;
    TextView categoryTitle;
    ListView listView;

    ArrayList<ArrayList> itemList = new ArrayList<>();
    String category;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        if (getArguments() != null)
            category = getArguments().getString("cat");

        categoryTitle = view.findViewById(R.id.category_title);
        categoryTitle.setText(category);
        listView = view.findViewById(R.id.item_list_view);

        if (category != null) {
            if (category.equals("Popular") || category.equals("Latest")){
                dbRef = FirebaseDatabase.getInstance().getReference().child("Catalog");
                dbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        itemList.clear();
                        ArrayList<Item> itemLine = new ArrayList<>();
                        int counter = 0;

                        for (DataSnapshot dbCategory : snapshot.getChildren()) {
                            for (DataSnapshot item : dbCategory.getChildren()) {
                                for (DataSnapshot tag : item.child("tags").getChildren()) {
                                    String id = item.getKey();
                                    String name = item.child("name").getValue().toString();
                                    String shop = item.child("shop").getValue().toString();
                                    double price = item.child("price").getValue(Integer.class);
                                    String image = item.child("image").getValue().toString();
                                    String desc = item.child("desc").getValue().toString();
                                    double rating = item.child("rating").getValue(Integer.class);
                                    Item newItem = new Item(id, name, shop, image, price, desc, rating);
                                    if (tag.getValue().toString().equals(category.toLowerCase())) {
                                        itemLine.add(newItem);
                                        counter++;
                                        if (counter == 2) {
                                            ArrayList<Item> addedLine = new ArrayList<>();
                                            addedLine.add(itemLine.get(0));
                                            addedLine.add(itemLine.get(1));
                                            ;
                                            itemList.add(addedLine);
                                            itemLine.clear();
                                            counter = 0;
                                        }
                                    }
                                }
                            }
                        }
                        if (!itemLine.isEmpty()){
                            ArrayList<Item> addedLine = new ArrayList<>();
                            addedLine.add(itemLine.get(0));
                            itemList.add(addedLine);
                        }
                        fillItems(itemList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                if (category.equals("Favourites")){
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    dbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("Favourites");
                } else{
                    dbRef = FirebaseDatabase.getInstance().getReference().child("Catalog").child(category);
                }

                dbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    itemList.clear();
                    ArrayList<Item> itemLine = new ArrayList<>();
                    int counter = 0;

                    for (DataSnapshot item : snapshot.getChildren()) {
                        String id = item.getKey();
                        String name = item.child("name").getValue().toString();
                        String shop = item.child("shop").getValue().toString();
                        double price = item.child("price").getValue(Integer.class);
                        String image = item.child("image").getValue().toString();
                        String desc = item.child("desc").getValue().toString();
                        double rating = item.child("rating").getValue(Integer.class);
                        Item newItem = new Item(id, name, shop, image, price, desc, rating);
                        itemLine.add(newItem);
                        counter ++;
                        if (counter == 2){
                            ArrayList<Item> addedLine = new ArrayList<>();
                            addedLine.add(itemLine.get(0));
                            addedLine.add(itemLine.get(1));
;                           itemList.add(addedLine);
                            itemLine.clear();
                            counter = 0;
                        }
                    }
                    if (!itemLine.isEmpty()){
                        ArrayList<Item> addedLine = new ArrayList<>();
                        addedLine.add(itemLine.get(0));
                        itemList.add(addedLine);
                    }
                    fillItems(itemList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            }
        }

        backButton = view.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStackImmediate();
            }
        });

        return view;
    }

    public void fillItems(ArrayList<ArrayList> itemList){
        if (getContext() != null){
            listView.setAdapter(null);
            itemsAdapter = new itemListViewAdapter(getContext(), itemList);
            listView.setAdapter(itemsAdapter);
        }
    }
}