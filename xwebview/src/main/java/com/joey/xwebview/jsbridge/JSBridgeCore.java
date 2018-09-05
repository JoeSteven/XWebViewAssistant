package com.joey.xwebview.jsbridge;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.webkit.JavascriptInterface;

import com.joey.xwebview.XWebView;
import com.joey.xwebview.exception.JSBridgeException;
import com.joey.xwebview.jsbridge.method.DefaultAuthorizedChecker;
import com.joey.xwebview.jsbridge.method.IAuthorizedChecker;
import com.joey.xwebview.jsbridge.method.JSMessage;
import com.joey.xwebview.jsbridge.method.XJavaMethod;
import com.joey.xwebview.log.XWebLog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Description: core class for JS bridge
 * author:Joey
 * date:2018/8/20
 */
public class JSBridgeCore {
    public static final int STATUS_SUCCESS = 0;
    public static final int STATUS_ERROR = 1;

    private final int MSG_INVOKE_JAVA = 1;
    private final int MSG_INVOKE_JS = 0;
    private final String JS_CALLBACK = "_xwebview_callback";
    private JSBridgeRegister jsBridgeRegister;
    private XWebView webView;
    private IAuthorizedChecker authorizedChecker = new DefaultAuthorizedChecker();
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INVOKE_JAVA:
                    disPatchJsMessage((JSMessage) msg.obj);
                    break;
                case MSG_INVOKE_JS:
                    webView.loadUrl((String) msg.obj);
                    break;
            }
        }
    };


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
     * invoke JS method
     *
     * @param func   JavaScript func
     * @param params params for this func
     */
    public void invokeJavaScript(String func, String... params) {
        StringBuilder stringBuilder = new StringBuilder("javascript:");
        stringBuilder.append(func)
                .append("(");
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                stringBuilder.append(params[i]);
                if (i < params.length - 1) {
                    stringBuilder.append(",");
                }
            }
        }
        stringBuilder.append(")");
        if (webView != null) {
            String method = stringBuilder.toString();
            Message msg = Message.obtain();
            msg.what = MSG_INVOKE_JS;
            msg.obj = method;
            handler.sendMessage(msg);
        }
    }

    /**
     * JavaScript invoke Java method
     */
    @JavascriptInterface
    public void invokeJavaMethod(String msg) {
        try {
            msg = new String(Base64.decode(msg, Base64.NO_WRAP));
            JSONObject jsonMsg = new JSONObject(msg);
            JSMessage jsMessage = new JSMessage();
            jsMessage.callbackID = jsonMsg.optString("callback_id");
            jsMessage.javaMethod = jsonMsg.optString("func");
            jsMessage.params = jsonMsg.optJSONObject("params");
            Message dispatchMsg = Message.obtain();
            dispatchMsg.what = MSG_INVOKE_JAVA;
            dispatchMsg.obj = jsMessage;
            handler.sendMessage(dispatchMsg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void disPatchJsMessage(JSMessage message) {
        message.hostUrl = webView.webView().getUrl();
        XJavaMethod method = jsBridgeRegister.findMethod(message.javaMethod);
        JSONObject callback;
        try {
            if (method == null) {
                throw new JSBridgeException("can't find Java method:" + message.javaMethod +
                        " ,please register this method in JSBridgeRegister");
            }
            switch (method.permission()) {
                case PRIVATE:
                    if (jsBridgeRegister.isInWhiteList(message.hostUrl)) {
                        callback = method.invoke(message, webView);
                    } else {
                        throw new JSBridgeException("Java method:" + message.javaMethod +
                                " is private, host:" + message.hostUrl +
                                " don\'t have permission, add host in white list to invoke private method!");
                    }
                    break;
                case AUTHORIZED:
                    if (authorizedChecker.isAuthorized(message.javaMethod, message.hostUrl) || jsBridgeRegister.isInWhiteList(message.hostUrl)) {
                        callback = method.invoke(message, webView);
                    } else {
                        throw new JSBridgeException("Java method:" + message.javaMethod +
                                " permission is Authorized, hostUrl:" + message.hostUrl +
                                " don't have permission, check you AuthorizedChecker!");
                    }
                    break;
                case PUBLIC:
                default:
                    callback = method.invoke(message, webView);
                    break;
            }
            if (callback != null) {
                invokeJSCallback(message.callbackID, STATUS_SUCCESS, "success", callback);
            }
        } catch (Exception e) {
            invokeJSCallback(message.callbackID, STATUS_ERROR, e.toString(), null);
            XWebLog.error(e);
        }
    }

    public void invokeJSCallback(String id, int statusCode, String message,JSONObject params) {
        JSONObject callback = new JSONObject();
        try {
            callback.put("callback_id", id);
            callback.put("status", statusCode);
            callback.put("message", message);
            if (params != null) {
                callback.put("params", params);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            try {
                callback.put("callback_id", id);
                callback.put("status", STATUS_ERROR);
                callback.put("message", e.toString());
            } catch (JSONException ex) {
                e.printStackTrace();
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("'");
        stringBuilder.append(Base64.encodeToString(callback.toString().getBytes(), Base64.NO_WRAP));
        stringBuilder.append("'");
        invokeJavaScript(JS_CALLBACK, stringBuilder.toString());
    }


    public void release() {
        jsBridgeRegister.release();
        handler.removeCallbacksAndMessages(null);
        authorizedChecker = null;
        webView = null;
        handler = null;
    }

}
