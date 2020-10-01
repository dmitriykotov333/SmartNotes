package com.kotov.smartnotes.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmObject;

public class Check extends RealmObject implements Serializable {
    private String title;
    private boolean check;
    public Check() {

    }
    public Check(String title, boolean check) {
        this.title = title;
        this.check = check;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
