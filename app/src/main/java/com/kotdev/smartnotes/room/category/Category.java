package com.kotdev.smartnotes.room.category;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.kotdev.smartnotes.room.note.Note;

@Entity(tableName = "categories")
public class Category implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "category")
    public String title;

    public Category() {

    }


    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }

    public Category(Parcel in) {
        id = in.readLong();
        title = in.readString();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeLong(id);
    }

}
