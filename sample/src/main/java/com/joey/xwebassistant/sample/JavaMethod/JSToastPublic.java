package com.joey.xwebassistant.sample.JavaMethod;

import android.text.TextUtils;
import android.widget.Toast;

import com.joey.xwebview.jsbridge.method.JSMessage;

import org.json.JSONObject;

/**
 * Description:
 * author:Joey
 * date:2018/8/20
 */
public class JSToastPublic extends JSToast{

    @Override
    public JSONObject call(JSMessage message, JSONObject callbackParams) throws Exception{
        if (TextUtils.isEmpty(message.params.optString("message"))) throw new Exception("no message found from js");
        Toast.makeText(context, message.params.optString("message"), Toast.LENGTH_SHORT).show();
        return callbackParams.put("message", "invoke JSToastPublic success");
    }

    @Override
    public Permission permission() {
        return Permission.PUBLIC;
    }
}
