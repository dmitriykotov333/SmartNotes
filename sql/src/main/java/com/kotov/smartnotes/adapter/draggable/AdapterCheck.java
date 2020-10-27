package com.kotov.smartnotes.adapter.draggable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.kotov.smartnotes.R;
import com.kotov.smartnotes.activity.editor.Presenter;
import com.kotov.smartnotes.adapter.OnClickListener;
import com.kotov.smartnotes.database.Action;
import com.kotov.smartnotes.database.CheckAction;
import com.kotov.smartnotes.model.Check;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterCheck extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements DragItemTouchHelper.MoveHelperAdapter {


    public Context ctx;
    public List<Check> items = new ArrayList();
    public OnStartDragListener mDragStartListener = null;
    public OnItemClickListener mOnItemClickListener;
    private OnClickListener<Check> onClickListener;
    private String date;


    public void setOnClickListener(OnClickListener<Check> onClickListener) {
        this.onClickListener = onClickListener;
    }
    public interface OnItemClickListener {
        void onItemClick(View view, Check social, int i);
    }

    public interface OnStartDragListener {
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public AdapterCheck(Context context, List<Check> items, String date) {
        this.items = items;
        this.ctx = context;
        this.date = date;
        checkAction = new CheckAction(ctx);
        action = new Action(ctx);
    }

    public void setDragListener(OnStartDragListener onStartDragListener) {
        this.mDragStartListener = onStartDragListener;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder implements DragItemTouchHelper.TouchViewHolder {
        public CheckBox checkBox;
        public ImageView bt_move;
        public View lyt_parent;
        public com.google.android.material.textfield.TextInputEditText name;

        public OriginalViewHolder(View view) {
            super(view);
            this.bt_move = (ImageView) view.findViewById(R.id.remove_check);
            this.name = (com.google.android.material.textfield.TextInputEditText) view.findViewById(R.id.check_title);
            this.checkBox = (CheckBox) view.findViewById(R.id.checkbox);
            this.lyt_parent = view.findViewById(R.id.lyt_parent);
        }

        public void onItemSelected() {
            this.itemView.setBackgroundColor(ctx.getResources().getColor(R.color.grey));
        }

        public void onItemClear() {
            this.itemView.setBackgroundColor(0);
        }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new OriginalViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.check_item, viewGroup, false));
    }

    @SuppressLint("ClickableViewAccessibility")
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int i) {
        if (viewHolder instanceof OriginalViewHolder) {
            OriginalViewHolder originalViewHolder = (OriginalViewHolder) viewHolder;
            Check social = items.get(i);
            //originalViewHolder.checkBox.setChecked(social.isCheck());
            boolean rst = social.isCheck() != -1;
            originalViewHolder.checkBox.setChecked(rst);

            originalViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int rs;
                    if (isChecked) {
                        rs = 1;
                    } else {
                        rs = -1;
                    }
                   checkAction.updateCheck(rs, items, i);
                    // сохраняем изменяемое значение в массив
                   /* Realm.getDefaultInstance().executeTransaction(realm -> {
                        items.get(i).setCheck(isChecked);
                        realm.insertOrUpdate(items);
                    });*/
                }
            });
            if (rst) {
                originalViewHolder.name.requestFocus();
            }
            originalViewHolder.name.setText(social.getTitle());
            originalViewHolder.name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        originalViewHolder.bt_move.setImageResource(R.drawable.ic_delete);
                        originalViewHolder.bt_move.setOnClickListener(view -> {
                            if (onClickListener != null) {
                              onClickListener.onItemClick(view, social, i);
                             }
                        });
                    } else {
                        originalViewHolder.bt_move.setImageResource(R.drawable.ic_view_headline_black_24dp);
                    }
                }
            });
            originalViewHolder.name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int is, int i1s, int i2s) {
                    /*Realm.getDefaultInstance().executeTransaction(realm -> {
                        items.get(i).setTitle(originalViewHolder.name.getText().toString());
                        items.get(i).setCheck(originalViewHolder.checkBox.isChecked());
                        realm.insertOrUpdate(items);
                    });*/

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    checkAction.updateCheckTitle(Objects.requireNonNull(originalViewHolder.name.getText()).toString(), items, i);
                }
            });
            originalViewHolder.lyt_parent.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, items.get(i), i);
                    }
                }
            });
            originalViewHolder.lyt_parent.setOnTouchListener((view, motionEvent) -> {
                if (MotionEventCompat.getActionMasked(motionEvent) != 0 || mDragStartListener == null) {
                    return false;
                }
                mDragStartListener.onStartDrag(viewHolder);
                return false;
            });
           /* originalViewHolder.bt_move.setOnTouchListener((view, motionEvent) -> {
                if (MotionEventCompat.getActionMasked(motionEvent) != 0 || mDragStartListener == null) {
                    return false;
                }
                mDragStartListener.onStartDrag(viewHolder);
                return false;
            });*/
        }
    }

    public int getItemCount() {
        return this.items.size();
    }
    public List<Check> get() {
        return items;
    }
    boolean rst = false;
    private CheckAction checkAction;
    public void addItem(Check dataObj, int index, String date) {
        items.add(dataObj);
        checkAction.addCheck(items);
        notifyItemInserted(index);
        rst = true;
    }
    public void deleteItem(int index) {
        /*Realm.getDefaultInstance().executeTransaction(realm -> {
            items.remove(index);
            realm.insertOrUpdate(items);
        });*/
        checkAction.deleteCheckNotesId(items);
        items.remove(index);
        checkAction.addOneCheck(items, date, action);
        // checkAction.deleteCheck(items.get(index).getUpdate_date());
        notifyItemRemoved(index);
        notifyItemRangeChanged(index, getItemCount());
    }
    Action action;
    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {

      checkAction.deleteCheckNotesId(items);
        Collections.swap(items, fromPosition, toPosition);
        checkAction.addOneCheck(items, date, action);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }


}