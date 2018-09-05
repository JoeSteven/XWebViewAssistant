package com.joey.xwebview.jsbridge.method;

/**
 * Description:
 * author:Joey
 * date:2018/8/20
 */
public class DefaultAuthorizedChecker implements IAuthorizedChecker {
    @Override
    public boolean isAuthorized(String javaFunc, String url) {
        return false;
    }
}
