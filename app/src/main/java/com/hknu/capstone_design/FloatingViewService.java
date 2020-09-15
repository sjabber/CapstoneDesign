package com.hknu.capstone_design;


import android.app.Service;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import java.util.ArrayList;


import static com.hknu.capstone_design.AddActivity.inputedName; //AddActivity 에서 입력받은 값을 저장해놓은 변수
import static com.hknu.capstone_design.MainActivity.Mac_number;



public class FloatingViewService extends Service implements View.OnClickListener {

    public static int num;
    public static ArrayList<Float> x_data_list = new ArrayList<Float>(); //좌표값을 임시저장할 리스트
    public static ArrayList<Float> y_data_list = new ArrayList<Float>();
    public static ArrayList<Long> now_time_list = new ArrayList<Long>();

    private WindowManager mWindowManager;
    private View mFloatingView;
    private View collapsedView;
    private View expandedView;
    private int LAYOUT_FLAG;
    private Intent intent2;
    MacroDBHelper helper;
    MacroDBHelper helper2;


    SQLiteDatabase db = null;
    SQLiteDatabase DB = null;


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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }
        //레이아웃 파라미터 설정
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
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



    //@RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
            switch (v.getId()) {

            //이 두가지경우 똑같은 일을 수행하므로 묶는다.
            case R.id.buttonClose:

            case R.id.buttonClose2:

                //취소됐으므로 ArrayList 에 임시로 들어간 값들을 전부 삭제한다.
                x_data_list.clear();
                y_data_list.clear();
                now_time_list.clear();
                TouchInput touchInput = (TouchInput) TouchInput.TouchInput; // 저장버튼을 누르면 TouchInput 을 완전히 종료하기 위함
                if(touchInput != null) {
                    touchInput.finish(); //TouchInput Activity 종료
                }

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

            case R.id.buttonStart:

                 x_data_list.clear();
                 y_data_list.clear();
                 now_time_list.clear();

                 //핵심 기능이 완성되면 여기서 구동되도록 실현시킨다.
                 Intent intent = new Intent(FloatingViewService.this, TouchInput.class);
                 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                 startActivity(intent);
                 break;

            case R.id.buttonStop:
                String sql = "INSERT INTO Macro (Mac_name, ACTIVE) VALUES (?, ?)";
                String[] arg = {inputedName, Integer.toString(0)};

                try{
                    // TODO: 저장버튼을 누른 시점에 좌표값이 저장되도록 구현한 부분
                    for(int i = 0; i < x_data_list.size(); i++) {
                        insertData(Mac_number+1, x_data_list.get(i), y_data_list.get(i), now_time_list.get(i));
                    }
                    db.execSQL(sql, arg);

                    Toast toast = Toast.makeText(getApplicationContext(), "저장되었습니다.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.LEFT|Gravity.BOTTOM, 330, 180);
                    toast.show();

                    x_data_list.clear();
                    y_data_list.clear();
                    now_time_list.clear();

                    TouchInput TI = (TouchInput) TouchInput.TouchInput; // 저장버튼을 누르면 TouchInput 을 완전히 종료하기 위함
                    if(TI != null) {
                        TI.finish(); //TouchInput Activity 종료
                    }
                    Log.d("저장버튼", "매크로 정보 저장완료");
                }
                catch (Exception e) {
                    Log.d("저장버튼 문제", "쿼리문제 발생지점");
                    Toast.makeText(getApplicationContext(), "데이터베이스 오류1", Toast.LENGTH_SHORT).show();
                }


                // 앱을 재실행시켜 메인화면으로 이동
                intent = new Intent(this, MainActivity.class);
                ComponentName cn = intent.getComponent();
                intent2 = Intent.makeRestartActivityTask(cn);
                startActivity(intent2); //자기자신을 재실행한다.
                num = 0;

                stopSelf(); //위 작업을 마치고 뷰를 종료함.
                break;


        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }

    // DB에 값들을 일괄적으로 집어넣기 위한 메서드
    public long insertData(int Act_Mac, float x_data_list, float y_data_list, long now_time_list) {
        helper2 = new MacroDBHelper(this);
        DB = helper2.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MacroDBHelper.MAC_NUM, Act_Mac); //Macro 테이블의 마지막 Mac_num 값에 +1 값을 집어넣도록 한다.
        values.put(MacroDBHelper.X, x_data_list);
        values.put(MacroDBHelper.Y, y_data_list);
        values.put(MacroDBHelper.TIME, now_time_list);

        return DB.insert(MacroDBHelper.TABLE_NAME, null, values);
    }


}