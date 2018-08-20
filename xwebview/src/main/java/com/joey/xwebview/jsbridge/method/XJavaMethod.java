package com.joey.xwebview.jsbridge.method;

import com.joey.xwebview.XWebView;

/**
 * Description: Java Method for JS
 * author:Joey
 * date:2018/8/20
 */
public abstract class XJavaMethod {
    protected XWebView webView;

    public void invoke(JSMessage message, XWebView webView) {
        this.webView = webView;
        call(message);
    }

    /**
     * implements this method for JS
     */
    public abstract void call(JSMessage message);

    /**
     * @return permission need for this method
     */
    public abstract Permission permission();

    /**
     * call this method to notify js after success
     * @param callback
     * @param callbackMessage
     */
    protected void callback(String callback, String...callbackMessage) {
        if (webView != null) {
            webView.invokeJavaScript(callback, callbackMessage);
        }
    }

    /**
     * call this method to notify js when error occur
     */
    protected void callError(String callError, String...errorParams) {
        if (webView != null) {
            webView.invokeJavaScript(callError, errorParams);
        }
    }


    public void release() {
        webView = null;
    }

    /**
     *
     */
    public enum Permission{
        PUBLIC,// all web sites can invoke this method

        AUTHORIZED,// only web sites has been authorized can invoke this method

        PRIVATE // only web sites in white list can invoke this method
    }
}
