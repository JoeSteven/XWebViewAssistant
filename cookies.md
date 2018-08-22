# XWebCookies

该类提供了 WebView Cookie 的相关操作

### 1.初始化

使用之前需要先调用初始化代码，只需要调用一次

```java
XWebCookies.init(context);
```

### 2.同步 Cookie

```java
// 同步cookie在5.0以上会阻塞线程，
XWebCookies.syncCookie(url, cookie);
XWebCookies.syncCookies(url, cookies);
```

### 3.获取Cookie

```java
XWebCookies.getCookie(url);
```

### 4.清除cookie

```Java
XWebCookies.removeAllCookies(callback);
XWebCookies.removeSessionCookies(callback);
XWebCookies.removeExpiredCookies();// WebView会自动移除过期Cookies
```

