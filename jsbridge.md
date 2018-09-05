# JsBridge 模块

**注意：本库底层实现为 @JavascriptInterface 的方式因此只支持到minsdk-18，截止到今天（2018.9.5）minsdk-18 已覆盖90%手机。这种方式相比拦截URL的方式效率上高得多，耗时更低**
本库采用注册的方式来给前端页面提供 Java 方法，尽量实现每个方法逻辑单一，可复用，便于权限管理提供安全性

### 1.实现 Java 方法

提供给 JS 调用的方法需要继承 `XJavaMethod` ，实现两个方法，`call` 和`permission`。

在 `call` 中执行业务代码

- 支持以抛异常的方式来回调前端
- 同步回调，直接返回 `calbackParams` ，将回调所需要的数据放在该 Json 对象中
- return null ，不回调，**如果前端页面注册了回调函数，请务必回调，避免前端页内存泄露**
- 异步回调，在同步代码中return null，然后在异步代码块中调用`callback` 或者`callError` 来分别回调

```java
public class JSToastPublic extends XJavaMethod{

    @Override
    public JSONObject call(JSMessage message, JSONObject callbackParams) throws Exception{ 
      if (TextUtils.isEmpty(message.params.optString("message"))) 
        throw new Exception("no message found from js");
      
        Toast.makeText(context, 
                       message.params.optString("message"),
                       Toast.LENGTH_SHORT).show();
      
      // 同步回调
        return callbackParams.put("message", "invoke JSToastPublic success");
    }

    @Override
    public Permission permission() {
        return Permission.PUBLIC;
    }
}
```

### 2.权限管理

每一个注册的 Java 方法都必须指定调用权限

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

### 3.构造 `JSBridgeRegister`， 注册 Java 方法

```java
JSBridgeRegister register = JSBridgeRegister.create()
		.register("toast", JSToast.class)// js 约定的方法名及真实方法
		.register("login", JSLogin.class)
		.register("user_info", JSUserInfo.class)
		.whiteList("api.xwebview.com")// 设置域名白名单,支持两种方式，域名白名单，或者是正则匹配
		.whiteListPattern(Pattern.compile("file:///android_asset.*"));
```

### 4.`IMethodInitializer` 方法初始化器

如果注册的 Java 方法依赖外界注入参数，可以通过设置方法初始化接口，在创建该方法实例后注入参数等

```java
register.setMethodInitializer((func, method) -> {
                    if (method instanceof JSToast)
                        ((JSToast) method).setContext(SampleActivity.this);
                });
```

### 5.实现 JSBridge 协议解析接口

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

### 6.开启`JSBridge`

```java
xWeb.setJSBridgeUrlEnabled(register, urlParser
  //.setJSBridgePromptEnabled(register, promptParser)
  .setJSBridgeAuthorizedChecker(checker);//可选
```

**注意：拦截url和拦截 prompt 方案只能二选一，调用了其中一个就不能开启另外一个**