package com.yxyige.sdk.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.yxyige.sdk.core.INewUrl;
import com.yxyige.sdk.widget.AbstractView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ta.utdid2.android.utils.PhoneInfoUtils.getImei;

public class Util {

    private static final String YX_PERFS = "yx_s_prefs";

    private static final String GID = "gid";
    private static final String PTID = "ptid";
    private static final String REFID = "refid";
    private static final String DUID = "duid";
    private static final String APPKEY = "appkey";
    private static final String USERID = "userid";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String TOKEN = "token";
    private static final String GAMENAME = "gamename";
    private static final String NEWPAYURL = "newpayurl";

    private static final String NICK = "nick";
    private static final String SEX = "sex";
    private static final String BRITH = "birth";
    private static final String PHONE = "phone";
    private static final String DISNAME = "disname";
    private static final String LOGIN_NURL = "login_nurl";
    private static final String GET_VERIFY_CODE_LAST_TIME = "time_last_get_verify_code"; //最后点击请求短信验证码时间
    private static final String USER_AGREE_URL = "user_agree_url";

    public static void setUserAgreeUrl(Context context, String url) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = uiState.edit();
        editor.putString(USER_AGREE_URL, url);
        editor.commit();
    }

    public static String getUserAgreeUrl(Context context) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        return uiState.getString(USER_AGREE_URL, INewUrl.UAGREE);
    }

    public static void setVerifyCodeLastTime(Context context, long time) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = uiState.edit();
        editor.putLong(GET_VERIFY_CODE_LAST_TIME, time);
        editor.commit();
    }

    public static long getVerifyCodeLastTime(Context context) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        return uiState.getLong(GET_VERIFY_CODE_LAST_TIME, 0L);
    }

    public static void setDisname(Context context, String disname) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = uiState.edit();
        editor.putString(DISNAME, disname);
        editor.commit();
    }

    public static String getDisname(Context context) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        return uiState.getString(DISNAME, "");
    }

    public static void setLoginNurl(Context context, String url) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = uiState.edit();
        editor.putString(LOGIN_NURL, url);
        editor.commit();
    }

    public static String getLoginNurl(Context context) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        return uiState.getString(LOGIN_NURL, "");
    }

    public static void setPhone(Context context, String phone) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = uiState.edit();
        editor.putString(PHONE, phone);
        editor.commit();
    }

    public static String getPhone(Context context) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        return uiState.getString(PHONE, "");
    }

    public static void setBrith(Context context, String brith) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = uiState.edit();
        editor.putString(BRITH, brith);
        editor.commit();
    }

    public static String getBrith(Context context) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        return uiState.getString(BRITH, "");
    }

    public static void setSex(Context context, String sex) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = uiState.edit();
        editor.putString(SEX, sex);
        editor.commit();
    }

    public static String getSex(Context context) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        return uiState.getString(SEX, "");
    }

    public static void setNick(Context context, String nick) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = uiState.edit();
        editor.putString(NICK, nick);
        editor.commit();
    }

    public static String getNick(Context context) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        return uiState.getString(NICK, "");
    }

    public static void setGid(Context context, String gid) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = uiState.edit();
        editor.putString(GID, gid);
        editor.commit();
    }

    public static String getGid(Context context) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        return uiState.getString(GID, "");
    }

    public static void setPtid(Context context, String ptid) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = uiState.edit();
        editor.putString(PTID, ptid);
        editor.commit();
    }

    public static String getPtid(Context context) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        return uiState.getString(PTID, "");
    }

    public static void setRefid(Context context, String refid) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = uiState.edit();
        editor.putString(REFID, refid);
        editor.commit();
    }

    public static String getRefid(Context context) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        return uiState.getString(REFID, "");
    }

    public static void setDuid(Context context, String duid) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = uiState.edit();
        editor.putString(DUID, duid);
        editor.commit();
    }

    public static String getDuid(Context context) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        return uiState.getString(DUID, getLocalDev(context));
    }

    public static void setAppkey(Context context, String appkey) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = uiState.edit();
        editor.putString(APPKEY, appkey);
        editor.commit();
    }

    public static String getAppkey(Context context) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        return uiState.getString(APPKEY, "");
    }

    public static void setUsername(Context context, String username) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = uiState.edit();
        editor.putString(USERNAME, username);
        editor.commit();
    }

    public static String getUsername(Context context) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        return uiState.getString(USERNAME, "");
    }

    public static void setUserid(Context context, String userid) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = uiState.edit();
        editor.putString(USERID, userid);
        editor.commit();
    }

    public static String getUserid(Context context) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        return uiState.getString(USERID, "");
    }

    public static void setPassword(Context context, String password) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = uiState.edit();
        editor.putString(PASSWORD, password);
        editor.commit();
    }

    public static String getPassword(Context context) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        return uiState.getString(PASSWORD, "");
    }

    public static void setToken(Context context, String token) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = uiState.edit();
        editor.putString(TOKEN, token);
        editor.commit();
    }

    public static String getToken(Context context) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        return uiState.getString(TOKEN, "");
    }

    public static void setGamename(Context context, String gameName) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = uiState.edit();
        editor.putString(GAMENAME, gameName);
        editor.commit();
    }

    public static String getGamename(Context context) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        return uiState.getString(GAMENAME, "");
    }

    public static void setNewpayurl(Context context, String url) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = uiState.edit();
        editor.putString(NEWPAYURL, url);
        editor.commit();
    }

    public static String getNewpayurl(Context context) {
        SharedPreferences uiState = context.getSharedPreferences(YX_PERFS, Context.MODE_PRIVATE);
        return uiState.getString(NEWPAYURL, INewUrl.PAY);
    }

    public static String encodeUrl(Bundle parameters) {
        if (parameters == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String key : parameters.keySet()) {
            Object parameter = parameters.get(key);
            if (!(parameter instanceof String)) {
                continue;
            }
            if (first)
                first = false;
            else
                sb.append("&");
            sb.append(URLEncoder.encode(key) + "=" + URLEncoder.encode(parameters.getString(key)));
        }
        return sb.toString();
    }

    public static Bundle decodeUrl(String s) {
        Bundle params = new Bundle();
        if (s != null) {
            String array[] = s.split("&");
            for (String parameter : array) {
                String v[] = parameter.split("=");
                if (v.length == 2) {
                    params.putString(URLDecoder.decode(v[0]), URLDecoder.decode(v[1]));
                }
            }
        }
        return params;
    }


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

    private static String read(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(in), 1000);
        for (String line = r.readLine(); line != null; line = r.readLine()) {
            sb.append(line);
        }
        in.close();
        return sb.toString();
    }

    public static String getModel() {
        return android.os.Build.MODEL;
    }

    public static String getWH(Activity context) {
        WindowManager windowManager = context.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        return point.x + "x" + point.y;
    }

    public static String getIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId() != null ? tm.getDeviceId() : "";
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
        return m != null ? m.replaceAll("").trim() : "";
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
     * MAC可能获取不到的情况，则获取IMEI、IMSI
     *
     * @param context
     * @return
     */
    public static String getMAC(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(info.getMacAddress() != null ? info.getMacAddress() : "");

        if (!getIMEI(context).equals(""))
            return getIMEI(context);
        else if (!getIMSI(context).equals(""))
            return getIMSI(context);
        else if (!getSIM(context).equals(""))
            return getSIM(context);
        else if (!m.replaceAll("").equals(""))
            return m.replaceAll("").trim();
        else
            return "";
    }

    public static String getOsVersion() {
        return android.os.Build.VERSION.SDK;
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
        String versionName = "1.0.0";

        try {
            // 获取软件版本号，对应AndroidManifest.xml下android:versionName
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 获取网络状态
     *
     * @param context
     * @return
     */
    public static String getNetworkType(Context context) {
        String netType = "";
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return "";
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if (extraInfo != null && !extraInfo.equals("")) {
                netType = extraInfo.toLowerCase();
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = "wifi";
        }
        return netType;
    }

    /**
     * 实现文本复制功能
     *
     * @param content API 11之前： android.text.ClipboardManager
     *                API 11之后： android.content.ClipboardManager
     */
    public static boolean copyString2System(Context context, String content, String tips) {

        if (content == null || "".equals(content)) {

            return false;
        }

        boolean isCopySuccess = true;
        try {
            // 得到剪贴板管理器
            if (android.os.Build.VERSION.SDK_INT > 11) {
                android.content.ClipboardManager c = (android.content.ClipboardManager) context
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                c.setPrimaryClip(ClipData.newPlainText("code", content.trim()));

            } else {
                android.text.ClipboardManager c = (android.text.ClipboardManager) context
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                c.setText(content);
            }

            ViewController.showToast(context, tips);
        } catch (Exception e) {
            e.printStackTrace();
            isCopySuccess = false;
        }
        return isCopySuccess;
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
        int id = 0x0;
        try {
            id = context.getResources().getIdentifier(name, type, packageName);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(" TYPE:" + type + ",RES:" + name + " NOT FOUND!");
        }
        return id;
    }

    public static View getViewByIdName(AbstractView view, String name) {

        View tagView = null;
        Context context = view.getActivity();
        try {
            int id = context.getResources().getIdentifier(name, "id", context.getPackageName());
            tagView = view.findViewById(id);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("找不到" + name + "资源");
        }
        return tagView;
    }


    public static int getIdByName(String name, String type, Context context) {
        return getIdByName(name, type, context.getPackageName(), context);
    }

    /**
     * 验证手机号码是否符合格式
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((1[3,5,8][0-9])|(14[5,7])|(17[0,6,7,8]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 隐藏软键盘
     *
     * @param context
     * @param v
     */
    public static void hideSystemKeyBoard(Context context, View v) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    /**
     * 汉字转Unicode
     *
     * @param s
     * @return
     */
    public static String gbEncoding(final String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            str += "\\u" + Integer.toHexString(ch);
        }
        return str;
    }

    /**
     * Unicode转汉字
     *
     * @param str
     * @return
     */
    public static String encodingtoStr(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");
        }
        return str;
    }


    /**
     * 检测是否安装有指定应用
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean checkAppInstalled(Context context, String packageName) {

        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (pinfo.get(i).packageName.equalsIgnoreCase(packageName)) {

                return true;
            }
        }
        return false;
    }


    /**
     * SIM卡是否可用
     */
    public static boolean isSIMCardAvailable(Context context) {
        try {

            TelephonyManager mgr = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            return TelephonyManager.SIM_STATE_READY == mgr.getSimState();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取屏幕状态
     *
     * @param context
     * @return true 为竖屏，反之横屏
     */
    public static boolean isScreenOriatationPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    /**
     * 切换帐号是否直接跳过登录
     *
     * @param context
     * @return
     */
    public static boolean isSkipSQChangeAccountLogin(Context context) {
        boolean isSkipLogin = false;
        ApplicationInfo appInfo;
        String result;
        try {
            String pname = context.getPackageName();
            appInfo = context.getPackageManager().getApplicationInfo(pname, PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                result = appInfo.metaData.getString("YXSkipSwitchLogin");
                System.out.println("是否跳过切换帐号登录框：" + result);
                if (!TextUtils.isEmpty(result) && "yes".equals(result)) {
                    isSkipLogin = true;
                }
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return isSkipLogin;
    }

    /**
     * 获取本地dev
     *
     * @param context
     * @return
     */
    public static String getLocalDev(Context context) {
        String key = "-:&d4@zXqm-pLgW";
        return Md5(getMac(context) + getImei(context) + key)
                .toLowerCase();
    }

    /**
     * 清空用户本地存储数据
     *
     * @param context
     */
    public static void cleanUserData(Context context) {
        // sharePreference 都设置成""
        setUsername(context, "");
        setUserid(context, "");
        setPassword(context, "");
        setToken(context, "");
        setNick(context, "");
        setBrith(context, "");
        setPhone(context, "");
        setDisname(context, "");
        setLoginNurl(context, "");
    }
}
