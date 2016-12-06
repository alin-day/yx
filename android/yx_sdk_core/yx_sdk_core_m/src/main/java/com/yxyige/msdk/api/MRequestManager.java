package com.yxyige.msdk.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

import com.yxyige.afinal.FinalHttp;
import com.yxyige.afinal.http.AjaxCallBack;
import com.yxyige.afinal.http.AjaxParams;
import com.yxyige.msdk.BaseYXMCore;
import com.yxyige.msdk.utils.ZipString;
import com.yxyige.sdk.utils.Util;


/**
 * 网络请求细节封装类
 *
 * @author mac_tang
 * @since 2014年09月28日15:43:19
 */
public class MRequestManager {


    private Context mContext;
    private ProgressDialog waitDialog;

    public MRequestManager(Context context) {
        this.mContext = context;
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
     * 初始化接口
     *
     * @param reqestCallback
     * @param isShowDialog
     */
    public void initRequst(final MRequestCallBack reqestCallback, boolean isShowDialog) {
        //构建请求参数
        AjaxParams params = new AjaxParams();
        BaseYXMCore.sendLog("initRequest --> resolution --> " + MultiSDKUtils.getWpixels(mContext) + "*" + MultiSDKUtils.getHpixels(mContext));
        params.put("resolution", MultiSDKUtils.getWpixels(mContext) + "*" + MultiSDKUtils.getHpixels(mContext));
        params.put("model", IMUrl.MODE);
        params.put("os", IMUrl.OS);
        params.put("sysver", IMUrl.OSVER);
        params.put("brand", MultiSDKUtils.getBrand());
        params.put("pkgid", mContext.getPackageName());
        params.put("ntype", MultiSDKUtils.getNetType(mContext));
        addCommonParams(params);

        sendRequest(IMUrl.URL_M_INIT, params, reqestCallback, isShowDialog);

    }

    /**
     * 推送请求
     *
     * @param reqestCallback
     * @param tc             触发条件(1-开屏解锁;2-时间轮询;3-网络切换)
     */
    public void pushRequest(final MRequestCallBack reqestCallback, int tc) {
        AjaxParams pushParams = new AjaxParams();
        pushParams.put("token", Util.getToken(mContext));
        //2.1新增
        pushParams.put("mac", MultiSDKUtils.getDevMac(mContext));
        pushParams.put("imei", MultiSDKUtils.getDevImei(mContext));
        pushParams.put("idfa", "android");
        pushParams.put("puid", MultiSDKUtils.getPlatUserid(mContext));
        pushParams.put("puname", MultiSDKUtils.getPlatUsername(mContext));
        pushParams.put("uid", Util.getUserid(mContext));
        pushParams.put("uname", Util.getUsername(mContext));
        pushParams.put("tc", "" + tc);

        addCommonParams(pushParams);

        String url_push = MultiSDKUtils.getPushUrl(mContext);
        sendRequest(url_push, pushParams, reqestCallback, false);

    }

    /**
     * TOKEN校验接口
     *
     * @param pdata          验证扩展参数
     * @param ptoken         验证必选联运平台token
     * @param reqestCallback
     * @param isShowDialog
     */
    public void verifyTokenRequst(String pdata, String ptoken, final MRequestCallBack reqestCallback, boolean isShowDialog) {
        //构建请求参数
        AjaxParams params = new AjaxParams();
        params.put("ptoken", ptoken);
        params.put("pdata", pdata);

        addCommonParams(params);

        String url = MultiSDKUtils.getVerifyTokenUrl(mContext);
        sendRequest(url, params, reqestCallback, isShowDialog);

    }

    /**
     * 联运平台下单
     *
     * @param doid           CP订单ID
     * @param dsid           CP游戏服ID( 标识 )
     * @param dsname         CP游戏服ID
     * @param dext           CP扩展回调参数
     * @param drid           CP角色ID
     * @param drname         CP角色名
     * @param drlevel        CP角色等级
     * @param dmoney         CP金额(定额)
     * @param dradio         P兑换比率(1元兑换率默认1:10)
     * @param reqestCallback 请求回调
     * @param isShowDialog   是否显示loading
     */
    public void payRequest(String doid, String dsid, String dsname,
                           String dext, String drid, String drname, int drlevel, float dmoney,
                           int dradio, final MRequestCallBack reqestCallback, boolean isShowDialog) {
        AjaxParams payReParams = new AjaxParams();
        payReParams.put("doid", doid);
        payReParams.put("dsid", "" + dsid);
        payReParams.put("dsname", dsname);
        payReParams.put("dext", dext);
        payReParams.put("drid", "" + drid);
        payReParams.put("drname", drname);
        payReParams.put("drlevel", "" + drlevel);
        payReParams.put("dmoney", "" + dmoney);
        payReParams.put("dradio", "" + dradio);

        String uid;
        String uname;

        if (MultiSDKUtils.getPID(mContext).equals("1")) {
            //优象平台
            uid = MultiSDKUtils.getYXUserid(mContext);
            uname = MultiSDKUtils.getYXUsername(mContext);
        } else {
            //联运平台
            uid = MultiSDKUtils.getPlatUserid(mContext);
            uname = MultiSDKUtils.getPlatUsername(mContext);
        }

        String ptid = MultiSDKUtils.getPID(mContext);
        String gid = MultiSDKUtils.getGID(mContext);
        String refid = MultiSDKUtils.getRefer(mContext);
        String version = getVersionName(mContext);
        String sdkver = BaseYXMCore.M_SDK_VERSION;
        String time = "" + System.currentTimeMillis() / 1000;
        String duid = MultiSDKUtils.getDuid(mContext);
        String key = ZipString.zipString2Json(MultiSDKUtils.getKey(mContext));

        String comstr = ptid + gid + refid + duid + version + sdkver + time + key + doid + dsid + uid + uname;

        String sign = getSignString(comstr);

        payReParams.put("uid", uid);
        payReParams.put("uname", uname);
        payReParams.put("ptid", ptid);
        payReParams.put("gid", gid);
        payReParams.put("refid", refid);
        payReParams.put("duid", duid);
        payReParams.put("version", version);
        payReParams.put("sdkver", sdkver);
        payReParams.put("time", time);
        payReParams.put("sign", sign);
        payReParams.put("mac", MultiSDKUtils.getMac(mContext));
        payReParams.put("imei", MultiSDKUtils.getIMEI(mContext));

        String url = MultiSDKUtils.getPayOrderUrl(mContext);
        sendRequest(url, payReParams, reqestCallback, isShowDialog);
    }

    private void sendRequest(final String reqeustUrl, AjaxParams params, final MRequestCallBack reqestCallback, boolean isShowDialog) {

        if (isShowDialog) {

            waitDialog = new ProgressDialog(mContext);
            waitDialog.setCancelable(false);

            if (waitDialog != null && !waitDialog.isShowing()) {
                waitDialog.setMessage("努力加载中...");
                waitDialog.show();
            }
        } else {
            if (waitDialog != null) {
                waitDialog.dismiss();
            }
        }

        BaseYXMCore.sendLog("yx-m-sendRequest --> requestUrl --> " + reqeustUrl);
        BaseYXMCore.sendLog("yx-m-sendRequest --> params --> " + params.toString());

        FinalHttp http = new FinalHttp();
        http.post(reqeustUrl, params, new AjaxCallBack<Object>() {

            @Override
            public void onSuccess(Object t) {
                if (waitDialog != null && waitDialog.isShowing()) {
                    waitDialog.dismiss();
                }

                String content = (String) t;
                BaseYXMCore.sendLog("yx-m-sendRequest--> response --> " + content);
                reqestCallback.onRequestSuccess(content);
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                if (waitDialog != null && waitDialog.isShowing()) {
                    waitDialog.dismiss();
                }
                reqestCallback.onRequestError("网络异常，请稍候再试");
                MultiSDKUtils.showTips(mContext, "网络请求失败，请重试");

            }

        });
    }

    /**
     * 添加网络请求通用的参数
     *
     * @param params
     * @return
     */
    public AjaxParams addCommonParams(AjaxParams params) {

        String ptid = MultiSDKUtils.getPID(mContext);
        String gid = MultiSDKUtils.getGID(mContext);
        String refid = MultiSDKUtils.getRefer(mContext);
        String version = getVersionName(mContext);
        String sdkver = BaseYXMCore.M_SDK_VERSION;
        String time = "" + System.currentTimeMillis() / 1000;
        String key = ZipString.zipString2Json(MultiSDKUtils.getKey(mContext));
        String comstr = ptid + gid + refid + version + sdkver + time + key;
        BaseYXMCore.sendLog("initRequeset --> comstr --> " + ptid + " --> " + gid + " --> " + refid + " --> " + version + " --> " + sdkver + " --> " + time + " --> " + key);
        String sign = getSignString(comstr);

        params.put("ptid", ptid);
        params.put("gid", gid);
        params.put("refid", refid);
        params.put("version", version);
        params.put("sdkver", sdkver);
        params.put("time", time);
        params.put("sign", sign);
        params.put("mac", MultiSDKUtils.getMac(mContext));
        params.put("imei", MultiSDKUtils.getIMEI(mContext));
        return params;
    }

    public void setContext(Context cxt) {
        mContext = cxt;
    }

    private String getSignString(String comStr) {
        String signStr = Util.Md5(comStr).toLowerCase();
        return signStr;
    }
}
