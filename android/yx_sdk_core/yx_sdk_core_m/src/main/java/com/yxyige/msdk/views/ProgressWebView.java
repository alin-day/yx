package com.yxyige.msdk.views;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.yxyige.sdk.utils.AppUtils;

/**
 * 带进度条的WebView
 */
public class ProgressWebView extends WebView {

    private ProgressBar progressbar;

    public ProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        progressbar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        progressbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 5, 0, 0));
        addView(progressbar);
        setWebViewClient(new WebViewClient());
        setWebChromeClient(new WebChromeClient());
        setBackgroundColor(0);
        getSettings().setAppCacheMaxSize(1024 * 1024 * 5);
        String appCachePath = context.getApplicationContext().getCacheDir().getAbsolutePath();
        getSettings().setAppCachePath(appCachePath);
        getSettings().setAllowFileAccess(true);
        getSettings().setAppCacheEnabled(true);
        getSettings().setBuiltInZoomControls(false);
        getSettings().setSupportZoom(false);
        getSettings().setUseWideViewPort(true);
        getSettings().setLoadWithOverviewMode(true);
        getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

        getSettings().setJavaScriptEnabled(true);

        addJavascriptInterface(new SQWebJsObj(), "fee");

    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        progressbar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public class SQWebJsObj {
        /*
         * 打开一个外部连接，附带参数
         */
        @JavascriptInterface
        public void sqOpenUrl(String url) {

            AppUtils.toSdkUrl(getContext(), url);
        }
    }

    public class WebChromeClient extends android.webkit.WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {

            if (newProgress == 100) {
                progressbar.setVisibility(GONE);
            } else {
                if (progressbar.getVisibility() == GONE)
                    progressbar.setVisibility(VISIBLE);
                progressbar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }


    }
}