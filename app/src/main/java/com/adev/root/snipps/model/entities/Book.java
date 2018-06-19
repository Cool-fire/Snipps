package com.adev.root.snipps.model.entities;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Book extends RealmObject {

    @PrimaryKey
    private long id;

    private String BookTitle;

    private  String BookAuthor;

    private Date creationDate;

    private RealmList<Snippet> snippetsList;

    public Book(long id, String bookTitle, String bookAuthor, Date creationDate) {
        this.id = id;
        BookTitle = bookTitle;
        BookAuthor = bookAuthor;
        this.creationDate = creationDate;
    }

    public RealmList<Snippet> getSnippetsList() {
        return snippetsList;
    }

    public void setSnippetsList(RealmList<Snippet> snippetsList) {
        this.snippetsList = snippetsList;
    }

    public Book()
    {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBookTitle() {
        return BookTitle;
    }

    public void setBookTitle(String bookTitle) {
        BookTitle = bookTitle;
    }

    public String getBookAuthor() {
        return BookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        BookAuthor = bookAuthor;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
