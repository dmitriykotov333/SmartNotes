package com.kotov.smartnotes.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.kotov.smartnotes.model.Check;
import com.kotov.smartnotes.model.Images;

import java.util.ArrayList;
import java.util.List;

public class CheckAction {

    private DatabaseHelper databaseHelper;

    public CheckAction(Context context) {
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

    /* public void addCheckbox(Check check) {
         if (check != null) {
             databaseHelper.getWritableDatabase().execSQL(String.format("insert into checkbox (title, checking) " +
                     "values('%s', '%s');", check.getTitle(), check.isCheck()));
             close(null);
         }
     }*/

    public List<Check> getAllCheckNotesId(String date, Action action) {
        List<Check> list = new ArrayList<>();
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(String.format("SELECT title, checking, create_date, update_date, notes_checkbox_id FROM checkbox where notes_checkbox_id = '%s' ;",
                action.getNotesId(date)), null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Check contact = new Check(cursor.getString(cursor.getColumnIndex("title")),
                        cursor.getInt(cursor.getColumnIndex("checking")),
                        cursor.getString(cursor.getColumnIndex("create_date")),
                        cursor.getString(cursor.getColumnIndex("update_date")),
                        cursor.getInt(cursor.getColumnIndex("notes_checkbox_id")));
                list.add(contact);
            } while (cursor.moveToNext());
        }
        close(cursor);
        return list;
    }

    public List<Check> getAllChecks() {
        List<Check> list = new ArrayList<>();
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(("SELECT title, checking, create_date, update_date, notes_checkbox_id FROM checkbox where notes_checkbox_id is null;"), null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Check contact = new Check(cursor.getString(cursor.getColumnIndex("title")),
                        cursor.getInt(cursor.getColumnIndex("checking")),
                        cursor.getString(cursor.getColumnIndex("create_date")),
                        cursor.getString(cursor.getColumnIndex("update_date")),
                        cursor.getInt(cursor.getColumnIndex("notes_checkbox_id")));
                list.add(contact);
            } while (cursor.moveToNext());
        }
        close(cursor);
        return list;
    }

    public void addOneCheck(List<Check> checks, String date, Action action) {
                for (Check check : checks) {
                    ContentValues values = new ContentValues();
                    values.put("title", check.getTitle());
                    values.put("checking", check.isCheck());
                    values.put("create_date", check.getCreate_date());
                    values.put("update_date", check.getUpdate_date());
                    values.put("notes_checkbox_id", action.getNotesId(date));
                    databaseHelper.getWritableDatabase().insert("checkbox", null, values);
                }
            close(null);
        }

    public void addCheck(List<Check> checks) {
        if (checks != null) {
            for (Check check : checks) {
                ContentValues values = new ContentValues();
                values.put("title", check.getTitle());
                values.put("checking", check.isCheck());
                values.put("create_date", check.getCreate_date());
                values.put("update_date", check.getUpdate_date());
                databaseHelper.getWritableDatabase().insert("checkbox", null, values);
            }
            close(null);
        }
    }

    public void addNotesChecksId(String date, List<Check> checks, Action action) {
        if (date != null) {
            for (Check check : checks) {
                databaseHelper.getWritableDatabase().execSQL(String.format("update checkbox set notes_checkbox_id = '%s' where update_date  = '%s';", action.getNotesId(date), check.getUpdate_date()));
                //databaseHelper.getWritableDatabase().execSQL(String.format("update checkbox set notes_checkbox_id = '%s', title = '%s', checking = '%s' where update_date  = '%s';",
                  //      action.getNotesId(date), check.getTitle(), check.isCheck(), check.getUpdate_date()));
            }
            close(null);
        }
    }

    public void updateCheck(int checking, List<Check> check, int i) {
        if (check != null) {
               databaseHelper.getWritableDatabase().execSQL(String.format("update checkbox set checking = '%s' where update_date  = '%s';",
                       checking, check.get(i).getUpdate_date()));
            close(null);
        }
    }


    public void updateCheckTitle(String title, List<Check> check, int i) {
        if (check != null) {
            databaseHelper.getWritableDatabase().execSQL(String.format("update checkbox set title = '%s' where update_date  = '%s';",
                    title, check.get(i).getUpdate_date()));
            close(null);
        }
    }
    public void deleteCheck(String date) {
        if (date != null) {
            databaseHelper.getWritableDatabase().execSQL(String.format("delete from checkbox where update_date  = '%s';", date));
            close(null);
        }
    }
    public void deleteCheckNotesId(List<Check> checks) {
        for (Check check : checks) {
            databaseHelper.getWritableDatabase().execSQL(String.format("delete from checkbox where notes_checkbox_id  = '%s';", check.getNotes_checkbox_id()));
            close(null);
        }
    }
    public void deleteCheckNullNotesId() {
        databaseHelper.getWritableDatabase().execSQL("delete from checkbox where notes_checkbox_id  is null;");
        close(null);
    }

}
