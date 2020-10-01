package com.kotov.smartnotes.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;

public class Item extends RealmObject implements Serializable{
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