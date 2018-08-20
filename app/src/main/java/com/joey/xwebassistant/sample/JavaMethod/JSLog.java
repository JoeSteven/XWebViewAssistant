package com.joey.xwebassistant.sample.JavaMethod;

import android.util.Log;

import com.joey.xwebview.jsbridge.method.JSMessage;
import com.joey.xwebview.jsbridge.method.XJavaMethod;

/**
 * Description:
 * author:Joey
 * date:2018/8/20
 */
public class JSLog extends XJavaMethod{
    @Override
    public void call(JSMessage message) {
        Log.d("js", message.params.optString("msg"));
        callback(message.callback, "success");
    }

    @Override
    public XJavaMethod.Permission permission() {
        return Permission.PUBLIC;
    }
}
