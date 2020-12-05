package com.inti.gifty;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WishlistSelectorBottomFragment extends BottomSheetDialogFragment {

    DatabaseReference dbUsers;

    TextView emptyListText;
    ListView wishlistSelectionListView;
    Button doneButton;

    ArrayList<Friend> friendsList = new ArrayList<Friend>();
    long FRIENDS_SIZE = 0;
    CartItem cartItem;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlist_selector, container,false);
        if (getArguments() != null){
            cartItem = (CartItem) getArguments().getSerializable("cartItem");
        }

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        emptyListText = view.findViewById(R.id.list_empty_text);
        wishlistSelectionListView = view.findViewById(R.id.wishlist_selection_list_view);
        doneButton = view.findViewById(R.id.done_button);

        dbUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("friends");
        dbUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    emptyListText.setVisibility(View.VISIBLE);
                    return;
                };
                emptyListText.setVisibility(View.INVISIBLE);
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

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb;
                for (int i = 0; i < wishlistSelectionListView.getChildCount(); i++){
                    cb = wishlistSelectionListView.getChildAt(i).findViewById(R.id.select_wishlist_checkbox);
                    if(cb.isChecked()){
                        String cartItemID = "ct" + String.format("%03d", (friendsList.get(i).getWishlist().size() + 1));
                        dbUsers.child(friendsList.get(i).getId()).child("wishlist").child(cartItemID).setValue(cartItem);
                        dbUsers.child(friendsList.get(i).getId()).child("wishlist").child(cartItemID).child("id").setValue(cartItemID);
                    }
                }
                dismiss();
            }
        });

        return view;
    }

    public void fillList(final ArrayList<Friend> friends){
        if (getActivity() != null){
            wishlistSelectionListView.setAdapter(null);
            wishlistSelectorListViewAdapter adapter = new wishlistSelectorListViewAdapter(getActivity(), friends);
            wishlistSelectionListView.setAdapter(adapter);
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
}
