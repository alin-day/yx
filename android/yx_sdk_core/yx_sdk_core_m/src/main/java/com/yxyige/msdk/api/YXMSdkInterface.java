package com.yxyige.msdk.api;

import android.content.Context;
import android.content.Intent;

import java.util.HashMap;

public interface YXMSdkInterface {

    /**
     * 登陆接口
     *
     * @param context
     * @param listener
     */
    void login(Context context, YXMResultListener listener);

    /**
     * 主动调用切换帐号接口
     *
     * @param context
     * @param listener
     */
    void changeAccount(Context context, YXMResultListener listener);

    /**
     * 从悬浮窗中切换帐号接口
     *
     * @param listener
     */
    void setSwitchAccountListener(YXMResultListener listener);

    /**
     * 退出游戏对话框
     *
     * @param context
     * @param listener
     */
    void showExitDailog(Context context, YXMResultListener listener);


    /**
     * 支付方法 标*号的为必传参数
     *
     * @param context
     * @param doid        (*必填)
     * @param dpt(*必填)
     * @param dcn         (*必填)
     * @param dsid        (*必填)
     * @param dsname      (*必填)
     * @param dext        (*必填)
     * @param drid        (*必填)
     * @param drname      (*必填)
     * @param drlevel     (*必填)
     * @param dmoney      (*必填)
     * @param dradio      (*必填)
     * @param payListener (*必填)
     */
    void pay(Context context, String doid, String dpt, String dcn, String dsid,
             String dsname, String dext, String drid, String drname, int drlevel, float dmoney,
             int dradio, YXMResultListener payListener);

    /**
     * 提交角色信息接口（可以提交指定格式数据）
     *
     * @param infos
     */
    void submitRoleInfo(HashMap<String, String> infos);//进入服务器时调用

    void creatRoleInfo(HashMap<String, String> infos);//创建角色时调用

    void upgradeRoleInfo(HashMap<String, String> infos);//角色升级时调用

    /**
     * 周期方法
     */
    void onStart();

    void onResume();//必接

    void onPause();//必接

    void onStop();//必接

    void onDestroy();

    void onRestart();

    void onActivityResult(int requestCode, int resultCode, Intent data);//必接

    void onNewIntent(Intent intent);//必接

    void setContext(Context context);//必接


}
