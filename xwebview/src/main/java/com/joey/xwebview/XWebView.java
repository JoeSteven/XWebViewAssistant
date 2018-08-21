package com.joey.xwebview;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.joey.xwebview.jsbridge.IJSBridgePromptParser;
import com.joey.xwebview.jsbridge.IJSBridgeUrlParser;
import com.joey.xwebview.jsbridge.JSBridgeCore;
import com.joey.xwebview.jsbridge.JSBridgeRegister;
import com.joey.xwebview.jsbridge.method.IAuthorizedChecker;
import com.joey.xwebview.ui.IWebProgress;
import com.joey.xwebview.ui.IWebTitle;

import java.util.Map;

/**
 * Description:
 * author:Joey
 * date:2018/8/20
 */
public class XWebView implements LifecycleObserver, IWebProgress, IWebTitle {
    private WebView webView;
    private IWebProgress webProgress;
    private IWebTitle webTitle;
    private JSBridgeCore jsBridgeCore;

    public static XWebView with(WebView webView, LifecycleOwner owner) {
        return new XWebView(webView, owner);
    }

    private XWebView(WebView webView, LifecycleOwner owner) {
        this.webView = webView;
        owner.getLifecycle().addObserver(this);
        initWebView();
    }

    private void initWebView() {
        setJavaScriptEnabled(true)
                .setDomStorageEnable(true)
                .setWebViewClient(new XWebViewClient())
                .setWebChromeClient(new XWebChromeClient());
    }

    /********************* api ***************************/

    public XWebView loadUrl(String url) {
        XLoadUrl.loadUrl(webView, url);
        return this;
    }

    public XWebView loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        webView().loadUrl(url, additionalHttpHeaders);
        return this;
    }

    public void invokeJavaScript(String func, String... params) {
        if (jsBridgeCore != null) jsBridgeCore.invokeJavaScript(func, params);
    }

    public boolean goBack() {
        if (webView().canGoBack()) {
            webView().goBack();
            return true;
        }
        return false;
    }

    public void clearCache(boolean includeDisFile) {
        webView().clearCache(includeDisFile);
    }

    public void clearHistory() {
        webView().clearHistory();
    }

    public void clearFormData() {
        webView().clearFormData();
    }

    public JSBridgeCore JSBridge() {
        return jsBridgeCore;
    }

    public WebSettings settings() {
        return webView().getSettings();
    }

    public WebView webView() {
        return webView;
    }


    /********************* setters ***************************/
    public XWebView setCacheMode(int mode) {
        webView().getSettings().setCacheMode(mode);
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public XWebView setMixedContentMode(int mode) {
        webView().getSettings().setMixedContentMode(mode);
        return this;
    }

    public XWebView setJSBridgeUrlEnabled(JSBridgeRegister register, IJSBridgeUrlParser parser) {
        jsBridgeCore = new JSBridgeCore(register, this);
        jsBridgeCore.setUrlParser(parser);
        return this;
    }

    public XWebView setJSBridgePromptEnabled(JSBridgeRegister register, IJSBridgePromptParser parser) {
        jsBridgeCore = new JSBridgeCore(register, this);
        jsBridgeCore.setPromptParser(parser);
        return this;
    }

    public XWebView setJSBridgeAuthorizedChecker(IAuthorizedChecker checker) {
        jsBridgeCore.setAuthorizedChecker(checker);
        return this;
    }

    public XWebView setJavaScriptEnabled(boolean enable) {
        webView().getSettings().setJavaScriptEnabled(enable);
        return this;
    }

    public XWebView setProgressEnable(IWebProgress progress) {
        webProgress = progress;
        return this;
    }

    public XWebView setWebTitleEnable(IWebTitle title) {
        webTitle = title;
        return this;
    }

    public XWebView setJavaScriptCanOpenWindowsAutomatically(boolean enable) {
        webView().getSettings().setJavaScriptCanOpenWindowsAutomatically(enable);
        return this;
    }

    public XWebView setAllowFileAccess(boolean allow) {
        webView().getSettings().setAllowFileAccess(allow);
        return this;
    }

    public XWebView setDomStorageEnable(boolean enable) {
        webView().getSettings().setDomStorageEnabled(enable);
        return this;
    }

    public XWebView setWebViewClient(WebViewClient client) {
        if (client instanceof XWebViewClient) {
            ((XWebViewClient) client).setXWebView(this);
        }
        webView().setWebViewClient(client);
        return this;
    }

    public XWebView setWebChromeClient(WebChromeClient client) {
        if (client instanceof XWebChromeClient) {
            ((XWebChromeClient) client).setXWebView(this);
        }
        webView().setWebChromeClient(client);
        return this;
    }


    /********************* Lifecycle ***************************/
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResume() {
        webView().onResume();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        webView().onPause();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        if (jsBridgeCore != null) jsBridgeCore.release();
        webView().removeAllViews();
        webView().destroy();
        webProgress = null;
        webTitle = null;
        jsBridgeCore = null;
    }

    @Override
    public void onProgressChanged(int progress) {
        if (webProgress != null) webProgress.onProgressChanged(progress);
    }

    @Override
    public void onProgressStart() {
        if (webProgress != null) webProgress.onProgressStart();
    }

    @Override
    public void onProgressDone() {
        if (webProgress != null) webProgress.onProgressDone();
    }

    @Override
    public void onTitleReady(String text) {
        if (webTitle != null) webTitle.onTitleReady(text);
    }
}
