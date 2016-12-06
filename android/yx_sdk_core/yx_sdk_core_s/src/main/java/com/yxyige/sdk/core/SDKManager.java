package com.yxyige.sdk.core;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.yxyige.sdk.bean.ALAccountInfo;
import com.yxyige.sdk.bean.YXConfig;
import com.yxyige.sdk.utils.AccountTools;
import com.yxyige.sdk.utils.LogUtil;
import com.yxyige.sdk.utils.Util;
import com.yxyige.sdk.utils.ViewController;
import com.yxyige.sdk.views.AutoLoginDialog;
import com.yxyige.sdk.views.LoginDialog;
import com.yxyige.sdk.views.PayWebDialog;

public class SDKManager implements IError {


    public static boolean isShowLogin = false;    //登录界面是否显示着
    public static boolean isShowPay = false;    //支付界面是否显示着
    public static YXSDKListener switchAccountListener;    //切换帐号回调
    public static YXSDKListener payListener;    //pay回调
    public Context context;
    private LoginDialog dialog;
    private YXConfig config = null;

    public SDKManager(Context context, String appkey, YXSDKListener initCallback) {
        this.context = context;

        Util.setAppkey(context, appkey);

        if (config == null) {
            config = new YXConfig(context, initCallback);
        }
    }

    private void showRegisterView(YXSDKListener listener) {

        dialog = new LoginDialog(context, listener, SDKManager.this, true);

        if (!dialog.isShowing()) {

            dialog.show();
            isShowLogin = true;
        }

        dialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                isShowLogin = false;
            }
        });
    }

    /**
     * 切换帐号
     *
     * @param listener
     */
    void changeAccount(YXSDKListener listener) {

        if (Util.isSkipSQChangeAccountLogin(context)) {
            //跳过登录界面，返回空的信息
            listener.onSuccess(new Bundle());
        } else {
            //显示登录界面
            showSQLoginView(listener);
        }
    }

    /**
     * 显示帐号登录界面
     *
     * @param listener
     */
    private void showSQLoginView(YXSDKListener listener) {

        dialog = new LoginDialog(context, listener, SDKManager.this);

        if (!dialog.isShowing()) {

            dialog.show();
            isShowLogin = true;
        }

        dialog.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                isShowLogin = false;
            }
        });
    }

    /**
     * 登录接口
     *
     * @param loginCallback
     */
    public void login(final YXSDKListener loginCallback) {

        if (loginCallback == null || (Util.getAppkey(context) == null)) {
            loginCallback.onFailture(PARAMS_ERROR, "必要参数不能为空");
            return;
        }

        if (context.checkCallingOrSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ViewController.showToast(context, "请添加网络访问权限");
        } else {
            ALAccountInfo account = AccountTools.getAutoLoginAccount(context);
            if (account != null) {
                Dialog dialog = new AutoLoginDialog(context, account, new Callback() {
                    @Override
                    public void callback() {
                        //显示登录界面
                        showSQLoginView(loginCallback);
                    }
                }, loginCallback);
                dialog.show();
            } else {
                //显示注册界面
                showRegisterView(loginCallback);
            }
        }
    }

    /**
     * 退出接口方法
     */
    void logout() {
        //清除用户数据
        Util.cleanUserData(context);
    }


    /**
     * 支付接口(供CP调用)
     *
     * @param doid    CP订单ID
     * @param dpt     CP商品名
     * @param dcn     CP货币名称
     * @param dsid    CP游戏服ID( 标识 )
     * @param dext    CP扩展回调参数
     * @param drid    CP扩展回调参数
     * @param drname  CP角色名
     * @param drlevel CP角色等级
     * @param dmoney  CP金额(定额)
     * @param dradio  CP兑换比率(1元兑换率默认1:10)
     * @param moid    SDK[M]订单IDz
     */
    public void pay(Context context, String doid, String dpt, String dcn, String dsid, String dsname, String dext, String drid, String drname,
                    int drlevel, float dmoney, int dradio, String moid, YXSDKListener payListener) {
        this.payListener = payListener;
        outPay(context, doid, dpt, dcn, dsid, dsname, dext, drid, drname, drlevel, dmoney, dradio, moid, 1, payListener);

    }

    /**
     * 仅供外部点击充值调用调用 (private ,need proguid)
     * web支付用的都是此参数
     *
     * @param context     上下文环境
     * @param doid        CP订单ID
     * @param dpt         CP商品名
     * @param dcn         CP货币名称
     * @param dsid        CP游戏服ID( 标识 )
     * @param dext        CP扩展回调参数
     * @param drid        CP扩展回调参数
     * @param drname      CP角色名
     * @param drlevel     CP角色等级
     * @param dmoney      CP金额(定额)
     * @param dradio      CP兑换比率(1元兑换率默认1:10)
     * @param moid        SDK[M]订单IDz
     * @param ig          是否游戏中跳出来 0:不是(默认)1:是 -- 页面将显示余额支付选项
     * @param payListener
     */
    private void outPay(Context context, String doid, String dpt, String dcn, String dsid, String dsname, String dext, String drid, String drname,
                        int drlevel, float dmoney, int dradio, String moid, int ig, YXSDKListener payListener) {

        if (payListener != null && "".equals(dsid)) {
            payListener.onFailture(PARAMS_ERROR, "区服ID不能为空");
            return;
        }

        String loginToken = Util.getToken(context);

        if ("".equals(loginToken) && payListener != null) {
            payListener.onFailture(IError.NO_LOGINED, "尚未登录，请登录");
            return;
        }

        Bundle params = new Bundle();
        params = RequestManager.addCommonParamsOfPay(context, params, Util.getUserid(context), moid);

        params.putString("token", Util.getToken(context));
        params.putString("gname", Util.getGamename(context));
        params.putString("doid", doid);
        params.putString("dpt", dpt);
        params.putString("dcn", dcn);
        params.putString("dsid", "" + dsid);
        params.putString("dsname", dsname);
        params.putString("dext", dext);
        params.putString("drid", drid);
        params.putString("drname", drname);
        params.putString("drlevel", "" + drlevel);
        params.putString("dmoney", "" + dmoney);
        params.putString("dradio", "" + dradio);
        params.putString("moid", moid);
        params.putString("ig", "1");    //默认是从游戏中跳出
        params.putString("os", "1");    //1,为Android设备
        params.putString("uname", Util.getUsername(context));
        params.putString("uid", Util.getUserid(context));
        params.putString("mac", Util.getMac(context));
        params.putString("imei", Util.getIMEI(context));
        params.putString("haswx", (Util.checkAppInstalled(context, "com.tencent.mm") ? 1 : 0) + ""); // 微信支付字段

        String payUrl = Util.getNewpayurl(context);

        String paymentUrl = payUrl + "?" + Util.encodeUrl(params);

        LogUtil.d("yx-s --> paymentUrl --> " + paymentUrl);

        if (context.checkCallingOrSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {

            ViewController.showToast(context, "请添加网络访问权限");

        } else {

            PayWebDialog dialog = new PayWebDialog(context,
                    Util.getIdByName("ContentOverlay", "style", context.getPackageName(), context),
                    paymentUrl,
                    payListener
            );

            dialog.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    isShowPay = false;
                }
            });

            if (!dialog.isShowing()) {

                dialog.show();

//                isShowPay = true;
//
//                dialog.waitShow();
            }

        }
    }


}
