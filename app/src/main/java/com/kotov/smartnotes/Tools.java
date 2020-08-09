package com.kotov.smartnotes;


import android.app.Activity;
import android.os.Build;
import android.view.Window;

/**
 * @author dmitriykotov333@gmail.com
 * @since 06.08.2020
 */
public class Tools {
    public static void setSystemBarColor(Activity activity) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = activity.getWindow();
            window.addFlags(Integer.MIN_VALUE);
            window.clearFlags(67108864);
            window.setStatusBarColor(activity.getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    public static void setSystemBarColor(Activity activity, int i) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = activity.getWindow();
            window.addFlags(Integer.MIN_VALUE);
            window.clearFlags(67108864);
            window.setStatusBarColor(activity.getResources().getColor(i));
        }
    }
}