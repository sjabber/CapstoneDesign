package com.hknu.adapter;

import android.content.Context;
import android.view.View;

import com.hknu.Touch.TouchPoint;

import static com.hknu.capstone_design.TouchInput.touchPoint;

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