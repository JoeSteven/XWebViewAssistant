package com.joey.xwebview.jsbridge;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Base64;
import android.webkit.JsPromptResult;

import com.joey.xwebview.XWebView;
import com.joey.xwebview.exception.JSBridgeException;
import com.joey.xwebview.jsbridge.method.DefaultAuthorizedChecker;
import com.joey.xwebview.jsbridge.method.IAuthorizedChecker;
import com.joey.xwebview.jsbridge.method.JSMessage;
import com.joey.xwebview.jsbridge.method.XJavaMethod;
import com.joey.xwebview.log.XWebLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Description: core class for JS bridge
 * author:Joey
 * date:2018/8/20
 */
public class JSBridgeCore {
    private final String DISPATCH_MSG = "://dispatch_js_message/";
    private final String FETCH_QUEUE = "://fetch_message_queue/";
    private final String JS_FUNC_FETCH_QUEUE = "fetch_queue";
    private final int MSG_INVOKE_JAVA = 1;
    private JSBridgeRegister jsBridgeRegister;
    private XWebView webView;
    private IAuthorizedChecker authorizedChecker = new DefaultAuthorizedChecker();
    private String bridgeSchema;
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_INVOKE_JAVA) invokeJavaMethod((JSMessage) msg.obj);
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

    public void setBridgeSchema(String schema) {
        this.bridgeSchema = schema;
    }

    /**
     * check scheme to handle Js message
     * @param url
     * @return
     */
    public boolean checkJsBridge(String url) {
        if (!url.startsWith(bridgeSchema)) return false;
        if ((bridgeSchema + DISPATCH_MSG).equals(url)) {
            // xwebview://dispatch_js_message/  get msg queue from JS
            invokeJavaScript(JS_FUNC_FETCH_QUEUE);
            return true;
        } else if (url.startsWith(bridgeSchema + FETCH_QUEUE)) {
            // xwebview://fetch_message_queue/&[{},{}]
            int index = url.indexOf("&");
            if (index < 0) return true;
            String msg = url.substring(index + 1);
            if (!TextUtils.isEmpty(msg)) {
                parseJSMessage(msg);
            }
            return true;
        }
        return false;
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
                stringBuilder.append("'");
                stringBuilder.append(params[i]);
                stringBuilder.append("'");
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
     * parse msg to JSMessage
     * @param msg
     */
    private void parseJSMessage(String msg) {
        try {
            JSONArray msgs = new JSONArray(new String(Base64.decode(msg, Base64.NO_WRAP)));
            int length = msgs.length();
            for (int i = 0; i < length ; i++) {
                JSONObject jsonMsg = msgs.getJSONObject(i);
                JSMessage jsMessage = new JSMessage();
                jsMessage.hostUrl = webView.webView().getUrl();
                jsMessage.callbackID = jsonMsg.optString("callback_id");
                jsMessage.javaMethod = jsonMsg.optString("func");
                jsMessage.params = jsonMsg.optJSONObject("params");
                Message dispatchMsg = Message.obtain();
                dispatchMsg.what = MSG_INVOKE_JAVA;
                dispatchMsg.obj = jsMessage;
                handler.sendMessage(dispatchMsg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
                if (jsBridgeRegister.isInWhiteList(message.hostUrl)) {
                    method.invoke(message, webView);
                } else {
                    XWebLog.error(new JSBridgeException("Java method:" + message.javaMethod +
                            " is private, host:" + message.hostUrl +
                            " don't have permission, add host in white list to invoke private method!"));
                }
                break;
            case AUTHORIZED:
                if (authorizedChecker.isAuthorized(message.javaMethod, message.hostUrl) || jsBridgeRegister.isInWhiteList(message.hostUrl)) {
                    method.invoke(message, webView);
                } else {
                    XWebLog.error(new JSBridgeException("Java method:" + message.javaMethod +
                            " permission is Authorized, hostUrl:" + message.hostUrl +
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
        handler.removeCallbacksAndMessages(null);
        authorizedChecker = null;
        webView = null;
        handler = null;
    }

}
