package com.example.capstone_design;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.Touch.TouchEvent;
import com.example.Touch.TouchPoint;
import com.example.TouchService.Touch;
import com.example.adapter.TouchPointAdapter;




@SuppressLint("ClickableViewAccessibility")
public class TouchInput2 extends Activity implements View.OnClickListener {

    //TextView textView;
    MacroDBHelper helper;
    SQLiteDatabase db = null;
    public static TouchPoint touchPoint;
    View view;
    public static int x_data;
    public static int y_data;
    public static long downTime;
    public static long eventTime;
    public static int action;
    public static int num;
    public static MotionEvent event;

    private TouchPointAdapter touchPointAdapter;

    @Override
    protected void onStart() {
        super.onStart();
        //ToastUtil.show("터치입력 시작");
        TouchEvent.postPauseAction();

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty2);

        helper = new MacroDBHelper(this);
        db = helper.getWritableDatabase();
        num = 0;

        view = findViewById(R.id.transparent_view);
        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                x_data = (int) event.getRawX(); // x좌표
                y_data = (int) event.getRawY(); // y좌표
                int transparent = v.getId();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {

                        num++;

                        String sql = "INSERT INTO Act (Act_x, Act_y) VALUES (?, ?)";
                        String[] arg = {Float.toString(x_data), Float.toString(y_data)};

                        try {
                            db.execSQL(sql, arg);
                            final Toast toast1 = Toast.makeText(getApplicationContext(), num + "번", Toast.LENGTH_SHORT);
                            toast1.show();
                            new CountDownTimer(500, 500) {
                                public void onTick(long millisUntillFinished) {
                                    toast1.show();
                                }

                                public void onFinish() {
                                    toast1.show();
                                }
                            }.start(); // 토스트 1초만 뜨도록 설정


                            System.out.println(x_data);
                            System.out.println(y_data);
                            touchPoint = new TouchPoint(x_data, y_data, 0);


                            Log.d("터치" + num, "터치" + num);
                            System.out.println(event);
                            //onTouchEvent(event);

//                            /////////////////////////////
//                            if (transparent == R.id.testing2) {
//                                Intent intent = new Intent(TouchInput2.this, Touch.class);
//                                startService(intent);
//                                System.out.println(intent);
//                                System.out.println(transparent);
//                                System.out.println(R.id.testing2);
//                            }

                        } catch (Exception e) {
                            final Toast toast1 = Toast.makeText(getApplicationContext(), "입력 불가 오류발생", Toast.LENGTH_SHORT);
                            toast1.show();
                        }
                        return false;


                    }


//                    case MotionEvent.ACTION_MOVE: {
//                        printS("손가락 움직임 : "+x_data+", "+y_data);
//                        return true;
//                    }
//
                    case MotionEvent.ACTION_UP: {
//                        SystemClock.sleep(2000);
                        return false;
                    }

                    default: return false;
                }
            }
        });

    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.transparent_view :
                TouchEvent.postStartAction(touchPoint);
                Log.d("터치 서비스 시작", "터치 서비스 시작");
                startService(new Intent(TouchInput2.this, Touch.class));
                finish();
                break;
        }

//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//            TouchEvent.postStartAction(touchPoint);
//            startService(new Intent(TouchInput2.this, Touch2.class));
//            finish();
//
//        } else if (Settings.canDrawOverlays(this)) {
//            TouchEvent.postStartAction(touchPoint); //값이 들어가는 부분.
//            startService(new Intent(TouchInput2.this, Touch2.class));
//            finish();
//
//        } else {
//            Toast.makeText(this, "진입 불가", Toast.LENGTH_SHORT).show();
//        }
    }


}

