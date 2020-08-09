package com.kotov.smartnotes.action.imageadapter;


import io.realm.RealmObject;

public class Item extends RealmObject  {
    private byte[] image;

    public Item() {

    }
    public Item(byte[] image) {
        this.image = image;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

}
