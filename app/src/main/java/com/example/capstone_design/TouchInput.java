package com.example.capstone_design;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import com.example.Touch.TouchEvent;
import com.example.Touch.TouchPoint;


import static  com.example.capstone_design.FloatingViewService.num;
import static  com.example.capstone_design.FloatingViewService.x_data_list;
import static  com.example.capstone_design.FloatingViewService.y_data_list;
import static  com.example.capstone_design.FloatingViewService.now_time_list;

//터치를 입력받을 때 사용하는 클래스 -> Touch_in 서비스에 필요한 값들을 넘겨준다
@SuppressLint("ClickableViewAccessibility")
public class TouchInput extends Activity implements View.OnClickListener {

    public static Activity TouchInput;
    private float x_data;
    private float y_data;
    private long now_time;
    public static TouchPoint touchPoint;
    MacroDBHelper helper;
    SQLiteDatabase db = null;
    View view;

    @Override
    protected void onStart() {
        super.onStart();
        //터치이벤트 정지상태
        TouchEvent.postPauseAction();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty2);

        TouchInput = TouchInput.this;

        helper = new MacroDBHelper(this);
        db = helper.getWritableDatabase();
        view = findViewById(R.id.transparent_view);
        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                x_data = (float) event.getRawX(); // x좌표
                y_data = (float) event.getRawY(); // y좌표
                now_time = SystemClock.currentThreadTimeMillis();


                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {

                        ++num;

//                        String sql = "INSERT INTO Act (Act_x, Act_y) VALUES (?, ?)";
//                        String[] arg = {Float.toString(x_data), Float.toString(y_data)};

                        try {
                            x_data_list.add(x_data);
                            y_data_list.add(y_data);
                            now_time_list.add(now_time);

                            Log.d("리스트에 저장된 내용", "잘 저장됐는지 점검");
                            System.out.println(x_data_list);
                            System.out.println(y_data_list);
                            System.out.println(now_time_list);

//                            db.execSQL(sql, arg);
//                            final Toast toast1 = Toast.makeText(getApplicationContext(), num + "번", Toast.LENGTH_SHORT);
//                            toast1.show();
//                            new CountDownTimer(500, 500) {
//                                public void onTick(long millisUntillFinished) {
//                                    toast1.show();
//                                }
//
//                                public void onFinish() {
//                                    toast1.show();
//                                }
//                            }.start(); // 토스트 1초만 뜨도록 설정

                            // 터치 신호를 입력받을 때 좌표값을 변수에 한시적으로 저장하여 화면상에 터치할 좌표정보를 전달한다.
                            touchPoint = new TouchPoint(x_data, y_data, 0);

                            Log.d("터치" + num, "터치" + num);
                            System.out.println(event);
                            //onTouchEvent(event);

                        } catch (Exception e) {
                            final Toast toast1 = Toast.makeText(getApplicationContext(), "입력 불가 오류발생", Toast.LENGTH_SHORT);
                            toast1.show();
                        }
                        return false;


                    }

                    case MotionEvent.ACTION_UP: {
                        // 터치를 떼는 시점에 기능을 추가하고자 할경우를 위해 남겨놓음
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
                finish();
                break;
        }
    }
}

