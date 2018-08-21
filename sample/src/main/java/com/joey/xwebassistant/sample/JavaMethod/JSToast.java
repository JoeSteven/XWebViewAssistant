package com.joey.xwebassistant.sample.JavaMethod;

import android.content.Context;

import com.joey.xwebview.jsbridge.method.XJavaMethod;

/**
 * Description:
 * author:Joey
 * date:2018/8/21
 */
public abstract class JSToast extends XJavaMethod{
    protected Context context;

    public void setContext(Context context) {
        this.context = context;
    }

}
