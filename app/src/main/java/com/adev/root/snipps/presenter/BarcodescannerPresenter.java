package com.adev.root.snipps.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.adev.root.snipps.model.NewApiInterface;
import com.adev.root.snipps.model.NewBookApiclient;
import com.adev.root.snipps.model.NewbookModelInterface;
import com.adev.root.snipps.utils.Item;
import com.adev.root.snipps.utils.NewBookUtil;
import com.adev.root.snipps.view.BarcodescannerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.support.constraint.Constraints.TAG;

public class BarcodescannerPresenter {

    private final BarcodescannerView view;
    private final Context context;
    private final NewbookModelInterface model;



    public BarcodescannerPresenter(BarcodescannerView view, Context context, NewbookModelInterface model) {

        this.view = view;
        this.context = context;
        this.model = model;

    }


    public boolean checkforPermissions() {

        return (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED);
    }



}
