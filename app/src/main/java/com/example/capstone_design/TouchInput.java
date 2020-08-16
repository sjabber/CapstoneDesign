package com.example.capstone_design;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;
import androidx.core.view.GestureDetectorCompat;
import java.util.ArrayList;


public class TouchInput extends Activity implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener{

    MacroDBHelper helper;
    SQLiteDatabase db = null;
    long interval;
    long downTime;
    long eventTime;
    int action;

    private static final String DEBUG_TAG = "Gestures";
    private GestureDetectorCompat mDetector;

    int num = 1;

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty);

        helper = new MacroDBHelper(this);
        db = helper.getWritableDatabase();

        // Instantiate the gesture detector with the
        // application context and an implementation of
        // GestureDetector.OnGestureListener
        // empty(투명) 레이아웃 실행
        mDetector = new GestureDetectorCompat(this,this);
        // Set the gesture detector as the double tap
        // listener.
        mDetector.setOnDoubleTapListener(this);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (this.mDetector.onTouchEvent(event)) {

//            float x_data = event.getX(); // x좌표
//            float y_data = event.getY(); // y좌표
//
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN: {
//
//                    printS("손가락 눌림 : "+x_data+", "+y_data);
//
//                    downTime = SystemClock.uptimeMillis();
//                    eventTime = SystemClock.uptimeMillis();
//                    action = MotionEvent.ACTION_DOWN;
//
//                    interval = downTime - eventTime; //시간 간격정보를 저장한다.
//
//                    event = MotionEvent.obtain(downTime, eventTime, action, x_data, y_data, 0);
//                    onTouchEvent(event);
//
//                    System.out.println(x_data);
//                    System.out.println(y_data);
//                    return true;
//                }
//
//                case MotionEvent.ACTION_MOVE: {
//                    printS("손가락 움직임 : "+x_data+", "+y_data);
//                    return true;
//                }
//
//                case MotionEvent.ACTION_UP: {
//                    printS("손가락 뗌 : "+x_data+", "+y_data);
//                    return false;
//                }
//
//                default: return false;
//            }
            return true;
        }
        return super .onTouchEvent(event);
    }

    //단순 터치정보
    @Override
    public boolean onDown(MotionEvent event) { //리스트에 단순터치정보 저장
        Log.d(DEBUG_TAG,"onDown: " + event.toString());
        float x_data = event.getX(); // x좌표
        float y_data = event.getY(); // y좌표

        String sql = "INSERT INTO Act (Act_x, Act_y) VALUES (?, ?)";
        String[] arg = {Float.toString(x_data), Float.toString(y_data)};

            try {
                db.execSQL(sql, arg);
                final Toast toast = Toast.makeText(getApplicationContext(), num + "번", Toast.LENGTH_SHORT);
                toast.show();
                new CountDownTimer(1000, 1000) {
                    public void onTick(long millisUntillFinished) {toast.show();}
                    public void onFinish() {toast.show();}
                }.start(); // 토스트 1초만 뜨도록 설정

                System.out.println(x_data);
                System.out.println(y_data);

            } catch (Exception e) {
                final Toast toast = Toast.makeText(getApplicationContext(), "입력 불가 오류발생", Toast.LENGTH_SHORT);
                toast.show();
            }
            num += 1;


        finish();
        return true;
    }


    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
//        Log.d(DEBUG_TAG, "onFling: " + event1.toString() + event2.toString());
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
//        Log.d(DEBUG_TAG, "onLongPress: " + event.toString());
    }

    @Override
    public boolean onScroll(MotionEvent event1, MotionEvent event2, float distanceX,
                            float distanceY) {
//        Log.d(DEBUG_TAG, "onScroll: " + event1.toString() + event2.toString());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent event) {
//        Log.d(DEBUG_TAG, "onShowPress: " + event.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
//        Log.d(DEBUG_TAG, "onSingleTapUp: " + event.toString());
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
//        Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
//        Log.d(DEBUG_TAG, "onDoubleTapEvent: " + event.toString());
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
//        Log.d(DEBUG_TAG, "onSingleTapConfirmed: " + event.toString());
        return true;
    }

    public void printS(String s) {
        Toast toast = Toast.makeText(getApplicationContext(), s+"\n", Toast.LENGTH_SHORT);
        toast.show();
    }
}

