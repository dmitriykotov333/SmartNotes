package com.kotov.smartnotes.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Check {
    private Integer id;
    private String title;
    private Integer check;
    private String create_date;
    private String update_date;
    private Integer notes_checkbox_id;
    public Check() {

    }
    public Check(String title, Integer check, String create_date, String update_date, Integer notes_checkbox_id) {
        this.title = title;
        this.check = check;
        this.create_date = create_date;
        this.update_date = update_date;
        this.notes_checkbox_id = notes_checkbox_id;
    }
    public Check(String title, Integer check, String create_date, String update_date) {
        this.title = title;
        this.check = check;
        this.create_date = create_date;
        this.update_date = update_date;
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

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getUpdate_date() {
        return update_date;
    }

    public void setUpdate_date(String update_date) {
        this.update_date = update_date;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
