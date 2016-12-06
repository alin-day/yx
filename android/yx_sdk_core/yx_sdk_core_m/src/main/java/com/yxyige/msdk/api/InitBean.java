package com.yxyige.msdk.api;

import com.yxyige.msdk.YXMCore;

import java.util.Properties;

public class InitBean {

    private static String appId;
    private static String appKey;
    private static String landScape;
    private static String isQuato;
    private static String gameId;
    private static String debug;
    private static String gameName;
    private static String isSplashShow;
    private static String usePlatformExit;
    private static String usesdk;
    private static String isPushDelay = "1";//1:表示开启6个小时的延迟判断，否则不开启

    public static InitBean inflactBean(Properties prop) {
        InitBean bean = null;
        if (prop != null) {
            bean = new InitBean();
            appId = prop.getProperty("appId");
            appKey = prop.getProperty("appKey");
            landScape = prop.getProperty("landScape");
            isQuato = prop.getProperty("isQuato");
            gameId = prop.getProperty("gameId");
            debug = prop.getProperty("debug");
            gameName = prop.getProperty("gameName");
            isSplashShow = prop.getProperty("isSplashShow");
            usePlatformExit = prop.getProperty("usePlatformExit");
            isPushDelay = prop.getProperty("isPushDelay");
            usesdk = prop.getProperty("usesdk");
        } else {
            YXMCore.sendLog("yx --> yx_m_config --> 文件读取错误");
        }
        return bean;
    }

    public static String getAppId() {
        return appId;
    }

    public static void setAppId(String appId) {
        InitBean.appId = appId;
    }

    public static String getAppKey() {
        return appKey;
    }

    public static void setAppKey(String appKey) {
        InitBean.appKey = appKey;
    }

    public static String getLandScape() {
        return landScape;
    }

    public static void setLandScape(String landScape) {
        InitBean.landScape = landScape;
    }

    public static String getIsQuato() {
        return isQuato;
    }

    public static void setIsQuato(String isQuato) {
        InitBean.isQuato = isQuato;
    }

    public static String getGameId() {
        return gameId;
    }

    public static void setGameId(String gameId) {
        InitBean.gameId = gameId;
    }

    public static String getGameName() {
        return gameName;
    }

    public static void setGameName(String gameName) {
        InitBean.gameName = gameName;
    }

    public static String getDebug() {
        return debug;
    }

    public static void setDebug(String debug) {
        InitBean.debug = debug;
    }

    public static String getIsSplashShow() {
        return isSplashShow;
    }

    public static void setIsSplashShow(String isSplashShow) {
        InitBean.isSplashShow = isSplashShow;
    }

    public static String getUsePlatformExit() {
        return usePlatformExit;
    }

    public static void setUsePlatformExit(String usePlatformExit) {
        InitBean.usePlatformExit = usePlatformExit;
    }

    public static String getUsesdk() {
        return usesdk;
    }

    public static void setUsesdk(String usesdk) {
        InitBean.usesdk = usesdk;
    }

    public static String getIsPushDelay() {
        return isPushDelay;
    }

    public static void setIsPushDelay(String isPushDelay) {
        InitBean.isPushDelay = isPushDelay;
    }
}
