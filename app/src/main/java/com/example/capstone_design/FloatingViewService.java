package com.example.capstone_design;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import static com.example.capstone_design.AddActivity.inputedName; //AddActivity 에서 입력받은 값을 저장해놓은 변수
import androidx.core.content.IntentCompat;


public class FloatingViewService extends Service implements View.OnClickListener {

    private WindowManager mWindowManager;
    private View mFloatingView;
    private View collapsedView;
    private View expandedView;
    private Intent intent;
    private Intent intent2;
    MacroDBHelper helper;
    SQLiteDatabase db = null;
    private final String pakageName = "com.example.capstone_design";


    public FloatingViewService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        helper = new MacroDBHelper(this);
        db = helper.getWritableDatabase();

        //레이아웃 인플레이터로 xml에서 위젯 레이아웃을 가져온다.
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.activity_floating_widget, null);

        //레이아웃 파라미터 설정
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        //Floating window의 시작위치 조정
        params.gravity = Gravity.CENTER | Gravity.TOP;
        params.x -= 600; //중앙 0으로부터 -650만큼 이동한 위치에서 시작(값을 너무 크게주면 매끄럽지 못함)


        //Windows 서비스 받기 및 floating view 추가
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);


        //floating view 축소, 확대 보기
        collapsedView = mFloatingView.findViewById(R.id.layoutCollapsed);
        expandedView = mFloatingView.findViewById(R.id.layoutExpanded);

        //buttonStop 을 눌렀을 때 이 어플리케이션이 다시 실행되도록 하기 위한 Intent 값 저장객체
        //intent = this.getPackageManager().getLaunchIntentForPackage(pakageName);



        //클릭리스너 추가하여 닫기버튼이나 확장 view 적용
        //버튼같은경우 여기에 추가적으로 추가할 필요성이 있다.
        mFloatingView.findViewById(R.id.buttonClose).setOnClickListener(this); //x버튼 누르면 꺼지도록
        mFloatingView.findViewById(R.id.buttonStop).setOnClickListener(this); //매크로 저장버튼
        mFloatingView.findViewById(R.id.buttonStart).setOnClickListener(this); // 매크로 녹화시작버튼
        mFloatingView.findViewById(R.id.buttonBack).setOnClickListener(this); //뒤로가기버튼
        mFloatingView.findViewById(R.id.buttonClose2).setOnClickListener(this); //x버튼 누르면 꺼지도록


        //collapsedView.setOnClickListener(this); // 추가된 내용 -> 누르면 꺼짐
        //this : 이 클래스 자체, FloatingViewService 를 지칭한다.
        expandedView.setOnClickListener(this); // 누르면 꺼지게 설계되어있음.

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
                        initialX = params.x; // 뷰의 시작점 x좌표
                        initialY = params.y; // 뷰의 시작점 y좌표
                        initialTouchX = event.getRawX(); //터치 시작점 x좌표 (내가 터치한 x 좌표값)
                        initialTouchY = event.getRawY(); //터치 시작점 y좌표 (내가 터치한 y 좌표값)

                        //좌표값 테스트용
                        //int x = params.x;
                        //int y = params.y;
                        //Toast toast = Toast.makeText(FloatingViewService.this, "좌표값 :" + x + ", "+ y , Toast.LENGTH_SHORT);
                        //toast.show();
                        //return을 false로 하면 제대로 작동하지를 않음..
                        return true; //false : 다른 이벤트도 작동할 수 있게 하기위함

                    case MotionEvent.ACTION_UP:
                        //드래그상태 종료, 눌렀다 땠을때 위젯상태변화
                        expandedView.setVisibility(View.VISIBLE);
                        collapsedView.setVisibility(View.GONE);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        //누르고 움직였을 때
                        //이 코드가 위젯이 손가락만으로 화면주위를 움직일 수 있게 도와줌
                        params.x = initialX + (int) (event.getRawX() - initialTouchX); //그 후 움직이면 dx, dy에 의해 얼마만큼 이동했는지에 대한 값이 갱신된다.
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return true;
            }
        });
    }
//    class ClickWindow implements View.OnClickListener{
//
//    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            //이 두가지경우 똑같은 일을 수행하므로 묶는다.
            case R.id.buttonClose:

            case R.id.buttonClose2:
                Toast toast1 = Toast.makeText(FloatingViewService.this, "취소됐습니다.", Toast.LENGTH_SHORT);
                toast1.show();
                //뷰를 종료한다.
                stopSelf();
                break;

            //버튼 클릭시 레이아웃 화면 확장, 변환
            case R.id.buttonBack:
                //뷰를 이전으로 되돌린다.
                collapsedView.setVisibility(View.VISIBLE);
                expandedView.setVisibility(View.GONE);
                break;
            //뷰를 종료한다.

            case R.id.buttonStop:
                String sql = "INSERT INTO Macro (Mac_name, ACTIVE) VALUES (?, ?)";
                String[] arg = {inputedName, Integer.toString(0)};

                try{
                    db.execSQL(sql, arg);
                    Toast toast = Toast.makeText(getApplicationContext(), "저장되었습니다.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.LEFT|Gravity.BOTTOM, 330, 180);
                    toast.show();
                }
                catch (Exception e) {
                    Log.d("Problem1", "쿼리문제 발생지점");
                    Toast.makeText(getApplicationContext(), "데이터베이스 오류1", Toast.LENGTH_SHORT).show();
                }

                // 앱을 재실행시켜 메인화면으로 이동
                intent = new Intent(this, MainActivity.class);
                ComponentName cn = intent.getComponent();
                intent2 = Intent.makeRestartActivityTask(cn);
                startActivity(intent2); //자기자신을 재실행한다.

                stopSelf(); //위 작업을 마치고 뷰를 종료함.
                break;

            case R.id.buttonStart:
                //핵심 기능이 완성되면 여기서 구동되도록 실현시킨다.
                Toast toast3 = Toast.makeText(FloatingViewService.this, "매크로 녹화기능 미구현", Toast.LENGTH_SHORT);
                toast3.show();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }

}