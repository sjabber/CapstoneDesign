package com.example.capstone_design;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import static com.example.capstone_design.TouchInput2.x_data;
import static com.example.capstone_design.TouchInput2.y_data;
import static com.example.capstone_design.TouchInput2.downTime;
import static com.example.capstone_design.TouchInput2.eventTime;
import static com.example.capstone_design.TouchInput2.action;
import static com.example.capstone_design.TouchInput2.event;

public class TouchOutput extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty2);

        View view = findViewById(R.id.testing2);


        downTime = SystemClock.uptimeMillis();
        eventTime = SystemClock.uptimeMillis();
        event = MotionEvent.obtain(downTime, eventTime, action, x_data, y_data, 0);
        onTouchEvent(event);

        System.out.println(event);
        Log.d("터치2", "터치2");
        System.out.println(event);

        Intent intent = new Intent(TouchOutput.this, TouchInput2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }
}
