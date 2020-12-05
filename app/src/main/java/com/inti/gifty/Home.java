package com.inti.gifty;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Home extends Fragment {

    DatabaseReference dbRef, dbRefArticle, dbUsers;

    ImageView articleImage;
    TextView articleTitleTextView, articleTextTextView, welcomeTextView, moreButtonTextView;
    Button catPopularButton, catLatestButton, catStationeryButton, catFoodButton, cartButton;
    ImageButton[] itemImages;
    TextView[] itemTitles;
    TextView[] itemPrices;

    ArrayList<Item> popularItems = new ArrayList<>();
    ArrayList<Item> latestItems = new ArrayList<>();
    ArrayList<Item> stationeryItems = new ArrayList<>();
    ArrayList<Item> foodItems = new ArrayList<>();
    categoryPagerAdapter cpAdapter;
    ViewPager categoryPager;

    String currentCategory = "Popular";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        welcomeTextView = view.findViewById(R.id.home_welcome_text);
        articleTitleTextView = view.findViewById(R.id.article_title);
        articleTextTextView = view.findViewById(R.id.article_text);
        articleImage = view.findViewById(R.id.article_image);
        catPopularButton = view.findViewById(R.id.cat_popular_button);
        catLatestButton = view.findViewById(R.id.cat_latest_button);
        catStationeryButton = view.findViewById(R.id.cat_stationery_button);
        catFoodButton = view.findViewById(R.id.cat_food_button);
        cartButton = view.findViewById(R.id.cart_button);
        moreButtonTextView = view.findViewById(R.id.more_button);
        itemImages = new ImageButton[6];
        itemTitles = new TextView[6];
        itemPrices = new TextView[6];

        categoryPager = view.findViewById(R.id.category_pager);

        catPopularButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryPager.setCurrentItem(0);
            }
        });
        catLatestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryPager.setCurrentItem(1);
            }
        });
        catStationeryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryPager.setCurrentItem(2);
            }
        });
        catFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryPager.setCurrentItem(3);
            }
        });
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToMyCart();
            }
        });
        moreButtonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToCategoryPage(currentCategory);
            }
        });

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        dbUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                welcomeTextView.append(" " + snapshot.child("name").getValue().toString().split(" ",2)[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dbRef = FirebaseDatabase.getInstance().getReference().child("Catalog");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                popularItems.clear();
                latestItems.clear();
                stationeryItems.clear();
                foodItems.clear();

                for (DataSnapshot category : dataSnapshot.getChildren()){
                    for (DataSnapshot item : category.getChildren()){
                        for (DataSnapshot tag : item.child("tags").getChildren()) {
                            String id = item.getKey();
                            String name = item.child("name").getValue().toString();
                            String shop = item.child("shop").getValue().toString();
                            double price = item.child("price").getValue(Integer.class);
                            String image = item.child("image").getValue().toString();
                            String desc = item.child("desc").getValue().toString();
                            double rating = item.child("rating").getValue(Integer.class);
                            Item newItem = new Item(id, name, shop, image, price, desc, rating);
                            switch (tag.getValue().toString()){
                                case "popular": if(popularItems.size() < 6) popularItems.add(newItem); break;
                                case "latest": if(latestItems.size() < 6) latestItems.add(newItem); break;
                                case "stationery": if(stationeryItems.size() < 6) stationeryItems.add(newItem); break;
                                case "food": if(foodItems.size() < 6) foodItems.add(newItem); break;
                            }
                        }

                    }
                }
                ArrayList categories = new ArrayList();
                categories.add(popularItems);
                categories.add(latestItems);
                categories.add(stationeryItems);
                categories.add(foodItems);
                fillCatalog(categories);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dbRefArticle = FirebaseDatabase.getInstance().getReference().child("Articles");
        dbRefArticle.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                articleTitleTextView.setText(snapshot.child("a001").child("title").getValue().toString());
                articleTextTextView.setText(snapshot.child("a001").child("text").getValue().toString());
                String imageUrl = snapshot.child("a001").child("image").getValue().toString();
                if (getContext() != null)
                    Glide.with(getContext())
                            .load(imageUrl)
                            .fitCenter()
                            .into(articleImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        categoryPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                switchActiveButtons(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
    }

    public void fillCatalog(ArrayList<ArrayList> list){
        cpAdapter = new categoryPagerAdapter(getContext(), list);
        categoryPager.setAdapter(cpAdapter);
    }

    public void switchActiveButtons(int pos){
        catPopularButton.setBackgroundResource(R.drawable.ripple_cat_inactive_button);;
        catLatestButton.setBackgroundResource(R.drawable.ripple_cat_inactive_button);;
        catStationeryButton.setBackgroundResource(R.drawable.ripple_cat_inactive_button);;
        catFoodButton.setBackgroundResource(R.drawable.ripple_cat_inactive_button);;
        switch (pos){
            case 0: catPopularButton.setBackgroundResource(R.drawable.ripple_cat_button); currentCategory = "Popular"; break;
            case 1: catLatestButton.setBackgroundResource(R.drawable.ripple_cat_button); currentCategory = "Latest"; break;
            case 2: catStationeryButton.setBackgroundResource(R.drawable.ripple_cat_button); currentCategory = "Stationery"; break;
            case 3: catFoodButton.setBackgroundResource(R.drawable.ripple_cat_button); currentCategory = "Food"; break;
        }

    }

    public void sendToMyCart(){
        Fragment fragment = new Cart();
        AppCompatActivity activity = (AppCompatActivity) getContext();
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
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
}