package com.hknu.permission;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Permission_Me extends BelowApi23CompatImpl {

    @Override
    public boolean isSupported() {
        return true;
    }


    @Override
    public boolean apply(Context context) {
        try {
            Intent intent = new Intent("apply");
            intent.putExtra("packageName", context.getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(context,intent);
        } catch (Exception e) {
            try {
                Api23CompatImpl.commonROMPermissionApplyInternal(context);
            } catch (Exception eFinal) {
                eFinal.printStackTrace();
            }
        }
        return true;
    }

    private void startActivity(Context context, Intent intent) {
        context.startActivity(intent);
    }

}
