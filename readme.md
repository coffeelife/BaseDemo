# Android组件化和模块化开发

借鉴网址：[https://www.jianshu.com/p/00746e6fb48a](https://www.jianshu.com/p/00746e6fb48a)

Github地址：[https://github.com/coffeelife/BaseModuleDemo](https://github.com/coffeelife/BaseModuleDemo)(此处是copy的借鉴网址代码) 。

本文章项目地址：[https://github.com/coffeelife/BaseDemo](https://github.com/coffeelife/BaseDemo)

本项目Github地址：

## 概念

Android模块化开发是将项目分为几个模块开发，主要为了提升开发效率，可以一起同步进行开发，主要解决项目臃肿、耦合严重，代码经过长时间和多个人开发之后，体积会不断增长，很多新加入的伙伴都不会动原来的代码，只会往上增加内容，而模块化开发的话就会把通用的这部分提取出来，避免重复引入。

组件化开发跟我目前接触的组件化开发很类似，即把通用的一些布局，控件提取出来重复利用，reactJs开发便是这样，把组件独立出来，其他人直接按照组件化的定义方式使用就可以，Andorid同样是这样，例如把一些空布局、标题、刷新甚至是webview提取出来，然后提高代码的重复利用率。

## 浅谈

借图

![5ce66eb724ce512060](https://i.loli.net/2019/05/23/5ce66eb724ce512060.png)

我们现有项目直接就是一个项目，加一个base库，加一个网络请求库，其他都是第三方库，其实并不是很清晰明了，所有的模块的业务都集中在app包内。

借图分析：我看了这个人的文章之后，更加明了组件化，它最上层只是一层壳，跟下图一对比就会发现有异曲同工之处，然后分为N个Moudle，不同业务不同Moudle开发，而不同的业务又基于Base类、网络、缓存和工具，然后引入的第三方库同样以library，使用直接引入使用。

![5ce670878dd5356327](https://i.loli.net/2019/05/23/5ce670878dd5356327.png)

这部分内容请直接学习我学习的博客：[https://www.jianshu.com/p/00746e6fb48a](https://www.jianshu.com/p/00746e6fb48a)

## 文章重点

本文章只介绍Base类moudle的部分编写。

项目结构

![5ce679cd5078680955](https://i.loli.net/2019/05/23/5ce679cd5078680955.png)

`AbsBaseActivity.class`

```
public class AbsBaseActivity extends AppCompatActivity implements IBaseActFrag {
    @Override
    public void gStartActivity(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void gStartActivity(Class<? extends Activity> cls) {
        gStartActivity(cls, null);
    }

    /* 打开新的Activity */
    @Override
    public void gStartActivity(Class<? extends Activity> cls, Bundle bundle) {
        Intent intent = new Intent();
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.setClass(this, cls);
        startActivity(intent);
    }

    @Override
    public void gStartShareActivity(String title, String shareContent) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        if (title != null) {
            intent.putExtra(Intent.EXTRA_TITLE, title);
        }
        intent.putExtra(Intent.EXTRA_TEXT, shareContent);
        startActivity(Intent.createChooser(intent,
            getString(R.string.share_message)));
    }

    @Override
    public void gStartImageShare(String shareContent, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("headpic/*");
        if (uri == null) {
            gStartShareActivity(null, shareContent);
            return;
        }
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra(Intent.EXTRA_TEXT, shareContent);
        intent.putExtra("sms_body", shareContent);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent,
            getString(R.string.share_message)));
    }

    /* 打开新的Activity for result */
    @Override
    public void gStartActivityForResult(Class<? extends Activity> cls,
                                        Bundle bundle, int requestCode) {
        Intent intent = new Intent();
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.setClass(this, cls);
        startActivityForResult(intent, requestCode);
    }

    /* 带结果返回上一个activity， 配合gStartActivityForResult使用 */
    @Override
    public void gBackForResult(int resultCode, Bundle bundle) {
        Intent intent = new Intent();
        if (bundle != null) {
            intent.putExtras(bundle);
        }
       setResult(resultCode, intent);
       finish();
    }

    /* 回到之前的Activity */
    @Override
    public void gBackToActivity(Class<? extends Activity> cls, Bundle bundle) {
        Intent intent = new Intent();
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.setClass(this, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    /* 根据url跳转Activity */
    public void gStartActivity(String url, Bundle bundle) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    public void openBrowser(String url) {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            this.startActivity(Intent.createChooser(intent, "请选择浏览器"));
        } else {
            ToastUtils.showToast(getContext(),"请下载浏览器");
        }
    }

    @Override
    public Context getContext() {
        return this;
    }
}

```

`AbsBaseActivity`内容很简单主要是封装了开启Activity的方式，包括回掉方式，打开外部url链接等操作。

`BaseActivity.class`

```
public abstract class BaseActivity extends AbsBaseActivity {
    protected ActivityManager activityManager = ActivityManager.getInstance();
    protected ImmersionBar mImmersionBar;

    private Unbinder unbinder;
    private ViewStub emptyView;
    protected Context mContext;
    protected LoadingDialog loadingDialog;
    protected Bundle mBundle;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (isActionBar()) {
            setContentView(R.layout.activity_base);
            ((ViewGroup) findViewById(R.id.fl_content)).addView(getLayoutInflater().inflate(getLayoutId(), null));
        } else {
            setContentView(getLayoutId());
        }
        //初始化ButterKnife
        unbinder = ButterKnife.bind(this);

        //沉浸式状态栏
        initImmersionBar(R.color.blue);

        //加入Activity管理器
        BaseApplication.getApplication().getActivityManage().addActivity(this);
        if (regEvent()) {
            EventBusUtils.register(this);
        }
        loadingDialog = new LoadingDialog(mContext);

        mBundle = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
        if (mBundle == null) {
            mBundle = new Bundle();
        }

        this.onInitialization(mBundle);
        this.initView(mBundle);
        this.initData(mBundle);

    }


    /**
     * 沉浸栏颜色
     */
    protected void initImmersionBar(int color) {
        mImmersionBar = ImmersionBar.with(this);
        if (color != 0) {
            mImmersionBar.statusBarColor(color);
        }
        mImmersionBar.init();
    }

    public void showToast(String msg) {
        ToastUtils.showToast(this, msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (regEvent()) {
            EventBusUtils.unregister(this);
        }
        //必须调用该方法，防止内存泄漏
        if (mImmersionBar != null) {
            mImmersionBar.destroy();
        }
        //将Activity从管理器移除
        BaseApplication.getApplication().getActivityManage().removeActivity(this);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        initView(mBundle);
    }

    //***************************************空页面方法*************************************
    protected void showEmptyView(String text) {
        showEmptyOrErrorView(text, R.drawable.bg_no_data);
    }

    protected void showEmptyView() {
        showEmptyView(getString(R.string.no_data));
    }

    protected void showErrorView(String text) {
        showEmptyOrErrorView(text, R.drawable.bg_no_net);
    }

    protected void showErrorView() {
        showErrorView(getString(R.string.error_data));
    }

    public void showEmptyOrErrorView(String text, int img) {

        if (emptyView == null) {
            emptyView = findViewById(R.id.vs_empty);
        }
        emptyView.setVisibility(View.VISIBLE);
        findViewById(R.id.iv_empty).setBackgroundResource(img);
        ((TextView) findViewById(R.id.tv_empty)).setText(text);
        findViewById(R.id.ll_empty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPageClick();
            }
        });
    }

    protected void hideEmptyView() {
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
    }

    /**
     * 空页面被点击
     */
    protected void onPageClick() {

    }

    //***************************************空页面方法*********************************


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Logger.e("permissions:" + Arrays.toString(permissions) + " grantResults:" + Arrays.toString(grantResults));

        //如果有未授权权限则跳转设置页面
        if (!requestPermissionsResult(grantResults)) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }


    }

    /**
     * 判断授权结果
     */
    private boolean requestPermissionsResult(int[] grantResults) {
        for (int code : grantResults) {
            if (code == -1) {
                return false;
            }
        }
        return true;
    }


    /**
     * 子类接受事件 重写该方法
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBus(Event event) {
    }


    /**
     * 是否需要ActionBar
     * TODO 暂时用此方法 后续优化
     */
    protected boolean isActionBar() {
        return false;
    }

    /**
     * 需要接收事件 重写该方法 并返回true
     */
    protected boolean regEvent() {
        return false;
    }

    protected abstract int getLayoutId();

    protected abstract void initView(Bundle bundle);

    protected abstract void onInitialization(Bundle bundle);

    protected abstract void initData(Bundle bundle);

}

```

`BaseActivity`包括对布局获取`getLayoutId()`、`initView`、`onInitialization`、`initData`等方法的封装，主要是为了统一初始化、获取布局、初始化布局页面和初始化数据方法。其中还包括对状态栏的初始化操作，`showtoast()`方法的封装，还有对`EventBus`的封装操作。

`MainActivity.class`

```
public class MainActivity extends BaseActivity {

    @Override
    protected void initImmersionBar(int color) {
        super.initImmersionBar(color);//可以重写statusbar
    }

    @Override
    protected boolean regEvent() {
        return super.regEvent();//可以返回true然后重写onEvnentBus()方法
    }

    @Override
    protected int getLayoutId() {
        return 0;//返回页面布局
    }

    @Override
    protected void initView(Bundle bundle) {
        showToast("主页");//直接调用显示toast
        //初始化view
    }

    @Override
    protected void onInitialization(Bundle bundle) {
//获取初始化页面bundle值和初始化操作
    }

    @Override
    protected void initData(Bundle bundle) {
    //获取数据
    }

    @Override
    public void onEventBus(Event event) {
        super.onEventBus(event);//可以重写
    }
}

```



使用`EventBus`只需要重写`regEvent`和`onEventBus`方法

```
@Override
 protected boolean regEvent() {
 return true;
 }
 
@Override
 public void onEventBus(Event event) {
 super.onEventBus(event);
 }
```

`AbsBaseFragment`和`BaseFragment`与`AbsBaseAchtivity`和`BaseActivity`类似，此处不做具体介绍。

`BaseMvpActivity.class`

```
@SuppressWarnings("unchecked")
public abstract class BaseMvpActivity<P extends BasePresenter> extends BaseActivity implements IBaseView {
    protected P presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //创建present
        presenter = createPresenter();
        if (presenter != null) {
            presenter.attachView(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.detachView();
            presenter = null;
        }

    }


    //***************************************IBaseView方法实现*************************************
    @Override
    public void showLoading() {
        if (loadingDialog != null && !loadingDialog.isShowing()) {
            loadingDialog.show();
        }
    }

    @Override
    public void dismissLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onEmpty(Object tag) {

    }

    @Override
    public void onError(Object tag, String errorMsg) {

    }

    @Override
    public Context getContext() {
        return mContext;
    }
    //***************************************IBaseView方法实现*************************************

    /**
     * 创建Presenter
     */
    protected abstract P createPresenter();
}

```

`BaseMvpAcitvity`主要是往上封装了一层Mvp的基类，这里主要为了方便使用Mvp，`Presenter`作为可选参数引入，然后在基类中初始化，Mvp如何使用这里不做介绍啊，个人感觉MVP模式虽然将moudle、present和view分开，但是需要建很多类，解耦处理的很好，但是文件很多很复杂，不如原始MVC使用的方便。`BaseMvpFragment`跟`BaseMvpActivity`类似，不做解释。

`BaseMvpSwipeBackActivity.class`

```
/**
 * 带滑动返回的mvp基类
 * @author gm
 * @param <P>
 */
public abstract class BaseMvpSwipeBackActivity<P extends BasePresenter> extends BaseMvpActivity<P> {

    protected SlidingLayout rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        rootView = new SlidingLayout(this);
        rootView.bindActivity(this);
        super.onCreate(savedInstanceState);
    }

    protected void setSliding(boolean isSliding) {
        rootView.setIsSlide(isSliding);//Activity可以重写禁止，但更好的方式是直接使用BaseActivity
    }
}
```

`BaseMvpSwipeBackActivity`主要是为了封装带有跟ios类似的滑动返回封装的基类，直接继承重写，如果有情况不需要直接重写`setSliding`方法

`BaseWebviewActivity.class`

```
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
```

这里封装的是包含有`webview`的基类，我这里面用到的是腾讯的x5webview，主要是为了兼容不同手机，当然也是加快网页加载速度，虽然我没有感觉出来快很多，但是原生的webview无法兼容全部手机。兼容不是重点，这里面做了一层封装，这个是带有进度条和bridge桥的webview。主要是为了原生和h5进行交互

我们项目用到了h5调用原生返回按钮、调用原生扫二维码、调用原生手机图片等一系列功能，我的Demo程序大部分都去掉了只留了几个案例。

`JSBridge`使用事例

```
 //调用退出登录
   mWebView.getWebView().registerHandler("loginoutJSBridge", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                Logger.d("点击退出");
                finish();
            }
        });


//调用获取图片       mWebView.getWebView().registerHandler("getImagesJSBridge", new BridgeHandler() {

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

//调用二维码       
        mWebView.getWebView().registerHandler("QRCodeJSBridge", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                barcodeCallback = function;
            }
        });
        
//调用关闭返回
 mWebView.getWebView().registerHandler("backJSBridge", new BridgeHandler() {
 @Override
 public void handler(String data, CallBackFunction function) {
 this.finish();
 }
 });
```

回退封装，如果有历史先回上一页，滑动返回不支持回历史功能

```
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
```

cookie带入封装

```
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
```

此处cookie引入见仁见智。

`BaseFragmentAdapter.class`

```
public class BaseFragmentAdapter extends FragmentPagerAdapter {

    public static class TabInfo {
        public Class<?> clss;
        public String fTitle;
        public Bundle args;
        public String tag;
    }

    private final Context mContext;
    private List<TabInfo> infoList;

    public BaseFragmentAdapter(Context context, FragmentManager fm, List<TabInfo> infoList) {
        super(fm);
        this.mContext = context;
        this.infoList = infoList;
    }

    @Override
    public Fragment getItem(int position) {
        TabInfo tabInfo = infoList.get(position);
        Fragment instantiate = Fragment.instantiate(mContext, tabInfo.clss.getName(), tabInfo.args);
        return instantiate;
    }

    @Override
    public int getCount() {
        return infoList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return infoList.get(position).fTitle;
    }
}

```

`BaseFragmentAdapter`主要是为了`viewpager+Fragment`布局使用，这节放入一个`TabInfo`的数组对象，然后绑定viewpager就可以看到效果，和tab一起配合使用效果更好。`BaseFragmentStateAdapter`类似，只是发生状态可以即时改变，没有缓存。

`CommonRecyclerAdapter.class`

```
public abstract class CommonRecyclerAdapter<T> extends RecyclerView.Adapter<CommonRecyclerViewHolder> {
    protected Context mContext;
    protected int mLayoutId;
    public List<T> mDatas = new ArrayList<>();
    protected LayoutInflater mInflater;

    private OnItemClickListener mOnItemClickListener;


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
    public CommonRecyclerAdapter(Context context, int layoutId) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId;
    }
    public CommonRecyclerAdapter(Context context, int layoutId, List<T> datas) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mLayoutId = layoutId;
        mDatas = datas;
    }

    @Override
    public CommonRecyclerViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        CommonRecyclerViewHolder viewHolder = CommonRecyclerViewHolder.get(mContext, null, parent, mLayoutId, -1);
        setListener(parent, viewHolder, viewType);
        return viewHolder;
    }

    protected int getPosition(CommonRecyclerViewHolder viewHolder) {
        return viewHolder.getAdapterPosition();
    }

    protected boolean isEnabled(int viewType) {
        return true;
    }


    protected void setListener(final ViewGroup parent, final CommonRecyclerViewHolder viewHolder, int viewType) {
        if (!isEnabled(viewType)) return;
        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = getPosition(viewHolder);
                    mOnItemClickListener.onItemClick(parent, v, mDatas.get(position), position);
                }
            }
        });
        viewHolder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = getPosition(viewHolder);
                    return mOnItemClickListener.onItemLongClick(parent, v, mDatas.get(position), position);
                }
                return false;
            }
        });
    }

    @Override
    public void onBindViewHolder(CommonRecyclerViewHolder hepler, int position) {
        hepler.updatePosition(position);
        convert(hepler, mDatas.get(position));
    }

    public abstract void convert(CommonRecyclerViewHolder holder, T t);

    @Override
    public int getItemCount() {
        return mDatas == null ? 0 : mDatas.size();
    }


    public interface OnItemClickListener<T> {
        void onItemClick(ViewGroup parent, View view, T t, int position);

        boolean onItemLongClick(ViewGroup parent, View view, T t, int position);
    }

}
```

`CommonRecyclerAdapter`主要是为了封装`recycleview`的`adapter`适配器，方便结合`recycleview`使用，把一些重复的代码封装起来，使用的话直接继承然后重写就可以，更强大的可以移步BaseQuickAdapter，Github上有库，也挺好用的。`CommonAdapter`可以`listview`、`viewpager`或者`gridview`使用。

## 总结

本编文章主要借鉴了大神的想法和文章思路，想要学习的请移步大神链接：[https://www.jianshu.com/p/00746e6fb48a](https://www.jianshu.com/p/00746e6fb48a)，主要内容在介绍Base基类的封装，在基础上增加了webview类的封装，增加JSBridge和Cookie操作，并增加了Adapter的基类，如果觉得不好用也可以在网上搜索使用其它，时间紧迫，写的比较仓促简单，代码后续会更新，此致敬礼。

借鉴网址：[https://www.jianshu.com/p/00746e6fb48a](https://www.jianshu.com/p/00746e6fb48a)

Github地址：[https://github.com/coffeelife/BaseModuleDemo](https://github.com/coffeelife/BaseModuleDemo)(此处是copy的借鉴网址代码) 。

本文章项目地址：[https://github.com/coffeelife/BaseDemo](https://github.com/coffeelife/BaseDemo)




