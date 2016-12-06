package com.yxyige.msdk.api;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.method.DigitsKeyListener;
import android.text.method.NumberKeyListener;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.ta.utdid2.device.DeviceInfo;
import com.yxyige.msdk.views.YXNoticeDialog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultiSDKUtils {

    private static final String YX_PREFS = "yx_m_prefs";

    private static final String YX_USERID = "yx_userid";
    private static final String YX_USERNAME = "yx_username";
    private static final String YX_TOKEN = "yx_token";

    //联运平台puid
    private static final String P_USERID = "puid";
    //联运平台puname
    private static final String P_USERNAME = "puname";
    //联运平台ptoken
    private static final String P_TOKEN = "ptoken";

    private static final String GID = "gid";
    private static final String PID = "pid";
    private static final String REFER = "refer";

    private static final String KEY = "yxAppkey";

    //优象新增
    private static final String DUID = "duid";

    /**
     * 订单查询地址
     */
    private static final String URL_PAY_QUERY = "pay_url_query";
    /**
     * 优象多平台TOKEN验证接口
     */
    private static final String URL_M_VERIFY_TOKEN = "vptapi";
    /**
     * 游戏充值按钮调用的下单接口
     */
    private static final String URL_M_ORDER = "oapi";
    /**
     * 数据统计日志接口
     */
    private static final String URL_M_SUBMIT = "lapi";
    /**
     * 提交用户角色信息接口
     */
    private static final String URL_M_ENTER = "eapi";
    /**
     * 推送地址
     */
    private static final String URL_PUSH = "push_url";
    /**
     * push时间
     */
    private static final String PUSH_DELAY = "push_delay";
    /**
     * 配置是否延迟push，主要是方便测试
     */
    private static final String PUSH_IS_DELAY = "push_is_delay";

    //新增缓存IMEI 和 MAC
    private static final String DEV_IMEI = "dev_imei";
    private static final String DEV_MAC = "dev_mac";

    private static Toast toast = null;

    public static void setString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(MultiSDKUtils.YX_PREFS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void removeString(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(MultiSDKUtils.YX_PREFS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }

    public static String getString(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(MultiSDKUtils.YX_PREFS, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    public static void setBoolean(Context context, String key, Boolean value) {
        SharedPreferences sp = context.getSharedPreferences(MultiSDKUtils.YX_PREFS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void removeBoolean(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(MultiSDKUtils.YX_PREFS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }

    public static boolean getBoolean(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(MultiSDKUtils.YX_PREFS, Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }

    public static void setPushIsDelay(Context context, boolean isDelay) {
        SharedPreferences sp = context.getSharedPreferences(MultiSDKUtils.YX_PREFS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(PUSH_IS_DELAY, isDelay);
        editor.commit();
    }

    public static boolean getPushIsDelay(Context context) {
        SharedPreferences sp = context.getSharedPreferences(MultiSDKUtils.YX_PREFS, Context.MODE_PRIVATE);
        return sp.getBoolean(PUSH_IS_DELAY, true);
    }

    public static void setDuid(Context context, String duid) {
        SharedPreferences sp = context.getSharedPreferences(MultiSDKUtils.YX_PREFS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(DUID, duid);
        editor.commit();
    }

    public static String getDuid(Context context) {
        SharedPreferences sp = context.getSharedPreferences(MultiSDKUtils.YX_PREFS, Context.MODE_PRIVATE);
        return sp.getString(DUID, "");
    }

    public static void setPushDelay(Context context, int time) {
        SharedPreferences sp = context.getSharedPreferences(MultiSDKUtils.YX_PREFS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putInt(PUSH_DELAY, time);
        editor.commit();
    }

    public static int getPushDelay(Context context) {
        SharedPreferences sp = context.getSharedPreferences(MultiSDKUtils.YX_PREFS, Context.MODE_PRIVATE);
        return sp.getInt(PUSH_DELAY, 6 * 60 * 60 * 1000);
    }

    public static void setPushUrl(Context context, String url) {
        SharedPreferences sp = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(URL_PUSH, url);
        editor.commit();
    }

    public static String getPushUrl(Context context) {
        SharedPreferences sp = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        return sp.getString(URL_PUSH, IMUrl.URL_PUSH);
    }

    public static void setPayQueryUrl(Context context, String url) {
        SharedPreferences sp = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(URL_PAY_QUERY, url);
        editor.commit();
    }

    public static String getPayQueryUrl(Context context) {
        SharedPreferences sp = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        return sp.getString(URL_PAY_QUERY, IMUrl.URL_PAY_QUERY);
    }

    public static void setPayOrderUrl(Context context, String url) {
        SharedPreferences sp = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(URL_M_ORDER, url);
        editor.commit();
    }

    public static String getPayOrderUrl(Context context) {
        SharedPreferences sp = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        return sp.getString(URL_M_ORDER, IMUrl.URL_M_ORDER);
    }


    public static void setVerifyTokenUrl(Context context, String url) {
        SharedPreferences sp = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(URL_M_VERIFY_TOKEN, url);
        editor.commit();
    }

    public static String getVerifyTokenUrl(Context context) {
        SharedPreferences sp = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        return sp.getString(URL_M_VERIFY_TOKEN, IMUrl.URL_M_VAREFY_TOKEN);
    }

    public static void setSubmitUrl(Context context, String url) {
        SharedPreferences sp = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(URL_M_SUBMIT, url);
        editor.commit();
    }

    public static String getSubmitUrl(Context context) {
        SharedPreferences sp = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        return sp.getString(URL_M_SUBMIT, IMUrl.URL_M_SUBMIT);
    }

    public static void setGID(Context context, String gid) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        Editor editor = uiState.edit();
        editor.putString(GID, gid);
        editor.commit();
    }

    public static String getGID(Context context) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        return uiState.getString(GID, "1000001");
    }

    public static void setPID(Context context, String pid) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        Editor editor = uiState.edit();
        editor.putString(PID, pid);
        editor.commit();
    }

    public static String getPID(Context context) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        return uiState.getString(PID, "1");
    }

    public static void setKey(Context context, String key) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        Editor editor = uiState.edit();
        editor.putString(KEY, key);
        editor.commit();
    }

    public static String getKey(Context context) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        return uiState.getString(KEY, "");
    }

    public static void setRefer(Context context, String refer) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        Editor editor = uiState.edit();
        editor.putString(REFER, refer);
        editor.commit();
    }

    public static String getRefer(Context context) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        return uiState.getString(REFER, "sy00000_1");
    }

    /**
     * 取用户填入的支付金额--用于定额支付的SDK，在支付前让用户填入金额
     *
     * @param context
     * @param listener
     */
    public static void getPayMoney(final Context context, final YXMResultListener listener) {
        final EditText money = new EditText(context);
        money.setKeyListener(new DigitsKeyListener(false, true));
        money.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        money.setKeyListener(new NumberKeyListener() {

            @Override
            public int getInputType() {

                return android.text.InputType.TYPE_CLASS_PHONE;
            }

            @Override
            protected char[] getAcceptedChars() {

                return new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
            }
        });
        money.setHint("请输入充值金额(不超过1万元)");

        Dialog alertDialog = new AlertDialog.Builder(context).
                setTitle("请输入充值金额").
                setView(money).
                setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String moneyText = money.getText().toString();
                        if (moneyText.length() == 0 || Integer.parseInt(moneyText) == 0) {
                            Toast.makeText(context, "请输入大于0的金额", Toast.LENGTH_LONG).show();
                            //不关闭对话框
                            try {
                                Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                field.setAccessible(true);
                                field.set(dialog, false);   //设定为false,则不可以关闭对话框
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {
                            //关闭对话框
                            try {
                                Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                                field.setAccessible(true);
                                field.set(dialog, true);   //设定为false,则不可以关闭对话框
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Bundle b = new Bundle();
                            b.putInt("money", Integer.parseInt(moneyText));
                            listener.onSuccess(b);
                            dialog.dismiss();
                        }
                    }
                }).
                setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //关闭对话框
                        try {
                            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                            field.setAccessible(true);
                            field.set(dialog, true);   //设定为false,则不可以关闭对话框
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        listener.onFailture(206, "用户取消支付");
                        dialog.dismiss();
                    }
                }).
                setCancelable(false).
                create();
        alertDialog.show();
    }

    /**
     * 取assets下的配置参数
     *
     * @param context
     * @param file
     * @return
     */
    public static Properties readPropertites(Context context, String file) {
        Properties p = null;
        try {
            InputStream in = context.getResources().getAssets().open(file);
            p = new Properties();
            p.load(in);
        } catch (IOException e) {
            p = null;
        }
        return p;
    }

    /**
     * MD5加密
     *
     * @param string
     * @return
     */
    public static String Md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

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

    public static String getIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId() != null ? tm.getDeviceId() : randomDeverceNum(context, 15);
    }

    /**
     * 随机生成imei码和mac地址
     *
     * @return
     */
    private static String randomDeverceNum(Context context, int length) {

        DeviceInfo deviceInfo = new DeviceInfo();

        Random random = new Random(System.currentTimeMillis());
        String num = "";
        for (int i = 0; i < length; i++) {
            int subNum = random.nextInt(9);
            num += subNum;
        }
        return num;
    }

    public static String getSIM(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSimSerialNumber() != null ? tm.getSimSerialNumber() : "";
    }

    public static String getIMSI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSubscriberId() != null ? tm.getSubscriberId() : "";
    }

    public static String getMac(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(info.getMacAddress() != null ? info.getMacAddress() : "");
        return m != null ? m.replaceAll("").trim() : randomDeverceNum(context, 12);//2016/08/31如果没有mac地址就随机生成一个12位的数字
    }

    public static String getLocalDev(Context context) {

        String key = "-:&d4@zXqm-pLgW";
        return Md5(getDevMac(context) + getDevImei(context) + key).toLowerCase();

    }

    private static String getDisplayScreenResolution(Context mContext) {
        int screen_w = 0;
        int screen_h = 0;
        try {
            screen_h = mContext.getResources().getDisplayMetrics().heightPixels;
            screen_w = mContext.getResources().getDisplayMetrics().widthPixels;
        } catch (Exception e) {
            return screen_w + "*" + screen_h;
        }

        Log.d("sqsdk_m", "Run2 Calibration  resolution:" + screen_w + "*" + screen_h);

        return screen_w + "*" + screen_h;
    }

    public static int getWpixels(Context mContext) {

        String screenResolution = getDisplayScreenResolution(mContext);
        String wpixels = screenResolution.substring(0, screenResolution.indexOf("*"));
        return Integer.valueOf(wpixels);
    }

    public static int getHpixels(Context mContext) {
        String screenResolution = getDisplayScreenResolution(mContext);
        String hpixels = screenResolution.substring(screenResolution.indexOf("*") + 1);
        return Integer.valueOf(hpixels);
    }

    /**
     * 获取制造厂商
     *
     * @return
     */
    public static String getBrand() {
        return Build.MODEL;
    }

    /**
     * getNumber(获取手机号。注：有些手机卡获取不到手机号) (这里描述这个方法适用条件 – 可选)
     *
     * @param context
     * @return String
     * @throws
     */
    public static String getNumber(Context context) {
        String number = "";
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        number = tm.getLine1Number();
        return number;
    }

    /**
     * sim卡存在的情况下，imei+imsi
     * sim卡不存在的情况下，imei+mac
     *
     * @param context
     * @return
     */
    public static String getSerial(Context context) {

        String imei = getIMEI(context);
        String imsi = getIMSI(context);

        if (!imsi.equals("")) {
            return imei + imsi;
        }

        String mac = getMac(context);
        if (!mac.equals(""))
            return imei + mac;

        String sim = getSIM(context);
        if (!sim.equals(""))
            return imei + sim;

        return "";

    }

    /**
     * 取SD卡路径
     *
     * @param context
     * @return
     */
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

    /**
     * 根据资源名字来反射出资源id
     * author:chuan
     *
     * @param name        资源的名字
     * @param type        资源的类型
     * @param packageName 项目的报名
     * @param context
     * @return 资源的id
     */
    public static int getIdByName(String name, String type, String packageName, Context context) {
        int id = context.getResources().getIdentifier(name, type, packageName);
        return id;
    }

    /**
     * 检查是否安装某个apk
     *
     * @param context
     * @param pack
     * @return
     */
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
     * 根据包名启动app
     *
     * @param context
     * @param packName
     */
    public static void launchApp(Context context, String packName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packName);
        context.startActivity(intent);
    }

    /**
     * 获取软件版本号
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取软件版本名称
     * 用于更新
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        String versionName = "1.0.0.0";

        try {
            // 获取软件版本号，对应AndroidManifest.xml下android:versionName
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 获取软件版本名称
     * 用于更新
     *
     * @param context
     * @return
     */
    public static Drawable getAppIcon(Context context) {
        Drawable icon = null;

        try {
            // 获取软件版本号，对应AndroidManifest.xml下android:icon
            PackageManager pm = context.getPackageManager();
            icon = pm.getPackageInfo(context.getPackageName(), 0).applicationInfo.loadIcon(pm);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return icon;
    }

    public static void showTips(Context context, String tips) {

        if (toast == null) {

            toast = Toast.makeText(context, tips, Toast.LENGTH_SHORT);
        }

        toast.setText("" + tips);
        toast.show();
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
     * 获取当前网络类型
     *
     * @time 2015年05月26日15:46:42新增
     * @author mac_tang
     */
    public static String getNetType(Context context) {

        String netType = "NULL"; //默认为停网状态

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        if (ni != null) {
            netType = ni.getTypeName();
        }

        return netType;
    }

    /**
     * 显示公告框
     *
     * @param context
     * @param title
     * @param url
     */
    public static void showNoticeDialog(Context context, String title, String url) {

        if (url == null || "".equals(url)) {
            return;
        }

        YXNoticeDialog dialog = new YXNoticeDialog(context);
        dialog.setCancelable(false);
        dialog.setTitle(title);
        dialog.setUrl(url);
        dialog.show();
    }

    /**
     * 网络下载图片
     *
     * @param link    下载地址
     * @param handler 1=传回bitmap，-1=失败
     */
    public static void downLoadBitmap(final String link, final Handler handler) {

        new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    URL url = new URL(link);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("GET");
                    if (conn.getResponseCode() == 200) {
                        InputStream inStream = conn.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(inStream);
                        handler.obtainMessage(1, bitmap).sendToTarget();
                    } else {
                        handler.obtainMessage(-1).sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.obtainMessage(-1).sendToTarget();
                }

            }
        }).start();
    }


    /**
     * 安装apk文件
     *
     * @param context
     * @param filePath
     * @return
     */
    public static boolean installApkByPath(Context context, String filePath) {
        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            String type = "application/vnd.android.package-archive";

            File f = new File(filePath);
            if (f.exists() && filePath.endsWith(".apk")) {
                System.out.println("文件存在，开始安装" + f.getAbsolutePath());
                intent.setDataAndType(Uri.fromFile(f), type);
                context.startActivity(intent);
            } else {
                System.out.println("文件不存在,或者不是apk文件!");
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * @param context
     * @return true 为竖屏，反之横屏
     */
    public static boolean isScreenOriatationPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    //保存mac到本地
    public static void setDevMac(Context context, String mac) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        Editor editor = uiState.edit();
        editor.putString(DEV_MAC, mac);
        editor.commit();
    }

    public static String getDevMac(Context context) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        return uiState.getString(DEV_MAC, "");
    }

    //保存imei到本地
    public static void setDevImei(Context context, String imei) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        Editor editor = uiState.edit();
        editor.putString(DEV_IMEI, imei);
        editor.commit();
    }

    public static String getDevImei(Context context) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        return uiState.getString(DEV_IMEI, "");
    }

    /**
     * 提交用户角色信息地址
     *
     * @param context
     * @param url
     */
    public static void setEnterUrl(Context context, String url) {
        SharedPreferences sp = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(URL_M_ENTER, url);
        editor.commit();
    }

    public static String getEnterUrl(Context context) {
        SharedPreferences sp = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        return sp.getString(URL_M_ENTER, IMUrl.URL_M_ENTER);
    }

    public static void setYXUserid(Context context, String userid) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        Editor editor = uiState.edit();
        editor.putString(YX_USERID, userid);
        editor.commit();
    }

    public static String getYXUserid(Context context) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        return uiState.getString(YX_USERID, "");
    }

    public static void setYXUsername(Context context, String username) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        Editor editor = uiState.edit();
        editor.putString(YX_USERNAME, username);
        editor.commit();
    }

    public static String getYXUsername(Context context) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        return uiState.getString(YX_USERNAME, "");
    }

    public static void setPlatUserid(Context context, String userid) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        Editor editor = uiState.edit();
        editor.putString(P_USERID, userid);
        editor.commit();
    }

    public static String getPlatUserid(Context context) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        return uiState.getString(P_USERID, "");
    }

    public static void setPlatUsername(Context context, String username) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        Editor editor = uiState.edit();
        editor.putString(P_USERNAME, username);
        editor.commit();
    }

    public static String getPlatUsername(Context context) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        return uiState.getString(P_USERNAME, "");
    }

    public static void setPlatToken(Context context, String username) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        Editor editor = uiState.edit();
        editor.putString(P_TOKEN, username);
        editor.commit();
    }

    public static String getPlatToken(Context context) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        return uiState.getString(P_TOKEN, "");
    }

    public static void setYXToken(Context context, String username) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        Editor editor = uiState.edit();
        editor.putString(YX_TOKEN, username);
        editor.commit();
    }

    public static String getYXToken(Context context) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PREFS, Context.MODE_PRIVATE);
        return uiState.getString(YX_TOKEN, "");
    }
}
