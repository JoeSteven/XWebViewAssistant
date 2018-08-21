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
import android.widget.Button;
import android.widget.EditText;

import com.joey.xwebassistant.sample.JavaMethod.JSToast;
import com.joey.xwebassistant.sample.JavaMethod.JSToastAuthorized;
import com.joey.xwebassistant.sample.JavaMethod.JSToastPrivate;
import com.joey.xwebassistant.sample.JavaMethod.JSToastPublic;
import com.joey.xwebview.XWebView;
import com.joey.xwebview.jsbridge.JSBridgeRegister;
import com.joey.xwebview.ui.IWebTitle;

public class SampleActivity extends AppCompatActivity implements IWebTitle {

    private XWebView webView;
    private EditText etUrl;
    private EditText etJs;
    private String authorized;
    private String whiteList;
    private Button btnWhiteList;
    private Button btnAuthorized;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        etUrl = findViewById(R.id.et_url);
        etJs = findViewById(R.id.et_js);

        webView = XWebView.with(findViewById(R.id.wv), this)
                .setWebTitleEnable(this)
                .setCacheMode(WebSettings.LOAD_NO_CACHE)
                .setProgressEnable(findViewById(R.id.progress_bar))
                .setJSBridgeUrlEnabled(register(), new JSBridgeUrlParser())
                .setJSBridgeAuthorizedChecker(this::isAuthorized);

        findViewById(R.id.btn_load).setOnClickListener(v -> webView.loadUrl(etUrl.getText().toString()));
        findViewById(R.id.btn_input).setOnClickListener(v-> webView.invokeJavaScript("msg", etJs.getText().toString()));
        btnWhiteList = findViewById(R.id.btn_whitelist);
        btnWhiteList.setOnClickListener(this::addWhiteList);
        btnAuthorized = findViewById(R.id.btn_authorized);
        btnAuthorized.setOnClickListener(this::authorized);
    }

    private void authorized(View view) {
        if (TextUtils.isEmpty(authorized)) {
            authorized = Uri.parse(etUrl.getText().toString()).getHost();
            btnAuthorized.setText("UnAuthorize");
        } else {
            authorized = null;
            btnAuthorized.setText("Authorize");
        }
    }

    private void addWhiteList(View view) {
        if (TextUtils.isEmpty(whiteList)) {
            whiteList = Uri.parse(etUrl.getText().toString()).getHost();
            btnWhiteList.setText("remove whitelist");
        } else {
            whiteList = null;
            btnWhiteList.setText("add whitelist");
        }
        webView.setJSBridgeUrlEnabled(register(), new JSBridgeUrlParser());
    }

    private boolean isAuthorized(String javafunc, String url) {
        return TextUtils.equals(authorized, Uri.parse(url).getHost());
    }

    private JSBridgeRegister register() {
        JSBridgeRegister register = JSBridgeRegister.create()
                .register("toast_public", JSToastPublic.class)
                .register("toast_private", JSToastPrivate.class)
                .register("toast_authorized", JSToastAuthorized.class)
                .setMethodInitializer((func, method) -> {
                    if (method instanceof JSToast)
                        ((JSToast) method).setContext(SampleActivity.this);
                });
        if (!TextUtils.isEmpty(whiteList)) {
            register.whiteList(whiteList);
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

}
