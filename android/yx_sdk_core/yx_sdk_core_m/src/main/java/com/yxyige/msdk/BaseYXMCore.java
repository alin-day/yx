package com.yxyige.msdk;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.yxyige.msdk.api.InitBean;
import com.yxyige.msdk.api.MRequestCallBack;
import com.yxyige.msdk.api.MRequestManager;
import com.yxyige.msdk.api.MultiSDKUtils;
import com.yxyige.msdk.api.YXAppConfig;
import com.yxyige.msdk.api.YXMResultListener;
import com.yxyige.msdk.api.YXMSdkInterface;
import com.yxyige.msdk.api.YXUpdateManager;
import com.yxyige.msdk.utils.ViewUtils;
import com.yxyige.msdk.utils.ZipString;
import com.yxyige.msdk.views.YXSplashDialog;
import com.yxyige.msdk.views.YXSplashDialog.SplahListener;
import com.yxyige.msdk.views.YXSplashDialogBlackStyle;
import com.yxyige.sdk.utils.Util;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Properties;

public abstract class BaseYXMCore implements YXMSdkInterface {

    //角色提交相关
    public static final String INFO_SERVERID = "serverId";
    public static final String INFO_SERVERNAME = "serverName";
    public static final String INFO_ROLEID = "roleId";
    public static final String INFO_ROLENAME = "roleName";
    public static final String INFO_ROLELEVEL = "roleLevel";
    public static final String INFO_BALANCE = "balance";
    public static final String INFO_PARTYNAME = "partyName";
    public static final String INFO_VIPLEVEL = "vipLevel";
    public static final String INFO_ROLE_TIME_CREATE = "roleCTime";
    public static final String INFO_ROLE_TIME_LEVEL = "roleLevelMTime";

    //平台
    public static final int Platform_YXGame = 1;
    public static final String SDKTYPE = "sdktype";
    public static final String CONFIG_FILE = "yx_m_config";
    public static final String SDKINFO_FILE = "yx_info";
    /**
     * M_SDK 版本
     */
    public static final String M_SDK_VERSION = "1.1.0";
    public static boolean isInitSuccess = false;
    public static boolean isLoginSuccess = false;
    //其他
    static YXMCore instance;
    static byte[] lock = new byte[0];
    static InitBean initBean;
    static YXMSdkInterface sdkapi;
    static boolean isPlatformInitRunning = false;
    private Context context;
    private YXMResultListener baseInitListener;
    private YXAppConfig yxAppConfig = null;
    private MRequestManager requestManager = null;
    private YXSplashDialogBlackStyle initLoading = null;
    private Handler initPaltformHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //显示闪屏和初始化平台
            showSplashAndInitPlatform();
        }
    };
    private String sdkversion;//是否显示版本

    /**
     * 打印日志
     *
     * @param logString
     */
    public static void sendLog(String logString) {
        if (initBean != null && initBean.getDebug().equals("1")) {
            Log.w("yx_m", logString);
        }
    }

    public static void sendLogNoDebug(String logString) {
        Log.d("yx_m", logString);
    }

    protected abstract YXMSdkInterface getPlatform(Context context, InitBean bean, YXMResultListener initListener);

    /**
     * 37平台初始化
     *
     * @param cxt
     * @param appkey
     * @param listener
     */
    public void init(final Context cxt, final String appkey, final YXMResultListener listener) {
        isInitSuccess = false;
        sendLogNoDebug("yx --> init --> do");
        //对初始化接口进行保护，在未完成之前，不可以进行第二次的操作。
        if (!isPlatformInitRunning) {
            isPlatformInitRunning = true;
            initCore(cxt, appkey, new YXMResultListener() {
                @Override
                public void onSuccess(Bundle bundle) {
                    isPlatformInitRunning = false;
                    listener.onSuccess(bundle);
                }

                @Override
                public void onFailture(int code, String msg) {
                    isPlatformInitRunning = false;
                    listener.onFailture(code, msg);
                }
            });
        } else {
            ViewUtils.showToast(cxt, "初始化进行中，请勿多次调用..");
        }
    }

    private void initCore(final Context cxt, final String appkey, final YXMResultListener listener) {
        this.context = cxt;
        //获取参数
        initConfig(appkey);
        //M层初始化，获取dev，拦截，更新等。
        initSQPlatformReqeust(listener);
        this.baseInitListener = listener;
    }

    /**
     * 初始化appConfig（包括yx_config.xml和yx_m_config）
     *
     * @param appkey
     */
    private void initConfig(final String appkey) {
        //-----------------------------配置dev mac imei-----------------------------
        String mac = MultiSDKUtils.getDevMac(context);
        String imei = MultiSDKUtils.getDevImei(context);

        if (TextUtils.isEmpty(mac)) {
            MultiSDKUtils.setDevMac(context, MultiSDKUtils.getMac(context));
        }
        if (TextUtils.isEmpty(imei)) {
            MultiSDKUtils.setDevImei(context, MultiSDKUtils.getIMEI(context));
        }

        //-----------------------读取yx配置文件-------------------------------
        yxAppConfig = new YXAppConfig(context, new YXMResultListener() {
            @Override
            public void onSuccess(Bundle data) {
                MultiSDKUtils.setGID(context, data.getString("gid"));
                MultiSDKUtils.setPID(context, data.getString("ptid"));
                MultiSDKUtils.setRefer(context, data.getString("refid"));
                MultiSDKUtils.setKey(context, ZipString.json2ZipString(appkey));
            }

            @Override
            public void onFailture(int code, String msg) {
                sendLogNoDebug("read xml config file  ERROR_CODE:" + code + " MSG:" + msg);
            }
        });

        //-------------------------读取多平台配置文件-----------------------------
        Properties prop = MultiSDKUtils.readPropertites(context, CONFIG_FILE);
        initBean = InitBean.inflactBean(prop);
        if (initBean == null) {
            //没有yx_m_config就自动生成一个。
            initBean = new InitBean();
            initBean.setDebug("0"); //关闭debug
            initBean.setUsesdk("1");
            initBean.setIsSplashShow("0"); //使用闪屏
            initBean.setUsePlatformExit("1"); //使用37退出框
            initBean.setIsPushDelay("1"); //开启push延时
        }
        //设置是否开启push的延迟
        if (initBean.getIsPushDelay().equals("1")) {
            MultiSDKUtils.setPushIsDelay(context, true);
        } else {
            MultiSDKUtils.setPushIsDelay(context, false);
        }

        //-----------------------------判断SDK版本-----------------------------
        Properties sdkInfo = MultiSDKUtils.readPropertites(context, SDKINFO_FILE);
        if (sdkInfo != null) {
            sdkversion = sdkInfo.getProperty("sdkverion") == null ? "1" : sdkInfo.getProperty("sdkverion");

            if (!TextUtils.isEmpty(BaseYXMCore.M_SDK_VERSION) && sdkversion != null && !BaseYXMCore.M_SDK_VERSION.equals(sdkversion)) {
                Toast.makeText(context, "sdkverion不正确，请检查yx_info文件", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, "assets缺少yx_info文件", Toast.LENGTH_LONG).show();
        }

        //----------读取参数 完成----------------
    }

    /**
     * 初始化YX平台request
     * 得到平台初始化数据(urls，判断是否更新)
     */
    private void initSQPlatformReqeust(final YXMResultListener initListener) {

        //初始化网络请求manager
        requestManager = new MRequestManager(context);

        requestManager.initRequst(new MRequestCallBack() {

            @Override
            public void onRequestSuccess(String content) {
                try {
                    JSONObject contentObj = new JSONObject(content);
                    if (contentObj.getString("state").equals("1")) {
                        //验证成功
                        String data = contentObj.getString("data");
                        JSONObject dataObj = new JSONObject(data);
                        //设置duid
                        MultiSDKUtils.setDuid(context, dataObj.getString("duid"));
                        if (dataObj.has("ctokenapi")) {
                            MultiSDKUtils.setVerifyTokenUrl(context, dataObj.getString("ctokenapi"));
                        }
                        if (dataObj.has("oapi")) {
                            MultiSDKUtils.setPayOrderUrl(context, dataObj.getString("oapi"));
                        }
                        if (dataObj.has("papi")) {
                            MultiSDKUtils.setPushUrl(context, dataObj.getString("papi"));
                        }
                        if (dataObj.has("ph")) {
                            MultiSDKUtils.setPushDelay(context, Integer.parseInt(dataObj.getString("ph")) * 60 * 60 * 1000);
                        }
                        if (dataObj.has("duid")) {
                            MultiSDKUtils.setDuid(context, dataObj.getString("duid"));
                            //设置单平台duid，单平台接口所使用的duid，统一使用多平台初始化后，回传的
                            Util.setDuid(context, dataObj.getString("duid"));
                        }
                        /*
                         * 更新类型:1, 忽略更新;2, 普通更新;3, 强制更新
                         */
                        if (dataObj.has("utype")) {
                            String updateType = dataObj.getString("utype");
                            String uurl = dataObj.getString("uurl");
                            String uct = dataObj.getString("uct");
                            String uvs = dataObj.getString("uvs");

                            if ("1".equals(updateType)) {
                                // 忽略强更
                            } else if ("2".equals(updateType)) {
                                YXUpdateManager.checkUpdate(context, false, uct, uurl, uvs);
                            } else if ("3".equals(updateType)) {
                                YXUpdateManager.checkUpdate(context, true, uct, uurl, uvs);
                            }

                        }

                        //公告控制
                        if (dataObj.has("nurl")) {
                            MultiSDKUtils.showNoticeDialog(context, "", dataObj.getString("nurl"));
                        }

                        //最后回调初始化成功方法
                        initPaltformHandler.sendEmptyMessageDelayed(0, 200);
                    } else {
                        initListener.onFailture(407, contentObj.getString("msg"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    BaseYXMCore.sendLog("yx --> init error --> content --> " + content);
                    BaseYXMCore.sendLog("yx --> init error --> e --> " + e);
                    initListener.onFailture(407, "服务器返回错误");
                }
            }

            @Override
            public void onRequestError(String errorMsg) {
                Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show();
                initListener.onFailture(204, errorMsg);
            }
        }, false);
    }

    private void showSplashAndInitPlatform() {
        sendLogNoDebug("yx --> showSplashAndInitPlatform --> do");
        if (initBean != null && initBean.getIsSplashShow().equals("1")) {
            YXSplashDialog splash = new YXSplashDialog(context, 3);
            splash.setSplashListener(new SplahListener() {

                @Override
                public void afterSplash() {
                    initPaltform(baseInitListener);
                }
            });
            splash.show();
        } else {
            initPaltform(baseInitListener);
        }
    }

    private synchronized void initPaltform(YXMResultListener initListener) {
        // 无论是单平台还是多请平台，全部走Platform
        sdkapi = getPlatform(context, initBean, initListener);
    }

    /**
     * 获取游戏配置信息
     * 包括：gameid
     *
     * @return
     */
    public YXAppConfig getAppConfig() {
        sendLogNoDebug("yx --> getAppConfig --> do");
        return yxAppConfig;
    }

    /**
     * 登录接口
     *
     * @param loginListener
     */
    public void login(Context context, YXMResultListener loginListener) {
        isLoginSuccess = false;
        if (!isInitSuccess) {
            loginListener.onFailture(604, "未初始化");
            return;
        }
        sendLogNoDebug("yx --> login --> do");
        if (sdkapi == null) {
            sendLogNoDebug("yx --> login --> sdkapi --> null");
            initPaltform(baseInitListener);
        }
        sdkapi.login(context, loginListener);
    }

    /**
     * 详细支付接口(供CP调用)
     *
     * @param doid        CP订单ID  (*必传)
     * @param dpt         CP商品名 (*必传)
     * @param dcn         CP货币名称  (*必传)
     * @param dsid        CP游戏服ID (*必传)
     * @param dsname      CP游戏服名称 (*必传)
     * @param dext        CP扩展回调参数 (*必传)
     * @param drid        CP角色ID (*必传)
     * @param drname      CP角色名 (*必传)
     * @param drlevel     CP角色等级 (*必传)
     * @param dmoney      CP金额(定额)  (*必传)
     * @param dradio      CP兑换比率(1元兑换率默认1:10) (*必传)
     * @param payListener 支付回调
     */
    public void pay(final Context context, final String doid, final String dpt, final String dcn,
                    final String dsid, final String dsname, final String dext, final String drid,
                    final String drname, final int drlevel, final float dmoney, final int dradio,
                    final YXMResultListener payListener) {

        if (!isInitSuccess) {
            payListener.onFailture(604, "未初始化");
            return;
        }

        if (!isLoginSuccess) {
            payListener.onFailture(604, "未登录");
            return;
        }

        sendLogNoDebug("yx --> pay --> do");
        StringBuilder payParams = new StringBuilder();
        payParams.append("  doid=" + doid + "\n");
        payParams.append("  dpt=" + dpt + "\n");
        payParams.append("  dcn=" + dcn + "\n");
        payParams.append("  dsid=" + dsid + "\n");
        payParams.append("  dsname=" + dsname + "\n");
        payParams.append("  dext=" + dext + "\n");
        payParams.append("  drid=" + drid + "\n");
        payParams.append("  drname=" + drname + "\n");
        payParams.append("  drlevel=" + drlevel + "\n");
        payParams.append("  dmoney=" + dmoney + "\n");
        payParams.append("  dradio=" + dradio + "\n");
        sendLogNoDebug("请CP检查参数是否为空:\n" + payParams.toString());

        if (sdkapi == null) {
            payListener.onFailture(205, "初始化还未完成");
            return;
        }
        //检查参数是否为空
        if (TextUtils.isEmpty(doid) || TextUtils.isEmpty(dpt) || TextUtils.isEmpty(dcn) ||
                TextUtils.isEmpty(dsid) || TextUtils.isEmpty(dsname) ||
                TextUtils.isEmpty(drid) || TextUtils.isEmpty(drname) ||
                TextUtils.isEmpty("" + drlevel) || TextUtils.isEmpty("" + dmoney) || TextUtils.isEmpty("" + dradio)
                ) {
            MultiSDKUtils.showTips(context, "pay方法提交的参数不能为空");
            payListener.onFailture(205, "pay方法提交的参数不能为空");
            return;
        }
        sdkapi.pay(context, doid, dpt, dcn, dsid, dsname, dext, drid, drname, drlevel, dmoney, dradio, payListener);
    }

    /**
     * 切换帐号监听，返回与登录回调一样的用户信息
     * 必须先调用初始化
     *
     * @param listener
     */
    public void setSwitchAccountListener(YXMResultListener listener) {
        if (sdkapi != null) {
            sendLogNoDebug("yx --> setSwitchAccount --> do");
            sdkapi.setSwitchAccountListener(listener);
        }
    }

    /**
     * 游戏方主动切换帐号，回调信息与登录回调一致
     *
     * @param context
     * @param changeAccountListener
     */
    public void changeAccount(Context context, YXMResultListener changeAccountListener) {
        isLoginSuccess = false;
        if (!isInitSuccess) {
            changeAccountListener.onFailture(604, "未初始化");
        }
        if (sdkapi != null) {
            sendLogNoDebug("yx --> changeAccount --> do");
            sdkapi.changeAccount(context, changeAccountListener);
        }
    }

    /* 退出游戏接口，有3种退出方式；
     * 1.直接原生退出
     * 2.平台有退出框
     * 3.平台无退出框，调用安卓退出框
     */
    public void showExitDailog(final Context context, final YXMResultListener logoutListener) {
        sendLogNoDebug("yx --> showExitDialog --> do");
        if (sdkapi != null) {
            if (initBean != null && initBean.getUsePlatformExit().equals("1")) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        sdkapi.showExitDailog(context, logoutListener);
                    }

                });
            } else {
                showAndoridExit(context, logoutListener);
            }
        } else {
            showAndoridExit(context, logoutListener);
        }
    }

    /**
     * 安卓原生的退出框
     *
     * @param logoutListener
     */
    private void showAndoridExit(final Context context, final YXMResultListener logoutListener) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {

            @Override
            public void run() {
                new AlertDialog.Builder(context)
                        .setMessage("您确定退出游戏吗？")
                        .setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                logoutListener.onSuccess(new Bundle());
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }


        });

    }

    /**
     * 角色创建
     *
     * @param infos
     */
    @Override
    public void creatRoleInfo(HashMap<String, String> infos) {
        if (sdkapi != null) {
            sendLogNoDebug("yx --> createRoleInfo --> do");
            sdkapi.creatRoleInfo(infos);
        }
    }

    /**
     * 升级角色接口
     *
     * @param infos
     */
    @Override
    public void upgradeRoleInfo(HashMap<String, String> infos) {
        if (sdkapi != null) {
            sendLogNoDebug("yx --> upgradeRoleInfo -->do");
            sdkapi.upgradeRoleInfo(infos);
        }
    }

    /**
     * 提交角色信息接口
     *
     * @param infos
     */
    public void submitRoleInfo(HashMap<String, String> infos) {
        if (sdkapi != null) {
            sdkapi.submitRoleInfo(infos);
            sendLogNoDebug("yx --> sumbitRoleInfo --> do");
        }
    }

    //——————————————————————————————————————生命周期————————————————————————————————
    @Override
    public void onStart() {

        if (sdkapi != null) {
            sendLogNoDebug("yx --> onStart --> do");
            sdkapi.onStart();
        }
    }

    @Override
    public void onRestart() {

        if (sdkapi != null) {
            sendLogNoDebug("yx --> onRestart --> do");
            sdkapi.onRestart();
        }
    }

    public void onResume() {

        if (sdkapi != null) {
            sendLogNoDebug("yx --> onResume --> do");
            sdkapi.onResume();
        }
    }

    public void onPause() {

        if (sdkapi != null) {
            sendLogNoDebug("yx --> onPause --> do");
            sdkapi.onPause();
        }

    }

    public void onStop() {

        if (sdkapi != null) {
            sendLogNoDebug("yx --> onStop --> do");
            sdkapi.onStop();
        }
    }

    public void onDestroy() {

        if (sdkapi != null) {
            sendLogNoDebug("yx --> onStop --> do");
            sdkapi.onDestroy();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (sdkapi != null) {
            sendLogNoDebug("yx --> onActivityResult --> do");
            sdkapi.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onNewIntent(Intent intent) {
        if (sdkapi != null) {
            sendLogNoDebug("yx --> onNewIntent --> do");
            sdkapi.onNewIntent(intent);
        }
    }

    /**
     * 设置当前context，解决游戏有2个activiyty的情况
     *
     * @param cxt
     */
    public void setContext(Context cxt) {

        this.context = cxt;
        if (sdkapi != null) {
            sdkapi.setContext(cxt);
        }

        if (requestManager != null) {
            requestManager.setContext(cxt);
        }
    }
}

