package com.kotov.smartnotes.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Check {
    private Integer id;
    private String title;
    private Integer check;
    private Integer notes_checkbox_id;
    public Check() {

    }
    public Check(String title, Integer check) {
        this.title = title;
        this.check = check;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer isCheck() {
        return check;
    }

    public void setCheck(Integer check) {
        this.check = check;
    }

    public Integer getNotes_checkbox_id() {
        return notes_checkbox_id;
    }

    public void setNotes_checkbox_id(Integer notes_checkbox_id) {
        this.notes_checkbox_id = notes_checkbox_id;
    }
}
