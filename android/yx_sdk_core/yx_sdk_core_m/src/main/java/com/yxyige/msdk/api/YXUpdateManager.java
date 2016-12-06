package com.yxyige.msdk.api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.yxyige.msdk.YXMCore;
import com.yxyige.msdk.views.YXConfirmDialog;
import com.yxyige.msdk.views.YXConfirmDialog.ConfirmListener;
import com.yxyige.msdk.views.YXUpdateDialog;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class YXUpdateManager {


    /**
     * 记录文件大小用来比对
     */
    public static final String UPDATE_PREF = "update_pref";


    //--------------------------------------一些方法类-----------------------------------

    /**
     * 检查更新
     *
     * @param context
     * @param isForce //是否强制更新
     * @param content //更新内容
     * @param url     //更新地址
     * @param version //更新版本号
     */
    public static void checkUpdate(Context context, boolean isForce, String content, String url, String version) {
        String filename = getFileNameOfUrl(context, url, version);
        String filepath = getSDPath(context) + filename;
        File file = new File(filepath);
        if (file.exists() && readFileLength(context, filename) == file.length()) {
            //文件存在且和记录的大小一致则直接安装。
            checkAndInstall(isForce, context, file);
        } else {
            //不存在文件后者文件大小不一致
            YXUpdateDialog update_dialog = new YXUpdateDialog(context, isForce, content, url, version);
            update_dialog.show();
        }

    }

    public static void saveFileLength(Context context, String filename, long size) {
        SharedPreferences sp = context.getSharedPreferences(UPDATE_PREF, Context.MODE_PRIVATE);
        sp.edit()
                .putLong(filename, size)
                .commit();
    }

    public static long readFileLength(Context context, String filename) {
        SharedPreferences sp = context.getSharedPreferences(UPDATE_PREF, 0);
        return sp.getLong(filename, 0);
    }

    public static String getFileNameOfUrl(Context context, String url, String version) {
        //20151111单身节，加上“.apk”作为结尾，防止出现下载下来后不是apk无法被解析的情况。
        String fileName;
        if (url != null && !"".equals(url)) {

            String pre = url.substring(url.lastIndexOf("/") + 1);

            if (!"".equals(pre) && pre != null) {

                if (pre.contains(".apk")) {
                    fileName = pre;
                } else {
                    fileName = pre + ".apk";
                }

            } else {
                String gid = MultiSDKUtils.getGID(context);
                String pid = MultiSDKUtils.getPID(context);
                String refer = MultiSDKUtils.getRefer(context);
                fileName = gid + "_" + pid + "_" + refer + ".apk";
            }
            //加上版本号以示区别
            fileName = version + "_" + fileName;
            System.out.println("下载的文件名：" + fileName);
            return fileName;

        } else {
            return null;
        }
    }

    /**
     * 获取sd中存储路径
     *
     * @param context
     * @return
     */
    public static String getSDPath(Context context) {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            File dirFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "download");
            try {

                if (!(dirFile.exists() && !(dirFile.isDirectory()))) {
                    dirFile.mkdirs();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return dirFile.getAbsolutePath() + File.separator;
        }
        return null;

    }

    /**
     * 安装apk文件
     *
     * @param context
     * @param file
     */
    public static void installApk(Context context, File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        String type = "application/vnd.android.package-archive";
        intent.setDataAndType(Uri.fromFile(file), type);
        context.startActivity(intent);
    }

    /**
     * 检查并安装app
     *
     * @param file
     */
    public static void checkAndInstall(final boolean isForce, final Context context, final File file) {
        if (file != null) {
            String path = file.getAbsolutePath();
            if (path.endsWith(".apk")) {
                //是安装文件
                int theme = getIdByName("Mdialog", "style", context.getPackageName(), context);
                String msg = "更新已完成,是否安装？";
                final YXConfirmDialog confirmDialog = new YXConfirmDialog(context, theme, msg);
                confirmDialog.setConfirmListenr(new ConfirmListener() {

                    @Override
                    public void onConfirm() {
                        YXUpdateManager.installApk(context, file);
                    }

                    @Override
                    public void onCancel() {
                        if (isForce) {
                            showTips(context, "此次更新为强制更新。\n为了给您更好的游戏体验，更新后才能进入游戏。");
                        } else {
                            confirmDialog.dismiss();
                        }
                    }
                });
                if (isForce) {
                    //强制更新不可取消。
                    confirmDialog.setCancelable(false);
                }
                confirmDialog.show();
            } else {
                showTips(context, "安装失败：文件格式不对。");
            }
        } else {
            showTips(context, "安装失败：安装文件为空，请重新下载");
        }
    }

    /**
     * 显示提示和打印log
     *
     * @param context
     * @param tips
     */
    public static void showTips(Context context, String tips) {

        Toast.makeText(context, tips, Toast.LENGTH_SHORT).show();
        System.out.println(tips);
    }


    /**
     * @param name
     * @param type
     * @param packageName
     * @param context
     * @return
     */
    public static int getIdByName(String name, String type, String packageName, Context context) {
        int id = context.getResources().getIdentifier(name, type, packageName);
        return id;
    }

    public static String getCurrentDate() {
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = format.format(new Date());
        YXMCore.sendLog("获取的时间是：" + date);
        return date;
    }

}
