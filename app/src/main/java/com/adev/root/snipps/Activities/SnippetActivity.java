package com.adev.root.snipps.Activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ActionMenuView;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.adev.root.snipps.R;
import com.adev.root.snipps.adapters.RecyclerTouchListener;
import com.adev.root.snipps.adapters.SnippetAdapter;
import com.adev.root.snipps.model.entities.Book;
import com.adev.root.snipps.model.entities.Snippet;
import com.adev.root.snipps.presenter.SnippetActivityPresenter;
import com.adev.root.snipps.view.SnippetActivityView;
import com.github.clans.fab.FloatingActionMenu;


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
    private static final int RESULT_LOAD_IMG = 22222;
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
    private RecyclerView recyclerview1;
    private SnippetAdapter mAdapter;
    private FloatingActionMenu fabmenu;
    private CoordinatorLayout coordinatorlayout;
    private Object busEventListener;
    private int Position;
    private String mPhotoPathString;
    private ImageView snippetOutlineImg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snippet);

        Intent intent = getIntent();
        BookTitle = getIntent().getStringExtra("title");
        BookPosition = getIntent().getStringExtra("position");

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(BookTitle);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Realm.init(getApplicationContext());

        realm = Realm.getDefaultInstance();

        RealmList<Snippet> sortedbooks = getSortedBooks();


        setupviews();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("EventSnippetAdded"));
        view = this;
        context = getApplicationContext();
        Snippetpresenter = new SnippetActivityPresenter(view, context);

        if(savedInstanceState!=null)
        {
            mPhotoPathString = savedInstanceState.getString("imagePath");
        }

        cameraBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(SnippetActivity.this,Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {


                    if (ActivityCompat.shouldShowRequestPermissionRationale(SnippetActivity.this, Manifest.permission.CAMERA))
                    {
                       Toast.makeText(getApplicationContext(),"Grant Permission to access camera",Toast.LENGTH_SHORT).show();
                       callForPermissions();

                    }
//                    else if(!ActivityCompat.shouldShowRequestPermissionRationale(SnippetActivity.this, Manifest.permission.CAMERA))
//                    {
//                        Toast.makeText(getApplicationContext(),"please enable camera permission in settings",Toast.LENGTH_SHORT).show();
//                    }
                    else {
                        callForPermissions();
                    }
                }
                else {
                    Log.d("TAG", "onA: camera ");
                    dispatchTakePictureIntent();
                    fabmenu.close(true);
                }
                
               
            }
        });

        galleryBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent PhotoPickIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
              //  PhotoPickIntent.setType("image/*");
                Log.d("TAG", "onA: gallery ");
                startActivityForResult(PhotoPickIntent,RESULT_LOAD_IMG);
                Log.d("TAG", "onA: galleryit ");
                fabmenu.close(true);
            }
        });


        recyclerview1 = (RecyclerView)findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerview1.setLayoutManager(layoutManager);



        recyclerview1.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerview1, new RecyclerTouchListener.Clicklistner() {
            @Override
            public void onclick(View view, int position) {
                Log.d("TAG", "onclick() returned: " +position );

                Intent intent1 = new Intent(SnippetActivity.this,OpenSnippetActivity.class);
                long bookId = book.getId();
                intent1.putExtra("snippetPosition",Integer.toString(position));
                intent1.putExtra("bookId",String.valueOf(bookId));

                startActivity(intent1);
            }

            @Override
            public void onLongClick(View view, int position) {

                Position = position;
                showPopup(view);
            }

            @Override
            public void onDoubleTap(View view, int position) {

            }
        }));




    }

    private RealmList<Snippet> getSortedBooks() {
        books = realm.where(Book.class).findAll();
        sortedBooks = books.sort("creationDate", Sort.DESCENDING);
        book = sortedBooks.get(Integer.valueOf(BookPosition));
        snippetsList = book.getSnippetsList();
        return snippetsList;

    }

    @Override
    protected void onStart() {

        super.onStart();
        int noOfSnippets = book.getSnippetsList().size();
        if(noOfSnippets>0)
        {
            snippetOutlineImg.setVisibility(View.GONE);
        }
        mAdapter = new SnippetAdapter(book);
        recyclerview1.setAdapter(mAdapter);

    }





    private void callForPermissions() {
        ActivityCompat.requestPermissions(SnippetActivity.this,
                new String[]{Manifest.permission.CAMERA},
                MY_PERMISSIONS_REQUEST_CAMERA);

    }


    private void setupviews() {
       cameraBttn = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.camera);
       galleryBttn = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.browse);
       fabmenu = (FloatingActionMenu)findViewById(R.id.fabmenu);
       coordinatorlayout = (CoordinatorLayout)findViewById(R.id.coordinator);
       snippetOutlineImg = (ImageView)findViewById(R.id.snippetOutline);
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
        File storageDir = getExternalFilesDir("Pictures/Snipps/");
        image = File.createTempFile(
                imageFileName, ".jpg", storageDir
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        mPhotoPathString = mCurrentPhotoPath.toString();
        Log.d("TAG", "createImageFile: "+mCurrentPhotoPath);
        return image;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("imagePath",mPhotoPathString);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("TAG", "onActivityResult: "+requestCode);

            if(requestCode == REQUEST_TAKE_PHOTO)
            {

                if(resultCode == RESULT_CANCELED)
                {
                    try
                    {
                        image.delete();
                    }
                    catch (Exception e){}

                }
                if(resultCode == RESULT_OK)
                {

                        Intent intent = new Intent(SnippetActivity.this, CropActivity.class);
                        intent.putExtra("booktitle",BookTitle);
                        intent.putExtra("position",BookPosition);
                        intent.putExtra("PhotoUri", photoURI);
                        intent.putExtra("photoPath",mPhotoPathString);
                        startActivity(intent);

                }

            }

            else if(requestCode == RESULT_LOAD_IMG)
            {


                    try {
                        final Uri imageUri = data.getData();
                        Intent intent = new Intent(SnippetActivity.this, CropActivity.class);
                        intent.putExtra("booktitle",BookTitle);
                        intent.putExtra("position",BookPosition);
                        intent.putExtra("PhotoUri", imageUri);
                        String imagePath = getPhotoPathFromGallery(imageUri).toString();
                        File imageFile = new File(imagePath);
                        if(imageFile.exists())
                        {
                            //Toast.makeText(getApplicationContext(),"File loaded",Toast.LENGTH_SHORT).show();
                            intent.putExtra("photoPath",imagePath);
                            startActivity(intent);
                            Log.d("TAG", "onActivityResult: "+imageUri.getPath());
                        }
                        else
                        {
                            Snackbar.make(coordinatorlayout,"File Doesn't Exists",Snackbar.LENGTH_SHORT).show();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();

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


    public String getPhotoPathFromGallery(Uri uri) {
        if (uri == null) {
            // TODO perform some logging or show user feedback
            return null;
        }

        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null); //Since manageQuery is deprecated
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }

        return uri.getPath();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            switch (message) {
                case "snippet_added_snippets_activity":
                    recyclerview1.scrollToPosition(mAdapter.getItemCount()-1);
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.delete_action_list, popup.getMenu());
        popup.show();


        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.snippetDelete:
                        deleteItem();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void deleteItem() {

        try
        {
            RealmList<Snippet> snippetslist = getSortedBooks();
            Snippet snippet = snippetslist.get(Position);
            String Imagepath = snippet.getImagePath();
            File imageFile = new File(Imagepath);
            if(imageFile.exists())
            {
                imageFile.delete();
            }
            mAdapter.deleteBook(realm,Position);
            Snackbar.make(coordinatorlayout,"Snippet Deleted",Snackbar.LENGTH_SHORT).show();
            int noOfSnippets = book.getSnippetsList().size();
            if(noOfSnippets==0)
            {
                snippetOutlineImg.setVisibility(View.VISIBLE);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}
