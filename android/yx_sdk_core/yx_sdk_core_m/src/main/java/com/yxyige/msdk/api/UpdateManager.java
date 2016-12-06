package com.yxyige.msdk.api;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yxyige.msdk.utils.Downloader;
import com.yxyige.msdk.utils.ViewUtils;
import com.yxyige.sdk.utils.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class UpdateManager {
    /*
     * 更新类型:1, 忽略更新;2, 普通更新;3, 强制更新
     */
    public static final int UPDATE_SELECT = 2; //选择更新(可取消)
    public static final int UPDATE_FORCE = 3; //强制更新(不可取消)
    /* 下载中 */
    private static final int DOWNLOAD = 1;
    /* 下载结束 */
    private static final int DOWNLOAD_FINISH = 2;
    /* 保存解析的XML信息 */
    HashMap<String, String> mHashMap;
    /* 下载保存路径 */
    private String mSavePath;
    /* 记录进度条数量 */
    private int progress;
    /* 是否取消更新 */
    private boolean isCancelUpdate = false;
    private Context mContext;
    /* 更新进度条 */
    private ProgressBar mProgress;
    //	private Dialog mDownloadDialog;
    private boolean isForceDown;   //是否为强制更新
    private boolean isFinishDown = false;  //apk包是否下载完成
    private Button down;
    private Button cancel;
    private Dialog downDialog;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 正在下载
                case DOWNLOAD:

                    // 设置进度条位置
//			    System.out.println("进度："+progress);
                    mProgress.setProgress(progress);

                    break;
                case DOWNLOAD_FINISH:

			    /*
                 * 处理apk下载完成逻辑
			     */
                    isFinishDown = true;

                    if (isForceDown) {
                        downDialog.setCancelable(false);
                    } else {
                        downDialog.setCancelable(true);
                    }

                    down.setText("重新安装");
                    cancel.setVisibility(View.GONE);
                    mProgress.setProgress(100);
                    installApk();
                    down.setEnabled(true);

                    break;
                default:
                    break;
            }
        }

    };

    public UpdateManager(Context context) {
        this.mContext = context;
    }


    /**
     * 根据url获取apk文件名
     * def: temp.apk
     *
     * @param url
     * @return
     */
    private String getApkNameFormUrl(String url) {

        String apkName = "temp.apk";

        if (url != null && url.endsWith(".apk")) { //规范的apk文件

            if (url.lastIndexOf("/") != -1) { //

                apkName = url.substring(url.lastIndexOf("/") + 1, url.length());
            }

        }
        return apkName;
    }


    /**
     * 检测软件更新
     *
     * @return
     */
    public void checkUpdate(String utype, String uContent, String updateUrl) {

        boolean isNeedCheckUpdate = true;
        int type = 0;
        try {
            type = Integer.parseInt(utype);
        } catch (Exception e) {
            e.printStackTrace();
            //抛出异常则无须更新
            isNeedCheckUpdate = false;
        }

        isForceDown = (type == UPDATE_FORCE);

        if (isNeedCheckUpdate) {

            switch (type) {
                case UPDATE_SELECT:
                case UPDATE_FORCE:

                    mHashMap = new HashMap<String, String>();

                    mHashMap.put("name", getApkNameFormUrl(updateUrl));
                    mHashMap.put("url", updateUrl);
                    mHashMap.put("content", uContent);

                    showUpdateDialog(uContent, updateUrl, type);

                    break;

                default:
                    break;
            }

        }


    }


    /**
     * 显示更新对话框
     *
     * @param content
     * @param apkUrl
     * @param force
     */
    private void showUpdateDialog(String content, final String apkUrl, final int dtype) {


        final Dialog updateDialog = new Dialog(mContext, Util.getIdByName("ContentOverlay", "style", mContext.getPackageName(), mContext));
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(Util.getIdByName("sy37_update_dialog", "layout", mContext), null);

        TextView contentView = (TextView) view.findViewById(Util.getIdByName("updateContent", "id", mContext));
        Button update = (Button) view.findViewById(Util.getIdByName("toUpdate", "id", mContext));
        Button cancel = (Button) view.findViewById(Util.getIdByName("toCancel", "id", mContext));

        contentView.setText(content);

        update.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                updateDialog.dismiss();
                showDownloadDialog();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                if (isForceDown) {

                    ViewUtils.showToast(mContext, "此版本为强制更新版本，必须更新后才能继续游戏.");
                } else {
                    updateDialog.dismiss();
                }

            }
        });

        updateDialog.setContentView(view);
        updateDialog.setCanceledOnTouchOutside(false);
        updateDialog.setCancelable(false);
        updateDialog.show();

    }

    /**
     * 显示软件下载对话框
     */
    private void showDownloadDialog() {

        downDialog = new Dialog(mContext, Util.getIdByName("ContentOverlay", "style", mContext.getPackageName(), mContext));
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(Util.getIdByName("sy37_update_down_dialog", "layout", mContext), null);
        mProgress = (ProgressBar) view.findViewById(Util.getIdByName("downProcessBar", "id", mContext));
        down = (Button) view.findViewById(Util.getIdByName("toDown", "id", mContext));
        cancel = (Button) view.findViewById(Util.getIdByName("toCancel", "id", mContext));

        //由下载类别判断界面显示
        if (isForceDown) {

            down.setText("重新安装");
            down.setEnabled(false);
            cancel.setVisibility(View.GONE);

        } else {

            down.setText("后台下载");
            down.setEnabled(true);
            cancel.setVisibility(View.VISIBLE);
        }


        down.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (isFinishDown) {

                    installApk();

                } else if (!isForceDown) {

                    isCancelUpdate = true;
                    // TODO call bg down mothed
                    // ...

                    downApkInBg(mHashMap.get("url"));
                    downDialog.dismiss();

                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                isCancelUpdate = true;
                downDialog.dismiss();
            }
        });

        downDialog.setContentView(view);
        downDialog.setCanceledOnTouchOutside(false);
        downDialog.setCancelable(false);
        downDialog.show();

        // 下载文件
        downloadApk();

    }


    /**
     * 后台下载文件
     */
    private void downApkInBg(String url) {

        Downloader downutil = new Downloader(mContext);

        downutil.download(url, "apk");


    }


    /**
     * 下载apk文件
     */
    private void downloadApk() {
        // 启动新线程下载软件
        System.out.println("启动下载线程");
        new downloadApkThread().start();
    }

    /**
     * 安装APK文件
     */
    private void installApk() {
        File apkfile = new File(mSavePath, mHashMap.get("name"));
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        mContext.startActivity(i);
    }

    /**
     * 下载文件线程
     */
    private class downloadApkThread extends Thread {
        @Override
        public void run() {
            try {
                // 判断SD卡是否存在，并且是否具有读写权限
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    // 获得存储卡的路径
                    String sdpath = Environment.getExternalStorageDirectory() + "/";
                    mSavePath = sdpath + "download/temp";
                    URL url = new URL(mHashMap.get("url"));
                    // 创建连接
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    // 获取文件大小
                    int length = conn.getContentLength();
                    // 创建输入流
                    InputStream is = conn.getInputStream();

                    File file = new File(mSavePath);
                    // 判断文件目录是否存在
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    File apkFile = new File(mSavePath, mHashMap.get("name"));
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;
                    // 缓存
                    byte buf[] = new byte[1024];
                    // 写入到文件中
                    do {
                        int numread = is.read(buf);
                        count += numread;

                        // 计算进度条位置
                        progress = (int) (((float) count / length) * 100);
                        // 更新进度
                        mHandler.sendEmptyMessage(DOWNLOAD);
                        if (numread <= 0) {
                            // 下载完成
                            mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                            break;
                        }
                        // 写入文件
                        fos.write(buf, 0, numread);
                    } while (!isCancelUpdate);// 点击取消就停止下载.
                    fos.close();
                    is.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 取消下载对话框显示
//			mDownloadDialog.dismiss();
        }
    }
}