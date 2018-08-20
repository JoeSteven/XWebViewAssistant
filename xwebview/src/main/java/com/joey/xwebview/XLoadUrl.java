package com.joey.xwebview;

import android.annotation.TargetApi;
import android.os.Build;
import android.webkit.WebView;

/**
 * Description:
 * author:Joey
 * date:2018/8/20
 */
public class XLoadUrl {

    private static class BaseImpl {

        public void loadUrl(WebView webView, String url) {
            if (webView == null) {
                return;
            }
            try {
                webView.loadUrl(url);
            } catch (Throwable t) {
                // ignore
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static class KitKatImpl extends BaseImpl {

        @Override
        public void loadUrl(WebView webView, String url) {
            if (webView == null) {
                return;
            }
            boolean handled = false;
            if (url != null && url.startsWith("javascript:")) {
                try {
                    webView.evaluateJavascript(url, null);
                    handled = true;
                } catch (Throwable t) {
                    if (t instanceof IllegalStateException) {
                        // For java.lang.IllegalStateException: This API not supported on Android 4.3 and earlier
                        handled = false;
                    } else {
                        // ignore
                    }
                }
            }
            if (!handled) {
                try {
                    webView.loadUrl(url);
                } catch (Throwable t) {
                    // ignore
                }
            }
        }
    }

    static final BaseImpl IMPL;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            IMPL = new KitKatImpl();
        } else {
            IMPL = new BaseImpl();
        }
    }

    public static void loadUrl(WebView webView, String url) {
        IMPL.loadUrl(webView, url);
    }
}
