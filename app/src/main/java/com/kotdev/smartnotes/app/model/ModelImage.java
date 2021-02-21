package com.kotdev.smartnotes.app.model;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.kotdev.smartnotes.room.Database;
import com.kotdev.smartnotes.room.category.Category;
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
public class ModelImage {


    private final Database database;
    private Disposable disposable;

    @Inject
    public ModelImage(Database database) {
        this.database = database;
    }


    public void insert(Image image) {
        database.getDatabase().getImageDao().insert(image).subscribe();
    }

    public void insertWithoutId(Image image) {
        database.getDatabase().getImageDao().insertWithoutId(image).subscribe();
    }

    public void delete(Image image) {
        database.getDatabase().getImageDao().deleteImage(image).subscribe();
    }
    public void deleteImageNullNotesId() {
        database.getDatabase().getImageDao().deleteImageNullNotesId().subscribe();
    }

    public Flowable<List<Image>> getAllImagesNotesId(long id) {
        return database
                .getDatabase()
                .getImageDao()
                .getAllImagesNotesId(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Image>> getAllImages() {
        return database
                .getDatabase()
                .getImageDao()
                .getAllImagesAction()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Image getImg(String date) {
        return database.getDatabase().getImageDao().getImg(date);
    }

    public void addNotesImagesId(long id, String update_date) {
        database.getDatabase().getImageDao().addNotesImagesId(id, update_date).subscribe();
    }


}
