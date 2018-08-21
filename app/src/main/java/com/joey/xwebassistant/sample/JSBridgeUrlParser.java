package com.joey.xwebassistant.sample;

import android.net.Uri;

import com.joey.xwebview.jsbridge.IJSBridgeUrlParser;
import com.joey.xwebview.jsbridge.method.JSMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Description:
 * author:Joey
 * date:2018/8/20
 */
public class JSBridgeUrlParser implements IJSBridgeUrlParser{
    @Override
    public JSMessage parse(String url) {
        if (!url.startsWith("Xwebview")) return null;
        Uri uri = Uri.parse(url);
        try {
            JSONObject params = new JSONObject(uri.getQueryParameter("params"));
        return new JSMessage(url,
                uri.getQueryParameter("callback"),
                uri.getQueryParameter("error_callback"),
                uri.getQueryParameter("func"),
                params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
