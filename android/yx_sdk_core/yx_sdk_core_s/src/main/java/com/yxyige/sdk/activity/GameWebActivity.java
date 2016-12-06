package com.yxyige.sdk.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yxyige.sdk.utils.AppUtils;
import com.yxyige.sdk.utils.Util;

import java.lang.reflect.Field;

public class GameWebActivity extends Activity {

    private static final String TEL = "tel:";
    private static final String MAILTO = "mailto:";
    private static final String QQ = "qq:";
    private static final String WEIXIN = "weixin:";
    private static final String WEICHAT = "weichat:";
    private static final int SMS_TYPE = 0;
    private static final int MAIL_TYPE = 1;
    private static final int MOBILEQQ_TYPE = 2;
    private static final int WEIXIN_TYPE = 3;
    private static final int QQWEIBO_TYPE = 4;
    private static final int SINAWEIBO_TYPE = 5;
    private static final int RENREN_TYPE = 6;
    private static final int QQZONE_TYPE = 7;
    Handler colseHandler = new Handler() {

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);

            finish();
        }
    };
    private RelativeLayout header;
    private ImageButton togame;
    private TextView title;
    private WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String url = getIntent().getExtras().getString("url");
        String titleStr = getIntent().getExtras().getString("title");
        setContentView(Util.getIdByName("yx_web_dialog", "layout", GameWebActivity.this.getPackageName(), GameWebActivity.this));


        // 杩涘叆鍗抽殣钘忚蒋閿洏
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                        | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        header = (RelativeLayout) findViewById(Util.getIdByName("header", "id", GameWebActivity.this.getPackageName(), GameWebActivity.this));
        title = (TextView) findViewById(Util.getIdByName("title", "id", GameWebActivity.this.getPackageName(), GameWebActivity.this));
        togame = (ImageButton) findViewById(Util.getIdByName("togame", "id", GameWebActivity.this.getPackageName(), GameWebActivity.this));
        togame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (webView != null) {

                    webView.stopLoading();
                }

                finish();
            }
        });

        title.setText(titleStr);

        webView = (WebView) findViewById(Util.getIdByName("webView", "id", GameWebActivity.this.getPackageName(), GameWebActivity.this));

        webView.setBackgroundColor(Color.WHITE);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVisibility(View.VISIBLE);
        webView.getSettings().setSavePassword(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setFocusable(true);
        webView.setDownloadListener(new MyWebViewDownLoadListener());

        webView.setWebViewClient(new WebViewClient() {


            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                view.stopLoading();
                view.clearView();
                title.setText("缃戠粶寮傚父");
                webView.loadData(getResources().getString(Util.getIdByName("kefu_webview_404", "string", getPackageName(), GameWebActivity.this)), "text/html; charset=UTF-8", null);
            }


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (url.startsWith(TEL)) {

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);

                } else if (url.startsWith(QQ)) {

                    String qq = url.substring(3);
                    shareTo(MOBILEQQ_TYPE, qq);

                } else if (url.startsWith(MAILTO)) {

                    Uri uri = Uri.parse(url);
                    Intent it = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(it);

                } else if (url.startsWith(WEIXIN)) {

                    shareTo(WEIXIN_TYPE, url);

                } else {
                    /* 褰撳紑鍚柊鐨勯〉闈㈢殑鏃讹拷?锟界敤webview鏉ヨ繘琛屽鐞嗭拷?锟戒笉鏄敤绯荤粺鑷甫鐨勬祻瑙堝櫒澶勭悊 */
                    view.loadUrl(url);

                }
                return true;
            }
        });

        /**
         * 闃叉杈撳叆妗嗙偣鍑诲悗椤甸潰鏀惧ぇ
         */
        webView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    try {
                        Field defaultScale = WebView.class.getDeclaredField("mDefaultScale");
                        defaultScale.setAccessible(true);
                        //WebViewSettingUtil.getInitScaleValue(VideoNavigationActivity.this, false )/100.0f 鏄垜鐨勭▼搴忕殑锟�?涓柟娉曪紝鍙互鐢╢loat 鐨剆cale鏇夸唬
                        defaultScale.setFloat(webView, 1);
                    } catch (Exception e) {
                        //
                    }
                }
            }
        });

        WebSettings ws = webView.getSettings();
//		ws.setAllowFileAccess(true);
//		ws.setBuiltInZoomControls(true);
//		ws.setSupportZoom(true);
//		ws.setDefaultZoom(ZoomDensity.CLOSE);
//		ws.setRenderPriority(RenderPriority.HIGH);
        ws.setAllowFileAccess(true);
        ws.setJavaScriptEnabled(true);
        ws.setBuiltInZoomControls(false);
        webView.requestFocus();
        webView.loadUrl(url);

    }

    private void shareTo(int type, String content) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        String toastContent = "";
        switch (type) {
            case MOBILEQQ_TYPE:
                toastContent = "宸插鍒禥Q鍙风爜鍒板壀璐寸増";
                Util.copyString2System(this, content, toastContent);
                intent.setAction(Intent.ACTION_SENDTO);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse("imto://qq"));
                break;
            case WEIXIN_TYPE:
                toastContent = "宸插鍒跺井淇″彿鐮佸埌鍓创鏉�";
//                Util.copyString2System(this, content,toastContent);
                intent.setAction(Intent.ACTION_SENDTO);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(content));
                break;
            default:
                break;
        }

        try {
            this.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            if (WEIXIN_TYPE == type) {

                Toast.makeText(this, "璇峰畨瑁呭井淇★紝" + toastContent, Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, "璇峰畨瑁呯Щ鍔≦Q" + toastContent, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            System.out.println("--onBackPressed,canGoBack:" + webView.canGoBack());

            if (webView.canGoBack()) {

                webView.goBack();

            } else {

                colseHandler.sendEmptyMessageDelayed(0, 200);
            }

        }
        return true;
    }

    private class MyWebViewDownLoadListener implements DownloadListener {

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {

            AppUtils.toUri(GameWebActivity.this, url);

        }

    }


}
