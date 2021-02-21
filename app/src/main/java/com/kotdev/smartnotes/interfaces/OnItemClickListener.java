package com.kotdev.smartnotes.interfaces;

import android.view.View;

import com.kotdev.smartnotes.room.checkbox.Checkbox;

public interface OnItemClickListener {
    void onItemClick(View view, Checkbox social, int i);
}
