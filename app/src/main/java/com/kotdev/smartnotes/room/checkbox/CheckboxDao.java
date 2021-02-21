package com.kotdev.smartnotes.room.checkbox;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.kotdev.smartnotes.room.image.Image;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface CheckboxDao {

    @Insert
    Completable insert(Checkbox checkbox);

    @Insert
    Completable insert(List<Checkbox> checkbox);

    @Insert
    Completable insertWithoutId(Checkbox checkbox);

    @Query("update checkbox set checkbox = :checking where create_date  = :create_date")
    Completable updateCheck(int checking, String create_date);

    @Query("update checkbox set title_check = :titles where _id  = :create_date")
    void updateCheckTitle(String titles, long create_date);

    @Query("SELECT * FROM checkbox where notes_checkboxes_id = :id ORDER BY update_date desc")
    Flowable<List<Checkbox>> getAllCheckboxNotesId(long id);

    @Query("SELECT * FROM checkbox")
    Flowable<List<Checkbox>> getAllCheckbox();

    @Query("select * from checkbox where update_date = :date")
    Checkbox getCheckbox(String date);

    @Query("update checkbox set notes_checkboxes_id = :id where create_date  = :update_date")
    Completable addNotesCheckboxId(long id, String update_date);

    @Delete
    Completable deleteCheckbox(Checkbox checkbox);

    @Delete
    Completable deleteCheckbox(List<Checkbox> checkbox);

    @Query("delete from checkbox where notes_checkboxes_id  is null")
    Completable deleteCheckboxNullNotesId();
}