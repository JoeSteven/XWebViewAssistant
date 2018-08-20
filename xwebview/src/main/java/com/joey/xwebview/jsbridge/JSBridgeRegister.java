package com.joey.xwebview.jsbridge;

import android.util.LruCache;

import com.joey.xwebview.jsbridge.method.XJavaMethod;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: register Java Method for JS and register white list
 * author:Joey
 * date:2018/8/20
 */
public class JSBridgeRegister {
    private HashMap<String, Class<? extends XJavaMethod>> javaMethod;
    private LruCache<String, XJavaMethod> javaMethodCache;
    private List<String> whiteList;
    private IMethodInitializer initializer;
    public interface IMethodInitializer{
        /**
         * this method will be called after create XJavaMethod instance
         * you can init some params for method here
         */
        void init(String func, XJavaMethod method);
    }

    private JSBridgeRegister() {
        javaMethod = new HashMap<>();
        whiteList = new ArrayList<>();
    }

    /**
     * @return create a JSBridgeRegister instance
     */
    public static JSBridgeRegister create() {
        return new JSBridgeRegister();
    }

    /**
     * register Java Method for JavaScript
     * @param funcName func name for JavaScript
     * @param method Real method in java
     */
    public JSBridgeRegister register(String funcName, Class<? extends XJavaMethod> method) {
        javaMethod.put(funcName, method);
        return this;
    }

    /**
     * if you want to init your method with some params, set an initializer
     */
    public JSBridgeRegister setMethodInitializer(IMethodInitializer initializer) {
        this.initializer = initializer;
        return this;
    }

    /**
     * set white list ,host in white list can invoke all method
     */
    public JSBridgeRegister whiteList(String host) {
        whiteList.add(host);
        return this;
    }

    /**
     * is host in white list
     */
    public boolean isInWhiteList(String host) {
        return whiteList.contains(host);
    }

    /**
     * find method instance
     */
    public XJavaMethod findMethod(String funcName) {
        if (javaMethodCache == null) {
            javaMethodCache = new LruCache<>(javaMethod.size()/2);
        }
        XJavaMethod method = javaMethodCache.get(funcName);

        if (method == null) {
            Class clazz = javaMethod.get(funcName);
            try {
                method = (XJavaMethod) clazz.newInstance();
                if (initializer != null) initializer.init(funcName, method);
                javaMethodCache.put(funcName, method);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
       return method;
    }

    /**
     * release method, avoid memory leak
     */
    public void release() {
        Map<String, XJavaMethod> map = javaMethodCache.snapshot();
        for (Map.Entry<String, XJavaMethod> entry: map.entrySet()) {
            if (entry.getValue() != null) entry.getValue().release();
        }
        javaMethodCache.evictAll();
    }
}
