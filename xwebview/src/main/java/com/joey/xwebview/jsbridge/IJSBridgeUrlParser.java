package com.joey.xwebview.jsbridge;

import com.joey.xwebview.jsbridge.method.JSMessage;

/**
 * Description: parse hostUrl
 * author:Joey
 * date:2018/8/20
 */
public interface IJSBridgeUrlParser {
    JSMessage parse(String hostUrl, String url);
}
