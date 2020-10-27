package com.kotov.smartnotes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import com.kotov.smartnotes.model.Images;
import java.util.ArrayList;
import java.util.List;

public class ImageAction {

    private DatabaseHelper databaseHelper;

    public ImageAction(Context context) {
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

    public List<Images> getAllImagesNotesId(String date, Action action) {
        List<Images> list = new ArrayList<>();
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(String.format("SELECT image, create_date, update_date, notes_images_id FROM images where notes_images_id = '%s' " +
                "ORDER BY update_date desc;", action.getNotesId(date)), null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Images contact = new Images(cursor.getBlob(cursor.getColumnIndex("image")),
                        cursor.getString(cursor.getColumnIndex("create_date")),
                        cursor.getString(cursor.getColumnIndex("update_date")),
                        cursor.getInt(cursor.getColumnIndex("notes_images_id")));
                list.add(contact);
            } while (cursor.moveToNext());
        }
        close(cursor);
        return list;
    }

    public List<Images> getAllImages() {
        List<Images> list = new ArrayList<>();
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(("SELECT image, create_date, update_date, notes_images_id FROM images where notes_images_id is null;"), null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Images contact = new Images(cursor.getBlob(cursor.getColumnIndex("image")),
                        cursor.getString(cursor.getColumnIndex("create_date")),
                        cursor.getString(cursor.getColumnIndex("update_date")),
                        cursor.getInt(cursor.getColumnIndex("notes_images_id")));
                list.add(contact);
            } while (cursor.moveToNext());
        }
        close(cursor);
        return list;
    }

    public Images getImg(String date) {
        Images images = null;
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(String.format("select * from images where update_date = '%s';", date), null);
        if (cursor != null && cursor.moveToFirst()) {
            images = new Images(cursor.getBlob(cursor.getColumnIndex("image")),
                    cursor.getString(cursor.getColumnIndex("create_date")),
                    cursor.getString(cursor.getColumnIndex("update_date")),
                    cursor.getInt(cursor.getColumnIndex("notes_images_id")));
        }
        close(cursor);
        return images;
    }

    public void addOneImage(Images image, String date, Action action) {
        if (image != null) {
            {
                ContentValues values = new ContentValues();
                values.put("image", image.getImage());
                values.put("create_date", image.getCreate_date());
                values.put("update_date", image.getUpdate_date());
                values.put("notes_images_id", action.getNotesId(date));
                databaseHelper.getWritableDatabase().insert("images", null, values);
            }
            close(null);
        }
    }

    public void addImage(List<Images> images) {
        if (images != null) {
            for (Images image : images) {
                ContentValues values = new ContentValues();
                values.put("image", image.getImage());
                values.put("create_date", image.getCreate_date());
                values.put("update_date", image.getUpdate_date());
                databaseHelper.getWritableDatabase().insert("images", null, values);
            }
            close(null);
        }
    }

    public void addNotesImagesId(String date, List<Images> images, Action action) {
        if (date != null) {
            for (Images image : images) {
                databaseHelper.getWritableDatabase().execSQL(String.format("update images set notes_images_id = '%s' where update_date  = '%s';", action.getNotesId(date), image.getUpdate_date()));
            }
           // close(null);
        }
    }

    public void deleteImage(String date) {
        if (date != null) {
            databaseHelper.getWritableDatabase().execSQL(String.format("delete from images where update_date  = '%s';", date));
            close(null);
        }
    }

    public void deleteImageNullNotesId() {
        databaseHelper.getWritableDatabase().execSQL("delete from images where notes_images_id  is null;");
        close(null);
    }
}
