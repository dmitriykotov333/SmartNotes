package com.kotov.smartnotes.database;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;

import com.kotov.smartnotes.activity.editor.Notes;
import com.kotov.smartnotes.adapter.AdapterList;
import com.kotov.smartnotes.model.Category;
import com.kotov.smartnotes.model.Check;
import com.kotov.smartnotes.model.Images;
import com.kotov.smartnotes.model.Note;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.TreeMap;


/**
 * @author dmitriykotov333@gmail.com
 * @since 06.08.2020
 */
public class Action implements AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(Action.class);

    private DatabaseHelper databaseHelper;

    private SQLiteDatabase sqLiteDatabase;

    private Cursor cursor;

    public Action(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }


    @Override
    public void close() {
        if (databaseHelper != null) {
            Log.i("ACTION CLOSE", " - databaseHelper CLOSE");
            databaseHelper.close();
        }
        if (cursor != null) {
            Log.i("ACTION CLOSE", " - cursor CLOSE");
            cursor.close();
        }
        if (sqLiteDatabase != null) {
            Log.i("ACTION CLOSE", " - sqLiteDatabase CLOSE");
            sqLiteDatabase.close();
        }
    }

    public Integer getNotesId(String update_date) {
        Integer rst = null;
        sqLiteDatabase = databaseHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery(String.format("select id from notes where update_date = '%s';", update_date),
                null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                rst = cursor.getInt(cursor.getColumnIndex("id"));
            }
        }
        return rst;
    }

    public List<Images> getAllImagesNotesId(Integer id) {
        List<Images> list = new ArrayList<Images>();
        sqLiteDatabase = databaseHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery(String.format("SELECT * FROM images where notes_images_id = '%s';", id), null);
        if (cursor.moveToFirst()) {
            do {
                Images contact = new Images(cursor
                        .getString(cursor.getColumnIndex("image")), cursor.getString(cursor.getColumnIndex("create_date")),
                        cursor.getString(cursor.getColumnIndex("update_date")),
                        cursor.getInt(cursor.getColumnIndex("notes_images_id")));
                list.add(contact);
            } while (cursor.moveToNext());
        }
        return list;
    }

    public void addImage(Images images) {
        if (images != null) {
            sqLiteDatabase = databaseHelper.getWritableDatabase();
            sqLiteDatabase.execSQL(String.format("insert into images (image, create_date, update_date, notes_images_id) " +
                    "values('%s', '%s', '%s', '%s');", images.getImage(), images.getCreate_date(), images.getUpdate_date(), images.getNotes_images_id()));
        }
    }
    public static String CREATE_NOTES = "create table notes (id INTEGER PRIMARY KEY AUTOINCREMENT not null, title text, description text," +
            " create_date text, update_date text not null, priority integer, password text, fix_note integer check (fix_note IN (1, -1)) default(-1)," +
            " category_id integer not null references category(id));";
    public Note getNote(String update_date) {
        Note inbox = null;
        sqLiteDatabase = databaseHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery(String.format("select * from notes where update_date = '%s';", update_date), null);
        if (cursor != null && cursor.moveToFirst()) {
            inbox = new Note(cursor.getString(cursor.getColumnIndex("title")), cursor.getString(cursor.getColumnIndex("description")),
                    cursor.getString(cursor.getColumnIndex("create_date")), cursor.getString(cursor.getColumnIndex("update_date")),
                    cursor.getInt(cursor.getColumnIndex("priority")), cursor.getString(cursor.getColumnIndex("password")),
                    cursor.getInt(cursor.getColumnIndex("fix_note")), cursor.getInt(cursor.getColumnIndex("category_id")));
        }
        return inbox;
    }

    public Category getCategory(String date) {
        Category category = null;
        sqLiteDatabase = databaseHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery(String.format("select ct.name from category as ct inner join notes as nt on" +
                " ct.id = nt.category_id where update_date = '%s';", date), null);
        if (cursor != null) {
            cursor.moveToFirst();
            category = new Category(cursor.getString(cursor.getColumnIndex("name")));
        }
        return category;
    }

    public Integer getCategoryId(String name) {
        Integer rst = null;
        sqLiteDatabase = databaseHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery(String.format("select id from category where name = '%s';", name), null);
        if (cursor != null) {
            cursor.moveToFirst();
            rst = cursor.getInt(cursor.getColumnIndex("id"));
        }
        return rst;
    }

    public void addCheckbox(Check check) {
        if (check != null) {
            sqLiteDatabase = databaseHelper.getWritableDatabase();
            sqLiteDatabase.execSQL(String.format("insert into checkbox (title, checking, notes_checkbox_id) " +
                    "values('%s', '%s', '%s');", check.getTitle(), check.isCheck(), check.getNotes_checkbox_id()));
        }
    }

    public void addCategory(String name) {
        if (name != null) {
            sqLiteDatabase = databaseHelper.getWritableDatabase();
            sqLiteDatabase.execSQL(String.format("insert into category (name) values('%s');", name));
        }
    }

    public void add(String key, Note note) {
        /*if (note != null && key.equals("All Notes")) {
            sqLiteDatabase = databaseHelper.getWritableDatabase();
            sqLiteDatabase.execSQL(String.format("insert into notes (title, description, create_date, update_date, priority, password, fix_note," +
                            " category_id) values('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');",
                    note.getTitle(), note.getDescription(), note.getCreate_date(), note.getUpdate_date(), note.getPriority(),
                    note.getPassword(), note.isFixNote(), getCategoryId(key)));
        } else if (note != null){
            addCategory(key);
            sqLiteDatabase = databaseHelper.getWritableDatabase();
            sqLiteDatabase.execSQL(String.format("insert into notes (title, description, create_date, update_date, priority, password, fix_note," +
                            " category_id) values('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');",
                    note.getTitle(), note.getDescription(), note.getCreate_date(), note.getUpdate_date(), note.getPriority(),
                    note.getPassword(), note.isFixNote(), getCategoryId(key)));
        }*/
        boolean newCategory = true;
        if (getAllNotes().isEmpty() && key.equals("All Notes")) {
            insert(note, key);
        } else if (!getAllNotes().isEmpty()) {
            for (Category category : getCategory()) {
                if (category.getName().equals(key)) {
                    insert(note, key);
                    newCategory = false;
                    break;
                }
            }
            if (newCategory) {
                addCategory(key);
                insert(note, key);
            }
        }
        /*RealmResults<MapNote> newExperiments = mRealm.where(MapNote.class).equalTo("key", key).findAll();
        try {
            mRealm.executeTransaction(realm -> {
                        if (newExperiments.size() != 0) {
                            for (MapNote newExperiment : newExperiments) {
                                if (newExperiment.getKey().equals(key)) {
                                    newExperiment.getNotes().add(map);//.iterator().next().setId(generateId());
                                    realm.insertOrUpdate(newExperiment);
                                    break;
                                }
                            }
                        } else {
                            experiment.setKey(key);
                            experiment.setNotes((new RealmList<>(map)));
                            realm.insertOrUpdate(experiment);
                        }
                        realm.insertOrUpdate(map);
                    }
            );
        } catch (RealmException e) {
            LOG.error(e.getMessage(), e);
        }*/
    }

    private void insert(Note note, String key) {
        sqLiteDatabase = databaseHelper.getWritableDatabase();
        sqLiteDatabase.execSQL(String.format("insert into notes (title, description, create_date, update_date, priority, password, fix_note," +
                        " category_id) values('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s');",
                note.getTitle(), note.getDescription(), note.getCreate_date(), note.getUpdate_date(), note.getPriority(),
                note.getPassword(), note.isFixNote(), getCategoryId(key)));
    }

    public void remove(String date) {
       /* mRealm.executeTransaction(realm1 -> {
            realm1.where(Inbox.class).equalTo("create_date", id).findAll().deleteAllFromRealm();
        });*/
        if (date != null) {
            sqLiteDatabase = databaseHelper.getWritableDatabase();
            sqLiteDatabase.execSQL(String.format("delete from notes where update_date  = '%s';", date));
        }
    }

    public void replaceNote(String key, String date, Note note) {
        if (key != null && date != null && note != null) {
            sqLiteDatabase = databaseHelper.getWritableDatabase();
            sqLiteDatabase.execSQL(String.format("update notes set title = '%s', description = '%s', update_date = '%s', priority = '%s', password = '%s', fix_note = '%s', category_id = '%s' where update_date = '%s';",
                    note.getTitle(), note.getDescription(), note.getUpdate_date(), note.getPriority(), note.getPassword(), note.isFixNote(),
                    getCategoryId(key), date));
        }
       /* RealmResults<MapNote> categories = mRealm.where(MapNote.class).equalTo("key", key).findAll();
        try {
            mRealm.executeTransaction(realm -> {
                for (MapNote m : categories) {
                    if (m.getKey().equals(key)) {
                        for (Inbox newExperiment : m.getNotes()) {
                            if (newExperiment.getCreate_date().equals(id)) {
                                newExperiment.setTitle(inbox.getTitle());
                                newExperiment.setDescription(inbox.getDescription());
                                newExperiment.setUpdate_date(inbox.getUpdate_date());
                                newExperiment.setPriority(inbox.getPriority());
                                newExperiment.setPassword(inbox.getPassword());
                                newExperiment.setFixNote(inbox.isFixNote());
                                realm.insertOrUpdate(newExperiment);
                                break;
                            }
                        }
                    }
                }
            });
        } catch (RealmException e) {
            LOG.error(e.getMessage(), e);
        }*/
    }

    public List<Note> getNotes(String key) {
        return checkFixed(key, -1);
    }

    public List<Note> getFixedNotes(String key) {
        return checkFixed(key, 1);
    }

    public List<Category> getCategory() {
        List<Category> list = new ArrayList<Category>();
        sqLiteDatabase = databaseHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("SELECT name FROM category;", null);
        if (cursor.moveToFirst()) {
            do {
                Category contact = new Category(cursor.getString(cursor.getColumnIndex("name")));
                list.add(contact);
            } while (cursor.moveToNext());
        }
        return list;
    }

    private List<Note> checkFixed(String key, Integer b) {
        List<Note> list = new ArrayList<Note>();
        sqLiteDatabase = databaseHelper.getReadableDatabase();
        String script = String.format("SELECT title, description, create_date, update_date, priority, password FROM notes where category_id = '%s'" +
                        " and fix_note = '%s' ORDER BY COALESCE(priority, priority), update_date desc;",
                getCategoryId(key), b);
        cursor = sqLiteDatabase.rawQuery(script, null);
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
        return list;
    }

    public List<Note> getAllNotes() {
        List<Note> list = new ArrayList<Note>();
        sqLiteDatabase = databaseHelper.getReadableDatabase();
        cursor = sqLiteDatabase.rawQuery("SELECT category_id FROM notes;", null);
        if (cursor.moveToFirst()) {
            do {
                Note contact = new Note(cursor.getInt(cursor.getColumnIndex("category_id")));
                list.add(contact);
            } while (cursor.moveToNext());
        }
        return list;
    }
}
