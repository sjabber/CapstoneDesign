package com.example.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.example.Touch.TouchPoint;

import static com.example.capstone_design.TouchInput.touchPoint;

public class TouchPointAdapter extends View implements View.OnClickListener {

    private OnClickListener onClickListener;

    public TouchPointAdapter(Context context) {
        super(context);
    }


    @Override
    public void onClick(View v) {
        if (onClickListener != null) {
            TouchPoint touchPoint_ = touchPoint;
            onClickListener.onClickListener(v, touchPoint_);
//            Log.d("브레이크포인트_1", "브레이크포인트_1");
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClickListener(View view, TouchPoint touchPoint);
    }



}