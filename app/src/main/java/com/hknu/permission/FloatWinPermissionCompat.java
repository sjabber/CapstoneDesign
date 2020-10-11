package com.hknu.permission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Method;

public class FloatWinPermissionCompat {

    private static final String TAG = FloatWinPermissionCompat.class.getSimpleName();

    public static FloatWinPermissionCompat getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final FloatWinPermissionCompat INSTANCE = new FloatWinPermissionCompat();
    }

    private CompatImpl compat;

    private FloatWinPermissionCompat() {
        // 6.0 버전 이후 처리
        if (Build.VERSION.SDK_INT < 23) {
            if (RomUtils.isMe()) {
                compat = new Permission_Me();
            } else {
                // Android6.0 다음 비 호환 모델은 기본적으로 구현됩니다.
                compat = new BelowApi23CompatImpl() {
                    @Override
                    public boolean isSupported() {
                        return false;
                    }

                    @Override
                    public boolean apply(Context context) {
                        return false;
                    }
                };
            }
        } else {
            if (RomUtils.isMe()) {
                compat = new Permission_Me();
            } else {
                // 6.0 버전 이후 구글이 플로팅 윈도우 권한 관리를 추가했기 때문에 방식이 통일되었습니다.
                compat = new Api23CompatImpl();
            }
        }
    }

    // 플로팅 뷰 권한 확인
    public boolean check(Context context) {
        return compat.check(context);
    }

    public boolean isSupported() {
        return compat.isSupported();
    }


    /**
     * op 값을 확인하여 플로팅 윈도우가 승인되었는지 확인
     *
     * @param context
     * @param op
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean checkOp(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Class clazz = AppOpsManager.class;
                Method method = clazz.getDeclaredMethod("checkOp", int.class, int.class, String.class);
                return AppOpsManager.MODE_ALLOWED == (int) method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName());
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        } else {
            Log.e(TAG, "Below API 19 cannot invoke!");
        }
        return false;
    }


    public interface CompatImpl {

        // 권한 확인
        boolean check(Context context);

        boolean isSupported();

        // 접근 요청
        boolean apply(Context context);
    }


    public static final int REQUEST_CODE_SYSTEM_WINDOW = 1001;
    private Activity activity;
    private Context context;
    private boolean forResult = false;

    public void startActivity(Intent intent) {
        try {
            if (intent == null || context == null) {
                return;
            }
            if (!forResult) {
                context.startActivity(intent);
            } else {
                if (activity != null) {//권한 설정 페이지를 연 후 실행되는 이유 onActivityResult？
                    activity.startActivityForResult(intent, REQUEST_CODE_SYSTEM_WINDOW);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}