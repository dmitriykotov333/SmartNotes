package com.kotdev.smartnotes.room.image;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.kotdev.smartnotes.room.category.Category;
import com.kotdev.smartnotes.room.note.Note;

import static androidx.room.ColumnInfo.BLOB;
import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "images", foreignKeys = @ForeignKey(entity = Note.class, parentColumns = "_id", childColumns = "notes_images_id", onDelete = CASCADE))
public class Image implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "_id")
    public long id;

    @ColumnInfo(typeAffinity = BLOB, name = "image")
    public byte[] image;

    @ColumnInfo(name = "create_date")
    public String create_date;

    @ColumnInfo(name = "update_date")
    public String update_date;

    @ColumnInfo(name = "notes_images_id")
    public Long notes_images_id;


    public Image() {

    }
    public Image(Parcel in) {
        in.readByteArray(image);
        create_date = in.readString();
        update_date = in.readString();
        notes_images_id = in.readLong();
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
              parcel.writeByteArray(image);
        parcel.writeString(create_date);
        parcel.writeString(update_date);
        parcel.writeLong(notes_images_id);
    }
}
