package com.hknu.permission;

import android.content.Context;
import android.os.Build;


public abstract class BelowApi23CompatImpl implements FloatWinPermissionCompat.CompatImpl {

  @Override
  public boolean check(Context context) {
    final int version = Build.VERSION.SDK_INT;
    if (version >= 19) {
      return FloatWinPermissionCompat.checkOp(context, 24); // 플로팅 뷰 권한의 op 값은 OP_SYSTEM_ALERT_WINDOW = 24입니다.
    }
    return true;
  }

}
