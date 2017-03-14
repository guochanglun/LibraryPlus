package com.gcl.library.activity;

import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gcl.library.util.MusicUtil;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class WebViewActivity extends AppCompatActivity {

    private com.tencent.smtt.sdk.WebView webView;
    private ProgressBar progressBar;

    private Toolbar mWebViewToolbar;

    private TextView mWebViewTitle;

    private boolean mIsFullScreen = false;

    private boolean mIsBackToMainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        // 初始化控件
        webView = (com.tencent.smtt.sdk.WebView) this.findViewById(R.id.web_view);
        progressBar = (ProgressBar) findViewById(R.id.webview_progress);
        mWebViewToolbar = (Toolbar) findViewById(R.id.web_view_custom_toolbar);
        mWebViewTitle = (TextView) findViewById(R.id.web_view_title);

        // 设置toolbar
        setSupportActionBar(mWebViewToolbar);
        String title = getIntent().getStringExtra("title");
        mWebViewTitle.setText(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mWebViewToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsBackToMainActivity = true;
                finish();
            }
        });

        // 设置webview
        com.tencent.smtt.sdk.WebSettings settings = webView.getSettings();
        settings.setAllowContentAccess(true);
        settings.setJavaScriptEnabled(true);

        settings.setUseWideViewPort(true);
        settings.setAppCacheEnabled(true);

        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setPluginsEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webView.setWebChromeClient(new com.tencent.smtt.sdk.WebChromeClient() {

            // 一个回调接口使用的主机应用程序通知当前页面的自定义视图已被撤职
            IX5WebChromeClient.CustomViewCallback customViewCallback;

            // 进入全屏的时候
            @Override
            public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback callback) {
                // 赋值给callback
                customViewCallback = callback;
                // 设置webView隐藏
                webView.setVisibility(View.GONE);

                // 隐藏toolbar
                mWebViewToolbar.setVisibility(View.GONE);

                // 声明video，把之后的视频放到这里面去
                FrameLayout video = (FrameLayout) findViewById(R.id.viedo);

                // 将video放到当前视图中
                video.addView(view);

                // 横屏显示
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

                // 设置全屏
                setFullScreen();
            }

            // 退出全屏的时候
            @Override
            public void onHideCustomView() {
                if (customViewCallback != null) {
                    // 隐藏掉
                    customViewCallback.onCustomViewHidden();
                }

                // 用户当前的首选方向
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);

                // 退出全屏
                quitFullScreen();

                // 设置WebView可见
                webView.setVisibility(View.VISIBLE);

                // 设置toolbar可见
                mWebViewToolbar.setVisibility(View.VISIBLE);

            }

            @Override
            public void onProgressChanged(com.tencent.smtt.sdk.WebView webView, int i) {
                super.onProgressChanged(webView, i);
                progressBar.setProgress(i);
                if (i >= 70) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

        });

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(com.tencent.smtt.sdk.WebView webView, String s, Bitmap bitmap) {
                super.onPageStarted(webView, s, bitmap);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(com.tencent.smtt.sdk.WebView webView, String s) {
                super.onPageFinished(webView, s);
                progressBar.setVisibility(View.INVISIBLE);
                progressBar.setProgress(0);
            }

            @Override
            public boolean shouldOverrideUrlLoading(com.tencent.smtt.sdk.WebView webView, String s) {
                //webView.loadUrl(s);
                super.shouldOverrideUrlLoading(webView, s);
                return false;
            }


            @Override
            public void onReceivedError(com.tencent.smtt.sdk.WebView webView, int i, String s, String s1) {
                super.onReceivedError(webView, i, s, s1);
                progressBar.setVisibility(View.INVISIBLE);
                progressBar.setProgress(0);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); // 接受网站证书
            }
        });

        String url = getIntent().getStringExtra("url");
        webView.loadUrl(url);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!mIsBackToMainActivity) {
            MusicUtil.pauseWithSystem();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MusicUtil.pauseMusicWithSystem) {
            MusicUtil.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null)
            webView.destroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mIsFullScreen) {
                //退出全屏
                quitFullScreen();
            } else {
                if (webView.canGoBack()) {
                    // goBack()表示返回WebView的上一页面
                    webView.goBack();
                } else {
                    mIsBackToMainActivity = true;
                    this.finish();
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 设置全屏
     */
    private void setFullScreen() {
        mIsFullScreen = true;
        // 设置全屏的相关属性，获取当前的屏幕状态，然后设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 全屏下的状态码：1098974464
        // 窗口下的状态吗：1098973440
    }

    /**
     * 退出全屏
     */
    private void quitFullScreen() {
        mIsFullScreen = false;
        // 声明当前屏幕状态的参数并获取
        final WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(attrs);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

}
