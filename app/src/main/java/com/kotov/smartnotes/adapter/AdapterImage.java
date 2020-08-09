package com.kotov.smartnotes.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kotov.smartnotes.R;
import com.kotov.smartnotes.model.Item;

import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;

public class AdapterImage extends RecyclerView.Adapter<AdapterImage.ViewHolder> {
    private Context context;
    private int current_selected_idx = -1;
    private List<Item> items;
    private OnClickListener<Item> onClickListener;
    private SparseBooleanArray selected_items;


    public void setOnClickListener(OnClickListener<Item> onClickListener) {
        this.onClickListener = onClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
       ImageView imageView;
        RelativeLayout lyt_checked;
        View lyt_parent;

        ViewHolder(View view) {
            super(view);
            this.imageView = view.findViewById(R.id.image);
            this.lyt_checked = view.findViewById(R.id.lyt_checked);
            this.lyt_parent = view.findViewById(R.id.lyt_parent);
        }
    }

    public AdapterImage(Context context, List<Item> list) {
        this.context = context;
        this.items = list;
        this.selected_items = new SparseBooleanArray();
    }

    @NonNull
    public AdapterImage.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new AdapterImage.ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_image, viewGroup, false));
    }


    public void onBindViewHolder(AdapterImage.ViewHolder viewHolder, final int i) {
        Item item = items.get(i);
        Glide.with(context).load(item.getImage()).diskCacheStrategy(DiskCacheStrategy.NONE).into(viewHolder.imageView);
        viewHolder.lyt_parent.setActivated(selected_items.get(i, false));
        viewHolder.lyt_parent.setOnClickListener(view -> {
            if (onClickListener != null) {
                onClickListener.onItemClick(view, item, i);
            }
        });
        viewHolder.lyt_parent.setOnLongClickListener(view -> {
            if (onClickListener == null) {
                return false;
            }
            onClickListener.onItemLongClick(view, item, i);
            return true;
        });
        toggleCheckedIcon(viewHolder, i);
    }


    private void toggleCheckedIcon(AdapterImage.ViewHolder viewHolder, int i) {
        if (selected_items.get(i, false)) {
            viewHolder.lyt_checked.setVisibility(View.VISIBLE);

            viewHolder.lyt_parent.setBackgroundColor(context.getResources().getColor(R.color.grey));
            if (current_selected_idx == i) {
                resetCurrentIndex();
                return;
            }
            return;
        }
        viewHolder.lyt_checked.setVisibility(View.GONE);
        viewHolder.lyt_parent.setBackgroundColor(0);
        if (current_selected_idx == i) {
            resetCurrentIndex();
        }
    }

    public Item getItem(int i) {
        return items.get(i);
    }

    public int getItemCount() {
        return items.size();
    }

    public void toggleSelection(int i) {
        current_selected_idx = i;
        if (selected_items.get(i, false)) {
            selected_items.delete(i);
        } else {
            selected_items.put(i, true);
        }
        notifyItemChanged(i);
    }

    public void clearSelections() {
        selected_items.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selected_items.size();
    }

    public List<Integer> getSelectedItems() {
        ArrayList<Integer> arrayList = new ArrayList<>(selected_items.size());
        for (int i = 0; i < selected_items.size(); i++) {
            arrayList.add(selected_items.keyAt(i));
        }
        return arrayList;
    }

    public void removeData(int i) {
        removeItem(items.get(i));
        resetCurrentIndex();
    }

    private void removeItem(Item item) {
        int position = items.indexOf(item);
        Realm realm = Realm.getDefaultInstance();
        final RealmResults<Item> results = realm.where(Item.class).equalTo("image", item.getImage()).findAll();
        realm.executeTransaction(realm1 -> results.deleteAllFromRealm());
        notifyItemRemoved(position);
    }

    private void resetCurrentIndex() {
        current_selected_idx = -1;
    }
}
