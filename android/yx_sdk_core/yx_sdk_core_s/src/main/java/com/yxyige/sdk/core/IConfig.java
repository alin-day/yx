package com.yxyige.sdk.core;

public class IConfig {

    public static final int Theme_NoTitleBar_Fullscreen = 16973831;    //android.R.style.Theme_NoTitleBar_Fullscreen的值,因为flash类游戏无法识别R

    // 图片下载器
    public static final String BITMAP_CACHE = "/yx_bitmap/";
    public static final int POLL_MAX_LOADIMAGE_NUM = 8;//图片加载线程池最大并发量

    // 短信中心相关
    public static String smst = "106901332915"; //默认的短号中心
    public static String aid = "1";
    public static String brand = "优象游戏";

    // 跑马灯相关内容
    public static String horse_race_lamp_content = "";
    public static String horse_race_lamp_url = "";
    public static boolean showHorseLamp = false;

    // 单平台初始化后，重定义url
    public static String userAgree = "";        // 新用户协议地址
    public static String newPayUrl = "";        // 新支付地址
    public static String mreg = "";             // 新手机注册地址

    // 退弹窗口相关
    public static String tt_image_url = "";
    public static String tt_download_link = "";
    public static String tt_packagename = "";
    public static boolean isHasExit = false;
}
