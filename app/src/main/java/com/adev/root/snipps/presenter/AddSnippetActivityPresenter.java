package com.adev.root.snipps.presenter;

import android.util.Log;
import android.widget.Toast;

import com.adev.root.snipps.Activities.AddSnippetActivity;

import com.adev.root.snipps.model.entities.Book;
import com.adev.root.snipps.model.entities.Snippet;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

public class AddSnippetActivityPresenter {

    private static final String TAG = "AddSnippet";
    private final AddSnippetActivity view;
    private final Realm realm;
    private RealmResults<Book> books;
    private Book book;

    public AddSnippetActivityPresenter(AddSnippetActivity view, Realm realm) {
        this.view = view;
        this.realm = realm;
    }


    public RealmResults<Book> getBooks() {
        books = realm.where(Book.class).findAll();
        return books;
    }

    public void addSnippet(final String snippet_name, final String page_no, final String bookPosition, final String croppedPath, final String date) {


        realm.executeTransactionAsync(new Realm.Transaction() {
            public Number presentId;
            public RealmResults<Book> sortedRealmBooks;
            public RealmResults<Book> realmbooks;

            @Override
            public void execute(Realm realm) {
                realmbooks = realm.where(Book.class).findAll();
                sortedRealmBooks = realmbooks.sort("creationDate", Sort.DESCENDING);
                book = sortedRealmBooks.get(Integer.valueOf(bookPosition));

                Log.d("TAG", "execute: " + book.getSnippetsList().size());

                presentId = realm.where(Snippet.class).max("id");
                int id = getNextID(presentId);
                Snippet snippet = realm.createObject(Snippet.class, id);
                snippet.setImagePath(croppedPath);
                snippet.setSnippetName(snippet_name);
                snippet.setSnippetPageNo(Long.valueOf(page_no));
                snippet.setDate(date);
                Log.d("TAG", "execute: " + book.getBookTitle());
                snippet = realm.copyToRealmOrUpdate(snippet);
                book.getSnippetsList().add(snippet);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                    Toast.makeText(view.getApplicationContext(),"Done",Toast.LENGTH_SHORT).show();
                    view.sendMessage();
                    view.finish();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Toast.makeText(view.getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
            }
        });
    }



    private int getNextID(Number max) {
        int nextId ;
        if(max == null)
        {
            nextId = 1;
        }
        else
        {
            nextId = max.intValue()+1;
        }

        return nextId;
    }
}
