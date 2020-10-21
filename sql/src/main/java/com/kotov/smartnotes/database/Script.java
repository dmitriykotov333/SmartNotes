package com.kotov.smartnotes.database;

public class Script {

    public static String CREATE_CATEGORY = "create table category (id INTEGER PRIMARY KEY AUTOINCREMENT not null, name text not null unique);";

    public static String ADD_CATEGORY = "insert into category (name) values('All Notes');";

    public static String CREATE_NOTES = "create table notes (id INTEGER PRIMARY KEY AUTOINCREMENT not null, title text, description text," +
            " create_date text, update_date text not null, priority integer, password text, fix_note integer check (fix_note IN (1, -1)) default(-1)," +
            " category_id integer not null references category(id));";

    public static String CREATE_IMAGES = "create table images (id INTEGER PRIMARY KEY AUTOINCREMENT not null, image text not null," +
            " create_date text not null, update_date text not null, notes_images_id integer not null references notes(id));";

    public static String CREATE_CHECKBOX = "create table checkbox (id INTEGER PRIMARY KEY AUTOINCREMENT not null, title text, " +
            "checking integer check (checking IN (1, -1)) default(-1), notes_checkbox_id integer not null references notes(id));";

    public static String CREATE_AUDIO = "create table audio (id INTEGER PRIMARY KEY AUTOINCREMENT not null, directory text not null, " +
            "notes_audio_id integer not null references notes(id));";

}
