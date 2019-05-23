package com.example.basemodule.views.jswebview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.basemodule.views.jswebview.jsbridge.BridgeWebView;
import com.example.basemodule.views.jswebview.jsbridge.CallBackFunction;
import com.example.basemodule.widget.NumberProgressBar;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.WebSettings;

/**
 * 带有进度条的 WebView
 *
 * @author YEZHENNAN220
 * @date 2016-07-07 17:08
 */
public class ProgressBarWebView extends LinearLayout {

    static final String TAG = ProgressBarWebView.class.getSimpleName();

    private NumberProgressBar mProgressBar;
    private BridgeWebView mWebView;

    public ProgressBarWebView(Context context) {
        super(context);
        init(context, null);
    }

    public ProgressBarWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ProgressBarWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ProgressBarWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setOrientation(LinearLayout.VERTICAL);

        // 初始化进度条
        if (mProgressBar == null) {
            mProgressBar = new NumberProgressBar(context, attrs);
        }
        addView(mProgressBar);

        // 初始化webview
        if (mWebView == null) {
            mWebView = new BridgeWebView(context);
        }
        WebSettings webviewSettings = mWebView.getSettings();
        // 判断系统版本是不是5.0或之上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //让系统不屏蔽混合内容和第三方Cookie
            CookieManager.getInstance().setAcceptThirdPartyCookies(mWebView, true);
            webviewSettings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        // 不支持缩放
        webviewSettings.setSupportZoom(true);

        // 自适应屏幕大小
        webviewSettings.setUseWideViewPort(true);
        webviewSettings.setLoadWithOverviewMode(true);
        mWebView.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        addView(mWebView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }


    public NumberProgressBar getProgressBar() {
        return mProgressBar;
    }


    public BridgeWebView getWebView() {
        return mWebView;
    }

    /**
     * Loads the given URL.
     *
     * @param url the URL of the resource to load
     */
    public void loadUrl(String url) {
        mWebView.loadUrl(url);
    }

    public void setWebChromeClient(CustomWebChromeClient chromeClient) {
        mWebView.setWebChromeClient(chromeClient);
    }

    public void send(String data) {
        mWebView.send(data);
    }

    public void send(String data, CallBackFunction responseCallback) {
        mWebView.send(data);
    }
}