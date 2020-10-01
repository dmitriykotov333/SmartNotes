package com.kotov.smartnotes.model;
import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;

public class MapNote extends RealmObject implements Serializable {
    private String key;
    private RealmList<Inbox> notes;

    public MapNote() {

    }

    public MapNote(String key, RealmList<Inbox> notes) {
        this.key = key;
        this.notes = notes;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public RealmList<Inbox> getNotes() {
        return notes;
    }

    public void setNotes(RealmList<Inbox> notes) {
        this.notes = notes;
    }

}