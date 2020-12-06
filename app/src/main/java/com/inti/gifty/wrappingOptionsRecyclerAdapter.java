package com.inti.gifty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class wrappingOptionsRecyclerAdapter extends RecyclerView.Adapter<wrappingOptionsRecyclerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Wrapping> items;

    private View.OnClickListener onItemClickListener;


    public wrappingOptionsRecyclerAdapter(Context context, ArrayList<Wrapping> items) {
        this.context = context;
        this.items = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView wrappingImage, checkedImage;

        public ViewHolder(View view) {
            super(view);
            wrappingImage = view.findViewById(R.id.wrapping_image);
            checkedImage = view.findViewById(R.id.checked_image);

            view.setTag(this);
            view.setOnClickListener(onItemClickListener);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.wrapping_option_recycler_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Wrapping item = items.get(position);
        Glide.with(context).load(item.getImage()).placeholder(R.color.light_gray).centerCrop().into(holder.wrappingImage);

        if (item.isSelected()){
            holder.checkedImage.setVisibility(View.VISIBLE);
        } else {
            holder.checkedImage.setVisibility(View.INVISIBLE);
        }
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener){
        onItemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
