package com.adev.root.snipps.Activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.adev.root.snipps.R;
import com.adev.root.snipps.model.entities.Book;
import com.adev.root.snipps.model.entities.Snippet;
import com.adev.root.snipps.utils.Utility;
import com.github.veritas1.verticalslidecolorpicker.VerticalSlideColorPicker;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;

import io.realm.Realm;
import io.realm.RealmList;
import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.ViewType;

import static android.view.View.INVISIBLE;

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
    private boolean BrushToggle = false;
    private boolean TextToggle = false;
    private Drawable DrawWrappedIcon;
    private Button undoBttn;
    private MenuItem Undoitem;
    private Drawable textDrawWrapedIcon;
    private TextView inputTextEt;
    private ScrollView sv;
    private String saveTag;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_image);


        Intent intent = getIntent();
        position = intent.getStringExtra("snippetPosition");
        BookId = intent.getStringExtra("bookId");

        Realm.init(getApplicationContext());

        realm = Realm.getDefaultInstance();
        book = realm.where(Book.class).equalTo("id", Long.valueOf(BookId)).findFirst();
        String bookTitle = book.getBookTitle();
        if (book != null) {
            snippetsList = book.getSnippetsList();
            snippet = snippetsList.get(Integer.valueOf(position));
        }

        setupViews();
        SelectedColor = getResources().getColor(R.color.colorWhite);

        inputTextEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    AddTextonImage(inputTextEt.getText().toString());
                    Utility.hideSoftKeyboard((Activity) DrawImageActivity.this);
                    inputTextEt.setVisibility(INVISIBLE);
                }
                return false;
            }
        });


        toolbar = findViewById(R.id.toolbar_black);
        toolbar.setTitle("Draw");
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);


        ImageFile = new File(snippet.getImagePath());
        emojiTypeface = Typeface.createFromAsset(getAssets(), "emoji-font/NotoColorEmoji.ttf");


        mPhotoEditor = new PhotoEditor.Builder(this, drawImageview).setPinchTextScalable(true).setDefaultEmojiTypeface(emojiTypeface).build();

        colorPicker = findViewById(R.id.color_picker);

        mPhotoEditor.setOnPhotoEditorListener(this);
        Picasso.get().load(ImageFile).into(drawImageview.getSource());


    }


    @Override
    protected void onStop() {
        super.onStop();
        if (DrawWrappedIcon != null) {
            DrawableCompat.setTint(DrawWrappedIcon, getResources().getColor(R.color.colorWhite));
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
        switch (item.getItemId()) {
            case R.id.draw:
                MenuItem Drawitem = menu.findItem(R.id.draw);
                changeIcon();
                DrawWrappedIcon = tintMenuIcon(getApplicationContext(), Drawitem);
                break;
            case R.id.undo:
                mPhotoEditor.undo();
                break;
            case R.id.textDraw:
                MenuItem textDrawItem = menu.findItem(R.id.textDraw);
                textDrawWrapedIcon = tintMenuIcon(getApplicationContext(), textDrawItem);
                changeTextIcon();
                break;
            case android.R.id.home:
                this.finish();
                break;

            case R.id.save:

                savePopup();
                break;


        }
        return (super.onOptionsItemSelected(item));
    }

    private void savePopup() {
        saveDialog();
        saveTag = "saveTag";
       // dialog.show(manager, saveTag);
    }

    private void saveDialog() {
        AlertDialog.Builder builder;
        context = getApplicationContext();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(DrawImageActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(DrawImageActivity.this);
        }
        builder.setMessage(R.string.overwritesnippet)
                .setTitle(R.string.sure)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        save();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void changeTextIcon() {

        if (menu != null) {
            MenuItem textDrawItem = menu.findItem(R.id.textDraw);
            if (textDrawItem != null) {
                if (!TextToggle) {
                    TextToggle = true;
                    addtext();
                } else if (TextToggle) {
                    TextToggle = false;
                    hidetext();
                }
            }
        }

    }

    private void hidetext() {
        Utility.hideSoftKeyboard((Activity) DrawImageActivity.this);
        inputTextEt.setVisibility(INVISIBLE);
    }

    private void addtext() {
        inputTextEt.setVisibility(View.VISIBLE);
        inputTextEt.setText(null);
        Utility.showSoftKeyboard((Activity) DrawImageActivity.this, inputTextEt);


    }

    private void AddTextonImage(String typedText) {
        int color = getResources().getColor(R.color.colorWhite);
        TextToggle = false;
        mPhotoEditor.addText(typedText, SelectedColor);
    }


    private void changeIcon() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (menu != null) {
                    MenuItem Drawitem = menu.findItem(R.id.draw);
                    if (Drawitem != null) {

                        if (!BrushToggle) {
                            BrushToggle = true;
                            mPhotoEditor.setBrushDrawingMode(true);
                            intializeDrawing();
                            colorPicker.setVisibility(View.VISIBLE);
                        } else if (BrushToggle) {
                            BrushToggle = false;
                            mPhotoEditor.setBrushDrawingMode(false);
                            if (DrawWrappedIcon != null) {
                                DrawableCompat.setTint(DrawWrappedIcon, getResources().getColor(R.color.colorWhite));
                            } else {
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

        int initialcolor = colorPicker.intialColor();
        if (initialcolor == Color.BLACK) {
            initialcolor = Color.WHITE;
        }
        mPhotoEditor.setBrushColor(initialcolor);
        if (DrawWrappedIcon != null) {
            DrawableCompat.setTint(DrawWrappedIcon, initialcolor);
        }
        colorPicker.setOnColorChangeListener(new VerticalSlideColorPicker.OnColorChangeListener() {
            @Override
            public void onColorChange(int selectedColor) {
                SelectedColor = selectedColor;
                mPhotoEditor.setBrushColor(SelectedColor);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (DrawWrappedIcon != null) {
                            if (SelectedColor != Color.BLACK) {
                                DrawableCompat.setTint(DrawWrappedIcon, SelectedColor);
                            } else {
                                DrawableCompat.setTint(DrawWrappedIcon, Color.WHITE);

                            }
                        }
                    }
                });
            }
        });

    }


    private void setupViews() {
        drawImageview = findViewById(R.id.drawImageView);
        inputTextEt = findViewById(R.id.add_text_et);

    }

    @Override
    public void onEditTextChangeListener(View rootView, String text, int colorCode) {

    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
        if (numberOfAddedViews > 0) {
            Undoitem.setVisible(true);
        } else {
            Undoitem.setVisible(false);
        }
    }

    @Override
    public void onRemoveViewListener(int numberOfAddedViews) {
        if (numberOfAddedViews == 0) {
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







    @SuppressLint("MissingPermission")
    private void save() {

        mPhotoEditor.saveAsFile(snippet.getImagePath(), new PhotoEditor.OnSaveListener() {
            @Override
            public void onSuccess(@NonNull String imagePath) {
                Log.e("PhotoEditor", "Image Saved Successfully");
                finish();
            }

            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("PhotoEditor", "Failed to save Image");
                Toast.makeText(getApplicationContext(),"Failed to save",Toast.LENGTH_SHORT).show();
            }
        });
    }

}


