package com.kotdev.smartnotes.app.model;

import com.kotdev.smartnotes.room.Database;
import com.kotdev.smartnotes.room.checkbox.Checkbox;
import com.kotdev.smartnotes.room.image.Image;

import java.util.List;

import javax.inject.Inject;

import dagger.Module;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@Module
public class ModelCheckbox {


    private final Database database;
    private Disposable disposable;

    @Inject
    public ModelCheckbox(Database database) {
        this.database = database;
    }


    public void insert(Image image) {
        database.getDatabase().getImageDao().insert(image).subscribe();
    }

    public void insertWithoutId(Checkbox checkbox) {
        database.getDatabase().getCheckboxDao().insertWithoutId(checkbox).subscribe();
    }

    public void updateCheck(int checking, String create_date) {
        database.getDatabase().getCheckboxDao().updateCheck(checking, create_date).subscribe();
    }

    public void updateCheckTitle(String title, long create_date) {
        database.getDatabase().getCheckboxDao().updateCheckTitle(title, create_date);
    }

    public void delete(Checkbox checkbox) {
        database.getDatabase().getCheckboxDao().deleteCheckbox(checkbox).subscribe();
    }
    public void deleteCheckboxNullNotesId() {
        database.getDatabase().getCheckboxDao().deleteCheckboxNullNotesId().subscribe();
    }

    public Flowable<List<Checkbox>> getAllCheckboxNotesId(long id) {
        return database
                .getDatabase()
                .getCheckboxDao()
                .getAllCheckboxNotesId(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Flowable<List<Checkbox>> getAllCheckbox() {
        return database
                .getDatabase()
                .getCheckboxDao()
                .getAllCheckbox()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Checkbox getCheckbox(String date) {
        return database.getDatabase().getCheckboxDao().getCheckbox(date);
    }

    public void addNotesCheckboxId(long id, String update_date) {
        database.getDatabase().getCheckboxDao().addNotesCheckboxId(id, update_date).subscribe();
    }


}
