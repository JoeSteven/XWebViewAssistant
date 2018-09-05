package com.joey.xwebassistant.sample.JavaMethod;

import com.joey.xwebview.jsbridge.method.JSMessage;
import com.joey.xwebview.jsbridge.method.XJavaMethod;

import org.json.JSONObject;

/**
 * Description:
 * author:Joey
 * date:2018/9/5
 */
public class JSAsyncFunc extends XJavaMethod{
    @Override
    public JSONObject call(JSMessage message, JSONObject callbackParams) throws Exception {
        // async method
        new Thread() {
            @Override
            public void run() {
                try {
                    sleep(3000);
                    // invoke callback for js
                    callback(message.callbackID, callbackParams.put("message", "call JSAsyncFunc success"));
                } catch (Exception e) {
                    // error occurred callError
                    callError(message.callbackID, e.toString());
                    e.printStackTrace();
                }
            }
        }
                .start();

        return null;// return null to tell XWebView not invoke callback right now
    }

    @Override
    public Permission permission() {
        return Permission.PUBLIC;
    }
}
