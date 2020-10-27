package com.kotov.smartnotes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.kotov.smartnotes.database.Action;
import com.kotov.smartnotes.database.DatabaseHelper;
import com.kotov.smartnotes.model.Audio;
import com.kotov.smartnotes.model.Check;

import java.util.ArrayList;
import java.util.List;

public class AudioAction {

    private DatabaseHelper databaseHelper;

    public AudioAction(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public void close(Cursor cursor) {
        if (databaseHelper != null) {
            //    databaseHelper.close();
            Log.i("close", "close DatabaseHelper");
        }
        if (cursor != null) {
            // cursor.close();
            Log.i("close", "close Cursor");
        }
    }


    public List<Audio> getAllAudioNotesId(String date, Action action) {
        List<Audio> list = new ArrayList<>();
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(String.format("SELECT directory, create_date, notes_audio_id FROM audio where notes_audio_id = '%s';",
                action.getNotesId(date)), null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Audio audio = new Audio(cursor.getString(cursor.getColumnIndex("directory")),
                        cursor.getString(cursor.getColumnIndex("create_date")),
                        cursor.getInt(cursor.getColumnIndex("notes_audio_id")));
                list.add(audio);
            } while (cursor.moveToNext());
        }
        close(cursor);
        return list;
    }

    public List<Audio> getAllAudios() {
        List<Audio> list = new ArrayList<>();
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(("SELECT directory, create_date, notes_audio_id FROM audio where notes_audio_id is null;"), null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Audio audio = new Audio(cursor.getString(cursor.getColumnIndex("directory")),
                        cursor.getString(cursor.getColumnIndex("create_date")),
                        cursor.getInt(cursor.getColumnIndex("notes_audio_id")));
                list.add(audio);
            } while (cursor.moveToNext());
        }
        close(cursor);
        return list;
    }

    public void addOneAudio(List<Audio> audios, String date, Action action) {
        for (Audio audio : audios) {
            ContentValues values = new ContentValues();
            values.put("directory", audio.getDirectory());
            values.put("create_date", audio.getCreate_date());
            values.put("notes_audio_id", action.getNotesId(date));
            databaseHelper.getWritableDatabase().insert("audio", null, values);
        }
        close(null);
    }
    public void addNotesAudiosId(String date, List<Audio> audios, Action action) {
        if (date != null) {
            for (Audio audio : audios) {
                databaseHelper.getWritableDatabase().execSQL(String.format("update audio set notes_audio_id = '%s' where create_date  = '%s';", action.getNotesId(date), audio.getCreate_date()));
            }
            close(null);
        }
    }


    public void addAudio(Audio audio) {
        if (audio != null) {
            ContentValues values = new ContentValues();
            values.put("directory", audio.getDirectory());
            values.put("create_date", audio.getCreate_date());
            databaseHelper.getWritableDatabase().insert("audio", null, values);
            close(null);
        }
    }


    public void deleteAudio(String date) {
        if (date != null) {
            databaseHelper.getWritableDatabase().execSQL(String.format("delete from audio where create_date  = '%s';", date));
            close(null);
        }
    }

    public void deleteAudioNullNotesId() {
        databaseHelper.getWritableDatabase().execSQL("delete from audio where notes_audio_id is null;");
        close(null);
    }

}
