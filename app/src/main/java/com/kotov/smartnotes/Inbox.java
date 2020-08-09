package com.kotov.smartnotes;

import com.kotov.smartnotes.action.imageadapter.Item;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * @author dmitriykotov333@gmail.com
 * @since 06.08.2020
 */
public class Inbox extends RealmObject  {

    private String title;
    private String description;
    private String create_date;
    private String update_date;
    private int priority;
    private RealmList<Item> image;
    public Inbox() {

    }
    public Inbox(String title, String description, String create_date, String update_date, int priority) {
        this.title = title;
        this.description = description;
        this.create_date = create_date;
        this.update_date = update_date;
        //this.image = image;
        this.priority = priority;
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


    public RealmList<Item> getImage() {
        return image;
    }

    public void setImage(RealmList<Item> image) {
        this.image = image;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
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
}
