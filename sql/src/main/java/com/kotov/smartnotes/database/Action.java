package com.kotov.smartnotes.database;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.kotov.smartnotes.model.Audio;
import com.kotov.smartnotes.model.Category;
import com.kotov.smartnotes.model.Check;
import com.kotov.smartnotes.model.Images;
import com.kotov.smartnotes.model.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dmitriykotov333@gmail.com
 * @since 06.08.2020
 */
public class Action {

    private DatabaseHelper databaseHelper;

    public Action(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public void close(Cursor cursor) {
        if (databaseHelper != null) {
            // databaseHelper.close();
            Log.i("close", "close DatabaseHelper");
        }
        if (cursor != null) {
            // cursor.close();
            Log.i("close", "close Cursor");
        }
    }

    public Integer getNotesId(String update_date) {
        Integer rst = null;
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(String.format("select id from notes where update_date = '%s';", update_date),
                null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                rst = cursor.getInt(cursor.getColumnIndex("id"));
            }
        }
        close(cursor);
        return rst;
    }


    public Note getNote(String update_date) {
        Note inbox = null;
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(String.format("select * from notes where update_date = '%s';", update_date), null);
        if (cursor != null && cursor.moveToFirst()) {
            inbox = new Note(cursor.getString(cursor.getColumnIndex("title")), cursor.getString(cursor.getColumnIndex("description")),
                    cursor.getString(cursor.getColumnIndex("create_date")), cursor.getString(cursor.getColumnIndex("update_date")),
                    cursor.getInt(cursor.getColumnIndex("priority")), cursor.getString(cursor.getColumnIndex("password")),
                    cursor.getInt(cursor.getColumnIndex("fix_note")), cursor.getInt(cursor.getColumnIndex("category_id")));
        }
        close(cursor);
        return inbox;
    }

    public Category getCategory(String date) {
        Category category = null;
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(String.format("select ct.name from category as ct inner join notes as nt on" +
                " ct.id = nt.category_id where update_date = '%s';", date), null);
        if (cursor != null) {
            cursor.moveToFirst();
            category = new Category(cursor.getString(cursor.getColumnIndex("name")));
        }
        close(cursor);
        return category;
    }

    public Integer getCategoryId(String name) {
        Integer rst = null;
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(String.format("select id from category where name = '%s';", name), null);
        if (cursor != null) {
            cursor.moveToFirst();
            rst = cursor.getInt(cursor.getColumnIndex("id"));
        }
        close(cursor);
        return rst;
    }


    public void addCategory(String name) {
        if (name != null) {
            databaseHelper.getWritableDatabase().execSQL(String.format("insert into category (name) values('%s');", name));
            close(null);
        }
    }

    public void add(String key, Note note, List<Images> images, ImageAction imageAction, List<Check> checks, CheckAction checkAction, List<Audio> audios, AudioAction audioAction) {
        boolean newCategory = true;
        if (getAllNotes().isEmpty() && key.equals("All Notes")) {
            insert(note, key, images, checks, audios, imageAction, checkAction, audioAction);
            Log.i("insert", "A");
        } else if (getAllNotes().isEmpty()) {
            addCategory(key);
            insert(note, key, images, checks, audios, imageAction, checkAction, audioAction);
            Log.i("insert", "B");
        } else if (!getAllNotes().isEmpty()) {
            for (Category category : getCategory()) {
                if (category.getName().equals(key)) {
                    insert(note, key, images, checks, audios, imageAction, checkAction, audioAction);
                    newCategory = false;
                    Log.i("insert", "C");
                }
            }
            if (newCategory) {
                addCategory(key);
                insert(note, key, images, checks, audios, imageAction, checkAction, audioAction);
                Log.i("insert", "D");
            }
        }
    }

    private void insert(Note note, String key, List<Images> images, List<Check> checks, List<Audio> audios, ImageAction imageAction, CheckAction checkAction, AudioAction audioAction) {
        databaseHelper.getWritableDatabase().execSQL(String.format("insert into notes (title, description, create_date, update_date, priority, password, fix_note," +
                        " category_id) values('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');",
                note.getTitle(), note.getDescription(), note.getCreate_date(), note.getUpdate_date(), note.getPriority(),
                note.getPassword(), note.isFixNote(), getCategoryId(key)));
        if (images != null) {
            imageAction.addNotesImagesId(note.getUpdate_date(), images, this);
        }
        if (checkAction != null) {
            checkAction.addNotesChecksId(note.getUpdate_date(), checks, this);
        }
        if (audioAction != null) {
            audioAction.addNotesAudiosId(note.getUpdate_date(), audios, this);
        }
        close(null);
    }

    public void remove(String date) {
        if (date != null) {
            databaseHelper.getWritableDatabase().execSQL(String.format("delete from notes where update_date  = '%s';", date));
            close(null);
        }
    }

    public void replaceNote(String key, String date, Note note, List<Images> images, List<Check> checks, List<Audio> audios, ImageAction imageAction, CheckAction checkAction, AudioAction audioAction) {
        if (key != null && date != null && note != null) {
            databaseHelper.getWritableDatabase().execSQL(String.format("update notes set title = '%s', description = '%s', update_date = '%s', priority = '%s', password = '%s', fix_note = '%s', category_id = '%s' where update_date = '%s';",
                    note.getTitle(), note.getDescription(), note.getUpdate_date(), note.getPriority(), note.getPassword(), note.isFixNote(),
                    getCategoryId(key), date));
            if (images != null) {
                imageAction.addNotesImagesId(note.getUpdate_date(), images, this);
            }
            if (checkAction != null) {
                checkAction.addNotesChecksId(note.getUpdate_date(), checks, this);
            }
            if (audioAction != null) {
                audioAction.addNotesAudiosId(note.getUpdate_date(), audios, this);
            }
            close(null);
        }
    }

    public List<Note> getNotes(String key) {
        return checkFixed(key, -1);
    }

    public List<Note> getFixedNotes(String key) {
        return checkFixed(key, 1);
    }

    public List<Category> getCategory() {
        List<Category> list = new ArrayList<>();
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery("SELECT name FROM category;", null);
        if (cursor.moveToFirst()) {
            do {
                Category contact = new Category(cursor.getString(cursor.getColumnIndex("name")));
                list.add(contact);
            } while (cursor.moveToNext());
        }
        close(cursor);
        return list;
    }

    private List<Note> checkFixed(String key, Integer b) {
        List<Note> list = new ArrayList<>();
        String script = String.format("SELECT title, description, create_date, update_date, priority, password FROM notes where category_id = '%s'" +
                        " and fix_note = '%s' ORDER BY COALESCE(priority, priority), update_date desc;",
                getCategoryId(key), b);
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(script, null);
        if (cursor.moveToFirst()) {
            do {
                Note contact = new Note(cursor.getString(cursor.getColumnIndex("title")),
                        cursor.getString(cursor.getColumnIndex("description")),
                        cursor.getString(cursor.getColumnIndex("create_date")),
                        cursor.getString(cursor.getColumnIndex("update_date")),
                        cursor.getInt(cursor.getColumnIndex("priority")),
                        cursor.getString(cursor.getColumnIndex("password")));
                list.add(contact);
            } while (cursor.moveToNext());
        }
        close(cursor);
        return list;
    }

    public List<Note> getAllNotes() {
        List<Note> list = new ArrayList<>();
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery("SELECT category_id FROM notes;", null);
        if (cursor.moveToFirst()) {
            do {
                Note contact = new Note(cursor.getInt(cursor.getColumnIndex("category_id")));
                list.add(contact);
            } while (cursor.moveToNext());
        }
        close(cursor);
        return list;
    }
}
