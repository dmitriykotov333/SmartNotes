package com.kotov.smartnotes.activity.editor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kotov.smartnotes.R;
import com.kotov.smartnotes.adapter.OnClickListener;
import com.kotov.smartnotes.model.Images;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.ViewHolder> {
    private Context context;
    private List<Images> items;
    private OnClickListener<Images> onClickListener;


    public void setOnClickListener(OnClickListener<Images> onClickListener) {
        this.onClickListener = onClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ViewHolder(android.view.View view) {
            super(view);
            this.imageView = view.findViewById(R.id.detail_image);
        }
    }

    public SliderAdapter(Context context, List<Images> list) {
        this.context = context;
        this.items = list;
    }

    @NonNull
    public SliderAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new SliderAdapter.ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_detail, viewGroup, false));
    }


    public void onBindViewHolder(SliderAdapter.ViewHolder viewHolder, final int i) {
        Images item = items.get(i);
        Glide.with(context).load(item.getImage()).diskCacheStrategy(DiskCacheStrategy.NONE).into(viewHolder.imageView);

        viewHolder.imageView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (onClickListener != null) {
                    onClickListener.onItemClick(v, item, i);
                }
            }
        });
    }

    public void removeData(int i) {
        removeItem(items.get(i));
    }

    private void removeItem(Images item) {
        int position = items.indexOf(item);

        /*Realm realm = Realm.getDefaultInstance();

        final RealmResults<Item> results = realm.where(Item.class).equalTo("image", item.getImage()).findAll();

        realm.executeTransaction(realm1 -> results.deleteAllFromRealm());*/
        notifyItemRemoved(position);
    }

    public Images getItem(int i) {
        return items.get(i);
    }

    public int getItemCount() {
        return items.size();
    }


}