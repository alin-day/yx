package com.yxyige.msdk.api.sdk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.yxyige.msdk.BaseYXMCore;
import com.yxyige.msdk.api.InitBean;
import com.yxyige.msdk.api.MRequestCallBack;
import com.yxyige.msdk.api.MRequestManager;
import com.yxyige.msdk.api.MultiSDKUtils;
import com.yxyige.msdk.api.YXMResultListener;
import com.yxyige.msdk.api.YXMSdkInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public abstract class Platform implements YXMSdkInterface {

    protected Context platformContext;
    protected MRequestManager requestManager = null;
    //如果联运平台不支持定额则设置为false
    protected boolean isNeedInputMoney = true;
    protected boolean upingData25g = false;

    public Platform(Context cxt, InitBean initBean, YXMResultListener initListener) {
        BaseYXMCore.sendLog("yx --> platform --> Platform()");
        platformContext = cxt;
        requestManager = new MRequestManager(platformContext);
        initPlatform(initListener);
    }

    /**
     * 把Map组装成Json格式
     *
     * @param map
     * @return
     */
    public static String mapToJson(HashMap<String, String> map) {
        if (map == null || map.isEmpty()) {
            return "";
        }
        Set<String> keys = map.keySet();
        Iterator<String> it = keys.iterator();
        String jsonStr = "";
        JSONObject obj = new JSONObject();
        while (it.hasNext()) {
            try {
                String key = it.next();
                String value = map.get(key);
                obj.put(key, value);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }
        jsonStr = obj.toString();
        return jsonStr;
    }

    /**
     * 登陆成功返回数据通用回调 便于统一返回数据
     *
     * @param content
     * @param loginListerner
     */
    protected void loginSuccessCallBack(String content, YXMResultListener loginListerner) {
        BaseYXMCore.sendLog("yx --> platform --> loginSuccessCallBack");
        try {
            JSONObject contentObj = new JSONObject(content);
            if (contentObj.getString("state").equals("1")) {

                //联运平台TOKEN验证等待补充

            } else {
                loginListerner.onFailture(407, contentObj.getString("msg"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            BaseYXMCore.sendLog("yx --> loginSuccessCallBack --> content --> " + content);
            BaseYXMCore.sendLog("yx --> loginSuccessCallBack --> e --> " + e);
        }
    }

    /**
     * 平台初始化接口调用
     *
     * @param initListener
     */
    protected abstract void initPlatform(YXMResultListener initListener);

    /**
     * 平台登录调用
     *
     * @param loginListener
     */
    protected abstract void loginPlatform(YXMResultListener loginListener);

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
    protected abstract void payPlatform(Context context, String doid,
                                        String dpt, String dcn, String dsid, String dsname, String dext,
                                        String drid, String drname, int drlevel, float dmoney, int dradio,
                                        String moid, String data, YXMResultListener payListener);

    @Override
    public void login(Context context, final YXMResultListener loginListener) {
        BaseYXMCore.sendLog("yx --> platform --> login");
        if (upingData25g) {
            Toast.makeText(context, "处理中，请稍候.", Toast.LENGTH_LONG).show();
            return;
        }
        if (context == null) {
            BaseYXMCore.sendLog("yx --> platform --> login --> context --> null");
            return;
        }
        upingData25g = true;
        loginPlatform(loginListener);
    }

    @Override
    public void pay(final Context context, final String doid, final String dpt,
                    final String dcn, final String dsid, final String dsname,
                    final String dext, final String drid, final String drname,
                    final int drlevel, final float dmoney, final int dradio,
                    final YXMResultListener payListener) {
        if (upingData25g) {
            Toast.makeText(context, "支付未完成，请稍后再试.", Toast.LENGTH_LONG).show();
            return;
        }
        upingData25g = true;
        if (dmoney == 0 && isNeedInputMoney) {
            // 当金额为0是，让用户输入金额
            MultiSDKUtils.getPayMoney(context, new YXMResultListener() {

                @Override
                public void onSuccess(Bundle bundle) {
                    upingData25g = false;
                    int payMoney = bundle.getInt("money"); // 取得金额并再次调用pay方法
                    pay(context, doid, dpt, dcn, dsid, dsname, dext, drid,
                            drname, drlevel, payMoney, dradio, payListener);
                }

                @Override
                public void onFailture(int code, String msg) {
                    upingData25g = false;
                    payListener.onFailture(code, msg);

                }
            });
        } else {
            requestManager.payRequest(doid, dsid, dsname, dext, drid, drname,
                    drlevel, dmoney, dradio, new MRequestCallBack() {
                        @Override
                        public void onRequestSuccess(String content) {
                            //多平台下单成功后，逻辑处理
                            try {
                                JSONObject contentObj = new JSONObject(content);
                                if (contentObj.getString("state").equals("1")) {
                                    JSONObject dataObj = new JSONObject(contentObj.getString("data"));
                                    if (dataObj.has("moid")) {
                                        String moid = dataObj.getString("moid");
                                        payPlatform(context, doid, dpt, dcn, dsid, dsname, dext, drid, drname, drlevel, dmoney, dradio, moid, "", payListener);
                                    }
                                } else {
                                    payListener.onFailture(407, contentObj.getString("msg"));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                BaseYXMCore.sendLog("yx-m --> mPay --> failure --> e --> " + e.toString());
                                payListener.onFailture(407, "服务器返回错误");
                            }
                        }

                        @Override
                        public void onRequestError(String errorMsg) {
                            payListener.onFailture(404, errorMsg);
                        }
                    }, true);
        }
    }

    @Override
    public void changeAccount(Context context, YXMResultListener listener) {

    }

    @Override
    public void setSwitchAccountListener(YXMResultListener listener) {

    }

    @Override
    public void showExitDailog(Context context, YXMResultListener listener) {

    }

    @Override
    public void submitRoleInfo(HashMap<String, String> infos) {

    }

    @Override
    public void upgradeRoleInfo(HashMap<String, String> infos) {

    }

    @Override
    public void creatRoleInfo(HashMap<String, String> infos) {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onRestart() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void setContext(Context context) {
        platformContext = context;
    }

    @Override
    public void onNewIntent(Intent intent) {

    }
}