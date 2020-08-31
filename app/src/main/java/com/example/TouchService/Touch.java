package com.example.TouchService;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.annotation.SuppressLint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.Touch.TouchEvent;
import com.example.Touch.TouchPoint;
import com.example.adapter.TouchPointAdapter;
import com.example.capstone_design.R;
import com.example.capstone_design.TouchEventManager;
import com.example.utils.DensityUtil;
import com.example.utils.WindowUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

import static com.example.capstone_design.TouchInput2.action;
import static com.example.capstone_design.TouchInput2.downTime;
import static com.example.capstone_design.TouchInput2.eventTime;
import static com.example.capstone_design.TouchInput2.x_data;
import static com.example.capstone_design.TouchInput2.y_data;
import static com.example.capstone_design.TouchInput2.touchPoint;



@RequiresApi(api = Build.VERSION_CODES.N)
public class Touch extends AccessibilityService {

    private WindowManager mWindowManager;
    private TouchPoint autoTouchPoint; // 자동클릭 이벤트
    @SuppressLint("HandlerLeak")
    private TextView mFloatingView;
    // 카운트 다운
    private float countDownTime;
    private int LAYOUT_FLAG;
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
        Log.d("AutoTouch", "onReciverTouchEvent: " + event.toString());
        TouchEventManager.getInstance().setTouchAction(event.getAction());
        handler.removeCallbacks(autoTouch);

        switch (event.getAction()) {
            case TouchEvent.ACTION_START:
                TouchPoint touch = touchPoint;
                autoTouchPoint = touch;
                onAutoClick();
                Log.d("터치신호 입력", "브레이크포인트1");
                break;
        }
    }

    private void onAutoClick() {
        if (autoTouchPoint != null) {
            //autoTouchPoint 가 null 이 아니어야만 터치가 작동한다.
            //TouchPoint autoTouchPoint -> 여기에 값을 어떻게 집어넣느냐..
            //event.getTouchPoint();를 집어넣는다.
            //event.getTouchPoint()는 무엇이냐 -> TouchPoint의 touch포인트를 리턴한 객체임 결국엔.
            //Auto Touch 는 어떻게 했길래 autoTouchService 가 intent 없이 실행된걸까

            handler.postDelayed(autoTouch, 1 * 1000L);
            //showTouchView();
            Log.d("onAutoClick", "onAutoClick");
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
        handler.removeCallbacksAndMessages(null);
        removeTouchView();
    }

    private void showTouchView() {
        if (autoTouchPoint != null) {
            //터치 포인트 생성View
            if (mFloatingView == null) {
                mFloatingView = (TextView) LayoutInflater.from(this).inflate(R.layout.service1, null);
            }
            //터치 포인트 표시View
            if (mWindowManager != null && !mFloatingView.isAttachedToWindow()) {
                int width = DensityUtil.dip2px(this, 40);
                int height = DensityUtil.dip2px(this, 40);
                WindowManager.LayoutParams params = WindowUtils.newWmParams(width, height);
                params.gravity = Gravity.START | Gravity.TOP;
                params.x = (int) autoTouchPoint.getX() - width/2;
                params.y = (int) autoTouchPoint.getY() - width;
                params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                mWindowManager.addView(mFloatingView, params);
            }

            countDownTime = autoTouchPoint.getDelay();
            if (touchViewRunnable == null) {
                touchViewRunnable = new Runnable() {
                    @Override
                    public void run() {
                        handler.removeCallbacks(touchViewRunnable);
                        if (countDownTime > 0) {
                            float offset = 0.1f;
                            mFloatingView.setText(floatDf.format(countDownTime));
                            countDownTime -= offset;
                            handler.postDelayed(touchViewRunnable, (long) (1000L * offset));
                        } else {
                            removeTouchView();
                        }
                    }
                };
            }
            handler.post(touchViewRunnable);
        }
    }




    private void removeTouchView() {
        if (mWindowManager != null && mFloatingView.isAttachedToWindow()) {
            mWindowManager.removeView(mFloatingView);
        }
    }


}
