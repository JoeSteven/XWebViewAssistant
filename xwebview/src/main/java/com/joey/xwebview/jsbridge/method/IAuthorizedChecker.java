package com.joey.xwebview.jsbridge.method;

/**
 * Description: authorized checker for AUTHORIZED method
 * author:Joey
 * date:2018/8/20
 */
public interface IAuthorizedChecker {
    boolean isAuthorized(String javaFunc, String url);
}
