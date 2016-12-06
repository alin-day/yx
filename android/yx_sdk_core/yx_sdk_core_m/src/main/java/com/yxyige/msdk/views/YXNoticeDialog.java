package com.yxyige.msdk.views;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.yxyige.msdk.YXMCore;
import com.yxyige.sdk.utils.Util;

public class YXNoticeDialog extends Dialog {

    private CharSequence mTitle;
    private String mUrl;
    private Context context;

    private ProgressWebView webview;

    public YXNoticeDialog(Context context) {
        this(context, Util.getIdByName("Dialog", "style", context.getPackageName(), context));
        this.context = context;
    }

    YXNoticeDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    YXNoticeDialog(Context context, boolean cancelable,
                   OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }

    public static YXNoticeDialog show(Context context, CharSequence msg) {
        return show(context, msg, false);
    }

    public static YXNoticeDialog show(Context context, int id) {
        return show(context, context.getResources().getString(id), false);
    }

    public static YXNoticeDialog show(Context context, CharSequence title, boolean cancelable) {
        YXNoticeDialog dialog = new YXNoticeDialog(context);
        dialog.setTitle(title);
        dialog.setCancelable(cancelable);
        dialog.show();
        return dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setAttributes(p);
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(Util.getIdByName("yx_m_notice_dialog", "layout", context.getPackageName(), context), null);

        if (!TextUtils.isEmpty(mTitle)) {
            TextView textView = (TextView) view.findViewById(Util.getIdByName("yx_m_text_notice_title", "id", context.getPackageName(), context));
            textView.setText(mTitle);
        }


        ImageView closeBtn = (ImageView) view.findViewById(Util.getIdByName("yx_m_img_notice_close", "id", context.getPackageName(), context));


        if (!TextUtils.isEmpty(mUrl)) {

            webview = (ProgressWebView) view.findViewById(Util.getIdByName("yx_m_webview_notice", "id", context.getPackageName(), context));

            DownloadListener listener = new DownloadListener() {

                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                            String mimetype, long contentLength) {

                    System.out.println("noticeDialog收到web下载请求，url：" + url);

                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    getContext().startActivity(intent);

                }
            };
            webview.setDownloadListener(listener); //支持下载连接下载文件


            YXMCore.sendLog("SQNoticeDialog mUrl：" + mUrl);

            webview.loadUrl(mUrl);


        }

        closeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                YXNoticeDialog.this.dismiss();
            }
        });


        setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public void setTitle(CharSequence title) {
        mTitle = title;
    }

    public void setUrl(String url) {
        YXMCore.sendLog("SQNoticeDialog mUrl:" + mUrl);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (webview != null && webview.canGoBack()) {
            webview.goBack();
        }

    }
}
