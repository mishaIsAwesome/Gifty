package com.inti.gifty;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class friendsListViewAdapter extends ArrayAdapter<Friend> {

    public friendsListViewAdapter(Context context, ArrayList<Friend> friends){
        super(context, 0, friends);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Friend friend = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.friend_item, parent, false);
        }

        final ImageView friendImage = convertView.findViewById(R.id.friend_image);
        TextView friendOccasion = convertView.findViewById(R.id.occasion_text);
        TextView occasionDate = convertView.findViewById(R.id.date_text);
        TextView daysAway = convertView.findViewById(R.id.days_away_text);
        TextView numCartItems = convertView.findViewById(R.id.cart_items_count_text);

        Uri newUri = Uri.parse(friend.getImage());
        Glide.with(getContext()).load(newUri).placeholder(R.drawable.ic_profile_fill_primary).circleCrop().into(friendImage);

        friendOccasion.setText(friend.getName() + "\'s " + friend.getOccasion());
        occasionDate.setText(friend.getDate());
        daysAway.setText(calculateDaysAway(friend.getDate()) + " days away");
        numCartItems.setText(String.valueOf(friend.getWishlist().size()));

        return convertView;
    }

    public long calculateDaysAway(String strDate){
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        Date occasion = new Date();
        try {
            occasion = sdf.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date today = new Date();
        long diff = occasion.getTime() - today.getTime();
        long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        if (days < 0){
            days = 0;
        }
        return days;
    }
}
