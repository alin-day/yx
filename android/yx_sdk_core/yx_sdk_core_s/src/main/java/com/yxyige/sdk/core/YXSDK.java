package com.yxyige.sdk.core;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.yxyige.sdk.utils.LogUtil;
import com.yxyige.sdk.utils.Util;
import com.yxyige.sdk.widget.ExitDialog;

import org.json.JSONException;
import org.json.JSONObject;


public class YXSDK implements SDKInterface, IError {

    public static final String sdkVersion = "1.1.0";
    private static YXSDK instance;
    private static byte[] lock = new byte[0];
    private SDKManager yxSDKManager;
    private RequestManager requestManager;
    private Context context;
    private boolean isInit = false;

    private YXSDK() {
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static YXSDK getInstance() {

        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new YXSDK();
                }
            }
        }
        return instance;
    }


    public void init(final Context cxt, String appkey, final YXSDKListener initCallback) {

        this.context = cxt;

        if (TextUtils.isEmpty(appkey)) {
            initCallback.onFailture(IError.PARAMS_ERROR, "参数错误");
            return;
        }

        yxSDKManager = new SDKManager(context, appkey, initCallback);
        requestManager = new RequestManager(context);

        requestManager.initRequst(new RequestCallBack() {

            @Override
            public void onRequestSuccess(String content) {

                try {

                    JSONObject jsonObj = new JSONObject(content);
                    int state = jsonObj.getInt("state");

                    if (state == 1) {

                        isInit = true;

                        String data = jsonObj.getString("data");
                        JSONObject jsonObj_data = new JSONObject(data);
                        if (jsonObj_data.has("u")) {
                            JSONObject uObj = new JSONObject(jsonObj_data.getString("u"));
                            if (uObj.has("pay")) {
                                IConfig.newPayUrl = uObj.getString("pay");
                                Util.setNewpayurl(context, IConfig.newPayUrl);
                            }

                            if (uObj.has("uagree")) {
                                Util.setUserAgreeUrl(context, uObj.getString("uagree"));
                            }

                            if (uObj.has("mreg")) {
                                IConfig.mreg = uObj.getString("mreg");
                            }
                        }

                        if (jsonObj_data.has("h")) {
                            IConfig.showHorseLamp = true;
                            JSONObject hObj = new JSONObject(jsonObj_data.getString("h"));
                            if (hObj.has("n")) {
                                if (hObj.getString("n").equals("")) {
                                    IConfig.showHorseLamp = false;
                                }
                                IConfig.horse_race_lamp_content = hObj.getString("n");
                            }

                            if (hObj.has("u")) {
                                IConfig.horse_race_lamp_url = hObj.getString("u");
                            }
                        } else {
                            IConfig.showHorseLamp = false;
                        }

                        // 退弹相关
                        if (jsonObj_data.has("tt")) {
                            IConfig.isHasExit = true;
                            JSONObject ttObj = new JSONObject(jsonObj_data.getString("tt"));
                            if (ttObj.has("img")) {
                                if (ttObj.getString("img").equals("")) {
                                    IConfig.isHasExit = false;
                                }
                                IConfig.tt_image_url = ttObj.getString("img");
                            }

                            if (ttObj.has("u")) {
                                IConfig.tt_download_link = ttObj.getString("u");
                            }

                            if (ttObj.has("dpgn")) {
                                IConfig.tt_packagename = ttObj.getString("dpgn");
                            }
                        } else {
                            IConfig.isHasExit = false;
                        }

                        initCallback.onSuccess(new Bundle());

                    } else {

                        String msg = jsonObj.getString("msg");

                        isInit = false;
                        initCallback.onFailture(IError.ERROR_GET_DATA_FAILD, msg);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    initCallback.onFailture(IError.ERROR_GET_DATA_FAILD, "数据异常，初始化失败");
                    isInit = false;

                } catch (Exception e) {
                    e.printStackTrace();
                    initCallback.onFailture(IError.ERROR_GET_DATA_FAILD, "");
                    isInit = false;

                }
            }

            @Override
            public void onRequestError(String errorMsg) {

                initCallback.onFailture(IError.ERROR_GET_DATA_FAILD, errorMsg);

            }
        }, false);


    }


    /**
     * 切换帐号
     *
     * @param listener
     */
    public void changeAccount(Context context, YXSDKListener listener) {
        // 检查是否初始化成功
        if (!isInit) {
            listener.onFailture(IError.ERROR_CLIENT, "未初始化成功");
            return;
        }
        yxSDKManager.changeAccount(listener);
    }

    /**
     * 帐号注册/登录接口
     *
     * @param listener
     */
    public void login(final Context context, final YXSDKListener listener) {

        // 检查是否初始化成功
        if (!isInit) {
            listener.onFailture(IError.ERROR_CLIENT, "未初始化成功");
            return;
        }

        yxSDKManager.login(new YXSDKListener() {

            @Override
            public void onSuccess(Bundle bundle) {
                listener.onSuccess(bundle);
            }

            @Override
            public void onFailture(int code, String msg) {
                listener.onFailture(code, msg);
            }
        });
    }

    /**
     * 退出接口方法
     *
     * @param context
     */
    public void logout(final Context context, final YXSDKListener listener) {

        LogUtil.d("yx --> do --> logout");
        showExitDialog(listener);
    }

    /**
     * 显示退弹框
     */
    private void showExitDialog(final YXSDKListener exitListener) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {

            @Override
            public void run() {

                if (IConfig.isHasExit) {
                    ExitDialog exitDialog = new ExitDialog(context, exitListener);
                    exitDialog.setUrl(IConfig.tt_download_link); //解析
                    exitDialog.setPkn(IConfig.tt_packagename); //包名
                    exitDialog.setImagePath(IConfig.tt_image_url); //初始化完成时开异步线程预加载
                    exitDialog.setCanceledOnTouchOutside(true);
                    exitDialog.setCancelable(true);
                    exitDialog.show();
                } else {
                    new AlertDialog.Builder(context)
                            .setMessage("您确定退出游戏吗？")
                            .setCancelable(false)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    exitListener.onSuccess(new Bundle());
                                }
                            })
                            .setNegativeButton("继续游戏", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    exitListener.onFailture(IError.ERROR_CLIENT, "继续游戏");
                                    dialog.cancel();
                                }
                            })
                            .show();
                }


            }
        });
    }

    /**
     * 支付接口
     *
     * @param context     上下文 (*必传)
     * @param doid        CP订单ID (*必传)
     * @param dpt         CP商品名(*必传)
     * @param dcn         CP货币名称 (*必传)
     * @param dsid        CP游戏服ID (*必传)
     * @param dext        CP扩展回调参数 (*必传)
     * @param drid        CP角色ID(*必传)
     * @param drname      CP角色名(*必传)
     * @param drlevel     CP角色等级(*必传)
     * @param dmoney      CP金额(定额) (*必传)
     * @param dradio      CP兑换比率(1元兑换率默认1:10)(*必传)
     * @param moid        CP金额(定额) (*必传)
     * @param payListener 充值回调 (*必传)
     */
    public void pay(Context context, String doid, String dpt, String dcn, String dsid, String dsname, String dext, String drid, String drname,
                    int drlevel, float dmoney, int dradio, String moid, YXSDKListener payListener) {

        // 检查是否初始化成功
        if (!isInit) {
            payListener.onFailture(IError.ERROR_CLIENT, "未初始化成功");
            return;
        }

        // 检查是否登录成功
        if (TextUtils.isEmpty(Util.getUserid(context))) {
            payListener.onFailture(IError.ERROR_CLIENT, "用户未登录");
            return;
        }

        yxSDKManager.pay(context, doid, dpt, dcn, dsid, dsname, dext, drid, drname, drlevel, dmoney, dradio, moid, payListener);

    }
}
