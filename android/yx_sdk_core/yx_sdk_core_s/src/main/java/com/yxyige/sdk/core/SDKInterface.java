package com.yxyige.sdk.core;

import android.content.Context;

public interface SDKInterface {

    //初始化接口
    public void init(Context context, String appkey, YXSDKListener initCallback);

    //登录接口
    public void login(Context context, YXSDKListener listener);

    //主动调用切换帐号接口
    public void changeAccount(Context context, YXSDKListener listener);

    //退出接口
    public void logout(Context context, YXSDKListener listener);

    //支付接口
    public void pay(Context context, String doid, String dpt, String dcn, String dsid, String dsname, String dext, String drid, String drname,
                    int drlevel, float dmoney, int dradio, String moid, YXSDKListener payListener);

}
