package com.kotov.smartnotes.activity.editor;

public interface View {

    void showProgress();

    void hideProgress();

    void onAddSuccess(String message);

    void onAddError(String message);
}
