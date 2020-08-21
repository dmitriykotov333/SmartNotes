package com.kotov.smartnotes.model;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * @author dmitriykotov333@gmail.com
 * @since 06.08.2020
 */
public class Inbox extends RealmObject {

    private String title;
    private String description;
    private String create_date;
    private String update_date;
    private int priority;
    private String password;
    private boolean fixNote;
    private RealmList<Item> image;
    public Inbox() {

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
    public Inbox(String title, String description, String create_date, String update_date, int priority, String password, boolean fixNote, RealmList<Item> image) {
        this.title = title;
        this.description = description;
        this.create_date = create_date;
        this.update_date = update_date;
        this.priority = priority;
        this.password = password;
        this.fixNote = fixNote;
        this.image = image;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isFixNote() {
        return fixNote;
    }

    public void setFixNote(boolean fixNote) {
        this.fixNote = fixNote;
    }



    private Date getDate(String dtStart) {
        //String dtStart = "2010-10-15T09:27:37Z";
        Date date = null;
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            date = format.parse(dtStart);
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
