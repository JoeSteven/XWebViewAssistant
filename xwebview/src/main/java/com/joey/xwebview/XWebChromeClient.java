package com.joey.xwebview;

import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * Description:
 * author:Joey
 * date:2018/8/20
 */
public class XWebChromeClient extends WebChromeClient{
    private XWebView xWebView;

    void setXWebView(XWebView xWebView) {
        this.xWebView = xWebView;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        xWebView.onProgressChanged(newProgress);
    }

}
