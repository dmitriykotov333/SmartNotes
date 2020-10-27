package com.kotov.smartnotes.audiorecord;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kotov.smartnotes.R;
import com.kotov.smartnotes.model.Audio;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.AudioViewHolder> {

    //private File[] allFiles;
    private List<Audio> audioList;
    private TimeAgo timeAgo;

    private onItemListClick onItemListClick;

    public AudioListAdapter(List<Audio> audioList, onItemListClick onItemListClick) {
        this.audioList = audioList;
        this.onItemListClick = onItemListClick;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item, parent, false);
        timeAgo = new TimeAgo();
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        File allFiles = new File(audioList.get(position).getDirectory());
        holder.list_title.setText(allFiles.getName());
        holder.list_date.setText(timeAgo.getTimeAgo(allFiles.lastModified()));
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    public class AudioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView list_image;
        private TextView list_title;
        private TextView list_date;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);

            list_image = itemView.findViewById(R.id.list_image_view);
            list_title = itemView.findViewById(R.id.list_title);
            list_date = itemView.findViewById(R.id.list_date);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onItemListClick.onClickListener(audioList.get(getAdapterPosition()), getAdapterPosition());
        }
    }

    public interface onItemListClick {
        void onClickListener(Audio file, int position);
    }

}
