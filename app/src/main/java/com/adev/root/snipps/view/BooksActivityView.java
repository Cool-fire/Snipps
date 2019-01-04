package com.adev.root.snipps.view;

import com.adev.root.snipps.model.entities.Book;

import io.realm.RealmResults;

public interface BooksActivityView {

    RealmResults<Book> getBooksList();
}
