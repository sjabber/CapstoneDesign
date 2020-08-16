package com.example.capstone_design;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
//import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;


public class TouchInput2 extends Activity {

    //TextView textView;
    MacroDBHelper helper;
    SQLiteDatabase db = null;
    public static float x_data;
    public static float y_data;
    public static long interval;
    public static long downTime;
    public static long eventTime;
    public static int action;
    public static int num;
    public static MotionEvent event;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty);

        //textView = findViewById(R.id.textView3);

        helper = new MacroDBHelper(this);
        db = helper.getWritableDatabase();

        final View view = findViewById(R.id.testing);


        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                x_data = event.getX(); // x좌표
                y_data = event.getY(); // y좌표
                num += 1;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        printS("손가락 눌림 : "+x_data+", "+y_data);

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

                            downTime = SystemClock.uptimeMillis();
                            eventTime = SystemClock.uptimeMillis();
                            action = MotionEvent.ACTION_DOWN;
                            interval = downTime - eventTime; //시간 간격정보를 저장한다.

                            System.out.println(x_data);
                            System.out.println(y_data);

                            event = MotionEvent.obtain(downTime, eventTime, action, x_data, y_data, 0);
                            Log.d("터치1", "터치1");
                            System.out.println(event);
                            //onTouchEvent(event);


//                            Intent intent = new Intent(TouchInput2.this, TouchOutput.class);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);

                            //finish();






                        } catch (Exception e) {
                            final Toast toast = Toast.makeText(getApplicationContext(), "입력 불가 오류발생", Toast.LENGTH_SHORT);
                            toast.show();
                        }




                        return true;


                    }

                    case MotionEvent.ACTION_MOVE: {
                        printS("손가락 움직임 : "+x_data+", "+y_data);
                        return true;
                    }

                    case MotionEvent.ACTION_UP: {
                        printS("손가락 뗌 : "+x_data+", "+y_data);
                        return false;
                    }

                    default: return false;
                }
            }
        });
    }

    public void printS(String s) {
        Toast toast = Toast.makeText(this, s + "\n", Toast.LENGTH_SHORT);
        toast.show();
//        textView.append(s+"\n");
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        float x_data = event.getX(); // x좌표
//        float y_data = event.getY(); // y좌표
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN: {
//
//                printS("손가락 눌림 : "+x_data+", "+y_data);
//
//                downTime = SystemClock.uptimeMillis();
//                eventTime = SystemClock.uptimeMillis();
//                action = MotionEvent.ACTION_DOWN;
//
//                interval = downTime - eventTime; //시간 간격정보를 저장한다.
//
//                event = MotionEvent.obtain(downTime, eventTime, action, x_data, y_data, 0);
//                onTouchEvent(event);
//
//                System.out.println(x_data);
//                System.out.println(y_data);
//                return true;
//            }
//
//            case MotionEvent.ACTION_MOVE: {
//                printS("손가락 움직임 : "+x_data+", "+y_data);
//                return true;
//            }
//
//            case MotionEvent.ACTION_UP: {
//                printS("손가락 뗌 : "+x_data+", "+y_data);
//                return false;
//            }
//
//            default:
//                return false;
//        }

}

