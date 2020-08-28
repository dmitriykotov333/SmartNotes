package com.kotov.smartnotes.activity.editor;

import com.kotov.smartnotes.R;

public interface View {
       void showProgress();
       void hideProgress();
       void onAddSuccess(String message);
    void onAddError(String message);
}
