package com.adev.root.snipps.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.adev.root.snipps.R;

public class OcrshowActivity extends AppCompatActivity {

    private TextView TextField;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocrshow);
        Intent intent = getIntent();
        String RecognisedText = intent.getStringExtra("RecognisedText");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Recognised Text");
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);


        setupviews();
        TextField.setText(RecognisedText);
        TextField.setMovementMethod(new ScrollingMovementMethod());
        TextField.setTextIsSelectable(true);
    }

    private void setupviews() {
        TextField =  (TextView)findViewById(R.id.body);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
