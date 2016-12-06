package com.yxyige.msdk.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yxyige.afinal.FinalHttp;
import com.yxyige.afinal.http.AjaxCallBack;
import com.yxyige.afinal.http.HttpHandler;
import com.yxyige.msdk.api.MultiSDKUtils;
import com.yxyige.msdk.api.YXUpdateManager;
import com.yxyige.sdk.utils.Util;

import java.io.File;

public class YXUpdateDialog extends Dialog {

    private Context context;
    private TextView update_notice;//更新通知
    private Button start, hide;//开始和停止下载，隐藏下载按钮
    private View hide_view;//隐藏按钮（可控制隐藏）
    private View progress_view;//隐藏按钮（可控制隐藏）

    private ProgressBar update_progress;//进度条
    private TextView update_size;//更新进度


    private String url, notice, version;//更新地址、提示和版本号
    private boolean isForceUpdate;//flag值识别：1=普通更新和2=强制更新。
    private boolean switch_on = false;//暂停结束标识
    //下载相关
    private FinalHttp http;
    private HttpHandler<?> handler;


    public YXUpdateDialog(Context context) {
        super(context);
    }

    /**
     * 加载更新进度框
     *
     * @param context       上下文
     * @param isForceUpdate 是否强制更新
     * @param notice        更新说明
     * @param url           更新地址
     */
    public YXUpdateDialog(Context context, boolean isForceUpdate, String notice, String url, String version) {
        super(context, MultiSDKUtils.getIdByName("Mdialog", "style", context.getPackageName(), context));
        this.context = context;
        this.isForceUpdate = isForceUpdate;
        this.notice = notice;
        this.url = url;
        this.version = version;
    }


    public YXUpdateDialog(Context context, int theme, boolean isForceUpdate, String notice, String url, String version) {
        super(context, theme);
        this.context = context;
        this.isForceUpdate = isForceUpdate;
        this.notice = notice;
        this.url = url;
        this.version = version;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(Util.getIdByName("yx_update_layout", "layout", context.getPackageName(), context));
        update_notice = (TextView) findViewById(Util.getIdByName("update_notice", "id", context.getPackageName(), context));
        update_size = (TextView) findViewById(Util.getIdByName("update_size", "id", context.getPackageName(), context));
        start = (Button) findViewById(Util.getIdByName("stop_start_btn", "id", context.getPackageName(), context));
        hide = (Button) findViewById(Util.getIdByName("hide_btn", "id", context.getPackageName(), context));
        hide_view = findViewById(Util.getIdByName("hide_view", "id", context.getPackageName(), context));
        progress_view = findViewById(Util.getIdByName("progress_view", "id", context.getPackageName(), context));
        update_progress = (ProgressBar) findViewById(Util.getIdByName("progressbar", "id", context.getPackageName(), context));

        //下载框架初始化
        http = new FinalHttp();

        //View的监听和赋值
        update_notice.setText(notice);
        start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (switch_on) {
                    //暂停
                    switch_on = false;
                    start.setText("开始下载");
                    stopDownload();

                } else {
                    //开始
                    switch_on = true;
                    start.setText("暂停下载");
                    startDownload(url);
                    //显示进度条
                    if (progress_view.getVisibility() == View.GONE) {
                        progress_view.setVisibility(View.VISIBLE);
                    }
                    //非强制更新显示隐藏按钮
                    if (!isForceUpdate) {
                        hide_view.setVisibility(View.VISIBLE);
                    }

                }
            }
        });

        //隐藏按钮消失
        hide.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //隐藏更新框，这是普通更新
                dismiss();
            }
        });

        //设置对话框属性
        setCanceledOnTouchOutside(false);
        //强制更新
        if (isForceUpdate == true) {
            setCancelable(false);
        }

    }


    /**
     * 下载
     *
     * @param down_url
     */
    public void startDownload(String down_url) {

        String targetPath = YXUpdateManager.getSDPath(context);

        if (targetPath != null && !"".endsWith(targetPath)) {
            //存在
            System.out.println("开始下载:" + down_url);

            final String down_name = YXUpdateManager.getFileNameOfUrl(context, down_url, version);
            final String target = targetPath + down_name;

            System.out.println("下载目录:" + target);

            handler = http.download(down_url, target, true, new AjaxCallBack<File>() {

                @Override
                public void onLoading(long count, long current) {
                    long delta = count / 100;
                    int progress = (int) (current / delta);

                    System.out.println("下载进度：" + current + "/" + count);

                    if (current <= count) {
                        update_size.setText(convert2MB(current) + "/" + convert2MB(count));
                        update_progress.setProgress(progress);
                    }

                }

                @Override
                public void onSuccess(File file) {
                    //下载完成，弹出安装界面
                    if (file != null && file.length() > 0) {
                        YXUpdateManager.showTips(context, "下载完成：" + file.getAbsoluteFile().toString());
                        YXUpdateManager.saveFileLength(context, down_name, file.length());
                        YXUpdateManager.checkAndInstall(isForceUpdate, context, file);
                        dismiss();
                    } else {
                        YXUpdateManager.showTips(context, "下载失败:建议在WIFI下重新启动游戏~");
                    }
                }

                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    if (errorNo == 416) {
                        //超出范围了，但是也可能是已经下载完成了。
                        File file = new File(target);
                        if (file != null && file.length() > 0) {
                            YXUpdateManager.checkAndInstall(isForceUpdate, context, file);
                            dismiss();
                        } else {
                            YXUpdateManager.showTips(context, "下载失败:建议在WIFI下重新启动游戏~");
                        }
                    } else if (errorNo == 403 || errorNo == 404) {
                        switch_on = false;
                        start.setText("开始下载");
                        stopDownload();
                        YXUpdateManager.showTips(context, "下载失败:无效的下载链接~");
                    }
                }

            });


        } else {
            YXUpdateManager.showTips(context, "下载失败:请您检查设备存储盘情况。");
        }

    }


    public void stopDownload() {

        System.out.println("请求暂停");
        if (handler != null) {
            System.out.println("确认暂停");
            handler.stop();
        }
    }

    public String convert2MB(long size) {

        int result = (int) (size / 1024 / 1024);//1次到KB，2次MB

        return result + "MB";
    }

}
