package com.kotov.smartnotes.model;

import java.util.List;

public class Note  {

    private String title;
    private String description;
    private String create_date;
    private String update_date;
    private Integer priority;
    private String password;
    private Integer fixNote;
    private Integer category_id;
    public Note() {

    }
    /*public Inbox(String title, String description, String create_date, String update_date, int priority, String password, boolean fixNote, RealmList<Item> image) {
        this.title = title;
        this.description = description;
        this.create_date = create_date;
        this.update_date = update_date;
        this.priority = priority;
        this.password = password;
        this.fixNote = fixNote;
        this.image = image;
    }*/
    public Note(String title, String description, String create_date, String update_date, Integer priority, String password, Integer fixNote, Integer category_id) {
        this.title = title;
        this.description = description;
        this.create_date = create_date;
        this.update_date = update_date;
        this.priority = priority;
        this.password = password;
        this.fixNote = fixNote;
        this.category_id = category_id;
    }
    public Note(String title, String description, String create_date, String update_date) {
        this.title = title;
        this.description = description;
        this.create_date = create_date;
        this.update_date = update_date;
    }
    public Note(String title, String description, String create_date, String update_date, Integer priority, String password, Integer fixNote) {
        this.title = title;
        this.description = description;
        this.create_date = create_date;
        this.update_date = update_date;
        this.priority = priority;
        this.password = password;
        this.fixNote = fixNote;
    }
    public Note(String title, String description, String update_date, Integer priority, String password, Integer fixNote) {
        this.title = title;
        this.description = description;
        this.update_date = update_date;
        this.priority = priority;
        this.password = password;
        this.fixNote = fixNote;
    }
    public Note(String title, String description, String create_date, String update_date, Integer priority, String password) {
        this.title = title;
        this.description = description;
        this.create_date = create_date;
        this.update_date = update_date;
        this.priority = priority;
        this.password = password;
    }
    public Note(Integer category_id) {
        this.category_id = category_id;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer isFixNote() {
        return fixNote;
    }

    public void setFixNote(Integer fixNote) {
        this.fixNote = fixNote;
    }

    public Integer getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Integer category_id) {
        this.category_id = category_id;
    }
}
