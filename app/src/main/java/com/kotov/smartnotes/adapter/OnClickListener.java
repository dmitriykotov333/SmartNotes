package com.kotov.smartnotes.adapter;

import android.view.View;

/**
 * @author dmitriykotov333@gmail.com
 * @since 06.08.2020
 */
public interface OnClickListener<T> {
    void onItemClick(View view, T inbox, int i);

    void onItemLongClick(View view, T inbox, int i);
}