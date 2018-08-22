package com.joey.xwebview.cookie;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;

import java.util.List;

/**
 * Description: handle WebView cookies
 * author:Joey
 * date:2018/8/22
 */
public class XWebCookies {
    private static boolean sInit;
    /**
     * init before use
     */
    public static void init(Context context) {
        if (sInit) return;
        CookieSyncManager.createInstance(context);
        sInit = true;
    }

    /**
     * better call this method in background thread
     */
    public static void syncCookie(String url, String cookie) {
        CookieManager.getInstance().setCookie(url, cookie);
        sync();
    }

    /**
     *  better call this method in background thread
     */
    public static void syncCookies(String url, List<String> cookies) {
        for (String cookie : cookies) {
            CookieManager.getInstance().setCookie(url, cookie);
        }
        sync();
    }

    /**
     * get WebView cookie
     */
    public static String getCookie(String url) {
        return CookieManager.getInstance().getCookie(url);
    }

    /**
     * remove all cookies
     */
    public static void removeAllCookies(@Nullable ValueCallback<Boolean> callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().removeAllCookies(callback);
        } else {
            CookieManager.getInstance().removeAllCookie();
            if (callback != null) {
                callback.onReceiveValue(!CookieManager.getInstance().hasCookies());
            }
        }
        sync();
    }

    /**
     * remove session cookies
     */
    public static void removeSessionCookies(@Nullable ValueCallback<Boolean> callback) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().removeSessionCookies(callback);
        } else {
            CookieManager.getInstance().removeSessionCookie();
            if (callback != null) {
                callback.onReceiveValue(true);
            }
        }
        sync();
    }

    /**
     * Removes all expired cookies.
     * @deprecated The WebView handles removing expired cookies automatically.
     */
    @Deprecated
    public static void removeExpiredCookies() {
        CookieManager.getInstance().removeExpiredCookie();
        sync();
    }

    private static void sync() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().flush();
        } else {
            CookieSyncManager.getInstance().sync();
        }
    }
}
