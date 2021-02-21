package com.kotdev.smartnotes.room.category;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface CategoryDao {

    @Insert
    Completable insert(Category category);

    @Query("SELECT * FROM categories")
    Flowable<List<Category>> getCategories();

    @Delete
    Completable delete(Category category);

    @Query("select id from categories where category = :name")
    Integer getCategoryId(String name);

    @Query("select ct.category from categories as ct inner join notes as nt on ct.id = nt.category_id where nt.category_id = :id")
    String getCategoryById(long id);
}