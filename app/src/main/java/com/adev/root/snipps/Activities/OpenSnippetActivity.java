package com.adev.root.snipps.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
       // Log.d(TAG, "onCreate: "+snippet.getSnippetName());
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        File newImageFile = new File(snippet.getImagePath());
        Picasso.get().load(newImageFile).memoryPolicy(MemoryPolicy.NO_CACHE).resize(1500,1500).centerInside().into(snippetImage);
//        Picasso.get().invalidate(newImageFile);
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
                   // Toast.makeText(getApplicationContext(),"Ocr Worked",Toast.LENGTH_SHORT).show();
                    for (FirebaseVisionText.Block block: firebaseVisionText.getBlocks()) {
                        Rect boundingBox = block.getBoundingBox();
                        Point[] cornerPoints = block.getCornerPoints();
                        String text = block.getText();
                        OcrText[0] = OcrText[0] + "\n"+text;

                    }
                    Log.d(TAG, "onSuccess: "+OcrText[0]);
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
                    Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            progressDialog.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    private void setupviews() {
        snippetImage = (PhotoView)findViewById(R.id.snippetIMG);
        snippetName = (TextView)findViewById(R.id.snippetNameTV);
        snippetPage = (TextView)findViewById(R.id.snippetPageNumberTV);
        snippetTime = (TextView)findViewById(R.id.snippetDateAddedTV);
        fabmenu = (FloatingActionMenu)findViewById(R.id.fabmenusnippet);
        snippetDetailsView = (RelativeLayout)findViewById(R.id.snippetDetailsView);
        OcrButton = (FloatingActionButton)findViewById(R.id.ocr);
        progressDialog = (ProgressBar)findViewById(R.id.progressDialog);
        drawImageButton = (FloatingActionButton)findViewById(R.id.draw_image);
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
