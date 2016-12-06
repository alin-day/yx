package com.yxyige.msdk.api;

public class IMUrl {

    public static final String MODE = android.os.Build.MODEL;
    public static final String OS = "android";
    public static final String OSVER = "android\n+" + android.os.Build.VERSION.RELEASE;

    /*
     * 初始化接口
     */
    public static String URL_M_INIT = "http://mapi.yxyige.com/active/init/";

    /*
     * 验证token接口
     */
    public static String URL_M_VAREFY_TOKEN = "http://verify.yxyige.com/client/token/";

    /*
     * 生成订单接口
     */
    public static String URL_M_ORDER = "http://mpay.yxyige.com/order/index/";

    /**
     * 是否需要显示充值弹窗显示
     */
    public static boolean isShowPayNotice = false;

    /**
     * 提交统计数据接口
     */
    public static String URL_M_SUBMIT = "";

    /**
     * 用户角色信息接口
     */
    public static String URL_M_ENTER = "";

    /**
     * 新推送接口
     */
    public static String URL_PUSH = "";

    /**
     * 订单查询接口
     */
    public static String URL_PAY_QUERY = "";
}
