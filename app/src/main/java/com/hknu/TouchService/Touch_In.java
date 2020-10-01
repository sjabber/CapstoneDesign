package com.hknu.TouchService;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Path;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import com.hknu.Touch.TouchEvent;
import com.hknu.Touch.TouchPoint;
import com.hknu.capstone_design.MacroDBHelper;
import com.hknu.capstone_design.TouchEventManager;
import com.hknu.capstone_design.TouchInput;
import com.hknu.utils.DensityUtil;
import com.hknu.utils.WindowUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import java.text.DecimalFormat;
import static com.hknu.capstone_design.TouchInput.touchPoint;
import static com.hknu.capstone_design.MainActivity.Voices;

// 직접적으로 화면에 터치를 가하는 서비스 클래스
// 전역변수들 추후에 지역변수로 바꿀 수 있으면 변경하기

@RequiresApi(api = Build.VERSION_CODES.N)
public class Touch_In extends AccessibilityService {

    SQLiteDatabase db;
    SQLiteDatabase MacroDatabaseHelper;
    MacroDBHelper helper;
    int Macro_Number;
    String Macro_Name;
    comparison comparison;
    Cursor cursor;
    TouchPoint touchPoint2;
    long t1;


    private WindowManager mWindowManager;
    private TouchPoint autoTouchPoint; // 자동클릭 이벤트
    @SuppressLint("HandlerLeak")
    private TextView mFloatingView;
    // 카운트 다운
    private long countDownTime;
//    private int LAYOUT_FLAG;
    private Runnable touchViewRunnable;
    private DecimalFormat floatDf = new DecimalFormat("#0.0");
    private TouchEvent events;
    Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        handler = new Handler();
        EventBus.getDefault().register(this);
        Log.d("서비스로 전환", "서비스로 전환");
        mWindowManager = WindowUtils.getWindowManager(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReciverTouchEvent(@NotNull TouchEvent event) {
        Log.d("AutoTouch", "onReCiVerTouchEvent: " + event.toString());
        TouchEventManager.getInstance().setTouchAction(event.getAction());
        handler.removeCallbacks(autoTouch);

        switch (event.getAction()) {
            case TouchEvent.ACTION_START:
                TouchPoint touch = touchPoint;
                autoTouchPoint = touch;
                onAutoClick();
                break;

            case TouchEvent.ACTION_PAUSE:
                handler.removeCallbacks(autoTouch);
                handler.removeCallbacks(touchViewRunnable);
                break;

            case TouchEvent.ACTION_STOP:
                handler.removeCallbacks(autoTouch);
                handler.removeCallbacks(touchViewRunnable);
                autoTouchPoint = null;

                break;

            case TouchEvent.ACTION_VOICE:
                Log.d("음성 매크로 시작", "음성매크로 시작");
                comparison = new comparison();
                int Macro_Number; // 매크로 숫자
                int length; // 해당 매크로의 동작 개수

                float x; // 대입할 X 좌표를 일시적으로 담을 변수
                float y; // 대입할 Y 좌표를 일시적으로 담을 변수
                 // 선행 동작 시간값을 일시적으로 담을 변수
                //long t2; // 후행 동작 시간값을 일시적으로 담을 변수

                //음성명령어와 일치하는 매크로의 번호값을 반환받는다.
                Macro_Number = comparison.comparison(Voices);

                // 매크로 번호와 일치하는 동작들을 조회한다.
                String SQL = "SELECT Act_x, Act_y, Act_time FROM Act WHERE Act_Mac = " + Macro_Number;

                cursor = db.rawQuery(SQL, null);
                length = cursor.getCount();

                for (int i = 0; i < length; i++) {
                    touchViewRunnable = null;
                    //맨 처음 대입은 delay 0초로 시작한다.
                    cursor.moveToNext();  // 맨 처음 값을 넣는다.
                    x = cursor.getFloat(0);
                    y = cursor.getFloat(1);
                    t1 = cursor.getLong(2);

                    touchPoint2 = new TouchPoint(x, y, t1);
                    TouchPoint touch2 = touchPoint2;
                    autoTouchPoint = touch2;
                    SystemClock.sleep(t1 + 500);

                    autoTouch2.run();
                }

                break;
        }
    }

    private void onAutoClick() {
        if (autoTouchPoint != null) {
            handler.postDelayed(autoTouch, 1 * 500L);
            //showTouchView();
            Log.d("onAutoClick", "화면에 스스로 터치를 가한다.");
        }
    }


    private Runnable autoTouch = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {
            Log.d("좌표터치", "x= " + autoTouchPoint.getX() + ", y= " + autoTouchPoint.getY());
            Path path = new Path();
            path.moveTo(autoTouchPoint.getX(), autoTouchPoint.getY());
            GestureDescription.Builder builder = new GestureDescription.Builder();
            GestureDescription gestureDescription = builder.addStroke(
                    new GestureDescription.StrokeDescription(path, 0, 100))
                    .build();
            dispatchGesture(gestureDescription, new AccessibilityService.GestureResultCallback() {
                @Override
                public void onCompleted(GestureDescription gestureDescription) {
                    super.onCompleted(gestureDescription);
                    Log.d("끝", "끝");
                }

                @Override
                public void onCancelled(GestureDescription gestureDescription) {
                    super.onCancelled(gestureDescription);
                    Log.d("취소", "취소");
                }
            }, null);

            SystemClock.sleep(1000);
            Intent intent = new Intent(getApplicationContext(), TouchInput.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent); //자기자신을 재실행한다.
        }
    };

    private Runnable autoTouch2 = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void run() {
            int width = DensityUtil.dip2px(Touch_In.this, 40);
            int height = DensityUtil.dip2px(Touch_In.this, 40);

            Log.d("좌표터치", "x= " + autoTouchPoint.getX() + ", y= " + autoTouchPoint.getY());
            Path path = new Path();
            path.moveTo(autoTouchPoint.getX(), autoTouchPoint.getY());
            GestureDescription.Builder builder = new GestureDescription.Builder();
            GestureDescription gestureDescription = builder.addStroke(
                    new GestureDescription.StrokeDescription(path, 0, 100))
                    .build();
            dispatchGesture(gestureDescription, new AccessibilityService.GestureResultCallback() {
                @Override
                public void onCompleted(GestureDescription gestureDescription) {
                    super.onCompleted(gestureDescription);
                    Log.d("끝", "끝");
                }

                @Override
                public void onCancelled(GestureDescription gestureDescription) {
                    super.onCancelled(gestureDescription);
                    Log.d("취소", "취소");
                }
            }, null);
        }

    };

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        handler.removeCallbacksAndMessages(null);
        removeTouchView();
    }

    private class comparison {
        private int comparison(String voice) {

            db =  openOrCreateDatabase("Macro_DB", MODE_PRIVATE, null);
            helper = new MacroDBHelper(getApplicationContext());
            db = helper.getWritableDatabase();


            try{
                String sql = "SELECT Mac_num, Mac_name FROM Macro WHERE Mac_name = ?";
                String[] V = new String[1];
                V[0] = voice;

                cursor = db.rawQuery(sql, V);
                cursor.moveToNext();

                Macro_Number = cursor.getInt(0);
                Macro_Name = cursor.getString(1);

            } catch (Exception e) {
                Toast toast = Toast.makeText(Touch_In.this, "오류발생", Toast.LENGTH_SHORT);
                toast.show();
            }

            return Macro_Number;
        }
    }

    private void removeTouchView() {
        if (mWindowManager != null && mFloatingView.isAttachedToWindow()) {
            mWindowManager.removeView(mFloatingView);
        }
    }
}
