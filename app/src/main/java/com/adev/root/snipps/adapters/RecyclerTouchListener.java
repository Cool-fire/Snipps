package com.adev.root.snipps.adapters;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.adev.root.snipps.R;

import static android.support.constraint.Constraints.TAG;

public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

    private final Clicklistner clicklistner;
    private final GestureDetector gestureDetector;


    public RecyclerTouchListener(final Context context, final RecyclerView recyclerView, final Clicklistner clicklistner) {
        this.clicklistner = clicklistner;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {

//                View child = recyclerView.findChildViewUnder(e.getX(),e.getY());
//                if (child != null && clicklistner != null) {
//                    clicklistner.onclick(child, recyclerView.getChildAdapterPosition(child));
//                }
                return true;
            }
            @Override
            public boolean onDoubleTap(MotionEvent e) {
//                View child = recyclerView.findChildViewUnder(e.getX(),e.getY());
//                if (child != null && clicklistner != null) {
//                    clicklistner.onDoubleTap(child, recyclerView.getChildAdapterPosition(child));
//                }
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if (child != null && clicklistner != null) {
                    clicklistner.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                }
            }


        });



    }



    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

        View child = rv.findChildViewUnder(e.getX(),e.getY());


        if(child!=null && clicklistner!=null && gestureDetector.onTouchEvent(e) )
        {
            clicklistner.onclick(child,rv.getChildAdapterPosition(child));
        }





        return false;

    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public interface Clicklistner{
        void onclick(View view,int position);

        void onLongClick(View view,int position);

        void onDoubleTap(View view,int position);
        }
}
