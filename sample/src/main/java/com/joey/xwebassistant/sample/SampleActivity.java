package com.joey.xwebassistant.sample;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import com.joey.xwebassistant.sample.JavaMethod.JSAsyncFunc;
import com.joey.xwebassistant.sample.JavaMethod.JSToast;
import com.joey.xwebassistant.sample.JavaMethod.JSToastAuthorized;
import com.joey.xwebassistant.sample.JavaMethod.JSToastPrivate;
import com.joey.xwebassistant.sample.JavaMethod.JSToastPublic;
import com.joey.xwebview.XWebView;
import com.joey.xwebview.jsbridge.JSBridgeRegister;
import com.joey.xwebview.ui.IWebTitle;

import java.util.regex.Pattern;

public class SampleActivity extends AppCompatActivity implements IWebTitle {

    private XWebView webView;
    private EditText etUrl;
    private EditText etJs;
    private boolean authorized;
    private String whiteList;
    private Pattern whiteListPattern;
    private Button btnWhiteList;
    private Button btnAuthorized;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        WebView.setWebContentsDebuggingEnabled(true);
        etUrl = findViewById(R.id.et_url);
        etJs = findViewById(R.id.et_js);

        webView = XWebView.with(findViewById(R.id.wv), this)
                .setWebTitleEnable(this)
                .setCacheMode(WebSettings.LOAD_NO_CACHE)
                .setProgressEnable(findViewById(R.id.progress_bar))
                .setJSBridgeEnabled(register())
                .setJSBridgeAuthorizedChecker(this::isAuthorized)
                .loadUrl("file:///android_asset/index.html");

        etUrl.setText("file:///android_asset/index.html");
        findViewById(R.id.btn_load).setOnClickListener(this::load);
        findViewById(R.id.btn_input).setOnClickListener(v->
                webView.invokeJavaScript("msg", "'"+etJs.getText().toString()+"'"));
        btnWhiteList = findViewById(R.id.btn_whitelist);
        btnWhiteList.setOnClickListener(this::addWhiteList);
        btnAuthorized = findViewById(R.id.btn_authorized);
        btnAuthorized.setOnClickListener(this::authorized);
    }

    private boolean isAuthorized(String javafunc, String url) {
        return authorized;
    }

    private JSBridgeRegister register() {
        JSBridgeRegister register = JSBridgeRegister.create()
                .register("toast_public", JSToastPublic.class)
                .register("toast_private", JSToastPrivate.class)
                .register("toast_authorized", JSToastAuthorized.class)
                .register("async_task", JSAsyncFunc.class)
                .setMethodInitializer((func, method) -> {
                    if (method instanceof JSToast)
                        ((JSToast) method).setContext(SampleActivity.this);
                });
        if (!TextUtils.isEmpty(whiteList)) {
            register.whiteList(whiteList);
        } else if(whiteListPattern != null) {
            register.whiteList(whiteListPattern);
        }
        return register;
    }

    @Override
    public void onTitleReady(String text) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(text);
        }
    }

    @Override
    public void onBackPressed() {
        if (!webView.goBack()) {
            super.onBackPressed();
        }
    }

    private void load(View view) {
        whiteList = "ddd";
        whiteListPattern = Pattern.compile(".*");
        authorized = false;
        addWhiteList(view);
        authorized(view);
        webView.loadUrl(etUrl.getText().toString());
    }

    private void authorized(View view) {
        if (!authorized) {
            authorized = true;
            btnAuthorized.setText("UnAuthorize");
        } else {
            authorized = false;
            btnAuthorized.setText("Authorize");
        }
    }

    private void addWhiteList(View view) {
        if (webView.webView().getUrl().contains("android_asset")) {
            if (whiteListPattern == null) {
                whiteList = null;
                whiteListPattern = Pattern.compile("file:///android_asset.*");
                btnWhiteList.setText("remove whitelist");
            } else {
                whiteListPattern = null;
                btnWhiteList.setText("add whitelist");
            }

        }else {
            if (TextUtils.isEmpty(whiteList)) {
                whiteListPattern = null;
                whiteList = Uri.parse(etUrl.getText().toString()).getHost();
                btnWhiteList.setText("remove whitelist");
            } else {
                whiteList = null;
                btnWhiteList.setText("add whitelist");
            }
        }

        webView.setJSBridgeEnabled(register())
                .setJSBridgeAuthorizedChecker(this::isAuthorized);
        webView.reload();
    }
}
