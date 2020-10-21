package com.kotov.smartnotes.database;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.kotov.smartnotes.database.Action;
import com.kotov.smartnotes.model.Images;
import com.kotov.smartnotes.model.Note;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ActionTest  {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.kotov.smartnotes", appContext.getPackageName());
    }

    private DatabaseHelper action;

    @Before
    public void setUp() throws Exception {
        action = new DatabaseHelper(getApplicationContext());
    }

    @Test
    public void getParameters() {
       SQLiteDatabase db = action.getReadableDatabase();
        Cursor cursor = db.query("category", new String[]{"id", "names"}, null, null, null,
                null, null);
        // Узнаем индекс каждого столбца
        int idColumnIndex = cursor.getColumnIndex("id");
        int nameColumnIndex = cursor.getColumnIndex("names");
        int currentID = 0;
        while (cursor.moveToNext()) {
            currentID = cursor.getInt(idColumnIndex);
            String currentName = cursor.getString(nameColumnIndex);
        }


        // Метод 2: Сырой SQL-запрос
        String query = "select names from category";
        Cursor cursor2 = db.rawQuery(query, null);
        while (cursor2.moveToNext()) {
            currentID = cursor2.getInt(idColumnIndex);
            String currentName = cursor2.getString(nameColumnIndex);
        }
        cursor.close();
        cursor2.close();

        assertEquals(currentID, 0);
    }

    /**
     * Insert
     */
    private void insertGuest() {

        // Gets the database in write mode
        SQLiteDatabase db = action.getWritableDatabase();
        // Создаем объект ContentValues, где имена столбцов ключи,
        // а информация о госте является значениями ключей
        ContentValues values = new ContentValues();
        values.put("names", "All notes");

        long newRowId = db.insert("category", null, values);
        if (newRowId == -1) {
            // Если ID  -1, значит произошла ошибка
        } else {

        }
    }
    private void insertGuest2(String value) {
        // Gets the database in write mode
        SQLiteDatabase sqdb = action.getWritableDatabase();
        String insertQuery = String.format("insert into category (names) values('%s')",
                value);
        sqdb.execSQL(insertQuery);
    }
    @Test
    public void insertInto() {
        insertGuest();
        SQLiteDatabase db = action.getReadableDatabase();
        String currentName = null;
        String query = "select names from category";
        Cursor cursor2 = db.rawQuery(query, null);
        while (cursor2.moveToNext()) {
            currentName = cursor2.getString(cursor2
                    .getColumnIndex("names"));
        }
        cursor2.close();
        assertEquals(currentName, "All notes");
    }
    @Test
    public void insertIntoMethod2() {
        insertGuest2("ASA");
       // update();
        SQLiteDatabase db = action.getReadableDatabase();
        String currentName = null;
        String query = "select names from category";
        Cursor cursor2 = db.rawQuery(query, null);
        while (cursor2.moveToNext()) {
            currentName = cursor2.getString(cursor2
                    .getColumnIndex("names"));
        }
        cursor2.close();
        assertEquals(currentName, "ASA");
    }
    private void update(String value, String update) {
        // Gets the database in write mode
        SQLiteDatabase sqdb = action.getWritableDatabase();
        String insertQuery = String.format("insert into category (names) values('%s')",
                value);
        sqdb.execSQL(insertQuery);
        // Gets the database in write mode
        String updateQuery = String.format("update category set names = '%s' where names  = 'Test'",
                update);;
        sqdb.execSQL(updateQuery);
    }
    @Test
    public void insertAndUpdate() {
        update("Test", "Log");
        SQLiteDatabase db = action.getReadableDatabase();
        String currentName = null;
        String query = "select names from category";
        Cursor cursor2 = db.rawQuery(query, null);
        while (cursor2.moveToNext()) {
            currentName = cursor2.getString(cursor2
                    .getColumnIndex("names"));
        }
        cursor2.close();
        assertEquals(currentName, "Log");
    }
    private void del(String value, String update) {
        // Gets the database in write mode
        SQLiteDatabase sqdb = action.getWritableDatabase();
        String insertQuery = String.format("insert into category (names) values('%s')",
                value);
        sqdb.execSQL(insertQuery);
        // Gets the database in write mode
        String updateQuery = String.format("delete from category where names = '%s'",
                update);
        sqdb.execSQL(updateQuery);
    }
    @Test
    public void delete() {
        del("Test", "Test");
        SQLiteDatabase db = action.getReadableDatabase();
        String currentName = null;
        String query = "select names from category";
        Cursor cursor2 = db.rawQuery(query, null);
        while (cursor2.moveToNext()) {
            currentName = cursor2.getString(cursor2
                    .getColumnIndex("names"));
        }
        cursor2.close();
        assertNull(currentName);
    }

    @Test
    public void referencesTable() {
        Note inbox = new Note("test", "testing", "2020-10-17 17:57:32", "2020-10-17 17:57:32",
                0, null ,0, 1);
        SQLiteDatabase sqdb = action.getWritableDatabase();
        String insertQuery = String.format("insert into notes (title, description, create_date, update_date, priority, password, fix_note, category_id) " +
                        "values('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')",
                inbox.getTitle(), inbox.getDescription(), inbox.getCreate_date(), inbox.getUpdate_date(), inbox.getPriority(), inbox.getPassword(),
                inbox.isFixNote(), inbox.getCategory_id());
        sqdb.execSQL(insertQuery);
        assertEquals(getNote("2020-10-17 17:57:32").getTitle(), inbox.getTitle());
    }

    /**
     * get all values from note(Inbox class)
     * @param update_date
     * @return
     */
    Note getNote(String update_date) {
        SQLiteDatabase db = action.getReadableDatabase();

        String query = String.format("select * from notes where update_date = '%s'", update_date);
        Cursor cursor = db.rawQuery(query, null);
        Note inbox = null;
        if (cursor != null) {
            cursor.moveToFirst();

            inbox = new Note(cursor.getString(1), cursor.getString(2), cursor.getString(3),
                    cursor.getString(4), cursor.getInt(5), cursor.getString(6),
                    cursor.getInt(7), cursor.getInt(8));
        }
        return inbox;
    }
    Integer getNotesId(String update_date) {
        SQLiteDatabase db = action.getReadableDatabase();
        Integer rst = null;
        String query = String.format("select id from notes where update_date = %s", update_date);
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                rst = cursor.getInt(cursor.getColumnIndex("id"));
            }
        }
        return rst;
    }
    @Test
    public void updateTable() {
        Note inbox = new Note("test", "testing", "2021", "2021",
                0, null ,0, 1);
        SQLiteDatabase sqdb = action.getWritableDatabase();
        String insertInbox = String.format("insert into notes (title, description, create_date, update_date, priority, password, fix_note, category_id) " +
                        "values('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')",
                inbox.getTitle(), inbox.getDescription(), inbox.getCreate_date(), inbox.getUpdate_date(), inbox.getPriority(), inbox.getPassword(),
                inbox.isFixNote(), inbox.getCategory_id());
        sqdb.execSQL(insertInbox);
        Integer date = getNotesId("2021");
        Images images1 = new Images("1111", "2020-10-10", "2020-10-10",
                date);
        Images images2 = new Images("2222", "2020-10-10", "2020-10-10",
                date);
        Images images3 = new Images("3333", "2020-10-10", "2020-10-10",
                date);


        String insertImages1 = String.format("insert into images (image, create_date, update_date, notes_images_id) " +
                "values('%s', '%s', '%s', '%s')", images1.getImage(), images1.getCreate_date(), images1.getUpdate_date(), images1.getNotes_images_id());
        sqdb.execSQL(insertImages1);
        String insertImages2 = String.format("insert into images (image, create_date, update_date, notes_images_id) " +
                "values('%s', '%s', '%s', '%s')", images2.getImage(), images2.getCreate_date(), images2.getUpdate_date(), images2.getNotes_images_id());
        sqdb.execSQL(insertImages2);
        String insertImages3 = String.format("insert into images (image, create_date, update_date, notes_images_id) " +
                "values('%s', '%s', '%s', '%s')", images3.getImage(), images3.getCreate_date(), images3.getUpdate_date(), images3.getNotes_images_id());
        sqdb.execSQL(insertImages3);

        int rst = 3;
        assertEquals(Integer.toString(getAllImagesNotesId(date).size()), Integer.toString(rst));

    }

    public List<Images> getAllImagesNotesId(Integer id) {
        List<Images> contactList = new ArrayList<Images>();

        String selectQuery = String.format("SELECT * FROM images where notes_images_id = %s", id);

        SQLiteDatabase db = action.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Images contact = new Images(cursor
                        .getString(cursor.getColumnIndex("image")), cursor.getString(cursor.getColumnIndex("create_date")),
                        cursor.getString(cursor.getColumnIndex("update_date")),
                        cursor.getInt(cursor.getColumnIndex("notes_images_id")));

                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        return contactList;
    }
}
