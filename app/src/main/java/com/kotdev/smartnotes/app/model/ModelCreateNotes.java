package com.kotdev.smartnotes.app.model;

import com.kotdev.smartnotes.room.category.Category;
import com.kotdev.smartnotes.room.note.Note;
import com.kotdev.smartnotes.room.Database;

import java.util.List;

import javax.inject.Inject;

import dagger.Module;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@Module
public class ModelCreateNotes {


    private final Database database;
    private Disposable disposable;

    @Inject
    public ModelCreateNotes(Database database) {
        this.database = database;
    }


    public void insert(Category category) {
        database.getDatabase().getCategoryDao().insert(category).subscribe();

    }
    public void insert( Note note) {
        database.getDatabase().getNoteDao().insert(note).subscribe();
    }

    public void update(Note notes) {
        disposable = database.getDatabase()
                .getNoteDao()
                .update(notes)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }
    public void update(String title, String content, String update_date, int priority, String password, long id, String date) {
        disposable = database.getDatabase()
                .getNoteDao()
                .update(title, content, update_date, priority, password, id, date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void delete(Note note) {
        database.getDatabase()
                .getNoteDao()
                .delete(note)
                .subscribe();
    }

    public Flowable<List<Category>> getCategories() {
        return database.getDatabase().getCategoryDao().getCategories().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Integer getCategoryId(String name) {
        return database.getDatabase()
                .getCategoryDao()
                .getCategoryId(name);
    }

    public String getCategoryById(long id) {
        return database.getDatabase()
                .getCategoryDao()
                .getCategoryById(id);
    }

    public void dispose() {
        if (disposable != null) {
            disposable.dispose();
        }
    }




}
