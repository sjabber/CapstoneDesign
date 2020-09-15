package com.hknu.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static android.content.Context.WINDOW_SERVICE;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * 화면 도구
 */
public class WindowUtils {

    /**
     * 배경이 투명한 활동인지 확인
     * @param context
     * @return
     */
    public static boolean isTranslucentOrFloating(Context context){
        boolean isTranslucentOrFloating = false;
        try {
            int [] styleableRes = (int[]) Class.forName("com.android.internal.R$styleable").getField("Window").get(null);
            final TypedArray ta = context.obtainStyledAttributes(styleableRes);
            Method m = ActivityInfo.class.getMethod("isTranslucentOrFloating", TypedArray.class);
            m.setAccessible(true);
            isTranslucentOrFloating = (boolean)m.invoke(null, ta);
            m.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isTranslucentOrFloating;
    }

    /**
     * 8.0 이상의 투명한 배경을 가진 활동이 방향을 지정할 때 충돌하는 버그 수정
     * @param activity
     */
    public static void fixOrientation(Activity activity){
        try {
            Field field = Activity.class.getDeclaredField("mActivityInfo");
            field.setAccessible(true);
            ActivityInfo o = (ActivityInfo)field.get(activity);
            o.screenOrientation = -1;
            field.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 화면 너비 가져 오기
     */
    public static int getScreenWidth(Context context){
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 화면 높이 가져 오기
     */
    public static int getScreenHeight(Context context){
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 가상 키 높이 가져 오기
     * @param context
     * @return
     */
    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        if (hasNavBar(context)) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    /**
     * 가상 버튼 모음이 있는지 확인
     * @param context
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static boolean hasNavBar(Context context) {
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
        if (resourceId != 0) {
            boolean hasNav = res.getBoolean(resourceId);
            // check override flag
            String sNavBarOverride = getNavBarOverride();
            if ("1".equals(sNavBarOverride)) {
                hasNav = false;
            } else if ("0".equals(sNavBarOverride)) {
                hasNav = true;
            }
            return hasNav;
        } else { // fallback
            return !ViewConfiguration.get(context).hasPermanentMenuKey();
        }
    }

    /**
     * 가상 버튼 모음이 다시 작성되었는지 확인
     * @return
     */
    private static String getNavBarOverride() {
        String sNavBarOverride = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {
            }
        }
        return sNavBarOverride;
    }

    /**
     * 전체 화면-상태 표시 줄 및 가상 버튼 숨기기
     * @param window
     */
    public static void setHideVirtualKey(Window window) {
        //레이아웃 유지
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE|
                //레이아웃은 상태 표시 줄 아래에 있습니다.
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
                //전체 화면
                View.SYSTEM_UI_FLAG_FULLSCREEN|
                //탐색 모음 숨기기
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= 19){
            uiOptions |= 0x00001000;
        }else{
            uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }
        window.getDecorView().setSystemUiVisibility(uiOptions);
    }


    /**	 페이지의 투명도 설정 @param bgAlpha 1은 불투명 함을 의미합니다.	 */
    public static void setBackgroundAlpha(Activity activity, float bgAlpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        if (bgAlpha == 1) {
            //신고가 제거되지 않으면 동영상 페이지의 동영상에 검은 색 화면 버그가 있습니다.
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        } else {
            //이 코드 줄은 주로 Huawei 전화에서 반투명 효과가 유효하지 않은 버그를 해결하기위한 것입니다.
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        activity.getWindow().setAttributes(lp);
    }

    /**
     * 상태 표시 줄의 높이 가져 오기
     */
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = res.getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    /**
     * 전체 화면보기
     * @param context
     * @param view
     */
    public static void enterFullScreen(Context context, View view){
        //从原有的View中取出来
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }

        //找到父布局
        ViewGroup contentView = scanForActivity(context)
                .findViewById(android.R.id.content);

        //添加到父布局中
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                MATCH_PARENT,MATCH_PARENT);
        contentView.addView(view,params);
    }

    /**
     * 전체 화면에서보기 종료
     */
    public static void exitFullScreen(Context context, View view){
        //找到父布局
        ViewGroup contentView = scanForActivity(context)
                .findViewById(android.R.id.content);
        contentView.removeView(view);
    }

    private static Activity scanForActivity(Context cont) {
        if (cont == null) {
            Log.d("scanForActivity","cont == null");
            return null;
        } else if (cont instanceof Activity) {
            Log.d("scanForActivity","Activity");
            return (Activity) cont;
        } else if (cont instanceof ContextWrapper) {
            Log.d("scanForActivity","ContextWrapper");
            return scanForActivity(((ContextWrapper) cont).getBaseContext());
        }
        Log.d("scanForActivity","not result");
        return null;
    }

    public static WindowManager.LayoutParams newWmParams(int width, int height) {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_SCALED
                | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        if (Build.VERSION.SDK_INT >= 26) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        params.width = width;
        params.height = height;
        params.format = PixelFormat.TRANSLUCENT;
        return params;
    }

    public static WindowManager getWindowManager(Context context) {
        return (WindowManager) context.getSystemService(WINDOW_SERVICE);
    }
}
