package com.inti.gifty;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import static android.app.Activity.RESULT_OK;

public class ProfileForm extends Fragment {

    DatabaseReference dbUsers;
    StorageReference storageRef;

    ImageView profileImage;
    EditText nameEditText;
    Button saveButton;
    ImageButton backButton;

    final static int REQUEST_CODE_CHOOSE = 1;
    Uri selectedImage;

    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_form, container, false);
        if (getArguments() != null){
            user = (User) getArguments().getSerializable("user");
        }

        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        dbUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        storageRef = FirebaseStorage.getInstance().getReference().child("Users").child(userId).child("profile");

        profileImage = view.findViewById(R.id.edit_profile_image);
        nameEditText = view.findViewById(R.id.name_edit_text);
        saveButton = view.findViewById(R.id.save_button);
        backButton = view.findViewById(R.id.back_button);

        nameEditText.setText(user.getName());
        if (getContext() != null)
            Glide.with(getContext()).load(user.getImage()).placeholder(R.drawable.ic_profile_fill).circleCrop().into(profileImage);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setProfilePicture();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (saveProfile()){
                    Toast.makeText(getActivity(), "Profile saved!", Toast.LENGTH_SHORT).show();
                    goBack();
                } else
                    Toast.makeText(getActivity(), "Something went wrong, try again later.", Toast.LENGTH_SHORT).show();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });

        return view;
    }

    private boolean saveProfile() {
        String name = nameEditText.getText().toString();
        if (TextUtils.isEmpty(name)){
            nameEditText.setError("Name is required.");
            return false;
        }

        dbUsers.child("name").setValue(name);
        if (selectedImage != null){
            storageRef.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                dbUsers.child("image").setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        return true;
    }

    private void setProfilePicture() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUEST_CODE_CHOOSE);
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
                Glide.with(getContext()).load(selectedImage).circleCrop().into(profileImage);
        }
    }

    public void goBack(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStackImmediate();
    }
}