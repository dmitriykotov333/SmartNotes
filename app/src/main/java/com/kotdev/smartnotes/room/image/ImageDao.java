package com.kotdev.smartnotes.room.image;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.kotdev.smartnotes.room.note.Note;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface ImageDao {

    @Insert
    Completable insert(Image image);

    @Insert
    Completable insertWithoutId(Image image);

    @Query("SELECT * FROM images where notes_images_id = :id ORDER BY update_date desc")
    Flowable<List<Image>> getAllImagesNotesId(long id);

    @Query("SELECT * FROM images where notes_images_id")
    Flowable<List<Image>> getAllImages();

    @Query("SELECT * FROM images ORDER BY update_date desc")
    Single<List<Image>> getAllImagesAction();

    @Query("select * from images where update_date = :date")
    Image getImg(String date);

    @Query("update images set notes_images_id = :id where create_date  = :update_date")
    Completable addNotesImagesId(long id, String update_date);

    @Delete
    Completable deleteImage(Image image);

    @Query("delete from images where notes_images_id  is null")
    Completable deleteImageNullNotesId();
}