package com.joey.xwebassistant.sample.JavaMethod;

import android.widget.Toast;

import com.joey.xwebview.jsbridge.method.JSMessage;

/**
 * Description:
 * author:Joey
 * date:2018/8/20
 */
public class JSToastAuthorized extends JSToast{

    @Override
    public void call(JSMessage message) {
        if (context != null){
            Toast.makeText(context, message.params.optString("message"), Toast.LENGTH_SHORT).show();
            callback(message.callback, "call JSToastAuthorized success");
        } else {
            callError(message.errorCallback, "call JSToastAuthorized failed! context is null!");
        }
    }

    @Override
    public Permission permission() {
        return Permission.PUBLIC;
    }
}
