package com.example.permission;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Meizu 부동 창 권한의 호환 가능한 구현
 */
public class MeizuCompatImpl extends BelowApi23CompatImpl {

    @Override
    public boolean isSupported() {
        return true;
    }

    /**
     * Meizu 허가 신청 페이지로 이동
     */
    @Override
    public boolean apply(Context context) {
        try {
            Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            intent.setClassName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity");
            intent.putExtra("packageName", context.getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(context,intent);
        } catch (Exception e) {
            try {
                Log.d("dq","flyme 6.2.5+,apply permission failed");
                Api23CompatImpl.commonROMPermissionApplyInternal(context);
            } catch (Exception eFinal) {
                eFinal.printStackTrace();
            }
        }
        return true;
    }

    private void startActivity(Context context, Intent intent) {
        context.startActivity(intent);
        //FloatWinPermissionCompat.getInstance().startActivity(intent);
    }

}
