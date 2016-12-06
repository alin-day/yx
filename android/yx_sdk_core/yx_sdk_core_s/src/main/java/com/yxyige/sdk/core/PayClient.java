package com.yxyige.sdk.core;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.yxyige.sdk.views.PayWebDialog;
import com.yxyige.sdk.views.PayWebDialog.PayWaitCallback;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PayClient extends WebViewClient {


    private PayWaitCallback mCallback;
    private Context mContext;

    public PayClient(Context context, PayWaitCallback callback) {

        this.mCallback = callback;
        this.mContext = context;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        super.shouldOverrideUrlLoading(view, url);
        if (url != null && url.startsWith("intent") && url.contains("alipay")) {
            //特殊处理支付宝
            handleAlipayUrl(url);
        } else {
            //其他跳转
            view.loadUrl(url);
        }

        return true;
    }


    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);

        mCallback.loadError(description);

    }

    @Override
    public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
        super.onPageStarted(view, url, favicon);

        //如果是第一个页面则启动等待界面
        mCallback.loadStart(url);

    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        mCallback.loadFinish();

    }


    private void handleAlipayUrl(String url) {
        // 解析scheme
        Pattern p = Pattern.compile("scheme=(.*?);");
        Matcher m = p.matcher(url);
        m.find();
        String scheme = m.group(1);
        // 解析package
        p = Pattern.compile("package=(.*?);");
        m = p.matcher(url);
        m.find();
        String packageName = m.group(1);
        // 跳转Intent
        Intent it = new Intent(Intent.ACTION_VIEW);
        if (!TextUtils.isEmpty(scheme)) {
            url = url.replaceFirst("intent", scheme);
        }
        it.setData(Uri.parse(url));
        if (!TextUtils.isEmpty(packageName)) {
            it.setPackage(packageName);
        }
        if (PayWebDialog.checkApkExist(mContext, it)) {
            mContext.startActivity(it);
        } else {
            Toast.makeText(mContext, "没有找到可用的应用.", Toast.LENGTH_SHORT).show();
        }
    }
}