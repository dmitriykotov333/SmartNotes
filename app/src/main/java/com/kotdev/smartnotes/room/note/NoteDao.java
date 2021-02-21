package com.kotdev.smartnotes.room.note;

import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;

@Dao
public interface NoteDao {

    @Insert
    Completable insert(Note note);

    @Query("select * from notes as nt inner join categories as ct on ct.id = nt.category_id where nt.category_id = :id AND fix_note = -1 order by COALESCE(priority_note, priority_note), update_date desc")
    LiveData<List<Note>> getNotes(long id);

    @Query("select * from notes as nt inner join categories as ct on ct.id = nt.category_id where nt.category_id = :id AND fix_note = 1 order by COALESCE(priority_note, priority_note), update_date desc")
    LiveData<List<Note>> getNotesFix(long id);

    @Query("update notes set title = :title, content = :content, update_date = :update_date, priority_note = :priority, password = :password, category_id = :id where create_date = :date")
    Completable update(String title, String content, String update_date, int priority, String password, long id, String date);

    @Delete
    Completable delete(Note notes);

    @Update
    Completable update(Note notes);
}