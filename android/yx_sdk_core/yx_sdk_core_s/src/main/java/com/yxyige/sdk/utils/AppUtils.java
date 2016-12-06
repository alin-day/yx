package com.yxyige.sdk.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class AppUtils {

    private final static SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 获取App安装包信息
     *
     * @return
     */
    public static PackageInfo getPackageInfo(Context context, String pack) {
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(pack, 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }

    public static Drawable getPackIcon(Context context, String pack) {
        Drawable icon = null;
        PackageInfo packInfo = getPackageInfo(context, pack);
        icon = packInfo.applicationInfo.loadIcon(context.getPackageManager());
        return icon;
    }

    public static boolean checkPackInstalled(Context context, String pack) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pack, 0);
        } catch (NameNotFoundException e) {
            packageInfo = null;
        }

        return packageInfo != null;
    }

    /**
     * 检测网络是否可用
     *
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    /**
     * 检测Service是否运行中
     *
     * @param context
     * @param pack
     * @return
     */
    public static boolean isWorked(Context context, String pack) {
        ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager.getRunningServices(45);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString().equals(pack)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将字符串转位日期类型
     *
     * @param sdate
     * @return
     */
    public static String toDate(long sdate) {
        return dateFormater.format(sdate * 1000);
    }


    public static void startAppFromPackage(Context context, String packageName) {
        PackageInfo pi = null;
        try {
            pi = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(pi.packageName);

        List<ResolveInfo> apps = context.getPackageManager().queryIntentActivities(resolveIntent, 0);

        ResolveInfo ri = apps.iterator().next();
        if (ri != null) {
            String packageName1 = ri.activityInfo.packageName;
            String className = ri.activityInfo.name;

            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            ComponentName cn = new ComponentName(packageName1, className);

            intent.setComponent(cn);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }


    /**
     * 获取手机浏览器列表的首项
     *
     * @param context
     * @return 浏览器列表首项，无则返回null
     */
    private static ComponentName getDefaultBrowserIntent(Context context) {

        PackageManager packageManager = context.getPackageManager();

        Intent defaultBrowserIntent = new Intent("android.intent.action.VIEW");
        defaultBrowserIntent.addCategory("android.intent.category.BROWSABLE");
        defaultBrowserIntent.addCategory("android.intent.category.DEFAULT");
        Uri uri = Uri.parse("http://");
        defaultBrowserIntent.setDataAndType(uri, null);

        // 找出手机当前安装的所有浏览器程序
        List<ResolveInfo> resolveInfoList = packageManager
                .queryIntentActivities(defaultBrowserIntent,
                        PackageManager.GET_INTENT_FILTERS);


        int size = resolveInfoList.size();
        ComponentName[] arrayOfComponentName = new ComponentName[size];
        ComponentName componentName = null;
        boolean hasUcBrowser = false;
        String defaultBrowserClassName = "com.UCMobile"; //"com.android.chrome"


        for (int i = 0; i < size; i++) {
            ActivityInfo activityInfo = resolveInfoList.get(i).activityInfo;
            String packageName = activityInfo.packageName;
            String className = activityInfo.name;

//            System.out.println("packageName  " + packageName + " | className  " + className);

            if (packageName.contains(defaultBrowserClassName)) { //默认UC打开
                hasUcBrowser = true;
                componentName = new ComponentName(packageName, className);
            } else {

                hasUcBrowser = false;
            }

            arrayOfComponentName[i] = new ComponentName(packageName, className);
        }

        if (arrayOfComponentName != null && arrayOfComponentName.length > 0 && !hasUcBrowser) {
            return arrayOfComponentName[0];
        }

        return componentName;
    }


    /**
     * 浏览器打开url
     *
     * @param context
     * @param url
     */
    public static void toUri(Context context, String url) {

        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setComponent(getDefaultBrowserIntent(context));
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    /**
     * 跳转到外部浏览器打开带参数的url
     *
     * @param context
     * @param sdkUrl
     */
    public static void toSdkUrl(Context context, String sdkUrl) {

        if (sdkUrl == null) {
            return;
        }

        AppUtils.toUri(context, sdkUrl);
    }


    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    public static int getStatusBarHeight(Context context) {

        int statusBarHeight = 0;
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = context.getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }
}
