package com.kotdev.smartnotes.ui.adapters;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.stream.MediaStoreImageThumbLoader;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.kotdev.smartnotes.R;
import com.kotdev.smartnotes.interfaces.CallbackClickListener;
import com.kotdev.smartnotes.room.image.Image;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.ViewHolder> {

    private List<Image> items = new ArrayList<>();

    public void setItems(List<Image> items) {
        this.items = items;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        SubsamplingScaleImageView imageView;


        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }

        public void bind(int position, Image image) {
            imageView.setImage(ImageSource.bitmap(BitmapFactory.decodeByteArray(image.image, 0, image.image.length)));
        }

    }


    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_image_full, viewGroup, false));
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
    public void remove(int position) {
        items.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, items.size());
    }

}