package com.inti.gifty;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

class Wrapping {
    private Drawable image;
    private boolean selected;

    public Wrapping(Drawable image) {
        this.image = image;
        this.selected = false;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}

public class Customise extends Fragment {

    RecyclerView solidColoursRecyclerView, patternsRecyclerView;
    ImageView uploadPatternImage, uploadPatternCheck, nonePatternCheck, uploadPatternIcon;
    TextView noneButton;
    EditText messageTextField;
    ImageButton backButton;
    Button continueButton;

    wrappingOptionsRecyclerAdapter solidColoursAdapter;
    wrappingOptionsRecyclerAdapter patternsAdapter;
    ArrayList<Wrapping> solidColours = new ArrayList<>();
    ArrayList<Wrapping> patterns = new ArrayList<>();
    final static int REQUEST_CODE_CHOOSE = 1;
    Order newOrder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null){
            newOrder = (Order) getArguments().getSerializable("order");
        }
        View view = inflater.inflate(R.layout.fragment_customise, container, false);

        solidColoursRecyclerView = view.findViewById(R.id.solid_colours_recycler_view);
        patternsRecyclerView = view.findViewById(R.id.patterns_recycler_view);
        uploadPatternImage = view.findViewById(R.id.upload_pattern_image);
        noneButton = view.findViewById(R.id.none_pattern_button);
        uploadPatternCheck = view.findViewById(R.id.upload_pattern_check);
        nonePatternCheck = view.findViewById(R.id.none_pattern_check);
        uploadPatternIcon = view.findViewById(R.id.upload_pattern_icon);
        messageTextField = view.findViewById(R.id.message_edit_text);
        backButton = view.findViewById(R.id.back_button);
        continueButton = view.findViewById(R.id.continue_button);

        fillLists();

        uploadPatternImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPattern();
                clearCheck();
                uploadPatternCheck.setVisibility(View.VISIBLE);
                updateAdapters();
            }
        });

        noneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCheck();
                nonePatternCheck.setVisibility(View.VISIBLE);
                updateAdapters();
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToPayment();
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

    public void fillLists(){
        if (getContext() == null) return;
        //Filling solid colours
        solidColours.add(new Wrapping(getResources().getDrawable(R.color.colorPrimary)));
        solidColours.add(new Wrapping(getResources().getDrawable(R.color.colorPrimaryLight)));
        solidColours.add(new Wrapping(getResources().getDrawable(R.color.colorPrimarySuperDark)));
        solidColours.add(new Wrapping(getResources().getDrawable(R.color.colorPrimarySuperLight)));
        solidColours.add(new Wrapping(getResources().getDrawable(R.color.colorPrimaryDark)));

        solidColoursAdapter = new wrappingOptionsRecyclerAdapter(getActivity(), solidColours);
        linkToAdapter(solidColoursRecyclerView, solidColoursAdapter, solidColours);

        // Filling patterns
        patterns.add(new Wrapping(getResources().getDrawable(R.drawable.wrapping_blue)));
        patterns.add(new Wrapping(getResources().getDrawable(R.drawable.wrapping_brown_paper)));
        patterns.add(new Wrapping(getResources().getDrawable(R.drawable.wrapping_christmas)));
        patterns.add(new Wrapping(getResources().getDrawable(R.drawable.wrapping_dark_celebration)));
        patterns.add(new Wrapping(getResources().getDrawable(R.drawable.wrapping_dots)));
        patterns.add(new Wrapping(getResources().getDrawable(R.drawable.wrapping_fallout)));
        patterns.add(new Wrapping(getResources().getDrawable(R.drawable.wrapping_gold_blue)));
        patterns.add(new Wrapping(getResources().getDrawable(R.drawable.wrapping_snowflakes)));

        patternsAdapter = new wrappingOptionsRecyclerAdapter(getActivity(), patterns);
        linkToAdapter(patternsRecyclerView, patternsAdapter, patterns);
    }

    public void linkToAdapter(RecyclerView recyclerView, wrappingOptionsRecyclerAdapter adapter, final ArrayList<Wrapping> list){
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearCheck();
                RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
                int position = viewHolder.getAdapterPosition();
                if (position >= 0){
                    Wrapping item = list.get(position);
                    item.setSelected(true);
                }
                updateAdapters();
            }
        });
    }

    public void clearCheck(){
        continueButton.setEnabled(true);
        for (Wrapping item : solidColours){
            item.setSelected(false);
        }
        for (Wrapping item : patterns){
            item.setSelected(false);
        }
        uploadPatternCheck.setVisibility(View.INVISIBLE);
        nonePatternCheck.setVisibility(View.INVISIBLE);
    }

    public void updateAdapters(){
        patternsAdapter.notifyDataSetChanged();
        solidColoursAdapter.notifyDataSetChanged();
    }

    private void uploadPattern() {
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
            Uri selectedImage = result.getUri();
            if (getContext() != null)
                Glide.with(getContext()).load(selectedImage).placeholder(R.color.light_gray).into(uploadPatternImage);
            uploadPatternIcon.setVisibility(View.INVISIBLE);
        }
    }

    public void sendToPayment(){
        Fragment fragment = new Payment();
        AppCompatActivity activity = (AppCompatActivity) getContext();
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        Bundle args = new Bundle();
        args.putSerializable("order", newOrder);
        fragment.setArguments(args);
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}