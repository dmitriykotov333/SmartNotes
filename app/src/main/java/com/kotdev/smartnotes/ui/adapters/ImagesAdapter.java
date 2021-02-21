package com.kotdev.smartnotes.ui.adapters;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kotdev.smartnotes.R;
import com.kotdev.smartnotes.helpers.utils.Utility;
import com.kotdev.smartnotes.helpers.utils.Utils;
import com.kotdev.smartnotes.interfaces.CallbackClickListener;
import com.kotdev.smartnotes.room.image.Image;
import com.kotdev.smartnotes.room.note.Note;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

    private List<Image> items = new ArrayList<>();
    private CallbackClickListener<Image> clickListener;

    public void setItems(List<Image> items) {
        this.items = items;
    }

    public void setClickListener(CallbackClickListener<Image> clickListener) {
        this.clickListener = clickListener;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;


        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }

        public void bind(int position, Image image) {
            Glide.with(imageView).load(image.image).diskCacheStrategy(DiskCacheStrategy.NONE).into(imageView);
            imageView.setOnClickListener(v -> clickListener.clickListener(position, image));
        }

    }


    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_image, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(position, items.get(position));
    }


    public Image getItem(int i) {
        return items.get(i);
    }

    public int getItemCount() {
        return items.size();
    }
    public List<Image> getItems() {
        return items;
    }


}