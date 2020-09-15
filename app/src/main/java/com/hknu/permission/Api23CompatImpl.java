package com.hknu.permission;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Android 6.0 버전 위의 플로팅 뷰 권한관련
 */
public class Api23CompatImpl implements FloatWinPermissionCompat.CompatImpl {

    private static final String TAG = Api23CompatImpl.class.getSimpleName();

    @Override
    public boolean check(Context context) {
        boolean result = true;
        if (Build.VERSION.SDK_INT >= 23) {
            try {
                Class clazz = Settings.class;
                Method canDrawOverlays = clazz.getDeclaredMethod("canDrawOverlays", Context.class);
                result = (Boolean) canDrawOverlays.invoke(null, context);
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
        return result;
    }

    @Override
    public boolean isSupported() {
        return true;
    }


    @Override
    public boolean apply(Context context) {
        try {
            commonROMPermissionApplyInternal(context);
            return true;
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
        return false;
    }

    /**
     * 일반 ROM 권한 신청
     *
     * @param context
     * @return
     */
    public static void commonROMPermissionApplyInternal(Context context) throws NoSuchFieldException, IllegalAccessException {
        Class clazz = Settings.class;
        Field field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION");
        Intent intent = new Intent(field.get(null).toString());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
        //FloatWinPermissionCompat.getInstance().startActivity(intent);
    }

}
