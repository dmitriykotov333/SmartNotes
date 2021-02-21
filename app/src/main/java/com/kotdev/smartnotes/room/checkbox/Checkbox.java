package com.kotdev.smartnotes.room.checkbox;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.kotdev.smartnotes.room.note.Note;

import static androidx.room.ColumnInfo.BLOB;
import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "checkbox", foreignKeys = @ForeignKey(entity = Note.class, parentColumns = "_id", childColumns = "notes_checkboxes_id", onDelete = CASCADE))
public class Checkbox {

    @PrimaryKey
    @ColumnInfo(name = "_id")
    public long id;

    @ColumnInfo(name = "title_check")
    public String title;

    @ColumnInfo(name = "checkbox")
    public Integer check;

    @ColumnInfo(name = "create_date")
    public String create_date;

    @ColumnInfo(name = "update_date")
    public String update_date;

    @ColumnInfo(name = "notes_checkboxes_id")
    public Long notes_checkboxes_id;

}
