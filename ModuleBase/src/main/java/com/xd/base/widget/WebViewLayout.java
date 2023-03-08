package com.pw.base.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pw.base.R;


/**
 * Created by developer on 2019/7/8.
 */
public class WebViewLayout extends LinearLayout {

    private LayoutInflater inflater;
    private View titleView;//头部
    private ProgressBar progressbar;//进度条
    private WebView webView;//网页
    private View errorView;

    private ImageView titleLeft;//返回
    private ImageView titleBefore;//返回前一个网页
    private TextView titleText;//标题
    private ImageView titleNext;//进入下一个网页
    private ImageView titleRight;//刷新

    private boolean isUpdateTitle;//是否根据网页改变title
    private boolean isShowIconBack;//是否显示上一页下一页图标
    private boolean isJavaScriptEnabled;//是否允许JavaScript
    private int titleHeight;//头部高度

    private WebViewCallBack callBack;//回调

    private boolean loading = false;

    public WebViewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public WebViewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init(Context context, AttributeSet attrs) {
        setOrientation(LinearLayout.VERTICAL);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WebViewLayout);
        isUpdateTitle = typedArray.getBoolean(R.styleable.WebViewLayout_isUpdateTitle, false);
        isShowIconBack = typedArray.getBoolean(R.styleable.WebViewLayout_isShowIconBack, false);
        isJavaScriptEnabled = typedArray.getBoolean(R.styleable.WebViewLayout_isJavaScriptEnabled, false);
        typedArray.recycle();

        //添加头部
        int selectTextSize = getResources().getDimensionPixelSize(R.dimen.title_height_45);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        titleView = inflater.inflate(R.layout.webview_title_bar, null, false);
        titleView.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, selectTextSize));
        titleLeft = (ImageView) titleView.findViewById(R.id.title_left);
        titleBefore = (ImageView) titleView.findViewById(R.id.title_before);
        titleText = (TextView) titleView.findViewById(R.id.title_text);
        titleNext = (ImageView) titleView.findViewById(R.id.title_next);
        titleRight = (ImageView) titleView.findViewById(R.id.title_right);
        progressbar = (ProgressBar) titleView.findViewById(R.id.progress);
        addView(titleView);

        //添加webview
        webView = new WebView(context);
        webView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        webView.getSettings().setJavaScriptEnabled(isJavaScriptEnabled);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        initWebView(webView);
        addView(webView);

        errorView = LayoutInflater.from(context).inflate(R.layout.view_swipstatus, null, false);
        errorView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.reload();
            }
        });
        errorView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        errorView.setVisibility(GONE);
        addView(errorView);

        setTitleView();//设置标题栏
    }

    /**
     * 设置标题栏
     */
    private void setTitleView() {
        titleLeft.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null) {
                    callBack.backOnclick();
                }
            }
        });
        titleBefore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.goBack();
            }
        });
        titleNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.goForward();
            }
        });
        titleRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loading) {
                    webView.stopLoading();
                    loading = false;
                    titleRight.setImageDrawable(getResources().getDrawable(R.mipmap.icon_close));
                    return;
                }
                webView.reload();
            }
        });
    }

    public class WebViewClient extends android.webkit.WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            if (isUpdateTitle)
                titleText.setText(view.getTitle());

            boolean back = view.canGoBack();
            boolean forward = view.canGoForward();
            if (back || forward) {
                titleBefore.setVisibility(isShowIconBack && back ? View.VISIBLE : View.GONE);
                titleNext.setVisibility(isShowIconBack && forward ? View.VISIBLE : View.GONE);
            } else {
                titleBefore.setVisibility(View.GONE);
                titleNext.setVisibility(View.GONE);
            }
        }
        //在开始加载网页时会回调
        @Override
        public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
            super.onPageStarted(webView, s, bitmap);
            errorView.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
        }
        //加载错误的时候会回调
        @Override
        public void onReceivedError(WebView webView, int i, String s, String s1) {
            super.onReceivedError(webView, i, s, s1);
            try {
                errorView.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @SuppressLint("NewApi")
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            if (request.isForMainFrame()) {
                errorView.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);
            }
        }
    }

    public class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressbar.setVisibility(GONE);
                titleRight.setImageDrawable(getResources().getDrawable(R.mipmap.icon_refresh));
                loading = false;
            } else {
                if (progressbar.getVisibility() == GONE)
                    progressbar.setVisibility(VISIBLE);
                progressbar.setProgress(newProgress);
                titleRight.setImageDrawable(getResources().getDrawable(R.mipmap.icon_close));
                loading = true;
            }
            super.onProgressChanged(view, newProgress);
        }

    }

    /**
     * 设置是否需要根据网页内容改变标题
     * @param needUpdate
     */
    public void setUpdateTitle(Boolean needUpdate) {
        isUpdateTitle = needUpdate;
    }

    public WebView getWebView() {
        return webView;
    }

    /**
     * 设置标题栏文字，只有在isUpdateTitle为false时有用
     */
    public void setTitleText(String text) {
        if (!isUpdateTitle) {
            titleText.setText(text);
        }
    }

    /**
     * 设置标题栏文字，只有在isUpdateTitle为false时有用
     */
    public void setTitleText(int textRes) {
        if (!isUpdateTitle) {
            titleText.setText(textRes);
        }
    }

    /**
     * 设置标题栏是否隐藏
     */
    public void setTitleVisibility(boolean isVisible) {
        if (isVisible) {
            titleView.setVisibility(View.VISIBLE);
        } else {
            titleView.setVisibility(View.GONE);
        }
    }

    /**
     * 加载网页
     * created by ydy on 2016/7/15 10:14
     */
    public void loadUrl(String url) {
        webView.loadUrl(url);
    }

    public void setWebViewCallBack(WebViewCallBack callBack) {
        this.callBack = callBack;
    }

    public interface WebViewCallBack {
        void backOnclick();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
            if (webView.canGoBack()) {
                webView.goBack();
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }


    private void initWebView(WebView webView) {
        WebSettings mSettings = webView.getSettings();
        mSettings.setJavaScriptEnabled(true);//开启javascript
        mSettings.setBlockNetworkImage(false);
        mSettings.setDomStorageEnabled(true);//开启DOM
        mSettings.setAppCacheMaxSize(1024 * 1024 * 8);//设置缓冲大小，我设的是8M
        mSettings.setUseWideViewPort(true); // 关键点
//        mSettings.setDatabasePath(appCacheDir);
//        mSettings.setDefaultTextEncodingName("utf-8");//设置字符编码
        //设置web页面
//        mSettings.setAllowFileAccess(true);//设置支持文件流
        // 设置可以支持缩放
//        mSettings.setSupportZoom(true);
        // 设置可以支持缩放
//        mSettings.setBuiltInZoomControls(true);
        //不显示webView缩放按钮
        mSettings.setDisplayZoomControls(false);
        //扩大比例的缩放 Android4.1，放大功能出现了异常
        mSettings.setUseWideViewPort(true);
        //自适应屏幕
        mSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//设置 缓存模式
        mSettings.setLoadWithOverviewMode(true);// 调整到适合webview大小
        mSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);// 屏幕自适应网页,如果没有这个，在低分辨率的手机上显示可能会异常
        mSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
//        mSettings.setAppCacheEnabled(true);//开启缓存机制
        mSettings.setJavaScriptCanOpenWindowsAutomatically(true);//js和android交互

//        if (Build.VERSION.SDK_INT >= 19) {
//            mSettings.setLoadsImagesAutomatically(true);
//        } else {
//            //导致android4.4系统下图片无法加载出来
//            mSettings.setLoadsImagesAutomatically(false);
//        }
        mSettings.setLoadsImagesAutomatically(true);//自动加载图片
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mSettings.setMixedContentMode();
//        }

    }
}