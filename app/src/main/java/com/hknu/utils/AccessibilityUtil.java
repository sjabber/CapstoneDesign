package com.hknu.utils;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class AccessibilityUtil {

    public static boolean isSettingOpen(Class service, Context cxt) {
        try {
            int enable = Settings.Secure.getInt(cxt.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED, 0);
            if (enable != 1)
                return false;
            String services = Settings.Secure.getString(cxt.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (!TextUtils.isEmpty(services)) {
                TextUtils.SimpleStringSplitter split = new TextUtils.SimpleStringSplitter(':');
                split.setString(services);
                // 열려있는 모든 보조 서비스 이름 탐색
                while (split.hasNext()) {
                    if (split.next().equalsIgnoreCase(cxt.getPackageName() + "/" + service.getName())) {
                        return true;
                    }
                }
            }
        } catch (Throwable e) {//이상이있는 경우 제조사가 휴대폰 설정을 변경하여 조정이 필요함을 의미합니다.
            Log.e(TAG, "isSettingOpen: " + e.getMessage());
        }
        return false;
    }

    /**
     * 시스템 설정으로 이동 : 보조 서비스 켜기
     */
    public static void jumpToSetting(final Context cxt) {
        try {
            cxt.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        } catch (Throwable e) { //제조사별 휴대폰의 설정, 권한 조정이 필요함
            try {
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                cxt.startActivity(intent);
            } catch (Throwable e2) {
                Log.e(TAG, "jumpToSetting: " + e2.getMessage());
            }
        }
    }
}

