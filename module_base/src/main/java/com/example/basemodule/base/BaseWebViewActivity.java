package com.example.basemodule.base;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.example.basemodule.R;
import com.example.basemodule.R2;
import com.example.basemodule.base.mvp.BasePresenter;
import com.example.basemodule.utils.CacheUtils;
import com.example.basemodule.utils.GsonUtils;
import com.example.basemodule.utils.ImageUtils;
import com.example.basemodule.views.jswebview.CustomWebChromeClient;
import com.example.basemodule.views.jswebview.ProgressBarWebView;
import com.example.basemodule.views.jswebview.jsbridge.BridgeHandler;
import com.example.basemodule.views.jswebview.jsbridge.BridgeWebViewClient;
import com.example.basemodule.views.jswebview.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.orhanobut.logger.Logger;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import butterknife.BindView;
import butterknife.OnClick;


public abstract class BaseWebViewActivity<P extends BasePresenter> extends BaseMvpSwipeBackActivity<P> {
    @BindView(R2.id.tv_title)
    protected TextView tvTitle;
    @BindView(R2.id.web_view)
    protected ProgressBarWebView mWebView;
    @BindView(R2.id.ll_back)
    protected LinearLayout llBack;
    protected String url;
    protected String cookie;
    @BindView(R2.id.error_layout)
    LinearLayout errorLayout;
    protected boolean isError = false;
    @BindView(R2.id.error_str)
    TextView errorStr;
    @BindView(R2.id.error_url)
    TextView errorUrl;
    @BindView(R2.id.tv_title_right)
    TextView tvTitleRight;
    private CallBackFunction imgsCallBack = null, addressCallBack = null, barcodeCallback = null, selectOrgCallBack = null;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview_layout;
    }

    @Override
    protected void onInitialization(Bundle bundle) {
        ARouter.getInstance().inject(this);
//        cookie = UserManager.getInstance().getCookie() == null ? "" : UserManager.getInstance().getCookie().getCookie();
        url = bundle.getString("url");
        if (TextUtils.isEmpty(url)) {
            showToast("链接出错了!");
            finish();
            return;
        }
    }

    @Override
    protected void initView(Bundle bundle) {
        initHardwareAccelerate();
        mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        WebSettings webSetting = mWebView.getWebView().getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(false);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(true);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
//        webSetting.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //取得缓存路径
        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();//设置路径
        // API 19 deprecated
        webSetting.setDatabasePath(appCachePath);
        // 设置Application caches缓存目录
        webSetting.setAppCachePath(appCachePath);
        // 设置存储模式 建议缓存策略为，判断是否有网络，有的话，使用LOAD_DEFAULT,无网络时，使用LOAD_CACHE_ELSE_NETWORK
        webSetting.setCacheMode(WebSettings.LOAD_DEFAULT);

        //去掉缩放按钮
        webSetting.setBuiltInZoomControls(true);
        webSetting.setDisplayZoomControls(false);
        // this.getSettingsExtension().setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);//extension
        // settings 的设计

        handleCookie();
        tvTitle.setText("");
        setWebClient();

        initHandler();

    }

    private void initHandler() {

        mWebView.getWebView().registerHandler("loginoutJSBridge", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                Logger.d("点击退出");
                finish();
            }
        });


        mWebView.getWebView().registerHandler("getImagesJSBridge", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                JsonObject jsonObject = GsonUtils.getRootJsonObject(data);
                int count = jsonObject.get("imageCount").getAsInt();
                imgsCallBack = function;
                PictureSelector.create(BaseWebViewActivity.this)
                        .openGallery(PictureMimeType.ofImage())//只显示图片
                        .maxSelectNum(count)//最多选择个数
                        .imageSpanCount(4)//每行显示个数
                        .selectionMode(PictureConfig.MULTIPLE)//是否可以多选
                        .compress(true)
                        .compressMaxKB(512)
                        .previewImage(true)//是否可预览
                        .isCamera(true)//是否启用拍照
                        .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调 onActivityResult code
            }
        });

        mWebView.getWebView().registerHandler("navRightButtonJSBridge", new BridgeHandler() {

            @Override
            public void handler(String eventName, CallBackFunction function) {
                if (isFinishing()) {
                    return;
                }
                if (TextUtils.isEmpty(eventName)) {
                    return;
                }
                Gson gson = new Gson();
                Map map = gson.fromJson(eventName, Map.class);

                String navRightButtonTitle = (String) map.get("navRightButtonTitle");
                if (!TextUtils.isEmpty(navRightButtonTitle)) {
                    tvTitleRight.setVisibility(View.VISIBLE);
                    tvTitleRight.setText(navRightButtonTitle);
                }

            }
        });


        mWebView.getWebView().registerHandler("QRCodeJSBridge", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                barcodeCallback = function;
            }
        });


    }


    /**
     * 启用硬件加速
     */
    private void initHardwareAccelerate() {
        try {
            if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 11) {
                getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }
        } catch (Exception e) {
        }
    }

    private void setWebClient() {
        mWebView.setWebChromeClient(new CustomWebChromeClient(mWebView.getProgressBar(), tvTitle));

        BridgeWebViewClient.BridgeCallBack bridgeCallBack = new BridgeWebViewClient.BridgeCallBack() {
            //网页加载成功回调
            @Override
            public void onSuccess(WebView view, String url) {
                if (isFinishing()) {
                    return;
                }
                if (url.equals("about:blank")) {//空白链接加载处理
                    isError = true;
                    tvTitle.setText("网页加载失败");
                    errorLayout.setVisibility(View.VISIBLE);
                    mWebView.setVisibility(View.GONE);
                    errorUrl.setTextColor(getResources().getColor(R.color.black));
                    return;
                }
                //回调成功后的相关操作
                isError = false;
                errorLayout.setVisibility(View.GONE);
                mWebView.setVisibility(View.VISIBLE);
            }

            //网页加载失败回调
            @Override
            public void onFail(WebView webView, int i, String errStr, String url) {
                if (isFinishing()) {
                    return;
                }
                isError = true;
                if (errorLayout != null) {
                    errorLayout.setVisibility(View.VISIBLE);
                }
                if (mWebView != null)
                    mWebView.setVisibility(View.GONE);
                tvTitle.setText("网页加载失败");
                if (errorStr != null) {
                    errorStr.setText(getString(R.string.string_webview_error, TextUtils.isEmpty(errStr) ? "" : errStr));
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Logger.d("onPageStarted");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Logger.d("onPageFinished");
                if (mWebView == null) {
                    return;
                }
                if (mWebView.getWebView().canGoBack())
                    llBack.setVisibility(View.VISIBLE);
                else
                    llBack.setVisibility(View.GONE);
            }
        };

        WebViewClient webViewClient = mWebView.getWebView().getWebViewClient();
        if (webViewClient instanceof BridgeWebViewClient)
            ((BridgeWebViewClient) webViewClient).setBridgeCallBack(bridgeCallBack);
    }

    @Override
    protected void initData(Bundle bundle) {
//        mWebView.loadUrl("file:///android_asset/index.html");
        mWebView.loadUrl(url);
    }

    @OnClick({R2.id.ll_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R2.id.ll_back:
                goBack();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    /**
     * 回退
     */
    protected void goBack() {
        if (mWebView.getWebView().canGoBack()) {
            mWebView.getWebView().goBack();
        } else {
            this.finish();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.getWebView().destroy();
        }
    }

    /**
     * 调用Js返回
     */
    protected void callJSBack() {
        if (mWebView == null || mWebView.getWebView() == null) {
            return;
        }
        mWebView.getWebView().callHandler("BackListener", "", new CallBackFunction() {
            @Override
            public void onCallBack(String s) {
                try {
                    Logger.e("返回数据" + s);
                    if (s != null && !s.equals("")) {
                        JsonObject backJson = GsonUtils.getRootJsonObject(s);
                        boolean isBack = backJson.get("isBack").getAsBoolean();
                        if (isBack) {
                            if (mWebView.getWebView().canGoBack()) {
                                mWebView.getWebView().goBack();
                            } else {
                                finish();
                            }
                        }
                    }
                } catch (Exception e) {

                }
            }
        });
    }

    private void handleCookie() {
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeSessionCookie();// 移除
        } else {
            cookieManager.removeSessionCookies(null);// 移除
        }

        if (!TextUtils.isEmpty(url)) {
            try {
                URL uri = new URL(url);
//                String host = uri.getHost();
//                URL baseUri = new URL(ApiManager.getInstance().getBaseUrl());
//                String baseHost = baseUri.getHost();
//                URL webUri = new URL(ApiManager.getInstance().getWebURL());
//                String webHost = webUri.getHost();
//                URL taskUri = new URL(ApiManager.getInstance().getTaskUrl());
//                String taskHost = taskUri.getHost();
//                for (int i = 0; i < cookie.split(";").length; i++) {
//                    Logger.e("cookie" + i + cookie.split(";")[i]);
//                    cookieManager.setCookie(host, cookie.split(";")[i]);//指定要修改的cookies
//                    cookieManager.setCookie(baseHost, cookie.split(";")[i]);//指定要修改的cookies
//                    cookieManager.setCookie(webHost, cookie.split(";")[i]);//指定要修改的cookies
//                    cookieManager.setCookie(taskHost, cookie.split(";")[i]);//指定要修改的cookies
//                }
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//                    CookieSyncManager.getInstance().sync();
//                } else {
//                    cookieManager.flush();
//                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    public void reloadUrl(String url) {
        this.url = url;
        handleCookie();
        mWebView.loadUrl(url);
    }


    @OnClick({ R2.id.ll_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R2.id.ll_back:
                this.finish();
                break;
        }
    }

}