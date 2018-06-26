package com.adev.root.snipps.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.adev.root.snipps.R;
import com.adev.root.snipps.model.entities.Book;
import com.adev.root.snipps.model.entities.Snippet;
import com.adev.root.snipps.presenter.SnippetActivityPresenter;
import com.adev.root.snipps.view.SnippetActivityView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

public class SnippetActivity extends AppCompatActivity implements SnippetActivityView{

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 10000;
    private SnippetActivity view;
    private Context context;
    private SnippetActivityPresenter Snippetpresenter;
    private String BookTitle;
    private com.github.clans.fab.FloatingActionButton cameraBttn;
    private com.github.clans.fab.FloatingActionButton galleryBttn;
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;
    private File image;
    private Uri photoURI;
    private String BookPosition;
    private Realm realm;
    private RealmResults<Book> books;
    private RealmResults<Book> sortedBooks;
    private Book book;
    private RealmList<Snippet> snippetsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snippet);

        Intent intent = getIntent();
        BookTitle = getIntent().getStringExtra("title");
        BookPosition = getIntent().getStringExtra("position");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(BookTitle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Realm.init(getApplicationContext());

        realm = Realm.getDefaultInstance();

        books = realm.where(Book.class).findAll();
        sortedBooks = books.sort("creationDate", Sort.DESCENDING);

        book = sortedBooks.get(Integer.valueOf(BookPosition));
        snippetsList = book.getSnippetsList();


        setupviews();

        view = this;
        context = getApplicationContext();
        Snippetpresenter = new SnippetActivityPresenter(view, context);

        cameraBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(SnippetActivity.this,Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {


                    if (ActivityCompat.shouldShowRequestPermissionRationale(SnippetActivity.this, Manifest.permission.READ_CONTACTS))
                    {
                       Toast.makeText(getApplicationContext(),"Grant Permission to access camera",Toast.LENGTH_SHORT).show();
                       callForPermissions();

                    }
                    else if(!ActivityCompat.shouldShowRequestPermissionRationale(SnippetActivity.this, Manifest.permission.READ_CONTACTS))
                    {
                        Toast.makeText(getApplicationContext(),"please enable camera permission in settings",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        callForPermissions();
                    }
                }
                else {


                    dispatchTakePictureIntent();



                }
                
               
            }
        });


    }

    private void callForPermissions() {
        ActivityCompat.requestPermissions(SnippetActivity.this,
                new String[]{Manifest.permission.CAMERA},
                MY_PERMISSIONS_REQUEST_CAMERA);

    }


    private void setupviews() {
       cameraBttn = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.camera);
       galleryBttn = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.browse);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if(id == android.R.id.home)
        {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();

            } catch (IOException ex) {
                Toast.makeText(getApplicationContext(),"Error Creating file",Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.adev.root.snipps.Activities.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir("Pictures/Snipps");
        image = File.createTempFile(
                imageFileName, ".jpg", storageDir
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.d("TAG", "createImageFile: "+mCurrentPhotoPath);
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode)
        {
            case REQUEST_TAKE_PHOTO :
            {
                if(resultCode == RESULT_CANCELED)
                {
                    image.delete();
                }
                if(resultCode == RESULT_OK)
                {
                    Intent intent = new Intent(SnippetActivity.this, CropActivity.class);
                    intent.putExtra("position",BookPosition);
                    intent.putExtra("PhotoUri", photoURI);
                    intent.putExtra("photoPath",mCurrentPhotoPath.toString());
                    startActivity(intent);
                }

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_CAMERA:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                   Toast.makeText(getApplicationContext(),"Permission Granted",Toast.LENGTH_SHORT).show();
                   dispatchTakePictureIntent();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Requires camera permission for functionality",Toast.LENGTH_SHORT).show();
                }

            }
        }
    }
}
