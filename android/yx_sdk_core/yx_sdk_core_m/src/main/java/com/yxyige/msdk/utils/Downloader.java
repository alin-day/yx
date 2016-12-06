package com.yxyige.msdk.utils;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;

public class Downloader {

    private Context context;
    private String URL;
    private String fileName;
    private long id;

    private int downState = 0;

    public Downloader(Context context) {
        this.context = context;

    }

    public static String getSDPath(Context context) {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            File dirFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/");
            try {

                if (!(dirFile.exists() && !(dirFile.isDirectory()))) {
                    dirFile.mkdirs();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return dirFile.getAbsolutePath();
        }
        return null;

    }

    //type未后缀名
    public void download(String URL, String type) {


        if (URL == null || "".equals(URL)) { //下载链接为空，不做处理
            return;
        }

        this.URL = URL;
        if (URL.endsWith("." + type))
            fileName = URL.substring(URL.lastIndexOf("/") + 1);
        else
            fileName = System.currentTimeMillis() + "." + type;

        if (getSDPath(context) != null) {

            String fpath = getSDPath(context) + "/" + fileName;

            File file = new File(fpath);
            if (file.exists()) {
                file.delete();
            }

            Download();
        } else {
            Toast.makeText(context, "请插入SD卡", Toast.LENGTH_SHORT).show();
        }
    }

    private String Download() {

        if (android.os.Build.VERSION.SDK_INT >= 9) {
            try {
                new LevelNineDown().down();
            } catch (Exception e) {
                e.printStackTrace();
                Uri uri = Uri.parse(URL);
                Intent dIntent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(dIntent);
            }

        } else {
            Uri uri = Uri.parse(URL);
            Intent dIntent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(dIntent);
        }

        return null;

    }

    @TargetApi(9)
    class LevelNineDown {

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);


        private void down() throws Exception {


            Thread download = new Thread(new Runnable() {

                @Override
                public void run() {
                    Uri uri = Uri.parse(URL);
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setAllowedNetworkTypes(Request.NETWORK_MOBILE | Request.NETWORK_WIFI);
                    request.setAllowedOverRoaming(false);
                    MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
                    String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(URL));
                    request.setMimeType(mimeString);
                    request.setShowRunningNotification(true);
                    request.setVisibleInDownloadsUi(true);//下载文件后在下载管理中显示
                    request.setDestinationInExternalPublicDir("/download/", fileName);
                    request.setTitle(fileName);
                    try {
                        id = downloadManager.enqueue(request);
                        downState = 1;

                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("-----enqueue Exception----");

                        downState = -1;

                        Uri uri1 = Uri.parse(URL);
                        Intent dIntent = new Intent(Intent.ACTION_VIEW, uri1);
                        context.startActivity(dIntent);
                        return;
                    }

                }
            });


            download.start();

            IntentFilter filter = new IntentFilter();
            filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

            context.registerReceiver(new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {

                    if (getSDPath(context) != null) {

                        String filepath = getSDPath(context) + "/" + fileName;
                        Uri uri = Uri.fromFile(new File(filepath)); // 这里是APK路径

                        System.out.println("URL:" + filepath);

                        Intent intent_complete = new Intent(Intent.ACTION_VIEW);
                        intent_complete.setDataAndType(uri,
                                "application/vnd.android.package-archive");
                        context.startActivity(intent_complete);
                        context.unregisterReceiver(this);
                    }


                }

            }, filter);


        }
    }

}
