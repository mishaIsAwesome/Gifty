package com.inti.gifty;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.ALARM_SERVICE;

public class WishlistForm extends Fragment {

    FirebaseAuth authRef;
    DatabaseReference dbUsers;
    StorageReference storageRef;

    TextView wishlistTitle;
    EditText nameTextField, dateTextField, addressLineTextField, stateTextField, cityTextField, postalCodeTextField, notesTextField;
    ImageView friendImage;
    RadioButton yearlyRButton, onceRButton;
    Spinner occasionSpinner;
    Switch prioritySwitch;
    Button submitButton;
    ImageButton backButton;

    final Calendar myCalendar = Calendar.getInstance();
    final static int REQUEST_CODE_CHOOSE = 1;
    long FRIENDS_SIZE = 0;
    Uri selectedImage;
    String dateFormat = "dd-MM-yyyy";
    Friend friend;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wishlist_form, container, false);
        createNotificationChannel();
        if (getArguments() != null){
            friend = (Friend) getArguments().getSerializable("friend");
            FRIENDS_SIZE = getArguments().getLong("size");
        }

        authRef = FirebaseAuth.getInstance();
        dbUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(authRef.getCurrentUser().getUid()).child("friends");
        storageRef = FirebaseStorage.getInstance().getReference().child("Users").child(authRef.getCurrentUser().getUid()).child("friends");

        wishlistTitle = view.findViewById(R.id.wishlist_form_title);
        nameTextField = view.findViewById(R.id.who_edit_text);
        occasionSpinner = view.findViewById(R.id.occasion_spinner);
        dateTextField = view.findViewById(R.id.date_edit_text);
        addressLineTextField = view.findViewById(R.id.address_line_edit_text);
        stateTextField = view.findViewById(R.id.state_edit_text);
        cityTextField = view.findViewById(R.id.city_edit_text);
        postalCodeTextField = view.findViewById(R.id.postal_code_edit_text);
        notesTextField = view.findViewById(R.id.notes_edit_text);
        friendImage = view.findViewById(R.id.add_friend_image);
        yearlyRButton = view.findViewById(R.id.yearly_radio_button);
        onceRButton = view.findViewById(R.id.once_radio_button);
        prioritySwitch = view.findViewById(R.id.priority_switch);
        submitButton = view.findViewById(R.id.submit_button);
        backButton = view.findViewById(R.id.back_button);

        String[] occasions = getResources().getStringArray(R.array.occasions);
        ArrayAdapter<String> occasionArrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item , occasions);
        occasionSpinner.setAdapter(occasionArrayAdapter);

        if (friend != null){
            wishlistTitle.setText(R.string.edit_wishlist_title);
            submitButton.setText(R.string.save_button_label);
            Glide.with(getContext()).load(friend.getImage()).placeholder(R.drawable.ic_profile_fill_super_light).circleCrop().into(friendImage);
            nameTextField.setText(friend.getName());
            int occasionIndex = 0;
            for (int i = 0; i< occasions.length; i++){
                if (friend.getOccasion().equals(occasions[i])){
                    occasionIndex = i;
                }
            }
            occasionSpinner.setSelection(occasionIndex);
            dateTextField.setText(friend.getDate());
            addressLineTextField.setText(friend.getAddressLine());
            stateTextField.setText(friend.getState());
            cityTextField.setText(friend.getCity());
            postalCodeTextField.setText(friend.getPostalCode());
            notesTextField.setText(friend.getNotes());
            if (friend.getOccurrence().equals("yearly"))
                yearlyRButton.setChecked(true);
            else onceRButton.setChecked(true);
            if (friend.getPriority())
                prioritySwitch.setChecked(true);
            else prioritySwitch.setChecked(false);
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
            try {
                myCalendar.setTime(sdf.parse(friend.getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            Glide.with(getContext()).load(R.drawable.ic_profile_fill_super_light).circleCrop().into(friendImage);
        }

        friendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProfilePicture();
            }
        });

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        dateTextField.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               DatePickerDialog datePicker =  new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
               datePicker.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
               datePicker.show();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (friend != null){
                    submitEntry(friend.getId());
                } else
                    submitEntry("f" + dbUsers.push().getKey());
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quitWithoutSavingAlert();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            Uri ImageUri = data.getData();
            Intent intent = CropImage.activity(ImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .getIntent(getActivity());
            startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
        }

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            selectedImage = result.getUri();
            if (getContext() != null)
                Glide.with(getContext()).load(selectedImage).circleCrop().into(friendImage);
        }
    }

    private void setProfilePicture() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_CODE_CHOOSE);
    }

    private void updateLabel() {
        String myFormat = "dd-MM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateTextField.setText(sdf.format(myCalendar.getTime()));
    }

    public boolean checkFields(String name, String date, String addressLine, String state, String city, String postalCode){

        if (TextUtils.isEmpty(name)){
            nameTextField.setError("Name is required.");
            return false;
        }

        if (TextUtils.isEmpty(date)){
            dateTextField.setError("Date is required.");
            return false;
        } else
            dateTextField.setError(null);

        if (TextUtils.isEmpty(addressLine)){
            addressLineTextField.setError("Address line is required.");
            return false;
        }

        if (TextUtils.isEmpty(state)){
            stateTextField.setError("State is required.");
            return false;
        }

        if (TextUtils.isEmpty(city)){
            cityTextField.setError("City is required.");
            return false;
        }

        if (TextUtils.isEmpty(postalCode)){
            postalCodeTextField.setError("Postal code is required.");
            return false;
        }
        return true;
    }

    public void submitEntry(final String friendID) {
        final String name = nameTextField.getText().toString().trim();
        String occasion = occasionSpinner.getSelectedItem().toString();
        final String date = dateTextField.getText().toString().trim();
        String addressLine = addressLineTextField.getText().toString().trim();
        String state = stateTextField.getText().toString().trim();
        String city = cityTextField.getText().toString().trim();
        String postalCode = postalCodeTextField.getText().toString().trim();
        String notes = notesTextField.getText().toString().trim();
        String occurrence = "yearly";
        if (onceRButton.isChecked())
            occurrence = "once";
        Boolean priority = false;
        if (prioritySwitch.isChecked())
            priority = true;

        if (checkFields(name, date, addressLine, state, city, postalCode)){
            final Long reminderId = System.currentTimeMillis();
            final Friend newFriend = new Friend(null, name, "null", occasion, date, occurrence,reminderId.intValue(), addressLine, state, city, postalCode, notes, priority, new ArrayList<CartItem>(0));
            dbUsers.child(friendID).setValue(newFriend);
            createNotification(occurrence, reminderId, name + "\'s " + occasion);

            if (friend != null){
                dbUsers.child(friendID).child("image").setValue(friend.getImage());
                for (CartItem item : friend.getWishlist()){
                    dbUsers.child(friendID).child("wishlist").child(item.getId()).setValue(item);
                }
            }

            if (selectedImage != null){
                storageRef.child(friendID).putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            storageRef.child(friendID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    dbUsers.child(friendID).child("image").setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
            if (friend != null)
                Toast.makeText(getActivity(), "Wishlist saved!", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getActivity(), "Wishlist added!", Toast.LENGTH_SHORT).show();
            goBack();
        }
    }

    public void createNotificationChannel (){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "ReminderNotificationChannel";
            String description = "Channel for event reminders";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("remindEvent", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void createNotification (String occurrence, Long id, String eventName) {
        long tenSeconds = 1000 * 30;
        Intent myIntent = new Intent(getActivity(), ReminderBroadcast.class );
        myIntent.putExtra("eventName", eventName);
        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService( ALARM_SERVICE );
        PendingIntent pendingIntent = PendingIntent.getBroadcast ( getActivity(), id.intValue() , myIntent , 0 );
        myCalendar.set(Calendar.SECOND, 0);
        myCalendar.set(Calendar.MINUTE, 0);
        myCalendar.set(Calendar.HOUR_OF_DAY, 0);
        myCalendar.set(Calendar.AM_PM , Calendar.AM);

        if (occurrence.equals("once")){
            alarmManager.set(AlarmManager. RTC_WAKEUP , id + tenSeconds, pendingIntent);
        } else
            alarmManager.setRepeating(AlarmManager. RTC_WAKEUP , myCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 365, pendingIntent); ;

        if (friend != null){
            PendingIntent p = PendingIntent.getBroadcast(getActivity(), friend.getReminderId(), myIntent, 0);
            alarmManager.cancel(p);
            p.cancel();
        }
    }

    public void quitWithoutSavingAlert(){
        if (getContext() == null)
            return;
        new AlertDialog.Builder(getContext())
                .setTitle("Quit without saving?")
                .setMessage("All unsaved changes will be lost.")
                .setIcon(R.drawable.ic_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        goBack();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void goBack() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStackImmediate();
    }
}
