package com.kotdev.smartnotes.ui.adapters.draggable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;


import com.kotdev.smartnotes.R;
import com.kotdev.smartnotes.app.presenter.PresenterCreateNotes;
import com.kotdev.smartnotes.helpers.utils.Utility;
import com.kotdev.smartnotes.helpers.utils.Utils;
import com.kotdev.smartnotes.interfaces.CallbackClickListener;
import com.kotdev.smartnotes.interfaces.CallbackUpdateCheck;
import com.kotdev.smartnotes.interfaces.OnItemClickListener;
import com.kotdev.smartnotes.interfaces.OnStartDragListener;
import com.kotdev.smartnotes.room.AppDatabase;
import com.kotdev.smartnotes.room.Database;
import com.kotdev.smartnotes.room.category.CategoryDao;
import com.kotdev.smartnotes.room.checkbox.Checkbox;
import com.kotdev.smartnotes.room.checkbox.CheckboxDao;
import com.kotdev.smartnotes.room.image.ImageDao;
import com.kotdev.smartnotes.room.note.Note;
import com.kotdev.smartnotes.room.note.NoteDao;
import com.kotdev.smartnotes.ui.adapters.NotesAdapter;
import com.kotdev.smartnotes.ui.adapters.SliderAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AdapterCheck extends RecyclerView.Adapter<AdapterCheck.ViewHolder> implements DragItemTouchHelper.MoveHelperAdapter {



    private List<Checkbox> items = new ArrayList<>();
    private OnStartDragListener mDragStartListener;
    private OnItemClickListener mOnItemClickListener;
    private CallbackClickListener<Checkbox> onClickListener;
    private CallbackUpdateCheck callbackUpdateCheck;

    public void setCallbackUpdateCheck(CallbackUpdateCheck callbackUpdateCheck) {
        this.callbackUpdateCheck = callbackUpdateCheck;
    }

    public void setCheckboxes(List<Checkbox> items) {
        this.items = items;
    }

    public void setOnClickListener(CallbackClickListener<Checkbox> onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setDragListener(OnStartDragListener onStartDragListener) {
        this.mDragStartListener = onStartDragListener;
    }

    public AdapterCheck() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.check_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(position, items.get(position), holder);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        ImageView bt_move;
        View lyt_parent;
        com.google.android.material.textfield.TextInputEditText name;

        public ViewHolder(View itemView) {
            super(itemView);
            bt_move = itemView.findViewById(R.id.remove_check);
            name = itemView.findViewById(R.id.check_title);
            checkBox = itemView.findViewById(R.id.checkbox);
            lyt_parent = itemView.findViewById(R.id.lyt_parent);
        }

        @SuppressLint("ClickableViewAccessibility")
        public void bind(int position, Checkbox checkbox, ViewHolder holder) {
            boolean rst = checkbox.check != -1;
            checkBox.setChecked(rst);

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int rs;
                if (isChecked) {
                    rs = 1;
                } else {
                    rs = -1;
                }
                callbackUpdateCheck.updateCheck(rs, checkbox.create_date);
            });

            name.setText(checkbox.title);
            name.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {

                    bt_move.setImageResource(R.drawable.ic_delete);
                    bt_move.setOnClickListener(view -> {
                        if (onClickListener != null) {
                            onClickListener.clickListener(position, checkbox);
                        }
                    });
                } else {
                    bt_move.setImageResource(R.drawable.ic_view_headline_black_24dp);
                }
            });
            name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int is, int i1s, int i2s) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    new Database(holder.checkBox.getContext()).getDatabase().getCheckboxDao().updateCheckTitle(editable.toString(), checkbox.id);

                }
            });
            lyt_parent.setOnClickListener(view -> {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(view, items.get(position), position);
                }
            });
            lyt_parent.setOnTouchListener((v, event) -> {
                if (MotionEventCompat.getActionMasked(event) != 0 || mDragStartListener == null) {
                    return false;
                }
                mDragStartListener.onStartDrag(holder);
                return false;
            });

        }

    }


    public int getItemCount() {
        return items.size();
    }

    public List<Checkbox> get() {
        return items;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {

        return true;
    }


}