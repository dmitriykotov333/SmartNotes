package com.kotov.smartnotes.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kotov.smartnotes.model.Inbox;
import com.kotov.smartnotes.R;
import com.kotov.smartnotes.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * @author dmitriykotov333@gmail.com
 * @since 06.08.2020
 */
public class AdapterList extends RecyclerView.Adapter<AdapterList.ViewHolder> {
    private Context context;
    private int current_selected_idx = -1;
    private List<Inbox> items;
    private OnClickListener<Inbox> onClickListener;
    private SparseBooleanArray selected_items;


    public void setOnClickListener(OnClickListener<Inbox> onClickListener) {
        this.onClickListener = onClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView desc;
        TextView title;
        RelativeLayout lyt_checked;
        View lyt_parent;
        View view_priority;

        ViewHolder(View view) {
            super(view);
            this.title = view.findViewById(R.id.title);
            this.desc = view.findViewById(R.id.desc);
            this.date = view.findViewById(R.id.date);
            this.lyt_checked = view.findViewById(R.id.lyt_checked);
            this.lyt_parent = view.findViewById(R.id.lyt_parent);
            this.view_priority = view.findViewById(R.id.view_priority);
        }
    }

    public AdapterList(Context context, List<Inbox> list) {
        this.context = context;
        this.items = list;
        this.selected_items = new SparseBooleanArray();
    }

    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_inbox, viewGroup, false));
    }


    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        Inbox inbox = items.get(i);
        viewHolder.title.setText(inbox.getTitle());
        viewHolder.desc.setText(inbox.getDescription());
        viewHolder.date.setText(inbox.getCreate_date());
        viewHolder.lyt_parent.setActivated(selected_items.get(i, false));
        if (inbox.getPriority() == Utils.PRIORITY_RED) {
            viewHolder.view_priority.setBackgroundColor(context.getResources().getColor(R.color.red_600));
        }
        if (inbox.getPriority() == Utils.PRIORITY_YELLOW) {
            viewHolder.view_priority.setBackgroundColor(context.getResources().getColor(R.color.yellow));
        }
        if (inbox.getPriority() == Utils.PRIORITY_GREEN) {
            viewHolder.view_priority.setBackgroundColor(context.getResources().getColor(R.color.green));
        }
        if (inbox.getPriority() == Utils.PRIORITY_DEFAULT) {
            viewHolder.view_priority.setBackgroundColor(0);
        }
        viewHolder.lyt_parent.setOnClickListener(view -> {
            if (onClickListener != null) {
                onClickListener.onItemClick(view, inbox, i);
            }
        });
        viewHolder.lyt_parent.setOnLongClickListener(view -> {
            if (onClickListener == null) {
                return false;
            }
            onClickListener.onItemLongClick(view, inbox, i);
            return true;
        });
        toggleCheckedIcon(viewHolder, i);
    }


    private void toggleCheckedIcon(ViewHolder viewHolder, int i) {
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

    public Inbox getItem(int i) {
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

    private void removeItem(Inbox item) {
        int position = items.indexOf(item);

        Realm realm = Realm.getDefaultInstance();

        final RealmResults<Inbox> results = realm.where(Inbox.class).equalTo("create_date", item.getCreate_date()).findAll();

        realm.executeTransaction(realm1 -> results.deleteAllFromRealm());
        notifyItemRemoved(position);
    }

    private void resetCurrentIndex() {
        current_selected_idx = -1;
    }
}
