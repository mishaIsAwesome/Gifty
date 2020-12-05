package com.inti.gifty;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class Wishlists extends Fragment {

    DatabaseReference dbUsers;
    StorageReference storageRef;

    TextView emptyListMessage;
    ListView wishlistListView;
    ImageButton addWishlistButton;

    ArrayList<Friend> friendsList = new ArrayList<Friend>();
    long FRIENDS_SIZE = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlists, container, false);

        wishlistListView = view.findViewById(R.id.wishlist_list_view);
        addWishlistButton = view.findViewById(R.id.add_friend_button);
        emptyListMessage = view.findViewById(R.id.wishlists_empty_text);

        addWishlistButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToAddWishlist();
            }
        });

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("friends");
        storageRef = FirebaseStorage.getInstance().getReference().child("Users").child(userId).child("friends");
        dbUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    emptyListMessage.setVisibility(View.VISIBLE);
                    return;
                };
                emptyListMessage.setVisibility(View.INVISIBLE);
                friendsList.clear();
                FRIENDS_SIZE = snapshot.getChildrenCount();

                for (DataSnapshot friend : snapshot.getChildren()){
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
                fillList(sortFriends(friendsList));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }

    public void fillList(final ArrayList<Friend> friends){
        if (getActivity() != null){
            wishlistListView.setAdapter(null);
            friendsListViewAdapter adapter = new friendsListViewAdapter(getActivity(), friends);
            wishlistListView.setAdapter(adapter);
            wishlistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    sendToWishlistPage(friends.get(position));
                }
            });
            wishlistListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    if (getContext() == null) return false;
                    new AlertDialog.Builder(getContext())
                            .setTitle("Delete Wishlist")
                            .setMessage("Are you sure you want to delete this wishlist?")
                            .setIcon(R.drawable.ic_trash)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dbUsers.child(friends.get(position).getId()).removeValue();
                                    storageRef.child(friends.get(position).getId()).delete();
                                    Toast.makeText(getContext(), R.string.wishlist_deleted_message, Toast.LENGTH_SHORT).show();
                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                    return true;
                }
            });
        }
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

    public void sendToAddWishlist(){
        Fragment fragment = new WishlistForm();
        AppCompatActivity activity = (AppCompatActivity) getContext();
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putLong("size", FRIENDS_SIZE);
        fragment.setArguments(args);
        fragmentTransaction.replace(R.id.fragment_container, fragment, "WISHLIST_FORM");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void sendToWishlistPage(Friend friend){
        Fragment fragment = new Wishlist();
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle args = new Bundle();
        args.putString("friendID", friend.getId());
        fragment.setArguments(args);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}