package com.inti.gifty;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.lang.reflect.Field;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;

public class categoryPagerAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<ArrayList> categoryList;
    private int currentViewPos;

    ImageButton[] itemImages;
    TextView[] itemTitles;
    TextView[] itemPrices;

    public categoryPagerAdapter(Context context, ArrayList<ArrayList> categoryList){
        this.context = context;
        this.categoryList = categoryList;
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.section_catalog, container, false);

        itemImages = new ImageButton[6];
        itemTitles = new TextView[6];
        itemPrices = new TextView[6];

        for (int i = 1; i<=6; i++) {
            String targetImage = "item_" + i + "_image";
            String targetTitle = "item_" + i + "_title";
            String targetPrice = "item_" + i + "_price";
            int imageResId = getResId(targetImage, R.id.class);
            int titleResId = getResId(targetTitle, R.id.class);
            int priceResId = getResId(targetPrice, R.id.class);
            ImageButton button = view.findViewById(imageResId);
            itemImages[i-1] = button;
            TextView title = view.findViewById(titleResId);
            itemTitles[i-1] = title;
            TextView price = view.findViewById(priceResId);
            itemPrices[i-1] = price;
        }
        clearCatalog();
        ArrayList<Item> list = categoryList.get(position);

        for (final Item obj : list){
            int index = list.indexOf(obj);
            itemTitles[index].setText(obj.getName());
            itemPrices[index].setText("RM " + String.format("%.2f", obj.getPrice()));
            if (context != null)
                Glide.with(context)
                        .load(obj.getImage())
                        .centerCrop()
                        .into(itemImages[index]);
            itemImages[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendToGiftPage(obj);
                }
            });
        }
        container.addView(view, 0);
        return view;
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        currentViewPos = position;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
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

    public void clearCatalog(){
        for (int i = 0; i < 6; i++){
            itemTitles[i].setText(R.string.phItemTitle);
            itemPrices[i].setText(R.string.phItemPrice);
            if (context != null)
                Glide.with(context).load(R.color.light_gray).centerCrop().into(itemImages[i]);
        }
    }

    public void sendToGiftPage(Item item){
        Fragment fragment = new Gift();
        AppCompatActivity activity = (AppCompatActivity) context;
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle args = new Bundle();
        args.putSerializable("item", item);
        fragment.setArguments(args);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
