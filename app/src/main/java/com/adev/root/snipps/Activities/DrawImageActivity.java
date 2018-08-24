package com.adev.root.snipps.Activities;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.adev.root.snipps.R;
import com.adev.root.snipps.model.entities.Book;
import com.adev.root.snipps.model.entities.Snippet;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmList;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.PhotoFilter;

public class DrawImageActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String position;
    private String BookId;
    private Realm realm;
    private Book book;
    private RealmList<Snippet> snippetsList;
    private Snippet snippet;
    private File ImageFile;
    private PhotoEditorView drawImageview;
    private PhotoEditor mPhotoEditor;
    private Button button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_image);


        Intent intent = getIntent();
        position =  intent.getStringExtra("snippetPosition");
        BookId = intent.getStringExtra("bookId");

        Realm.init(getApplicationContext());

        realm = Realm.getDefaultInstance();
        book = realm.where(Book.class).equalTo("id", Long.valueOf(BookId)).findFirst();
        String bookTitle = book.getBookTitle();
        if(book !=null)
        {
            snippetsList = book.getSnippetsList();
            snippet = snippetsList.get(Integer.valueOf(position));
        }

        setupViews();

        toolbar = (Toolbar) findViewById(R.id.toolbar_black);
        toolbar.setTitle("Draw");
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);


        ImageFile = new File(snippet.getImagePath());
        Picasso.get().load(ImageFile).into(drawImageview.getSource());


        mPhotoEditor = new PhotoEditor.Builder(this, drawImageview)
                .setPinchTextScalable(true)
                .build();

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callforChange();
            }
        });


    }

    private void callforChange() {
        mPhotoEditor.setBrushDrawingMode(true);
    }

    private void setupViews() {
        drawImageview = (PhotoEditorView)findViewById(R.id.drawImageView);
        button3 = (Button)findViewById(R.id.button3);
    }
}
