package com.adev.root.snipps.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.adev.root.snipps.R;
import com.bumptech.glide.Glide;
import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.SaveCallback;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CropActivity extends AppCompatActivity {

    private CropImageView mCropView;
    private Bitmap bitmap;
    private Uri imageUri;
    private boolean rotation = false;
    private File imageFile;
    private FloatingActionButton doneFabBttn;
    private File croppedImageFile;
    private Uri croppedImageUri;
    private ProgressBar progressBar;
    private FloatingActionButton rotateBttn;
    private String BookPosition;
    private String BookTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);

        Intent intent = getIntent();
        Uri photoUri = getIntent().getParcelableExtra("PhotoUri");
        BookPosition = getIntent().getStringExtra("position");
        BookTitle = getIntent().getStringExtra("booktitle");

        String path = getIntent().getStringExtra("photoPath");
        imageFile = new File(path);

        setupviews();
        if(photoUri == null)
        {
            photoUri = Uri.fromFile(imageFile);
        }

        Glide.with(this).load(photoUri).into(mCropView);
        mCropView.setHandleShowMode(CropImageView.ShowMode.SHOW_ALWAYS);
        mCropView.setGuideShowMode(CropImageView.ShowMode.SHOW_ON_TOUCH);
        mCropView.setInitialFrameScale(0.75f);
        mCropView.setCropMode(CropImageView.CropMode.FREE);
        doneFabBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                croppedImageFile = create_CroppedImageFile(mCropView.getCroppedBitmap());
                if (croppedImageFile != null) {
                    croppedImageUri = Uri.fromFile(croppedImageFile);
                }

                progressBar.setVisibility(View.VISIBLE);
                mCropView.saveAsync(croppedImageUri, mCropView.getCroppedBitmap(), new SaveCallback() {
                    @Override
                    public void onSuccess(Uri uri) {
                        progressBar.setVisibility(View.GONE);
                        Intent intent = new Intent(CropActivity.this,AddSnippetActivity.class);
                        intent.putExtra("croppedPath",croppedImageFile.getPath());
                        intent.putExtra("position",BookPosition);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                                e.printStackTrace();
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

        rotateBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try
                {
                    mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setupviews() {
        mCropView = findViewById(R.id.cropImageView);
        doneFabBttn = findViewById(R.id.doneBTN);
        rotateBttn = findViewById(R.id.rotateImageBTN);
        progressBar = findViewById(R.id.progressBarDialog);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        rotation=false;
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        rotation = true;
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isFinishing())
        {
            imageFile.delete();
        }


    }

    public File create_CroppedImageFile(Bitmap inImage) {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp+".jpg";

        File mediaStorageDir = getExternalFilesDir("Pictures/Snipps/");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        File f = new File(mediaStorageDir.getPath(), imageFileName);

        try {
            f.createNewFile();
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            inImage.compress(Bitmap.CompressFormat.PNG, 0, bos);
//            byte[] bitmapdata = bos.toByteArray();
//
//            FileOutputStream fos;
//            fos = new FileOutputStream(f);
//            fos.write(bitmapdata);
//            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return f;
    }
}

