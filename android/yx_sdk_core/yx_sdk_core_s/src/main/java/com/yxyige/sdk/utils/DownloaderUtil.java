package com.yxyige.sdk.utils;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.yxyige.sdk.db.DownloaddataBase;
import com.yxyige.sdk.receiver.DownloadReceiver;

import java.io.File;

public class DownloaderUtil {

    private DownloaddataBase downloaddataBase;
    private SQLiteDatabase database;
    private Context context;
    private String URL;
    private String fileName;
    private long id;

    private int downState = 0;

    public DownloaderUtil(Context context) {
        this.context = context;

        downloaddataBase = DownloaddataBase.getDownloaddataBase(context,
                DownloaddataBase.DATABASE);
        database = downloaddataBase.getWritableDatabase();


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
                    request.setVisibleInDownloadsUi(true);
                    request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
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
                    } finally {

                        try {


                            ContentValues values = new ContentValues();
                            values.put("url", URL);
                            values.put("downloading", downState + "");
                            values.put("filename", fileName);
                            values.put("id", id + "");
                            database.insert(DownloaddataBase.TABLE, null, values);

                        } catch (Exception e2) {
                            e2.printStackTrace();
                        }

                    }


                }
            });


            //如果有id下载记录，则查询是否已经下载失败
            Intent intent = new Intent();
            intent.setAction(DownloadReceiver.ACTION_DOWN_QUERY);
            context.sendBroadcast(intent);

            Cursor cursor = database.query(DownloaddataBase.TABLE,
                    new String[]{"url,id,downloading"}, "url=?",
                    new String[]{URL}, null, null, null);

            String target = "0";

            if (cursor.moveToFirst()) {

                target = cursor.getString(cursor.getColumnIndex("downloading"));

            }

			/*
             *  -1 是下载出错，调用外部web进行下载
			 *  0  是没有下载过
			 *  1  是下载中
			 *  2  是下载完成
			 */


            if (target.equals("-1")) {

                Toast.makeText(context, "使用浏览器进行文件下载", Toast.LENGTH_SHORT).show();
                downState = 2;

                Uri uri1 = Uri.parse(URL);
                Intent dIntent = new Intent(Intent.ACTION_VIEW, uri1);
                context.startActivity(dIntent);

            } else if (target.equals("0")) {
                Toast.makeText(context, "开始下载", Toast.LENGTH_SHORT).show();
                download.start();

            } else if (target.equals("1")) {
                Toast.makeText(context, "当前任务正在下载中", Toast.LENGTH_SHORT).show();

            } else if (target.equals("2")) {

                database.delete(DownloaddataBase.TABLE, "url=?", new String[]{URL});

                File file = new File(getSDPath(context) + "/" + fileName);
                if (file.exists()) {
                    file.delete();
                }
                Toast.makeText(context, "开始下载", Toast.LENGTH_SHORT).show();
                download.start();
            }
        }
    }

}
