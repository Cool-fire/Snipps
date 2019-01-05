package com.adev.root.snipps.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.adev.root.snipps.R;

import com.adev.root.snipps.model.entities.Book;
import com.adev.root.snipps.presenter.NewBookPresenter;
import com.adev.root.snipps.view.NewBookActivityView;

import java.util.Date;


import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmResults;

public class NewBookActivity extends AppCompatActivity implements NewBookActivityView{

    private static final int requestCode = 1000;
    private static final String TAG = "ACTIVTY";
    private EditText Author,Title;
    private Context context;
    private FloatingActionButton barcodeFab;

    private NewBookActivityView view;
    private static NewBookActivity Sapp;
    private RealmResults<Book> books;
    private RealmAsyncTask realmAsyncTaskTransaction;
    private Handler mHandler;
    private RealmAsyncTask RealmupdateTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_book);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.newBook);

        Author = findViewById(R.id.bookAuthor);
        Title = findViewById(R.id.BookTitle);

        view = this;
        Sapp = this;
        Realm.init(this);


        context = getApplicationContext();
        final NewBookPresenter presenter = new NewBookPresenter(view,context);

        // checking the Callingtype if it is update set the before Author,title
        final Intent intent = getIntent();
        final String CallingType = intent.getStringExtra("NewBookActivitytype");
        if(CallingType!=null)
        {

            if(CallingType.equals("UpdateBook"))
            {
                final Realm realm = Realm.getDefaultInstance();
                String id = intent.getStringExtra("updatebookId");
                long bookId = Long.parseLong(id);
                Book book = realm.where(Book.class).equalTo("id", bookId).findFirst();
                Author.setText(book.getBookAuthor());
                Title.setText(book.getBookTitle());
            }

        }

        FloatingActionButton doneFab = findViewById(R.id.fab);
        doneFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 String BookTitle = Title.getText().toString();
                 String BookAuthor = Author.getText().toString();

                if(BookTitle.isEmpty() || BookAuthor.isEmpty())
                {
                    if(BookTitle.isEmpty())
                    {
                        Title.setError("Book Title Required");
                    }
                    if(BookAuthor.isEmpty())
                    {
                        Author.setError("Book Author Required");
                    }
                }
                else
                {
                    if(CallingType!=null)
                    {

                        if(CallingType.equals("UpdateBook"))
                        {

                            String id = intent.getStringExtra("updatebookId");
                            long bookId = Long.parseLong(id);
                            updateBook(bookId);
                        }
                        else
                        {
                            addBook(); // Normal original adding
                        }
                    }
                }

            }
        });

       barcodeFab = findViewById(R.id.fab2);
        barcodeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               presenter.CheckForInternet();
            }
        });
        mHandler = new Handler();




    }

    private void updateBook(final long bookId) {

        final Realm realm = Realm.getDefaultInstance();
        final String BookTitle = Title.getText().toString();
        final String BookAuthor = Author.getText().toString();

        RealmupdateTransaction = realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Book book = realm.where(Book.class).equalTo("id", bookId).findFirst();
                if (book == null) {
                    book = realm.createObject(Book.class, bookId);
                }
                book.setBookTitle(BookTitle);
                book.setBookAuthor(BookAuthor);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                goBack();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(realmAsyncTaskTransaction!=null && !realmAsyncTaskTransaction.isCancelled())
        {
            realmAsyncTaskTransaction.cancel();
        }
        if(RealmupdateTransaction!=null && !RealmupdateTransaction.isCancelled())
        {
            RealmupdateTransaction.cancel();
        }
    }

    private void addBook() {


        final Realm realm = Realm.getDefaultInstance();
        final String BookTitle = Title.getText().toString();
        final String BookAuthor = Author.getText().toString();

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
                book.setId(id);
                realm.insertOrUpdate(book);

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                goBack();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Toast.makeText(getApplicationContext(), "Error Occured", Toast.LENGTH_SHORT).show();
            }
        });
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
        if(RealmupdateTransaction!=null && !RealmupdateTransaction.isCancelled())
        {
            RealmupdateTransaction.cancel();
        }

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
