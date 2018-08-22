# 基础用法

### 1.基本使用

```java
XWebView xWeb = XWebView.with(webView, this)// 传入原生WebView, 传入LifecycleOwner
					.setWebTitleEnable(titleView)// 开启标题
					.setProgressEnable(progressBar)// 开启加载进度
					.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK)// 设置缓存模式
					.setJSBridgeUrlEnabled(jsRegister, urlParser)// 开启JSBridge
					.loadUrl(url);// 加载url
```

### 2.生命周期

`Lifecycle` 为Google官方的生命周期管理方案，只要是继承自`AppCompatActivity` 或者v4包中的`Fragment`均已实现了`LifecycleOwner` 接口，如果页面没有实现这个接口，需要自行实现或者手动管理生命周期

### 3.标题

该接口会在标题获取成功后回调，可以在回调接口中设置标题，也可以通过一个自定义View实现这个接口

```java
public interface IWebTitle {
    void onTitleReady(String text);
}
```

### 4.加载进度

同标题，可以在回调中操作，也可以通过自定义View实现该接口，库中默认实现了 XWebProgressBar 直接继承自系统的 ProgressBar

```java
public interface IWebProgress {
    void onProgressChanged(int progress);

    void onProgressStart();

    void onProgressDone();
}
```

### 5.基本API

```java
public WebView webView()// 获取注入的WebView
  
public XWebView loadUrl(String url)//加载
public XWebView loadUrl(String url, Map<String, String> additionalHttpHeaders)

public void invokeJavaScript(String func, String... params)// 调用JS函数

public boolean goBack()// 前端页面回退，回退成功返回true，返回false则说明不可回退

public void clearCache(boolean includeDisFile)// 清除缓存

public void clearHistory() // 清除历史记录

public JSBridgeCore JSBridge()// 获取JSBridge 的操作类，一般不需要获取

public WebSettings settings()// 获取WebSettings
```

