package com.joey.xwebassistant.sample;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.widget.EditText;

import com.joey.xwebassistant.sample.JavaMethod.JSLog;
import com.joey.xwebassistant.sample.JavaMethod.JSToast;
import com.joey.xwebview.XWebView;
import com.joey.xwebview.jsbridge.JSBridgeRegister;
import com.joey.xwebview.ui.IWebTitle;

public class SampleActivity extends AppCompatActivity implements IWebTitle {

    private XWebView webView;
    private EditText etUrl;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        etUrl = findViewById(R.id.et_url);

        findViewById(R.id.btn_load).setOnClickListener(v -> webView.loadUrl(etUrl.getText().toString()));

        webView = XWebView.with(findViewById(R.id.wv), this)
                .setWebTitleEnable(this)
                .setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW)
                .setProgressEnable(findViewById(R.id.progress_bar))
                .setJSBridgeUrlEnabled(register(), new JSBridgeUrlParser())
                .loadUrl("https://www.joey.com");

        webView.invokeJavaScript("log", "hi, this is java");
    }

    private JSBridgeRegister register() {
        return JSBridgeRegister.create()
                .register("toast", JSToast.class)
                .register("log", JSLog.class)
                .whiteList("www.joey.com")
                .setMethodInitializer((func, method) -> {
                    if (method instanceof JSToast)
                        ((JSToast) method).setContext(SampleActivity.this);
                });
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
