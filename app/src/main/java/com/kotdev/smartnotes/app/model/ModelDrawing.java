package com.kotdev.smartnotes.app.model;

import androidx.lifecycle.LiveData;

import com.kotdev.smartnotes.room.Database;
import com.kotdev.smartnotes.room.image.Image;
import com.kotdev.smartnotes.room.note.Note;

import java.util.List;

import javax.inject.Inject;

import dagger.Module;

@Module
public class ModelDrawing {

    private final Database database;

    @Inject
    public ModelDrawing(Database database) {
        this.database = database;
    }

    public void insertWithoutId(Image image) {
        database.getDatabase().getImageDao().insertWithoutId(image).subscribe();
    }

}
