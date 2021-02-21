package com.kotdev.smartnotes.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kotdev.smartnotes.R;
import com.kotdev.smartnotes.helpers.utils.Utility;
import com.kotdev.smartnotes.helpers.utils.Utils;
import com.kotdev.smartnotes.interfaces.CallbackClickListener;
import com.kotdev.smartnotes.room.note.Note;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private List<Note> mNotes;
    private CallbackClickListener<Note> clickListener;

    public void setNotes(List<Note> mNotes) {
        this.mNotes = mNotes;
    }

    public void setClickListener(CallbackClickListener<Note> clickListener) {
        this.clickListener = clickListener;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_note_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       holder.bind(position, mNotes.get(position));
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public List<Note> getNotes() {
        return mNotes;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView timestamp, title, description;
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            timestamp = itemView.findViewById(R.id.date);
            view = itemView.findViewById(R.id.view_priority);
        }

        public void bind(int position, Note note) {
            title.setText(mNotes.get(position).title);
            description.setText(mNotes.get(position).content);
            String month = mNotes.get(position).update_date.substring(5, 7);
            month = Utility.getMonthFromNumber(month);
            String year = mNotes.get(position).update_date.substring(0, 4);

            timestamp.setText(String.format("%s %s %s", mNotes.get(position).update_date.substring(8, 10), month, year));


                if (mNotes.get(position).priority == Utils.PRIORITY_RED) {
                    view.setBackgroundColor(view.getResources().getColor(R.color.red_600));
                } else if (mNotes.get(position).priority == Utils.PRIORITY_YELLOW) {
                    view.setBackgroundColor(view.getResources().getColor(R.color.yellow));
                } else if (mNotes.get(position).priority == Utils.PRIORITY_GREEN) {
                    view.setBackgroundColor(view.getResources().getColor(R.color.green));
                } else if (mNotes.get(position).priority == Utils.PRIORITY_DEFAULT) {
                    view.setBackgroundColor(0);
                }


            itemView.setOnClickListener(v -> clickListener.clickListener(position, note));
        }

    }
    public void remove(int position) {
        mNotes.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mNotes.size());
    }
}




