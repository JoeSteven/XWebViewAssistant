package com.joey.xwebview.jsbridge;

import android.net.Uri;
import android.webkit.JsPromptResult;

import com.joey.xwebview.XWebView;
import com.joey.xwebview.exception.JSBridgeException;
import com.joey.xwebview.jsbridge.method.DefaultAuthorizedChecker;
import com.joey.xwebview.jsbridge.method.IAuthorizedChecker;
import com.joey.xwebview.jsbridge.method.JSMessage;
import com.joey.xwebview.jsbridge.method.XJavaMethod;
import com.joey.xwebview.log.XWebLog;

/**
 * Description: core class for JS bridge
 * author:Joey
 * date:2018/8/20
 */
public class JSBridgeCore {
    private JSBridgeRegister jsBridgeRegister;
    private XWebView webView;
    private IAuthorizedChecker authorizedChecker = new DefaultAuthorizedChecker();
    private IJSBridgeUrlParser urlParser;
    private IJSBridgePromptParser promptParser;


    public JSBridgeCore(JSBridgeRegister jsBridgeRegister, XWebView webView) {
        this.jsBridgeRegister = jsBridgeRegister;
        this.webView = webView;
    }

    /**
     * set authorized checker for permission is AUTHORIZED
     */
    public void setAuthorizedChecker(IAuthorizedChecker checker) {
        this.authorizedChecker = checker;
    }

    /**
     * parse url to JSMessage
     */
    public void setUrlParser(IJSBridgeUrlParser parser){
        if (promptParser != null){
            XWebLog.error(new JSBridgeException("already choose onJsPrompt to achieve JSBridge!"));
            return;
        }
        urlParser = parser;
    }

    /**
     * parse js prompt to JSMessage
     * @param parser
     */
    public void setPromptParser(IJSBridgePromptParser parser){
        if (urlParser != null){
            XWebLog.error(new JSBridgeException("already choose intercept url to achieve JSBridge!"));
            return;
        }
        promptParser = parser;
    }


    public boolean isEnableJSForUrl() {
        return urlParser != null;
    }

    public boolean isEnableJsForPrompt() {
        return promptParser != null;
    }

    /**
     * is js bridge message
     */
    public boolean checkJsBridge(String url, String message, String defaultValue, JsPromptResult result) {
        JSMessage msg = null;
        if (urlParser != null) {
            msg = urlParser.parse(url);
        } else if(promptParser != null) {
            msg = promptParser.parse(url, message, defaultValue, result);
        }
        if (msg == null) return false;
        invokeJavaMethod(msg);
        return true;
    }

    /**
     * invoke JS method
     * @param func JavaScript func
     * @param params params for this func
     */
    public void invokeJavaScript(String func, String... params) {
        StringBuilder stringBuilder = new StringBuilder("javascript:");
        stringBuilder.append(func)
                .append("(");
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                stringBuilder.append(params[0]);
                if (i < params.length - 1) {
                    stringBuilder.append(",");
                }
            }
        }
        stringBuilder.append(")");
        if (webView != null) {
            String method = stringBuilder.toString();
            webView.loadUrl(method);
        }
    }

    /**
     * JavaScript invoke Java method
     */
    private void invokeJavaMethod(JSMessage message) {
        XJavaMethod method = jsBridgeRegister.findMethod(message.javaMethod);
        if (method == null) {
            XWebLog.error(new JSBridgeException("can't find Java method:" + message.javaMethod +
                    " ,please register this method in JSBridgeRegister"));
            return;
        }
        switch (method.permission()) {
            case PRIVATE:
                Uri uri = Uri.parse(message.url);
                if (jsBridgeRegister.isInWhiteList(uri.getHost())) {
                    method.invoke(message, webView);
                } else {
                    XWebLog.error(new JSBridgeException("Java method:" + message.javaMethod +
                            " is private, host:" + uri.getHost() +
                            " don't have permission, add host in white list to invoke private method!"));
                }
                break;
            case AUTHORIZED:
                if (authorizedChecker.isAuthorized(message.javaMethod, message.url)) {
                    method.invoke(message, webView);
                } else {
                    XWebLog.error(new JSBridgeException("Java method:" + message.javaMethod +
                            " permission is Authorized, url:" + message.url +
                            " don't have permission, check you AuthorizedChecker!"));
                }
                break;
            case PUBLIC:
            default:
                method.invoke(message, webView);
                break;
        }
    }

    public void release() {
        jsBridgeRegister.release();
    }

}
