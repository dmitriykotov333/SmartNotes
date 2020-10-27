package com.kotov.smartnotes.model;

public class Audio {

    private Integer id;
    private String directory;
    private String create_date;
    private Integer notes_audio_id;

    public Audio(String directory, String create_date) {
        this.directory = directory;
        this.create_date = create_date;
    }

    public Audio(String directory, String create_date, Integer notes_audio_id) {
        this.directory = directory;
        this.create_date = create_date;
        this.notes_audio_id = notes_audio_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public Integer getNotes_audio_id() {
        return notes_audio_id;
    }

    public void setNotes_audio_id(Integer notes_audio_id) {
        this.notes_audio_id = notes_audio_id;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }
}
