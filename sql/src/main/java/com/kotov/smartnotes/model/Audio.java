package com.kotov.smartnotes.model;

public class Audio {

    private Integer id;
    private String directory;
    private Integer notes_audio_id;

    public Audio(Integer id, String directory, Integer notes_audio_id) {
        this.id = id;
        this.directory = directory;
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
}
