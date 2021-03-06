package com.adev.root.snipps.model.entities;

import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.LinkingObjects;
import io.realm.annotations.PrimaryKey;

public class Snippet extends RealmObject {

    private String date;
    @PrimaryKey
    private long id;

    private String snippetName;

    private long snippetPageNo;

    private String ImagePath;

    private String notes;


    @LinkingObjects("snippetsList")
    private final RealmResults<Book> Book = null;



    public Snippet(long id, String snippetName, long snippetPageNo, String imagePath, String date) {
        this.id = id;
        this.snippetName = snippetName;
        this.snippetPageNo = snippetPageNo;
        this.ImagePath = imagePath;
        this.date = date;
    }

    public Snippet()
    {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSnippetName() {
        return snippetName;
    }

    public void setSnippetName(String snippetName) {
        this.snippetName = snippetName;
    }

    public long getSnippetPageNo() {
        return snippetPageNo;
    }

    public void setSnippetPageNo(long snippetPageNo) {
        this.snippetPageNo = snippetPageNo;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
