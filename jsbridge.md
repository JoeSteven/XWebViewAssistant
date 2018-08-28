# JsBridge 模块

**注意：如果最低支持API高于19，请直接使用Android官方提供的方式，效率更高，另外本库的jsbridge方式需要优化为队列方式，请暂时不要使用本库到商业项目中**
本库采用注册的方式来给前端页面提供 Java 方法，尽量实现每个方法逻辑单一，可复用，便于权限管理提供安全性

### 1.实现 Java 方法

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