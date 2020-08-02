package com.example.capstone_design;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

public class FloatingViewService extends Service implements View.OnClickListener {

    private WindowManager mWindowManager;
    private View mFloatingView;
    private View collapsedView;
    private View expandedView;
    private View RecordView;

    public FloatingViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        //레이아웃 인플레이터로 xml에서 위젯 레이아웃을 가져온다.
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.activity_floating_widget, null);

        //레이아웃 파라미터 설정
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Windows 서비스 받기 및 floating view 추가
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);



        //floating view 축소, 확대 보기
        collapsedView = mFloatingView.findViewById(R.id.layoutCollapsed);
        expandedView = mFloatingView.findViewById(R.id.layoutExpanded);
        //RecordView = mFloatingView.findViewById(R.id.);


        //클릭리스너 추가하여 닫기버튼이나 확장 view 적용
        //버튼같은경우 여기에 추가적으로 추가할 필요성이 있다.
        mFloatingView.findViewById(R.id.buttonClose).setOnClickListener(this);
        expandedView.setOnClickListener(this);
        //+



        //플로팅 위젯의 드래그 이동을 위한 터치리스너
        mFloatingView.findViewById(R.id.relativeLayoutParent).setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //처음 눌렸을 때
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();

                        return true;
//
                    case MotionEvent.ACTION_UP:
                        //드래그상태 종료, 눌렀다 땠을때 위젯상태변화
                        expandedView.setVisibility(View.VISIBLE);
                        collapsedView.setVisibility(View.GONE);

                        return true;

                    case MotionEvent.ACTION_MOVE:
                        //누르고 움직였을 때
                        //이 코드가 위젯이 손가락만으로 화면주위를 움직일 수 있게 도와줌
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //버튼 클릭시 레이아웃 화면 확장, 변환
            case R.id.layoutExpanded:
                //뷰를 전환한다.
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);

                break;

            case R.id.buttonRecord:
                expandedView.setVisibility(View.GONE);
                collapsedView.setVisibility(View.VISIBLE);

            //위젯 종료
            case R.id.buttonClose:
                //뷰를 종료한다.
                stopSelf();
                break;

            case R.id.buttonStop:
                //뷰를 종료한다.
                Intent intent = new Intent(FloatingViewService.this, MainActivity.class);
                startActivity(intent);
                Toast toast = Toast.makeText(getApplicationContext(), "터치패턴 저장되었음", Toast.LENGTH_SHORT);
                toast.show();
                break;

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }
}