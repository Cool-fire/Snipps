package com.adev.root.snipps.model.entities;

import java.util.Date;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToMany;

@Entity
public class BookEntity {


    @Id
    private long id;

    private String BookTitle;

    private  String BookAuthor;

    private Date creationDate;

    @Backlink
    ToMany<SnippetEntity> snippets;

    public BookEntity(long id, String bookTitle, String bookAuthor, Date creationDate) {
        this.id = id;
        BookTitle = bookTitle;
        BookAuthor = bookAuthor;
        this.creationDate = creationDate;
    }

    public BookEntity() {

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
