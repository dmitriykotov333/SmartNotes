package com.kotdev.smartnotes.room.note;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import com.kotdev.smartnotes.room.category.Category;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = "notes", foreignKeys = @ForeignKey(entity = Category.class, parentColumns = "id", childColumns = "category_id", onDelete = CASCADE))
public class Note implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "_id")
    public long id;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "content")
    public String content;

    @ColumnInfo(name = "create_date")
    public String create_date;

    @ColumnInfo(name = "update_date")
    public String update_date;

    @ColumnInfo(name = "priority_note")
    public int priority;

    @ColumnInfo(name = "password")
    public String password;


    @ColumnInfo(name = "fix_note")
    public Integer fixNote;

    @ColumnInfo(name = "category_id")
    public long categoryId;

    public Note() {

    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", create_date='" + create_date + '\'' +
                ", update_date='" + update_date + '\'' +
                ", priority=" + priority +
                ", password='" + password + '\'' +
                ", fixNote=" + fixNote +
                ", categoryId=" + categoryId +
                '}';
    }

    public Note(Parcel in) {
        title = in.readString();
        content = in.readString();
        create_date = in.readString();
        update_date = in.readString();
        priority = in.readInt();
        password = in.readString();
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
        parcel.writeString(content);
        parcel.writeString(create_date);
        parcel.writeString(update_date);
        parcel.writeInt(priority);
        parcel.writeString(password);
    }

}
