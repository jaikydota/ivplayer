package com.ctrlvideo.nativeivview.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import com.ctrlvideo.ivplayer.R;

public class WebActivity extends Activity {

    protected WebView webView;
    private WebChromeClient chromeClient = null;
//    private ProgressBar pbWait;

    private String url;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        webView = findViewById(R.id.web_view);

        url = getIntent().getStringExtra("href_url");

        initViewView();
    }

    @SuppressLint("JavascriptInterface")
    private void initViewView() {


        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        webView.addJavascriptInterface(this, "ctrlvideoAndroid");

//        webView.getBackground().setAlpha(0);

        //设置pc的UserAgant
//        webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36");

        chromeClient = new MyChromeClient();
        webView.setWebChromeClient(chromeClient);
        webView.setWebViewClient(new MyWebViewClient());
//        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVerticalScrollBarEnabled(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.loadUrl(url);
    }


    //按下返回键
    public void clickBackKey() {
        if (nowLayer <= 0) {
            finish(); //层级 == 0 后退出 webView
        } else {
            //否则通知js 按了返回键
            webView.loadUrl("javascript:backKeyClick()");
        }
    }


    //当前层级
    int nowLayer = 0;

    /**
     * 视频切换层级
     *
     * @param isPrevious true:：返回上一层，false：进入下一层
     */
    @JavascriptInterface
    public void videoLayerChange(boolean isPrevious) {
        if (isPrevious)
            nowLayer--;
        else
            nowLayer++;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            clickBackKey();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onResume() {
        if (webView != null) {
            webView.onResume();
        }
        super.onResume();
    }


    @Override
    protected void onPause() {
        if (webView != null) {
            webView.onPause();
        }
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    public class MyChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress > 98) {
//                pbWait.setVisibility(View.GONE);
            }

            //            if (newProgress > 98) {
//                mBar.setVisibility(View.GONE);
//            } else {
//                if (View.GONE == mBar.getVisibility()) {
//                    mBar.setVisibility(View.VISIBLE);
//                }
//                mBar.setProgress(newProgress);
//            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
//            if (myView != null) {
//                callback.onCustomViewHidden();
//                return;
//            }
//            view.setBackgroundColor(Color.BLACK);
//            myView = view;
//            myCallBack = callback;
//            AndroidUtils.hideKeyboard(webView);
        }

        @Override
        public void onHideCustomView() {
//            if (myView == null) {
//                return;
//            }
//            myView = null;
//            myCallBack.onCustomViewHidden();
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            return super.onConsoleMessage(consoleMessage);
        }

        @Override
        public void onCloseWindow(WebView window) {
//            webviewGoBack();
            super.onCloseWindow(window);
        }
    }


    class MyWebViewClient extends WebViewClient {

        // 重写父类方法，让新打开的网页在当前的WebView中显示
        //当返回true时，你点任何链接都是失效的，需要你自己跳转。return false时webview会自己跳转。
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            if(isCallActivity(url)){
//                callActivity(url);
//                return true;
//            }
//            Uri uri = Uri.parse(url);
//            if (url == null || !uri.getScheme().startsWith("http")){
//                return true;
//            }
            return false;
        }

//        @Override
//        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
//            WebResourceResponse response = null;
//            response = super.shouldInterceptRequest(view, url);
//            return response;
//        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
//            String title = view.getTitle();
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
//            webView.loadDataWithBaseURL("", getNativePager("error_page.html"), "text/html", "UTF-8", "");
//            mHaveError = true;
        }
    }


}
