package com.kotdev.smartnotes;

public interface MvpPresenter<V> {

    void attachView(V mvpView);

    void detachView();

}