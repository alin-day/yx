package com.yxyige.sdk.receiver;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Toast;

import com.yxyige.sdk.db.DownloaddataBase;
import com.yxyige.sdk.utils.DownloaderUtil;

import java.io.File;

/**
 * 接收PUSH/网络连接广播
 *
 * @author bds
 */
public class DownloadReceiver extends BroadcastReceiver {

    public static String ACTION_DOWN_QUERY = "android.yx.down.query";
    private Context context;
    private NotificationManager notifyManager;
    private DownloaddataBase downloaddataBase;
    private SQLiteDatabase database;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String action = intent.getAction();
        if (action.equals(ACTION_DOWN_QUERY)) {

            if (android.os.Build.VERSION.SDK_INT >= 9) {
                downloaddataBase = DownloaddataBase.getDownloaddataBase(context,
                        DownloaddataBase.DATABASE);
                database = downloaddataBase.getWritableDatabase();
                new NineDown().updateDownInfo(context);
            }

        } else if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) { // 下载完成

            downloaddataBase = DownloaddataBase.getDownloaddataBase(context,
                    DownloaddataBase.DATABASE);
            database = downloaddataBase.getWritableDatabase();

            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);

            Cursor cursor = database.query(DownloaddataBase.TABLE, new String[]{
                    "filename"
            }, "id=?", new String[]{
                    id + ""
            }, null, null, null);
            String fileName = "";
            if (cursor.moveToFirst()) {
                fileName = cursor.getString(0);
            }
            cursor.close();
            if (fileName != null && !"".equals(fileName) && android.os.Build.VERSION.SDK_INT >= 9) // 路径不为�?
                new NineDown().TaskFind(context, id, fileName);
        }
    }

    class NineDown { // 内部类，防止1.6出错

        @TargetApi(9)
        private void updateDownInfo(Context context) {

            DownloaderUtil util = new DownloaderUtil(context);
            Cursor cursor = database.query(DownloaddataBase.TABLE, new String[]{
                    "id", "url"
            }, "downloading=?", new String[]{
                    "1"
            }, null, null, null);
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String url = cursor.getString(1);

                DownloadManager downloadManager = (DownloadManager) context
                        .getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(Long.parseLong(id));

                try {
                    Cursor cursorSys = downloadManager.query(query);

                    if (cursorSys.moveToFirst()) {
                        int status = cursorSys.getInt(cursorSys
                                .getColumnIndex(DownloadManager.COLUMN_STATUS));

                        if (status == DownloadManager.STATUS_FAILED) { // 下载失败了的
                            database.delete(DownloaddataBase.TABLE, "url = ?", new String[]{
                                    url
                            }); // 把旧记录删除�?
                            util.download(url, "apk");
                        }
                    } else {

                        database.delete(DownloaddataBase.TABLE, "url = ?", new String[]{
                                url
                        });

                    }

                    cursorSys.close();

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("--> sq down query error! ");
                }

            }
            cursor.close();

        }


        @TargetApi(9)
        private void checkGoOnDown(Context context) { // �?测数据库，有未完成的继续下载
            DownloaderUtil util = new DownloaderUtil(context);
            Cursor cursor = database.query(DownloaddataBase.TABLE, new String[]{
                    "id", "url"
            }, "downloading=?", new String[]{
                    "1"
            }, null, null, null);
            while (cursor.moveToNext()) {
                String id = cursor.getString(0);
                String url = cursor.getString(1);

                DownloadManager downloadManager = (DownloadManager) context
                        .getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(Long.parseLong(id));

                Cursor cursorSys = downloadManager.query(query);

                if (cursorSys == null) {

                    return;
                }

                if (cursorSys.moveToFirst()) {
                    int status = cursorSys.getInt(cursorSys
                            .getColumnIndex(DownloadManager.COLUMN_STATUS));

                    if (status == DownloadManager.STATUS_FAILED) { // 下载失败了的
                        database.delete(DownloaddataBase.TABLE, "url = ?", new String[]{
                                url
                        }); // 把旧记录删除�?
                        util.download(url, "apk");
                    }
                }
                cursorSys.close();
            }
            cursor.close();
        }


        @TargetApi(9)
        private void TaskFind(Context context, long id, String fileName) {
            DownloadManager downloadManager = (DownloadManager) context
                    .getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(id);
            Cursor cursor = downloadManager.query(query);
            if (cursor.moveToFirst()) {
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    // 更新数据�?
                    ContentValues values = new ContentValues();
                    values.put("downloading", 2);
                    database.update(DownloaddataBase.TABLE, values, "id=?", new String[]{
                            id + ""
                    });

                    if (DownloaderUtil.getSDPath(context) != null) {
                        Uri uri = Uri.fromFile(new File(DownloaderUtil.getSDPath(context) + "/"
                                + fileName)); // 这里是APK路径
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, "application/vnd.android.package-archive");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }

                } else if (status == DownloadManager.STATUS_FAILED) {
                    // 更新数据�?
                    ContentValues values = new ContentValues();
                    values.put("downloading", 0);
                    database.update(DownloaddataBase.TABLE, values, "id=?", new String[]{
                            id + ""
                    });
                    Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
                }
            }
            cursor.close();
        }
    }

}
