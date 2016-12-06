package com.yxyige.sdk.bean;

import android.content.Context;
import android.content.res.AssetManager;

import com.yxyige.sdk.core.IError;
import com.yxyige.sdk.core.YXSDKListener;
import com.yxyige.sdk.utils.LogUtil;
import com.yxyige.sdk.utils.Util;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;

public class YXConfig {


    public static String CONFIG_FILE_NAME = "yx_config.xml";
    public static String CONFIG_NODE_GAMEID = "gameid";
    public static String CONFIG_NODE_PTID = "ptid";
    public static String CONFIG_NODE_REFID = "refid";
    public static String CONFIG_NODE_GAMENAME = "gamename";

    private String gameid = "1000001";
    private String ptid = "1";
    private String refid = "";
    private String gamename = "";
    private Context context;
    private YXSDKListener initCallback;

    public YXConfig(Context context, YXSDKListener initCallback) {

        this.context = context;
        this.initCallback = initCallback;

        getGameInfo();

        saveConfigValue();

    }

    private void saveConfigValue() {
        Util.setGid(context, gameid);
        Util.setPtid(context, ptid);
        Util.setRefid(context, refid);
        Util.setGamename(context, gamename);
    }

    private void getGameInfo() {
        // 从资产中读取XMl文件
        // 获取资产管理器
        AssetManager assetManager = context.getAssets();
        if (assetManager == null) {
            LogUtil.e("Activity参数不能为空");
            if (initCallback != null) {
                initCallback.onFailture(IError.ERROR_BAD_PARAMES, "Activity参数不能为空");
            }
            return;
        }
        try {
            // 获取文件输入流
            InputStream is = assetManager.open(CONFIG_FILE_NAME);
            if (is == null) {
                LogUtil.e(CONFIG_FILE_NAME + "文件不存在");
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
                        if (xpp.getName().equals(CONFIG_NODE_GAMENAME)) {
                            gamename = xpp.nextText().trim();
                        }
                        break;
                    default:
                        break;
                }
                // 获取下一个事件
                eventType = xpp.next();
            }

        } catch (Exception e) {

            LogUtil.e(CONFIG_FILE_NAME + "文件配置错误");

            if (initCallback != null) {
                initCallback.onFailture(IError.ERROR_BAD_CONFIG, CONFIG_FILE_NAME + "文件配置错误");
            }

            return;
        }
    }

    public String toString() {
        return CONFIG_NODE_GAMEID + ":" + gameid
                + " " + CONFIG_NODE_PTID + ":" + ptid
                + " " + CONFIG_NODE_REFID + ":" + refid
                + " " + CONFIG_NODE_GAMENAME + ":" + gamename;
    }


}
