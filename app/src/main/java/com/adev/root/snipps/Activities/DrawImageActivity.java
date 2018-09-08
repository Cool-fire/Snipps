package com.adev.root.snipps.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.adev.root.snipps.R;
import com.adev.root.snipps.model.entities.Book;
import com.adev.root.snipps.model.entities.Snippet;
import com.github.veritas1.verticalslidecolorpicker.VerticalSlideColorPicker;
import com.squareup.picasso.Picasso;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmList;
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.ViewType;

public class DrawImageActivity extends AppCompatActivity implements OnPhotoEditorListener {

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
    private VerticalSlideColorPicker colorPicker;
    private int SelectedColor;
    private Typeface emojiTypeface;
    private Menu menu;
    private boolean Toggle = false;
    private Drawable DrawWrappedIcon;
    private Button undoBttn;
    private MenuItem Undoitem;

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

        toolbar = findViewById(R.id.toolbar_black);
        toolbar.setTitle("Draw");
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);


        ImageFile = new File(snippet.getImagePath());
        emojiTypeface = Typeface.createFromAsset(getAssets(), "emoji-font/NotoColorEmoji.ttf");



        mPhotoEditor = new PhotoEditor.Builder(this, drawImageview)
                .setPinchTextScalable(true)
                .setDefaultEmojiTypeface(emojiTypeface)
                .build();

        colorPicker = findViewById(R.id.color_picker);

        mPhotoEditor.setOnPhotoEditorListener(this);
        Picasso.get().load(ImageFile).into(drawImageview.getSource());

    }


    @Override
    protected void onStop() {
        super.onStop();
        if(DrawWrappedIcon!=null)
        {
            DrawableCompat.setTint(DrawWrappedIcon,getResources().getColor(R.color.colorWhite));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.draw_list, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Undoitem = menu.findItem(R.id.undo);
        switch(item.getItemId()) {
            case R.id.draw:
                    MenuItem Drawitem = menu.findItem(R.id.draw);
                    changeIcon();
                    DrawWrappedIcon = tintMenuIcon(getApplicationContext(),Drawitem);
                    return(true);
            case android.R.id.home:
                this.finish();
            case R.id.undo:
                mPhotoEditor.undo();


    }
        return(super.onOptionsItemSelected(item));
    }



    private void changeIcon() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (menu != null) {
                    MenuItem Drawitem = menu.findItem(R.id.draw);
                    if (Drawitem != null) {

                        if(!Toggle)
                        {
                            Toggle = true;
                            mPhotoEditor.setBrushDrawingMode(true);
                            intializeDrawing();
                            colorPicker.setVisibility(View.VISIBLE);
                        }
                        else if(Toggle)
                        {
                            Toggle = false;
                            mPhotoEditor.setBrushDrawingMode(false);
                            if(DrawWrappedIcon!=null)
                            {
                                DrawableCompat.setTint(DrawWrappedIcon, getResources().getColor(R.color.colorWhite));
                            }
                            else
                            {
                                Drawitem.setIcon(R.drawable.ic_edit);
                            }
                            colorPicker.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
    }

    public static Drawable tintMenuIcon(Context context, MenuItem item) {
        Drawable normalDrawable = item.getIcon();
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
        item.setIcon(wrapDrawable);
        return wrapDrawable;
    }

    private void intializeDrawing() {

        mPhotoEditor.setBrushColor(colorPicker.intialColor());
        if(DrawWrappedIcon!=null)
        {
            DrawableCompat.setTint(DrawWrappedIcon,colorPicker.intialColor());
        }
        colorPicker.setOnColorChangeListener(new VerticalSlideColorPicker.OnColorChangeListener() {
            @Override
            public void onColorChange(int selectedColor) {
                SelectedColor = selectedColor;
                mPhotoEditor.setBrushColor(SelectedColor);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(DrawWrappedIcon!=null)
                        {
                            DrawableCompat.setTint(DrawWrappedIcon,SelectedColor);
                        }
                    }
                });
            }
        });

    }


    private void setupViews() {
        drawImageview = findViewById(R.id.drawImageView);

    }

    @Override
    public void onEditTextChangeListener(View rootView, String text, int colorCode) {

    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
        if(numberOfAddedViews>0)
        {
            Undoitem.setVisible(true);
        }
        else
        {
            Undoitem.setVisible(false);
        }
    }

    @Override
    public void onRemoveViewListener(int numberOfAddedViews) {
        if(numberOfAddedViews == 0)
        {
            Undoitem.setVisible(false);
        }
    }

    @Override
    public void onRemoveViewListener(ViewType viewType, int numberOfAddedViews) {

    }

    @Override
    public void onStartViewChangeListener(ViewType viewType) {

    }

    @Override
    public void onStopViewChangeListener(ViewType viewType) {

    }
}
