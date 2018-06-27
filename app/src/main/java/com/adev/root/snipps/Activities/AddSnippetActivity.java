package com.adev.root.snipps.Activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.adev.root.snipps.R;
import com.adev.root.snipps.model.entities.Book;
import com.adev.root.snipps.model.entities.Snippet;
import com.adev.root.snipps.presenter.AddSnippetActivityPresenter;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

public class AddSnippetActivity extends AppCompatActivity {

    private TextInputEditText snippetname;
    private TextInputEditText pageno;
    private ImageView croppedimage;
    private FloatingActionButton doneBttn;
    private String croppedPath;
    private String BookPosition;
    private Realm realm;
    private AddSnippetActivity view;
    private AddSnippetActivityPresenter addSnippetActivityPresenter;
    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_snippet);

        Intent intent = getIntent();
        croppedPath = intent.getStringExtra("croppedPath");
        BookPosition = intent.getStringExtra("position");

        Toast.makeText(getApplicationContext(),BookPosition,Toast.LENGTH_SHORT).show();

        Realm.init(getApplicationContext());
        realm = Realm.getDefaultInstance();
        view = this;

        addSnippetActivityPresenter = new AddSnippetActivityPresenter(view, realm);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("New Snippet");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setupviews();

        Log.d("TAG", "onCreate: "+croppedPath);

        try
        {
            Picasso.get().load(new File(croppedPath)).resize(800,800).centerInside().into(croppedimage);

        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"Error Occurred ",Toast.LENGTH_SHORT).show();
        }

        doneBttn.setOnClickListener(new View.OnClickListener() {

            public RealmResults<Book> sortedBooks;
            private RealmResults<Book> books;

            @Override
            public void onClick(View view) {

                String snippet_name = snippetname.getText().toString();
                String page_no = pageno.getText().toString();

                if(snippet_name.isEmpty() || page_no.isEmpty())
                {
                    if(snippet_name.isEmpty())
                    {
                        snippetname.setError("Give it a good name");
                    }
                    if (page_no.isEmpty())
                    {
                        pageno.setError("give page number");
                    }
                }
                else
                {

                   addSnippetActivityPresenter.addSnippet(snippet_name,page_no,BookPosition,croppedPath);

                }

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {


        File file  = new File(croppedPath);
        if(file.exists())
        {
            file.delete();
        }
        finish();
        return super.onSupportNavigateUp();
    }

    private void setupviews() {
        snippetname = (TextInputEditText)findViewById(R.id.snippetName);
        pageno = (TextInputEditText)findViewById(R.id.pageNo);
        croppedimage = (ImageView)findViewById(R.id.croppedimageView);
        doneBttn = (FloatingActionButton)findViewById(R.id.doneBTTN);
    }

    public void addSnippet( final String snippet_name, final String page_no, final String bookPosition, final String croppedPath) {


        realm.executeTransaction(new Realm.Transaction() {
            public Number presentId;
            public RealmResults<Book> sortedRealmBooks;
            public RealmResults<Book> realmbooks;

            @Override
            public void execute(Realm realm) {
                realmbooks = realm.where(Book.class).findAll();
                sortedRealmBooks = realmbooks.sort("creationDate", Sort.DESCENDING);
                book = sortedRealmBooks.get(Integer.valueOf(bookPosition));

                Log.d("TAG", "execute: "+book.getSnippetsList().size());

                presentId = realm.where(Snippet.class).max("id");
                int id = getNextID(presentId);

                Snippet snippet = realm.createObject(Snippet.class,id);
                snippet.setImagePath(croppedPath);
                snippet.setSnippetName(snippet_name);
                snippet.setSnippetPageNo(Long.valueOf(page_no));
                Log.d("TAG", "execute: "+book.getBookTitle());
                snippet = realm.copyToRealmOrUpdate(snippet);
                book.getSnippetsList().add(snippet);
             //   Toast.makeText(getApplicationContext(),book.getBookTitle(),Toast.LENGTH_SHORT).show();
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
