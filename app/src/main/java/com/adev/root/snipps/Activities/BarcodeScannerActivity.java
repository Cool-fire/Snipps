package com.adev.root.snipps.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.adev.root.snipps.model.NewApiInterface;
import com.adev.root.snipps.model.NewBookApiclient;
import com.adev.root.snipps.model.NewBookModel;
import com.adev.root.snipps.presenter.BarcodescannerPresenter;
import com.adev.root.snipps.utils.Item;
import com.adev.root.snipps.utils.NewBookUtil;
import com.adev.root.snipps.view.BarcodescannerView;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zbar.ZBarScannerView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BarcodeScannerActivity extends AppCompatActivity implements BarcodescannerView,ZBarScannerView.ResultHandler{

    private static final String TAG = "SCAN_RESULT" ;
    private ZXingScannerView mScannerView;
    private Context context;
    private static final int REQUEST_CAMERA = 1000;
    private BarcodescannerPresenter presenter;
    private List<Item> items  = new ArrayList<>();
    private NewBookUtil newBookUtil;
    private ZBarScannerView mScanner;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       //setContentView(R.layout.activity_barcode_scanner);

       // mScannerView = new ZXingScannerView(BarcodeScannerActivity.this);   // Programmatically initialize the scanner view

        mScanner = new ZBarScannerView(BarcodeScannerActivity.this);
        //setContentView(mScannerView);

        setContentView(mScanner);

        context = getApplicationContext();

        NewBookModel model = new NewBookModel();

        BarcodescannerView view = this;
        presenter = new BarcodescannerPresenter(view,context,model);



        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(presenter.checkforPermissions())
            {

              //  mScannerView.setFlash(true);
               // Toast.makeText(getApplicationContext(),"Permission Granted", Toast.LENGTH_SHORT).show();

            }
            else
            {
                 AskForPermissions();
            }

        }



    }


    @Override
    public void onResume() {
        super.onResume();
        startScan();
       
//        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
//        mScannerView.startCamera();     // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
       // mScannerView.stopCameraPreview();
        mScanner.stopCameraPreview();
                // Stop camera on pause
    }

    @Override
    protected void onStop() {
        super.onStop();
       // mScannerView.stopCamera();
        mScanner.stopCamera();
        if(thread != null)
        {
            thread.interrupt();
        }

    }

    private void getBookDetailsByIsbn(String isbn) {


        NewApiInterface apiService =
                NewBookApiclient.getClient().create(NewApiInterface.class);

        Call<NewBookUtil> call = apiService.getBookDetails(isbn);

        call.enqueue(new Callback<NewBookUtil>() {
            @Override
            public void onResponse(Call<NewBookUtil> call, Response<NewBookUtil> response) {
                newBookUtil = response.body();
                handleResultofBookDetails(newBookUtil);
            }

            @Override
            public void onFailure(Call<NewBookUtil> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"No Internet",Toast.LENGTH_SHORT).show();
                finish();

            }
        });


    }


    private void handleResultofBookDetails(NewBookUtil newBookUtil) {

        if(newBookUtil.getItems()!=null)
        {
            final String BookTitle = newBookUtil.getItems().get(0).getVolumeInfo().getTitle();
            final String BookAuthor = newBookUtil.getItems().get(0).getVolumeInfo().getAuthors().get(0);

            thread = new Thread(new Runnable() {
                @Override
                public void run() {

                    if(!thread.isInterrupted())
                    {
                        Intent output = new Intent();
                        output.putExtra("BookTitle", BookTitle);
                        output.putExtra("BookAuthor", BookAuthor);
                        setResult(RESULT_OK, output);
                        finish();

                    }

                }
            });
            thread.start();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"No Results found",Toast.LENGTH_SHORT).show();
            finish();
        }
    
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case REQUEST_CAMERA:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    startScan();
                }
                else
                {
                    Toast.makeText(BarcodeScannerActivity.this,"Permission Denied",Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }
    }


    @Override
    public void AskForPermissions() {
        ActivityCompat.requestPermissions(BarcodeScannerActivity.this,new String[]{Manifest.permission.CAMERA},REQUEST_CAMERA);
    }

    @Override
    public void startScan() {
        mScanner.setResultHandler(this); // Register ourselves as a handler for scan results.
        new Thread(new Runnable() {
            @Override
            public void run() {
                mScanner.startCamera();
                mScanner.findFocus();
            }
        }).start();

//        mScannerView.setAutoFocus(true);

    }


    @Override
    public void handleResult(me.dm7.barcodescanner.zbar.Result result) {
        String isbnNo = result.getContents();
        String isbn = "isbn:"+isbnNo;
        //mScannerView.stopCamera();
        //mScannerView.resumeCameraPreview(this);

        getBookDetailsByIsbn(isbn);

    }
}
