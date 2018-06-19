package com.adev.root.snipps.model.entities;


import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.relation.ToOne;

@Entity
public class SnippetEntity {

    @Id
    private long id;

    private String snippetName;

    private long snippetPageNo;

    private String ImagePath;

    ToOne<BookEntity> Book;

    public SnippetEntity(long id, String snippetName, long snippetPageNo, String imagePath) {
        this.id = id;
        this.snippetName = snippetName;
        this.snippetPageNo = snippetPageNo;
        ImagePath = imagePath;
    }

    public SnippetEntity()
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
}
