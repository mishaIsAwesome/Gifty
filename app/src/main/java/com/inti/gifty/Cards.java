package com.inti.gifty;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

import java.util.ArrayList;

public class Cards extends Fragment {

    private DatabaseReference dbUsers;

    private TextView emptyListMessage;
    private ListView cardsListView;
    private ImageButton addCardButton, backButton;
    private ArrayList<Card> cardsList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cards, container, false);

        cardsListView = view.findViewById(R.id.cards_list_view);
        addCardButton = view.findViewById(R.id.add_card_button);
        backButton = view.findViewById(R.id.back_button);
        emptyListMessage = view.findViewById(R.id.list_empty_text);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("cards");
        dbUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    emptyListMessage.setVisibility(View.VISIBLE);
                    return;
                };
                emptyListMessage.setVisibility(View.INVISIBLE);
                cardsList.clear();

                for (DataSnapshot card : snapshot.getChildren()){
                    String id = card.getKey();
                    String name = card.child("name").getValue().toString();
                    String number = card.child("number").getValue().toString();
                    String service = card.child("service").getValue().toString();
                    String expiryDate = card.child("expiryDate").getValue().toString();
                    String cvv = card.child("cvv").getValue().toString();

                    Card newCard = new Card(id, name, number, service, expiryDate, cvv);
                    cardsList.add(newCard);
                }
                fillList(cardsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToAddCard();
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

    public void fillList(final ArrayList<Card> cardsList){
        if (getActivity() != null){
            cardsListView.setAdapter(null);
            cardsListViewAdapter adapter = new cardsListViewAdapter(getActivity(), cardsList);
            cardsListView.setAdapter(adapter);
            cardsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    if (getContext() == null) return false;
                    new AlertDialog.Builder(getContext())
                            .setTitle("Delete Card")
                            .setMessage("Are you sure you want to delete this card?")
                            .setIcon(R.drawable.ic_trash)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dbUsers.child(cardsList.get(position).getId()).removeValue();
                                    Toast.makeText(getContext(), R.string.card_deleted_message, Toast.LENGTH_SHORT).show();
                                }})
                            .setNegativeButton(android.R.string.no, null).show();
                    return true;
                }
            });
        }
    }

    public void sendToAddCard() {
        Fragment fragment = new CardForm();
        AppCompatActivity activity = (AppCompatActivity) getContext();
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}