package com.yxyige.sdk.core;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.yxyige.sdk.http.RequestParams;
import com.yxyige.sdk.utils.LogUtil;
import com.yxyige.sdk.utils.Util;
import com.yxyige.sdk.widget.ProgressDialog;

import java.util.Random;


/**
 * 网络请求细节封装类
 *
 * @author mac_tang
 * @since 2014年09月28日15:43:19
 */
public class RequestManager {


    private Context mContext;
    private ProgressDialog waitDialog;

    public RequestManager(Context context) {

        this.mContext = context;
    }

    public static Bundle addCommonParamsOfPay(Context context, Bundle params, String s1, String s2) {


        String ptid = Util.getPtid(context);
        String gid = Util.getGid(context);
        String refid = Util.getRefid(context);
        String duid = Util.getDuid(context);
        String version = Util.getVersionName(context);
        String sdkver = YXSDK.sdkVersion;
        String time = "" + System.currentTimeMillis() / 1000;

        String key = Util.getAppkey(context);

        String comstr = ptid + gid + time + key;

        //串签名
        String signStr = Util.Md5(comstr + s1 + s2).toLowerCase();

        params.putString("gid", gid);
        params.putString("ptid", ptid);
        params.putString("refid", refid);
        params.putString("duid", duid);
        params.putString("time", time);
        params.putString("sign", signStr);
        params.putString("version", version);
        params.putString("sdkver", sdkver);
        return params;
    }

    /**
     * 初始化
     *
     * @param reqestCallback
     * @param isShowDialog
     */
    public void initRequst(final RequestCallBack reqestCallback, boolean isShowDialog) {
        //构建请求参数
        RequestParams params = new RequestParams();
        addCommonParams(params);
        sendRequest(INewUrl.INIT, params, reqestCallback, isShowDialog);
    }

    /**
     * 登录请求
     *
     * @param loginName      帐号
     * @param loginPwd       密码
     * @param reqestCallback 登录网络请求回调
     * @param isShowDialog   是否显示网络加载框
     */
    public void loginRequest(String loginName, String loginPwd, final RequestCallBack reqestCallback, boolean isShowDialog) {
        RequestParams logparams = new RequestParams();
        logparams.put("uname", loginName);
        logparams.put("upwd", loginPwd);
        addCommonParams(logparams, loginName, loginPwd);
        sendRequest(INewUrl.LOGIN, logparams, reqestCallback, isShowDialog);
    }

    /**
     * 注册请求
     *
     * @param regName        注册的帐号
     * @param regPw          注册的密码
     * @param reqestCallback 注册网络请求回调
     * @param isShowDialog   是否显示网络加载框
     */
    public void registerRequest(String regName, String regPw, final RequestCallBack reqestCallback, boolean isShowDialog) {
        RequestParams regparams = new RequestParams();
        regparams.put("uname", regName);
        regparams.put("upwd", regPw);
        addCommonParams(regparams, regName, regPw);
        sendRequest(INewUrl.REG, regparams, reqestCallback, isShowDialog);
    }

    /**
     * 推送请求
     *
     * @param reqestCallback
     * @param isShowDialog
     */
    public void pushRequest(final RequestCallBack reqestCallback, boolean isShowDialog) {
        RequestParams getPushInfoparams = new RequestParams();
        getPushInfoparams.put("token", Util.getToken(mContext));
        addCommonParams(getPushInfoparams);
        sendRequest(INewUrl.PUSH, getPushInfoparams, reqestCallback, isShowDialog);
    }

    /**
     * 获取手机注册的时候的验证码
     *
     * @param phoneNum       手机号码
     * @param reqestCallback
     * @param isShowDialog
     */
    public void getVerifyCodeRequest(String phoneNum, final RequestCallBack reqestCallback, boolean isShowDialog) {
        RequestParams getVerifyCodeParams = new RequestParams();
        getVerifyCodeParams.put("uname", phoneNum);
        addCommonParams(getVerifyCodeParams, phoneNum, "");
        sendRequest(INewUrl.MSCODE, getVerifyCodeParams, reqestCallback, isShowDialog);
    }

    /**
     * 获取重置密码手机密保安全码
     *
     * @param phoneNum       手机号码
     * @param reqestCallback
     * @param isShowDialog
     */
    public void getResetVerifyCodeRequest(String phoneNum, final RequestCallBack reqestCallback, boolean isShowDialog) {
        RequestParams getVerifyCodeParams = new RequestParams();
        getVerifyCodeParams.put("uname", phoneNum);
        addCommonParams(getVerifyCodeParams, phoneNum, "");
        sendRequest(INewUrl.RESET_RMS, getVerifyCodeParams, reqestCallback, isShowDialog);
    }

    /**
     * 校验密保手机安全码重置密码
     *
     * @param phoneNum       手机号
     * @param verifyCode     验证码
     * @param reqestCallback
     * @param isShowDialog
     */
    public void phoneNumResetRequest(String phoneNum, String verifyCode, final RequestCallBack reqestCallback, boolean isShowDialog) {
        RequestParams phoneNumRegParams = new RequestParams();
        phoneNumRegParams.put("uname", phoneNum);
        phoneNumRegParams.put("scode", verifyCode);
        addCommonParams(phoneNumRegParams, phoneNum, verifyCode);
        sendRequest(INewUrl.RESET_RM, phoneNumRegParams, reqestCallback, isShowDialog);
    }

    /**
     * 手机号码注册接口
     *
     * @param phoneNum       手机号
     * @param verifyCode     验证码
     * @param reqestCallback
     * @param isShowDialog
     */
    public void phoneNumRegRequest(String phoneNum, String verifyCode, final RequestCallBack reqestCallback, boolean isShowDialog) {
        RequestParams phoneNumRegParams = new RequestParams();
        phoneNumRegParams.put("uname", phoneNum);
        phoneNumRegParams.put("scode", verifyCode);
        addCommonParams(phoneNumRegParams, phoneNum, verifyCode);
        sendRequest(INewUrl.MREG, phoneNumRegParams, reqestCallback, isShowDialog);
    }

    /**
     * 查询支付状态
     *
     * @param context
     * @param payWay
     * @param moid
     * @param reqestCallback
     * @param isShowDialog
     */
    public void checkPayStatus(Context context, String payWay, String moid, final RequestCallBack reqestCallback, boolean isShowDialog) {
        String ptid = Util.getPtid(context);
        String gid = Util.getGid(context);
        String refid = Util.getRefid(context);
        String duid = Util.getDuid(context);
        String version = Util.getVersionName(context);
        String sdkver = YXSDK.sdkVersion;
        String time = "" + System.currentTimeMillis() / 1000;
        String key = Util.getAppkey(context);
        String comstr = gid + ptid + time + key + Util.getUserid(context) + moid;
        //串签名
        String signStr = Util.Md5(comstr).toLowerCase();

        RequestParams wxCheckStausRp = new RequestParams();
        wxCheckStausRp.put("ptid", ptid);
        wxCheckStausRp.put("gid", gid);
        wxCheckStausRp.put("refid", refid);
        wxCheckStausRp.put("duid", duid);
        wxCheckStausRp.put("version", version);
        wxCheckStausRp.put("sdkver", sdkver);
        wxCheckStausRp.put("time", time);
        wxCheckStausRp.put("sign", signStr);
        wxCheckStausRp.put("token", Util.getToken(context));
        wxCheckStausRp.put("uuid", moid);
        wxCheckStausRp.put("pway", payWay);
        wxCheckStausRp.put("mac", Util.getMac(context));
        wxCheckStausRp.put("imei", Util.getIMEI(context));
        wxCheckStausRp.put("os", "1");
        sendRequest(INewUrl.PAY_CHECK, wxCheckStausRp, reqestCallback, true);
    }

    /**
     * 组装注册短信内容以及相关的vsign
     */
    public String[] constructVsignAndMsg() {
        String aid = IConfig.aid;
        String pid = Util.getPtid(mContext);
        String gid = Util.getGid(mContext);
        String vtime = "" + System.currentTimeMillis() / 1000; //单位秒
        int random = new Random().nextInt(899999) + 100000; //生成100000-999999的随机数

        String key = Util.getAppkey(mContext);

        String vsign = Util.Md5(aid + gid + pid + vtime + random + key);

        String brand = IConfig.brand;

        String msg = aid + "#" + pid + "#" + gid + "#" + vtime + "#" + random + "#" + vsign + "#" + brand;

        LogUtil.e("-->vsign:" + vsign + ", msgLength:" + msg.length() + " msgContent:" + msg);

        return new String[]{vsign, msg};
    }

   /*
    * -------------------------------------------------------------------------------------
    * -------------------------------------华丽丽的分割线-------------------------------------
    * -------------------------------------------------------------------------------------
    */

    //发送请求
    private void sendRequest(final String reqeustUrl, RequestParams params, final RequestCallBack reqestCallback, boolean isShowDialog) {

        if (isShowDialog) {

            if (waitDialog == null) {

                waitDialog = new ProgressDialog(mContext);
                waitDialog.setCancelable(false);

            }

            if (waitDialog != null && !waitDialog.isShowing()) {
                waitDialog.show();
            }
        }


        LogUtil.e("request: > " + reqeustUrl + "\n    " + params.toString());


        AppClient.postAbsoluterUrl(reqeustUrl, params, new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (waitDialog != null) {
                    waitDialog.dismiss();
                }


                int what = msg.what;
                String content = "";

                switch (what) {
                    case -1:

                        reqestCallback.onRequestError("网络异常，请稍候再试");

                        break;
                    case 1:

                        content = msg.obj.toString();

                        try {

//                        LogUtil.e( "response: > "+reqeustUrl+"\n    " + new String(content.getBytes("gbk"),"gbk"));
                            LogUtil.e("response: > " + reqeustUrl + "\n    " + Util.encodingtoStr(content));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        reqestCallback.onRequestSuccess(content);

                        break;

                    default:
                        break;
                }


            }

        });

    }

    /**
     * 添加网络请求通用的参数，支持签名扩展
     *
     * @param params
     * @param s1     签名扩展字符串1
     * @param s2     签名扩展字符串2
     * @return
     */
    private RequestParams addCommonParams(RequestParams params, String s1, String s2) {
        String gid = Util.getGid(mContext);
        String ptid = Util.getPtid(mContext);
        String refid = Util.getRefid(mContext);
        String version = Util.getVersionName(mContext);
        String time = "" + System.currentTimeMillis() / 1000;

        String mac = Util.getMac(mContext);
        String imei = Util.getIMEI(mContext);
        String duid = Util.getDuid(mContext);

        String comstr = ptid + gid + refid + duid + version + YXSDK.sdkVersion + time + Util.getAppkey(mContext);

        LogUtil.e("comstr:>" + ptid + ">" + gid + ">" + refid + ">" + duid + ">" + YXSDK.sdkVersion + ">" + version + ">" + time + ">" + Util.getAppkey(mContext));

        //串签名
        String sign = getSignString(comstr, s1, s2);

        params.put("gid", gid);
        params.put("ptid", ptid);
        params.put("refid", refid);
        params.put("duid", duid);
        params.put("version", version);
        params.put("sdkver", YXSDK.sdkVersion);
        params.put("time", time);
        params.put("sign", sign);
        params.put("mac", mac);
        params.put("imei", imei);

        return params;
    }

    //添加公共参数，可扩展
    private RequestParams addCommonParams(RequestParams params) {

        return addCommonParams(params, "", "");
    }

    private String getSignString(String comStr, String s1, String s2) {
        String signStr = Util.Md5(comStr + s1 + s2).toLowerCase();
        return signStr;
    }


}
