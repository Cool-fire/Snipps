package com.adev.root.snipps.presenter;

import android.content.Context;
import android.os.Environment;

import com.adev.root.snipps.Activities.SnippetActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SnippetActivityPresenter {


    private final SnippetActivity view;
    private final Context context;

    public SnippetActivityPresenter(SnippetActivity view, Context context) {
        this.view = view;
        this.context = context;
    }

}
