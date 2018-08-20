package com.joey.xwebview.exception;

/**
 * Description:
 * author:Joey
 * date:2018/8/20
 */
public class JSBridgeException extends Exception{

    public JSBridgeException(String message) {
        super(message);
    }

    public JSBridgeException(String message, Throwable cause) {
        super(message, cause);
    }

    public JSBridgeException(Throwable cause) {
        super(cause);
    }
}
