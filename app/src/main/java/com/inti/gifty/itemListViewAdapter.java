package com.inti.gifty;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class itemListViewAdapter extends ArrayAdapter<ArrayList> {

    private Context context;
    private ArrayList<ArrayList> itemList;

    ViewHolder holder;


    public itemListViewAdapter(Context context, ArrayList<ArrayList> itemList) {
        super(context, 0, itemList);
        this.context = context;
        this.itemList = itemList;
    }

    static class ViewHolder {
        TextView itemTitle1, itemTitle2;
        TextView itemPrice1, itemPrice2;
        ImageView itemImage1, itemImage2;
        SimpleRatingBar ratingBar1, ratingBar2;
        LinearLayout itemLayout1, itemLayout2;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ArrayList<Item> line = itemList.get(position);
        final Item item1 = line.get(0);

        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.simple_two_item_list, null);

            holder = new ViewHolder();

            holder.itemTitle1 = v.findViewById(R.id.item_1_title);
            holder.itemPrice1 = v.findViewById(R.id.item_1_price);
            holder.itemImage1 = v.findViewById(R.id.item_1_image);
            holder.ratingBar1 = v.findViewById(R.id.ratingBar_1);
            holder.itemLayout1 = v.findViewById(R.id.item_1_layout);

            holder.itemTitle2 = v.findViewById(R.id.item_2_title);
            holder.itemPrice2 = v.findViewById(R.id.item_2_price);
            holder.itemImage2 = v.findViewById(R.id.item_2_image);
            holder.ratingBar2 = v.findViewById(R.id.ratingBar_2);
            holder.itemLayout2 = v.findViewById(R.id.item_2_layout);
            v.setTag(holder);
        } else{
            holder = (ViewHolder) v.getTag();
        }

        clearItems(holder);

        holder.itemTitle1.setText(item1.getName());
        holder.itemPrice1.setText("RM " + item1.getPrice());
        if (context != null)
            Glide.with(context)
                    .load(item1.getImage())
                    .centerCrop()
                    .into(holder.itemImage1);
        holder.ratingBar1.setRating((float) item1.getRating());
        holder.itemLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToGiftPage(item1);
            }
        });

        if (line.size() == 2) {
            final Item item2 = line.get(1);

            holder.itemLayout2.setVisibility(View.VISIBLE);
            holder.itemTitle2.setText(item2.getName());
            holder.itemPrice2.setText("RM " + item2.getPrice());
            if (context != null)
                Glide.with(context)
                        .load(item2.getImage())
                        .centerCrop()
                        .into(holder.itemImage2);
            holder.ratingBar2.setRating((float) item2.getRating());
            holder.itemLayout2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendToGiftPage(item2);
                }
            });

        } else {
            holder.itemLayout2.setVisibility(View.INVISIBLE);
        }

        return v;
    }

    public void sendToGiftPage(Item item){
        Fragment fragment = new Gift();
        AppCompatActivity activity = (AppCompatActivity) getContext();
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle args = new Bundle();
        args.putSerializable("item", item);
        fragment.setArguments(args);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void clearItems(ViewHolder holder){
        holder.itemTitle1.setText("Unavailable");
        holder.itemPrice1.setText("RM 0.00");
        if (context != null)
            Glide.with(context).load(R.color.light_gray).centerCrop().into(holder.itemImage1);
        holder.ratingBar1.setRating(0);

        holder.itemTitle2.setText("Unavailable");
        holder.itemPrice2.setText("RM 0.00");
        if (context != null)
            Glide.with(context).load(R.color.light_gray).centerCrop().into(holder.itemImage2);
        holder.ratingBar2.setRating(0);
    }
}
