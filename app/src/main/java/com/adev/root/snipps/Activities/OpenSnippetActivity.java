package com.adev.root.snipps.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adev.root.snipps.R;
import com.adev.root.snipps.model.entities.Book;
import com.adev.root.snipps.model.entities.Snippet;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmList;

public class OpenSnippetActivity extends AppCompatActivity {

    private static final String TAG = "openSnippet";
    private Realm realm;
    private RealmList<Snippet> snippetsList;
    private Snippet snippet;
    private PhotoView snippetImage;
    private Book book;
    private TextView snippetName;
    private TextView snippetPage;
    private TextView snippetTime;
    private FloatingActionMenu fabmenu;
    private FloatingActionButton OcrButton;
    private File ImageFile;
    boolean gone = false;
    private RelativeLayout snippetDetailsView;
    private Toolbar toolbar;
    private ProgressBar progressDialog;
    private FloatingActionButton drawImageButton;
    private String position;
    private String BookId;
    private FloatingActionButton notesButton;
    private long id;
    private Snippet realmSnippet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_open_snippet);

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
            id = snippet.getId();
        }

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(bookTitle);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);

        setupviews();
        ImageFile = new File(snippet.getImagePath());
        Picasso.get().load(ImageFile).memoryPolicy(MemoryPolicy.NO_CACHE).resize(1500,1500).centerInside().into(snippetImage);
        snippetName.setText(snippet.getSnippetName());
        snippetPage.setText(String.valueOf(snippet.getSnippetPageNo()));
        try
        {
            snippetTime.setText(snippet.getDate());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        OcrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetupOcr();
            }
        });
        drawImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawOnImage();
            }
        });
        notesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNotes();
            }
        });
    }

    private void saveNotes() {
        AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.notes_dialog,null);
        builder.setView(dialogView);
        final EditText edt = dialogView.findViewById(R.id.edit1);
        if(snippet.getNotes()!=null)
        {
            String note = snippet.getNotes();
            edt.setText(note);
        }
        builder.setTitle(R.string.Note).setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String notes = edt.getText().toString();
                if(!notes.isEmpty())
                {
                    save(notes);
                }
                else{
                    edt.setError("Notes Required");
                }
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void save(final String notes) {

        try{
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realmSnippet = realm.where(Snippet.class).equalTo("id", id).findFirst();
                    realmSnippet.setNotes(notes);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getApplicationContext(),"notes saved",Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        File newImageFile = new File(snippet.getImagePath());
        Picasso.get().load(newImageFile).memoryPolicy(MemoryPolicy.NO_CACHE).resize(1500,1500).centerInside().into(snippetImage);
    }

    private void drawOnImage() {
            Intent drawingIntent = new Intent(OpenSnippetActivity.this,DrawImageActivity.class);
            drawingIntent.putExtra("snippetPosition",position);
            drawingIntent.putExtra("bookId",BookId);
            startActivity(drawingIntent);
    }

    private void SetupOcr() {
        FirebaseVisionImage image;
        final String[] OcrText = {""};
        try {
            progressDialog.setVisibility(View.VISIBLE);
            image = FirebaseVisionImage.fromFilePath(getApplicationContext(), Uri.fromFile(ImageFile));
            FirebaseVisionTextDetector detector = FirebaseVision.getInstance()
                    .getVisionTextDetector();
            Task<FirebaseVisionText> result = detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                @Override
                public void onSuccess(FirebaseVisionText firebaseVisionText) {
                    for (FirebaseVisionText.Block block: firebaseVisionText.getBlocks()) {
                        Rect boundingBox = block.getBoundingBox();
                        Point[] cornerPoints = block.getCornerPoints();
                        String text = block.getText();
                        OcrText[0] = OcrText[0] + "\n"+text;

                    }
                    if(OcrText[0] != "") {

                        Intent intent = new Intent(OpenSnippetActivity.this, OcrshowActivity.class);
                        intent.putExtra("RecognisedText", OcrText[0]);

                        progressDialog.setVisibility(View.GONE);
                        startActivity(intent);
                    }
                    else
                    {
                        progressDialog.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),"No Text Found",Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),"error occured",Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            progressDialog.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    private void setupviews() {
        snippetImage = findViewById(R.id.snippetIMG);
        snippetName = findViewById(R.id.snippetNameTV);
        snippetPage = findViewById(R.id.snippetPageNumberTV);
        snippetTime = findViewById(R.id.snippetDateAddedTV);
        fabmenu = findViewById(R.id.fabmenusnippet);
        snippetDetailsView = findViewById(R.id.snippetDetailsView);
        OcrButton = findViewById(R.id.ocr);
        notesButton = findViewById(R.id.write_description);
        progressDialog = findViewById(R.id.progressDialog);
        drawImageButton = findViewById(R.id.draw_image);
        snippetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(gone == false)
                {
                    snippetDetailsView.setVisibility(View.GONE);
                    fabmenu.setVisibility(View.GONE);
                    toolbar.setVisibility(View.GONE);
                    gone = true;
                }
                else
                {
                    snippetDetailsView.setVisibility(View.VISIBLE);
                    fabmenu.setVisibility(View.VISIBLE);
                    toolbar.setVisibility(View.VISIBLE);
                    gone = false;
                }

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }


}
