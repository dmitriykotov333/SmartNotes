package com.kotdev.smartnotes.room;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.kotdev.smartnotes.room.category.Category;
import com.kotdev.smartnotes.room.checkbox.Checkbox;
import com.kotdev.smartnotes.room.checkbox.CheckboxDao;
import com.kotdev.smartnotes.room.image.Image;
import com.kotdev.smartnotes.room.image.ImageDao;
import com.kotdev.smartnotes.room.note.Note;
import com.kotdev.smartnotes.room.category.CategoryDao;
import com.kotdev.smartnotes.room.note.NoteDao;


@Database(entities = {Note.class, Category.class, Checkbox.class, Image.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract NoteDao getNoteDao();
    public abstract CategoryDao getCategoryDao();
    public abstract ImageDao getImageDao();
    public abstract CheckboxDao getCheckboxDao();
}