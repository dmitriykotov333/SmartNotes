package com.kotdev.smartnotes.room;

import android.content.Context;

import androidx.room.Room;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Database {

    public static final String DATABASE_NAME = "notes_db";

    private final AppDatabase database;

    @Inject
    public Database(Context context) {
        database = Room
                .databaseBuilder(context, AppDatabase.class, DATABASE_NAME)
                .allowMainThreadQueries()
                .build();
    }

    public AppDatabase getDatabase() {
        return database;
    }

}
