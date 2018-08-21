# XWebViewAssistant 文档

### 概述

XWebViewAssistant 提供给Android开发者更简单的WebView开发方式，基于Android原生`WebView`，轻量封装相关操作

- 链式调用，初始化`WebView` 更简洁
- 无`Activity`或者`Fragment` 基类，不需要继承，在任何页面都可以直接使用
- 自动绑定生命周期，无需手动操作，避免内存泄露
- 支持`JSBridge` ，拦截URL或者`onJsPrompt`都的方式二选一（官方注解的方式本身不需要封装，如果使用注解的方式，关闭本库的`JSBridge`功能即可
- `JSBridge` 注册的Java方法支持权限管理，支持双向调用及回调

### Sample

- 可以通过输入框输入url进行加载


- add whitelist 和 authorize  这两个按钮分别可以将当前网站加入到白名单或者方法授权


- function: 后面有三个注册的Java方法，分别对应三种不同的权限，public都可以调用，private需要加入白名单可调用， authorized需要白名单或者授权可以调用。


- params：后面为三个方法的参数，json格式
- 最底部输入框为Java调用 js的sample

**该sample的前端调试页面放在asset目录中，你也可以在接入该库的时候使用这个页面进行本地调试，该页面由猴哥-[Jaeger](https://github.com/laobie) 友情赞助**

![sample](./sample.jpeg)

### 依赖



### 使用

#### 1.WebView 基本使用

```java
XWebView xWeb = XWebView.with(webView, this)// 传入原生WebView, 传入LifecycleOwner
					.setWebTitleEnable(titleView)// 开启标题
					.setProgressEnable(progressBar)// 开启加载进度
					.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK)// 设置缓存模式
					.setJSBridgeUrlEnabled(jsRegister, urlParser)// 开启JSBridge
					.loadUrl(url);// 加载url
```

- 生命周期

`Lifecycle` 为Google官方的生命周期管理方案，只要是继承自`AppCompatActivity` 或者v4包中的`Fragment`均已实现了`LifecycleOwner` 接口，如果页面没有实现这个接口，需要自行实现或者手动管理生命周期

- 标题

该接口会在标题获取成功后回调，可以在回调接口中设置标题，也可以通过一个自定义View实现这个接口

```Java
public interface IWebTitle {
    void onTitleReady(String text);
}
```

- 加载进度

同标题，可以在回调中操作，也可以通过自定义View实现该接口，库中默认实现了 XWebProgressBar 直接继承自系统的 ProgressBar

```java
public interface IWebProgress {
    void onProgressChanged(int progress);

    void onProgressStart();

    void onProgressDone();
}
```

- 基本API

```JAVA
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

#### 2.JSBridge 使用

本库采用注册的方式来给前端页面提供 Java 方法，尽量实现每个方法逻辑单一，可复用，便于权限管理提供安全性

- 实现 Java 方法

```java
public class ExampleMethod extends XJavaMethod{

    @Override
    public void call(JSMessage message) {
        //doSomething here
      JsonObject params = message.params
        
      // 执行成功后需要回调，则调用该方法，回调js函数由前端提供，参数为双方约定
      callback(message.callback, "success");
      
      // 异常回调
      callError(message.errorCallback," error");
    }

    @Override
    public Permission permission() {
      // 标记该方法的权限
        return Permission.PUBLIC;
    }
}
```

- 权限管理，每一个注册的 Java 方法都必须指定调用权限

```java
public enum Permission{
	PUBLIC,// 公开方法，所以的网页只要知道该应用的js协议都可以调用

	AUTHORIZED,// 授权方法，只有白名单或者被授权过的网页才可以调用该方法，授权判断由业务层自己实现

	PRIVATE // 私有方法，只有该在该应用中注册了域名白名单的网页可以调用
}

// 检查该网页是否有该方法的授权接口，默认实现为永远返回false
//例如：应用启动的时候从服务端获取一个对应方法的授权网页列表，调用的时候判断该网页是否有授权
public interface IAuthorizedChecker {
    boolean isAuthorized(String javaFunc, String url);
}

// 设置授权检查接口，该方法必须在setJSBridgeUrlEnabled或者setJSBridgePromptEnabled之后调用
xWeb.setJSBridgeAuthorizedChecker(checker);
```

- 构造 `JSBridgeRegister`， 注册 Java 方法

```java
JSBridgeRegister register = JSBridgeRegister.create()
		.register("toast", JSToast.class)// js 约定的方法名及真实方法
		.register("login", JSLogin.class)
		.register("user_info", JSUserInfo.class)
		.whiteList("api.xwebview.com")// 设置域名白名单,支持两种方式，域名白名单，或者是正则匹配
		.whiteListPattern(Pattern.compile("file:///android_asset.*"));
```

- `IMethodInitializer` 方法初始化器

如果注册的 Java 方法依赖外界注入参数，可以通过设置方法初始化接口，在创建该方法实例后注入参数等

```java
register.setMethodInitializer((func, method) -> {
                    if (method instanceof JSToast)
                        ((JSToast) method).setContext(SampleActivity.this);
                });
```

- 实现 JSBridge 协议解析接口

```java
// 拦截url的方式实现JSBridge
public interface IJSBridgeUrlParser {
    JSMessage parse(String url);
}

// 拦截onJsPrompt的方式实现
public interface IJSBridgePromptParser {
    JSMessage parse(String url, String message, String defaultValue, JsPromptResult result);
}

// 满足协议则返回一个 JSMessage 对象，否则返回null
public class JSMessage {
    public String hostUrl;// 调用该方法的网页url，可选
    public String callback;// 执行成功后需要回调的JS函数,可选
    public String errorCallback;// 异常发生时回调的JS函数，可选
    public String javaMethod;// 要调用的Java方法，必须
    public JSONObject params;// Java方法所需要的参数,Json格式，可选
}
```

- 开启`JSBridge`

```java
xWeb.setJSBridgeUrlEnabled(register, urlParser
  //.setJSBridgePromptEnabled(register, promptParser)
  .setJSBridgeAuthorizedChecker(checker);//可选
```

**注意：拦截url和拦截 prompt 方案只能二选一，调用了其中一个就不能开启另外一个**