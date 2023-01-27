package com.example.notisticapp;

public class NoteModel {

    String title;
    String description;
    String ID;

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    public NoteModel(String title, String description, String ID) {
        this.title = title;
        this.description = description;
        this.ID = ID;
    }

    public NoteModel(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public NoteModel() {
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
