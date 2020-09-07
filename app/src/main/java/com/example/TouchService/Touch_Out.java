//package com.example.TouchService;
//
//import android.accessibilityservice.AccessibilityService;
//import android.accessibilityservice.GestureDescription;
//import android.annotation.SuppressLint;
//import android.graphics.Path;
//import android.os.Build;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.SystemClock;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.WindowManager;
//import android.view.accessibility.AccessibilityEvent;
//import android.widget.TextView;
//
//import androidx.annotation.RequiresApi;
//import com.example.Touch.TouchEvent;
//import com.example.Touch.TouchPoint;
//import com.example.capstone_design.R;
//import com.example.capstone_design.TouchEventManager;
//import com.example.utils.DensityUtil;
//import com.example.utils.WindowUtils;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//import org.jetbrains.annotations.NotNull;
//
//import java.text.DecimalFormat;
//
//import static com.example.capstone_design.TouchInput.touchPoint;
//
//
//@RequiresApi(api = Build.VERSION_CODES.N)
//public class Touch_Out extends AccessibilityService {
//
//    private WindowManager mWindowManager;
//    private TouchPoint autoTouchPoint; // 자동클릭 이벤트
//    @SuppressLint("HandlerLeak")
//    private TextView mFloatingView;
//    private float countDownTime;
//
//    private int LAYOUT_FLAG;
//    private Runnable touchViewRunnable;
//    private DecimalFormat floatDf = new DecimalFormat("#0.0");
//    private TouchEvent events;
//
//    Handler handler = new Handler(Looper.getMainLooper());
//
//    @Override
//    protected void onServiceConnected() {
//        super.onServiceConnected();
//        handler = new Handler();
//        EventBus.getDefault().register(this);
//        Log.d("서비스로 전환2", "서비스로 전환2");
//        mWindowManager = WindowUtils.getWindowManager(this);
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onReciverTouchEvent(@NotNull TouchEvent event) {
//        Log.d("AutoTouch", "onReciverTouchEvent: " + event.toString());
//        TouchEventManager.getInstance().setTouchAction(event.getAction());
//        handler.removeCallbacks(autoTouch);
//
//        switch (event.getAction()) {
//            case TouchEvent.ACTION_RESTART:
//                Log.d("음성 매크로 시작", "음성매크로 시작");
//                TouchPoint touch2 = touchPoint;
//                autoTouchPoint = touch2;
//                onAutoClick();
//                break;
//        }
//    }
//
//    private void onAutoClick() {
//        if (autoTouchPoint != null) {
//            handler.postDelayed(autoTouch, 1 * 500L);
//            showTouchView();
//            Log.d("onAutoClick", "화면에 스스로 터치를 가한다.");
//        }
//    }
//
//    private Runnable autoTouch = new Runnable() {
//        @RequiresApi(api = Build.VERSION_CODES.N)
//        @Override
//        public void run() {
//            Log.d("좌표터치", "x= " + autoTouchPoint.getX() + ", y= " + autoTouchPoint.getY());
//            Path path = new Path();
//            path.moveTo(autoTouchPoint.getX(), autoTouchPoint.getY());
//            GestureDescription.Builder builder = new GestureDescription.Builder();
//            GestureDescription gestureDescription = builder.addStroke(
//                    new GestureDescription.StrokeDescription(path, 0, 3000))
//                    .build();
//            dispatchGesture(gestureDescription, new AccessibilityService.GestureResultCallback() {
//                @Override
//                public void onCompleted(GestureDescription gestureDescription) {
//                    super.onCompleted(gestureDescription);
//                    Log.d("끝", "끝");
//                }
//
//                @Override
//                public void onCancelled(GestureDescription gestureDescription) {
//                    super.onCancelled(gestureDescription);
//                    Log.d("취소", "취소");
//                }
//            }, null);
//
//            SystemClock.sleep(1000);
//        }
//    };
//
//
//
//
//    @Override
//    public void onAccessibilityEvent(AccessibilityEvent event) {
//
//    }
//
//    @Override
//    public void onInterrupt() {
//
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        EventBus.getDefault().unregister(this);
//        handler.removeCallbacksAndMessages(null);
//        removeTouchView();
//        TouchEvent.postRestartAction(touchPoint);
//    }
//
//    private void showTouchView() {
//        if (autoTouchPoint != null) {
//            //터치 포인트 생성View
//            if (mFloatingView == null) {
//                mFloatingView = (TextView) LayoutInflater.from(this).inflate(R.layout.service1, null);
//            }
//            //터치 포인트 표시View
//            if (mWindowManager != null && !mFloatingView.isAttachedToWindow()) {
//                int width = DensityUtil.dip2px(this, 40);
//                int height = DensityUtil.dip2px(this, 40);
//                WindowManager.LayoutParams params = WindowUtils.newWmParams(width, height);
//                params.gravity = Gravity.START | Gravity.TOP;
//                params.x = (int) autoTouchPoint.getX() - width/2;
//                params.y = (int) autoTouchPoint.getY() - width;
//                params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//                mWindowManager.addView(mFloatingView, params);
//            }
//
//            countDownTime = autoTouchPoint.getDelay();
//            if (touchViewRunnable == null) {
//                touchViewRunnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        handler.removeCallbacks(touchViewRunnable);
//                        if (countDownTime > 0) {
//                            float offset = 0.1f;
//                            mFloatingView.setText(floatDf.format(countDownTime));
//                            countDownTime -= offset;
//                            handler.postDelayed(touchViewRunnable, (long) (1000L * offset));
//                        } else {
//                            removeTouchView();
//                        }
//                    }
//                };
//            }
//            handler.post(touchViewRunnable);
//        }
//    }
//
//    private void removeTouchView() {
//        if (mWindowManager != null && mFloatingView.isAttachedToWindow()) {
//            mWindowManager.removeView(mFloatingView);
//        }
//    }
//}
