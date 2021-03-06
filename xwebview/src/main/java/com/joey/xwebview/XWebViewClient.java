package com.joey.xwebview;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Description:
 * author:Joey
 * date:2018/8/20
 */
public class XWebViewClient extends WebViewClient{
    private XWebView xWebView;

    void setXWebView(XWebView xWebView) {
        this.xWebView = xWebView;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        xWebView.onProgressStart();
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        xWebView.onProgressDone();
        xWebView.onTitleReady(view.getTitle());
    }


}
