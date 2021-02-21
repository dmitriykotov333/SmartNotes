package com.kotdev.smartnotes.app.model;

import androidx.lifecycle.LiveData;

import com.kotdev.smartnotes.room.note.Note;
import com.kotdev.smartnotes.room.Database;

import java.util.List;

import javax.inject.Inject;

import dagger.Module;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@Module
public class ModelNotes {

    private final Database database;

    @Inject
    public ModelNotes(Database database) {
        this.database = database;
    }

    public LiveData<List<Note>> getAllNotes(long id) {
        return database.getDatabase()
                .getNoteDao()
                .getNotes(id);
    }
    public LiveData<List<Note>> getAllNotesFix(long id) {
        return database.getDatabase()
                .getNoteDao()
                .getNotesFix(id);
    }

    public void delete(Note note) {
        database.getDatabase()
                .getNoteDao()
                .delete(note)
                .subscribe();
    }

}
