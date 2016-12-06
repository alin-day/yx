package com.yxyige.msdk.api.sdk;

import android.content.Context;
import android.os.Bundle;

import com.yxyige.msdk.BaseYXMCore;
import com.yxyige.msdk.api.InitBean;
import com.yxyige.msdk.api.MultiSDKUtils;
import com.yxyige.msdk.api.YXMResultListener;
import com.yxyige.msdk.utils.ZipString;
import com.yxyige.sdk.core.YXSDK;
import com.yxyige.sdk.core.YXSDKListener;

public class YXGame extends Platform {

    public YXGame(Context context, InitBean initBean, YXMResultListener initListener) {
        super(context, initBean, initListener);
    }

    @Override
    protected void initPlatform(final YXMResultListener initListener) {
        BaseYXMCore.sendLog("yx-YXGame --> initPlatform()");
        //优象平台初始化
        String key = ZipString.zipString2Json(MultiSDKUtils.getKey(platformContext));
        YXSDK.getInstance().init(platformContext, key, new YXSDKListener() {
            @Override
            public void onSuccess(Bundle bundle) {
                BaseYXMCore.sendLog("yx-YXGame --> initSuccess");
                BaseYXMCore.isInitSuccess = true;
                initListener.onSuccess(new Bundle());
            }

            @Override
            public void onFailture(int i, String s) {
                BaseYXMCore.sendLog("yx-YXGame --> initFailure --> msg --> " + s);
                BaseYXMCore.isInitSuccess = false;
                initListener.onFailture(i, s);
            }
        });
    }


    @Override
    protected void loginPlatform(final YXMResultListener loginListener) {
        BaseYXMCore.sendLog("yx-YXGame --> loginPlatform()");
        YXSDK.getInstance().login(platformContext, new YXSDKListener() {
            @Override
            public void onSuccess(Bundle bundle) {
                upingData25g = false;
                MultiSDKUtils.setYXUserid(platformContext, bundle.getString("userid"));
                MultiSDKUtils.setYXUsername(platformContext, bundle.getString("username"));
                MultiSDKUtils.setYXToken(platformContext, bundle.getString("token"));
                Bundle mBundle = new Bundle();
                mBundle.putString("token", bundle.getString("token"));
                mBundle.putString("pid", MultiSDKUtils.getPID(platformContext));
                mBundle.putString("gid", MultiSDKUtils.getGID(platformContext));
                BaseYXMCore.sendLog("yx-YXGame --> loginSuccess --> data --> " + mBundle.toString());
                BaseYXMCore.isLoginSuccess = true;
                loginListener.onSuccess(mBundle);
            }

            @Override
            public void onFailture(int i, String s) {
                BaseYXMCore.sendLog("yx-YXGame --> loginFailure --> msg --> " + s);
                BaseYXMCore.isLoginSuccess = false;
                loginListener.onFailture(i, s);
                upingData25g = false;
            }
        });
    }

    @Override
    public void changeAccount(Context context, final YXMResultListener changeAccountListener) {
        super.changeAccount(context, changeAccountListener);
        BaseYXMCore.sendLog("yx-YXGame --> changeAccount()");
        YXSDK.getInstance().changeAccount(platformContext, new YXSDKListener() {
            @Override
            public void onSuccess(Bundle bundle) {
                MultiSDKUtils.setYXUserid(platformContext, bundle.getString("userid"));
                MultiSDKUtils.setYXUsername(platformContext, bundle.getString("username"));
                MultiSDKUtils.setYXToken(platformContext, bundle.getString("token"));
                Bundle mBundle = new Bundle();
                mBundle.putString("token", bundle.getString("token"));
                mBundle.putString("pid", MultiSDKUtils.getPID(platformContext));
                mBundle.putString("gid", MultiSDKUtils.getGID(platformContext));
                BaseYXMCore.sendLog("yx-YXGame --> changeAccountSuccess --> data --> " + mBundle.toString());
                BaseYXMCore.isLoginSuccess = true;
                upingData25g = false;
                changeAccountListener.onSuccess(mBundle);
            }

            @Override
            public void onFailture(int i, String s) {
                upingData25g = false;
                BaseYXMCore.sendLog("yx-YXGame --> changeAccountFailure --> msg --> " + s);
                BaseYXMCore.isLoginSuccess = false;
                changeAccountListener.onFailture(i, s);
            }
        });
    }

    /**
     * @param context     上下文环境
     * @param doid        CP订单ID  (*必传)
     * @param dpt         CP商品名 (*必传)
     * @param dcn         CP货币名称  (*必传)
     * @param dsid        CP游戏服ID (*必传)
     * @param dsname      CP游戏服ID (*必传)
     * @param dext        CP扩展回调参数 (*必传)
     * @param drid        CP角色ID (*必传)
     * @param drname      CP角色名 (*必传)
     * @param drlevel     CP角色等级 (*必传)
     * @param dmoney      CP金额(定额)  (*必传)
     * @param dradio      CP金额(定额)  (*必传)
     * @param moid        SDK[M]订单ID
     * @param data        SDK[M]多平台下单，服务器返回附加参数
     * @param payListener
     */
    @Override
    protected void payPlatform(Context context, String doid, String dpt, String dcn, String dsid, String dsname, String dext, String drid, String drname, int drlevel, float dmoney, int dradio, String moid, String data, final YXMResultListener payListener) {
        BaseYXMCore.sendLog("yx-YXGame --> payPlatform()");
        YXSDK.getInstance().pay(context, doid, dpt, dcn, dsid, dsname, dext, drid, drname, drlevel, dmoney, dradio, moid, new YXSDKListener() {
            @Override
            public void onSuccess(Bundle bundle) {
                BaseYXMCore.sendLog("yx-YXGame --> paySuccess");
                upingData25g = false;
                payListener.onSuccess(new Bundle());
            }

            @Override
            public void onFailture(int code, String msg) {
                BaseYXMCore.sendLog("yx-YXGame --> payFailure --> code --> " + code + " --> msg --> " + msg);
                upingData25g = false;
                payListener.onFailture(code, msg);
            }
        });

    }

    @Override
    public void showExitDailog(Context context, final YXMResultListener exitListener) {
        super.showExitDailog(context, exitListener);
        BaseYXMCore.sendLog("yx-YXGame --> showExitDialog()");
        YXSDK.getInstance().logout(platformContext, new YXSDKListener() {
            @Override
            public void onSuccess(Bundle bundle) {
                BaseYXMCore.sendLog("yx-YXGame --> exitSuccess");
                upingData25g = false;
                exitListener.onSuccess(new Bundle());
            }

            @Override
            public void onFailture(int i, String s) {
                BaseYXMCore.sendLog("yx-YXGame --> exitFailure --> msg --> " + s);
                upingData25g = false;
                exitListener.onFailture(604, s);
            }
        });
    }
}
