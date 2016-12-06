package com.yxyige.msdk.api;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;

import com.yxyige.sdk.core.IError;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;

public class YXAppConfig {


    public static String CONFIG_FILE_NAME = "yx_config.xml";
    public static String CONFIG_NODE_GAMEID = "gameid";
    public static String CONFIG_NODE_PTID = "ptid";
    public static String CONFIG_NODE_REFID = "refid";

    private Context context;
    private YXMResultListener initCallback;

    private String gameid = "1000001";
    private String ptid = "1";
    private String refid = "";


    public YXAppConfig(Context context, YXMResultListener initCallback) {

        this.context = context;
        this.initCallback = initCallback;

        if (!getLocalParameter(context)) {
            getGameInfo();
        }

        saveConfigValue();


        Bundle config_data = new Bundle();
        config_data.putString("gid", gameid);
        config_data.putString("ptid", ptid);
        config_data.putString("refid", refid);
        initCallback.onSuccess(config_data);

    }

    private void saveConfigValue() {
        MultiSDKUtils.setGID(context, gameid);
        MultiSDKUtils.setPID(context, ptid);
        MultiSDKUtils.setRefer(context, refid);
    }

    public String getGameid() {
        return gameid;
    }

    public void setGameid(String gameid) {
        this.gameid = gameid;
    }

    public String getPtid() {
        return ptid;
    }

    public void setPtid(String ptid) {
        this.ptid = ptid;
    }

    public String getRefid() {
        return refid;
    }

    public void setRefid(String refid) {
        this.refid = refid;
    }

    private void getGameInfo() {
        // 从资产中读取XMl文件
        // 获取资产管理器
        AssetManager assetManager = context.getAssets();
        if (assetManager == null) {
            if (initCallback != null) {
                initCallback.onFailture(IError.ERROR_BAD_PARAMES, "Activity参数不能为空");
            }
            return;
        }
        try {
            // 获取文件输入流
            InputStream is = assetManager.open(CONFIG_FILE_NAME);
            if (is == null) {
                Log.e("yx", CONFIG_FILE_NAME + "文件不存在");
                if (initCallback != null) {
                    initCallback.onFailture(IError.ERROR_NO_CONFIG_FILE, CONFIG_FILE_NAME + "文件不存在");
                }
                return;
            }
            // 创建构建XMLPull分析器工厂
            XmlPullParserFactory xppf = XmlPullParserFactory.newInstance();
            // 创建XMLPull分析器
            XmlPullParser xpp = xppf.newPullParser();
            // 设置分析器的输入流
            xpp.setInput(is, "utf-8");
            // 得到下一个事件
            int eventType;
            // 得到当前的事件
            eventType = xpp.getEventType();
            // 循环事件
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (xpp.getName().equals(CONFIG_NODE_GAMEID)) {
                            gameid = xpp.nextText().trim();
                        }
                        if (xpp.getName().equals(CONFIG_NODE_PTID)) {
                            ptid = xpp.nextText().trim();
                        }
                        if (xpp.getName().equals(CONFIG_NODE_REFID)) {
                            refid = xpp.nextText().trim();
                        }
                        break;
                    default:
                        break;
                }
                // 获取下一个事件
                eventType = xpp.next();
            }

        } catch (Exception e) {
            Log.e("yx", e.getLocalizedMessage(), e);
            Log.e("yx", CONFIG_FILE_NAME + "文件配置错误");
            if (initCallback != null) {
                initCallback.onFailture(IError.ERROR_BAD_CONFIG, CONFIG_FILE_NAME + "文件配置错误");
            }
            return;
        }
    }

    public String toString() {
        return CONFIG_NODE_GAMEID + ":" + gameid
                + " | " + CONFIG_NODE_PTID + ":" + ptid
                + " | " + CONFIG_NODE_REFID + ":" + refid;
    }

    /**
     * 本地是否存储yx_config.xml文件的参数
     *
     * @param context
     * @return
     */
    public boolean getLocalParameter(Context context) {

        boolean is_localparameter = false;

        String gid_parameter = MultiSDKUtils.getGID(context);
        String pid_parameter = MultiSDKUtils.getPID(context);
        String refer_parameter = MultiSDKUtils.getRefer(context);

        if (!"sy00000_1".equals(refer_parameter)) {
            //如果本地有存储yx_config.xml文件里的参数的时候，就读取本地的参数
            gameid = gid_parameter;
            ptid = pid_parameter;
            refid = refer_parameter;
            is_localparameter = true;
        }

        return is_localparameter;

    }


}
