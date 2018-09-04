package com.joey.xwebview.jsbridge.method;

import org.json.JSONObject;

/**
 * Description: message that JS gives to Java
 * author:Joey
 * date:2018/8/20
 */
public class JSMessage {
    public JSMessage() {}

    public String hostUrl;// the website hostUrl that invoke Java method
    public String callbackID;// callback id register by js
    public String javaMethod;// Java method to invoke
    public JSONObject params;// params for Java method

    @Override
    public String toString() {
        return "JSMessage{" +
                "hostUrl='" + hostUrl + '\'' +
                ", callbackID=" + callbackID +
                ", javaMethod='" + javaMethod + '\'' +
                ", params=" + params +
                '}';
    }
}
