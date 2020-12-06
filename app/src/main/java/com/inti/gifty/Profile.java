package com.inti.gifty;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


public class Profile extends Fragment {

    DatabaseReference dbUsers;

    ImageView profileImage;
    LinearLayout editButton, favouritesButton, myCardsButton, notificationsButton, logoutButton;
    TextView profileName, profileEmail, notificationsText;
    Switch notificationsSwitch;

    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean enabled = sharedPreferences.getBoolean("notifications", true);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        dbUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User currentUser = snapshot.getValue(User.class);
                user = currentUser;
                setInfo(currentUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        profileImage = view.findViewById(R.id.profile_image);
        profileName = view.findViewById(R.id.profile_name);
        profileEmail = view.findViewById(R.id.profile_email);
        notificationsButton = view.findViewById(R.id.notifications_button);
        notificationsSwitch = view.findViewById(R.id.notifications_switch);
        notificationsText = view.findViewById(R.id.notifications_text);
        editButton = view.findViewById(R.id.edit_profile_button);
        favouritesButton = view.findViewById(R.id.favourites_button);
        myCardsButton = view.findViewById(R.id.my_cards_button);
        logoutButton = view.findViewById(R.id.logout_button);

        notificationsSwitch.setChecked(enabled);
        setNotificationsText(enabled);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToProfileForm();
            }
        });
        favouritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToFavourites();
            }
        });
        myCardsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToMyCards();
            }
        });
        notificationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean newState = !notificationsSwitch.isChecked();
                notificationsSwitch.setChecked(newState);
            }
        });
        notificationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                notificationsSwitch.setChecked(isChecked);
                setNotificationsText(isChecked);
                editor.putBoolean("notifications", isChecked);
                editor.apply();
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getContext() == null)
                    return;
                new AlertDialog.Builder(getContext())
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setIcon(R.drawable.ic_logout)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(getContext(), Login.class));
                                getActivity().finish();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        return view;
    }

    public void setInfo (User user){
        if (getContext() != null){
            profileName.setText(user.getName());
            profileEmail.setText(user.getEmail());
            Glide.with(getContext()).load(user.getImage()).placeholder(R.drawable.ic_profile_fill).circleCrop().into(profileImage);
        }
    }

    public void setNotificationsText (boolean isChecked){
        if (isChecked){
            notificationsText.setText(getResources().getString(R.string.notifications_enabled_label));
        } else
            notificationsText.setText(getResources().getString(R.string.notifications_disabled_label));
    }

    public void sendToFavourites() {
        Fragment fragment = new Category();
        AppCompatActivity activity = (AppCompatActivity) getContext();
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putString("cat", "Favourites");
        fragment.setArguments(args);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void sendToMyCards() {
        Fragment fragment = new Cards();
        AppCompatActivity activity = (AppCompatActivity) getContext();
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void sendToProfileForm() {
        Fragment fragment = new ProfileForm();
        AppCompatActivity activity = (AppCompatActivity) getContext();
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        fragment.setArguments(args);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}