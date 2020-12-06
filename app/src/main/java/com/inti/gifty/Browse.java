package com.inti.gifty;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Browse extends Fragment {

    DatabaseReference dbRef;

    ImageView test;
    ImageView[] itemImages;
    ArrayList<String> imageURLs = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browse, container, false);

        test = view.findViewById(R.id.item_1_image);
        itemImages = new ImageView[9];

        for (int i = 1; i<=9; i++) {
            String targetImage = "item_" + i + "_image";
            int imageResId = getResId(targetImage, R.id.class);
            ImageView image = view.findViewById(imageResId);
            itemImages[i-1] = image;
        }

        dbRef = FirebaseDatabase.getInstance().getReference().child("Catalog").child("Thumbnails");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot thumbnails : snapshot.getChildren()){
                    String url = thumbnails.getValue().toString();
                    imageURLs.add(url);
                }
                fillCategoryImages(imageURLs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        itemImages[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToCategoryPage("Flowers");
            }
        });
        itemImages[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToCategoryPage("Bags");
            }
        });
        itemImages[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToCategoryPage("Food");
            }
        });
        itemImages[6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToCategoryPage("Stationery");
            }
        });
        itemImages[7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToCategoryPage("Jewelry");
            }
        });
        return view;
    }

    public void fillCategoryImages (ArrayList<String> list){
        for (String url : list){
            int index = list.indexOf(url);
            if (getContext() != null && index<=9) {
                Glide.with(getContext())
                        .load(url)
                        .centerCrop()
                        .into(itemImages[index]);
                switch (index) {
                    case 0:
                    case 2:
                    case 3:
                    case 5:
                    case 7:
                        itemImages[index].setColorFilter(ContextCompat.getColor(getContext(), R.color.color60PrimaryLight), android.graphics.PorterDuff.Mode.MULTIPLY);
                        break;
                    default:
                        itemImages[index].setColorFilter(ContextCompat.getColor(getContext(), R.color.color60PrimaryDark), android.graphics.PorterDuff.Mode.MULTIPLY);
                }
            }
        }
    }

    public void sendToCategoryPage(String category){
        Fragment fragment = new Category();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle args = new Bundle();
        args.putString("cat", category);
        fragment.setArguments(args);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public static int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}