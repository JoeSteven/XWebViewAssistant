package com.joey.xwebassistant.sample.JavaMethod;

import android.content.Context;
import android.widget.Toast;

import com.joey.xwebview.jsbridge.method.JSMessage;
import com.joey.xwebview.jsbridge.method.XJavaMethod;

/**
 * Description:
 * author:Joey
 * date:2018/8/20
 */
public class JSToast extends XJavaMethod{
    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void call(JSMessage message) {
        if (context != null){
            Toast.makeText(context, message.params.optString("message"), Toast.LENGTH_SHORT).show();
            callback(message.callback, "call JSToast success");
        } else {
            callError(message.errorCallback, "call JSToast failed! context is null!");
        }
    }

    @Override
    public Permission permission() {
        return Permission.PUBLIC;
    }
}
