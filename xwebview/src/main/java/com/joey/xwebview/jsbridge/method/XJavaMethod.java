package com.joey.xwebview.jsbridge.method;

import com.joey.xwebview.XWebView;
import com.joey.xwebview.jsbridge.JSBridgeCore;

import org.json.JSONObject;

/**
 * Description: Java Method for JS
 * author:Joey
 * date:2018/8/20
 */
public abstract class XJavaMethod {
    protected XWebView webView;

    public JSONObject invoke(JSMessage message, XWebView webView) throws Exception{
        this.webView = webView;
        return call(message, new JSONObject());
    }

    /**
     * implements this method for JS
     */
    public abstract JSONObject call(JSMessage message, JSONObject callbackParams) throws Exception;

    /**
     * @return permission need for this method
     */
    public abstract Permission permission();

    /**
     * call this method to notify js after execute
     * @param callbackID
     * @param params
     */
    protected void callback(String callbackID, JSONObject params) {
        if (webView != null) {
            webView.invokeJsCallback(callbackID, JSBridgeCore.STATUS_SUCCESS,"success", params);
        }
    }

    protected void callError(String callbackID, String errorMessage) {
        if (webView != null) {
            webView.invokeJsCallback(callbackID, JSBridgeCore.STATUS_ERROR,errorMessage, null);
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
