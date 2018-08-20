package com.joey.xwebview.log;

import android.util.Log;

/**
 * Description: debug log for module
 * author:Joey
 * date:2018/8/20
 */
public class XWebLog {
    private static boolean sDebug;
    public static void Debug(boolean enable) {
        sDebug = enable;
    }

    public static void e(String message) {
        if (sDebug) {
            Log.e("XWebView", message);
        }
    }

    public static void d(String message) {
        if (sDebug) {
            Log.d("XWebView", message);
        }
    }

    public static void error(Exception e) {
        Log.e("XWebView", e.toString());
    }
}
