package com.example.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.capstone_design.R;
import com.example.Touch.TouchPoint;
import java.util.List;

import static com.example.capstone_design.TouchInput2.touchPoint;

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
            Log.d("브레이크포인트_1", "브레이크포인트_1");
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClickListener(View view, TouchPoint touchPoint);
    }



}