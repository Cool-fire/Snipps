package com.adev.root.snipps.presenter;

import com.adev.root.snipps.model.entities.Book;
import com.adev.root.snipps.view.BooksActivityView;

import io.realm.Realm;
import io.realm.RealmResults;

public class BooksActivityPresenter {
    private final BooksActivityView view;
    private final Realm realm;
    private RealmResults<Book> books;


    public BooksActivityPresenter(BooksActivityView view, Realm realm) {
        this.view = view;
        this.realm = realm;
    }

    public RealmResults<Book> getBooks() {
        books = realm.where(Book.class).findAll();
        return books;
    }
}
