package com.kotov.smartnotes.model;

import java.io.Serializable;

public class Images implements Serializable {

    private Integer id;
    private byte[] image;
    private String create_date;
    private String update_date;
    private Integer notes_images_id;

    public Images(Integer id, byte[] image, String create_date, String update_date, Integer notes_images_id) {
        this.id = id;
        this.image = image;
        this.create_date = create_date;
        this.update_date = update_date;
        this.notes_images_id = notes_images_id;
    }
    public Images(byte[] image, String create_date, String update_date, Integer notes_images_id) {
        this.image = image;
        this.create_date = create_date;
        this.update_date = update_date;
        this.notes_images_id = notes_images_id;
    }
    public Images(byte[] image, String create_date, String update_date) {
        this.image = image;
        this.create_date = create_date;
        this.update_date = update_date;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
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

    public Integer getNotes_images_id() {
        return notes_images_id;
    }

    public void setNotes_images_id(Integer notes_images_id) {
        this.notes_images_id = notes_images_id;
    }
}
