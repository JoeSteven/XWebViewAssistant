package com.joey.xwebview.jsbridge;

import android.webkit.JsPromptResult;

import com.joey.xwebview.jsbridge.method.JSMessage;

/**
 * Description:parse prompt
 * author:Joey
 * date:2018/8/20
 */
public interface IJSBridgePromptParser {
    JSMessage parse(String url, String message, String defaultValue, JsPromptResult result);
}
