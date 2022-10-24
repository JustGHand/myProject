package com.pw.read.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class Utils {

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int getRealHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if (display == null) {
            return 0;
        }
        display.getRealMetrics(dm);
        return dm.heightPixels;
    }

}
