package com.adev.root.snipps.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.adev.root.snipps.R;

import com.adev.root.snipps.model.entities.Book;
import com.adev.root.snipps.model.entities.BookEntity;
import com.adev.root.snipps.presenter.NewBookPresenter;
import com.adev.root.snipps.view.NewBookActivityView;

import java.util.Date;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmResults;

public class NewBookActivity extends AppCompatActivity implements NewBookActivityView{

    private static final int requestCode = 1000;
    private static final String TAG = "ACTIVTY";
    private EditText Author,Title;
    private Context context;
    private FloatingActionButton barcodeFab;
    private BoxStore mBoxStore;
    private NewBookActivityView view;
    private static NewBookActivity Sapp;
    private BoxStore boxStore = null;
    private Box<BookEntity> bookEntityBox;
    private RealmResults<Book> books;
    private RealmAsyncTask realmAsyncTaskTransaction;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_book);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.newBook);

        Author = (EditText)findViewById(R.id.bookAuthor);
        Title = (EditText)findViewById(R.id.BookTitle);

        view = this;
        Sapp = this;


        context = getApplicationContext();
        final NewBookPresenter presenter = new NewBookPresenter(view,context);

        FloatingActionButton doneFab = (FloatingActionButton) findViewById(R.id.fab);
        doneFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBook();
            }
        });

       barcodeFab = (FloatingActionButton)findViewById(R.id.fab2);
        barcodeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               presenter.CheckForInternet();
            }
        });
//        if(mBoxStore==null)
//        {
//            mBoxStore = MyObjectBox.builder().androidContext(NewBookActivity.this).build();
//
//        }
//
//
//        boxStore = NewBookActivity.getApp().getBoxStore();
//        bookEntityBox = boxStore.boxFor(BookEntity.class);

        mHandler = new Handler();
        Realm.init(this);


    }

    @Override
    protected void onPause() {
        super.onPause();
        if(realmAsyncTaskTransaction!=null && !realmAsyncTaskTransaction.isCancelled())
        {
            realmAsyncTaskTransaction.cancel();
        }
    }

    private void addBook() {


        final Realm realm = Realm.getDefaultInstance();
        final String BookTitle = Title.getText().toString();
        Log.d(TAG, "addBook: "+BookTitle);

        final String BookAuthor = Author.getText().toString();
        Log.d(TAG, "addBook: "+BookAuthor);

        final Book book = new Book();
        book.setBookAuthor(BookAuthor);
        book.setBookTitle(BookTitle);
        book.setCreationDate(new Date());



        realmAsyncTaskTransaction = realm.executeTransactionAsync(new Realm.Transaction() {

            private Number presentId;

            @Override
            public void execute(Realm realm) {

                presentId = realm.where(Book.class).max("id");


                int id = getNextID(presentId);

//                Book book = realm.createObject(Book.class,id);
//                book.setBookTitle(BookTitle);
//                book.setBookAuthor(BookAuthor);
//                book.setCreationDate(new Date());
                book.setId(id);
                Log.d(TAG, "execute: "+realm.where(Book.class).findAll());
                realm.insertOrUpdate(book);

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {

                Toast.makeText(getApplicationContext(), "Succesfully added", Toast.LENGTH_SHORT).show();
               // goBack();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.d(TAG, "onError: "+error.getMessage());
                Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_SHORT).show();
            }
        });

//        BookEntity Book = new BookEntity();
//        Book.setBookAuthor(BookAuthor);
//        Book.setBookTitle(BookTitle);
//        Book.setCreationDate(new Date());
//
//        bookEntityBox.put(Book);
//        Log.d(TAG, "addBook: id: "+Book.getId());
//        Toast.makeText(getApplicationContext(),"Inserted Id :"+Book.getId(),Toast.LENGTH_SHORT).show();
    }

    private void goBack() {
        finish();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==requestCode && resultCode == RESULT_OK)
        {
            if(data!=null)
            {
                final String BookAuthor = data.getStringExtra("BookAuthor");
                final String BookTitle = data.getStringExtra("BookTitle");


                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Author.setText(BookAuthor);
                        Title.setText(BookTitle);
                    }
                });


            }

        }
    }






    public static NewBookActivity getApp()
    {
        return Sapp;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(realmAsyncTaskTransaction!=null && !realmAsyncTaskTransaction.isCancelled())
        {
            realmAsyncTaskTransaction.cancel();
        }

    }

    public BoxStore getBoxStore()
    {
        return mBoxStore;
    }


    @Override
    public void sendForScan() {
        Intent intent = new Intent(getApplicationContext(),BarcodeScannerActivity.class);
        startActivityForResult(intent,requestCode);
    }

    @Override
    public void showNoInternet() {


        Toast.makeText(getApplicationContext(),"This action needs Internet",Toast.LENGTH_SHORT).show();

    }


}
