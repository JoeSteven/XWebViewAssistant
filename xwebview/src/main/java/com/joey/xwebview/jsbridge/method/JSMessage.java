package com.joey.xwebview.jsbridge.method;

import org.json.JSONObject;

/**
 * Description: message that JS gives to Java
 * author:Joey
 * date:2018/8/20
 */
public class JSMessage {
    public JSMessage(String url, String callback, String errorCallback,
                     String javaFunc, JSONObject params) {
        this.hostUrl = url;
        this.callback = callback;
        this.errorCallback = errorCallback;
        this.javaMethod = javaFunc;
        this.params = params;
    }

    public String hostUrl;// the website hostUrl that invoke Java method
    public String callback;// callback func for JS
    public String errorCallback;// error callback func for JS
    public String javaMethod;// Java method to invoke
    public JSONObject params;// params for Java method

    @Override
    public String toString() {
        return "JSMessage{" +
                "hostUrl='" + hostUrl + '\'' +
                ", callback='" + callback + '\'' +
                ", errorCallback='" + errorCallback + '\'' +
                ", javaMethod='" + javaMethod + '\'' +
                ", params=" + params +
                '}';
    }
}
