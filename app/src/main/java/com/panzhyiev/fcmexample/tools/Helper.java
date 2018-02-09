package com.panzhyiev.fcmexample.tools;

import android.app.ActivityManager;
import android.content.Context;

import com.panzhyiev.fcmexample.App;

import java.util.List;

public class Helper {

    public static boolean isAppForeground() {
        ActivityManager manager =
                (ActivityManager) App.getInstance().getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        List<ActivityManager.RunningAppProcessInfo> info = manager.getRunningAppProcesses();
        if (info == null || info.size() == 0) return false;
        for (ActivityManager.RunningAppProcessInfo aInfo : info) {
            if (aInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return aInfo.processName.equals(App.getInstance().getPackageName());
            }
        }
        return false;
    }
}
