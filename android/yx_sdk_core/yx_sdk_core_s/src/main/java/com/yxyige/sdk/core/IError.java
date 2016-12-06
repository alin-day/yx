package com.yxyige.sdk.core;

public interface IError {

    /**
     * 未登录
     */
    public static final int NO_LOGINED = 401;

    /**
     * 参数错误
     */
    public static final int PARAMS_ERROR = 403;

    /**
     * 网络错误
     */
    public static final int NET_ERROR = 404;

    /**
     * 取消登录
     */
    public static final int CANCEL_LOGIN = 405;

    /**
     * 取消支付
     */
    public static final int CANCEL_PAY = 406;

    /**
     * 服务器返回异常
     */
    public static final int ERROR_GET_DATA_FAILD = 407;

    /**
     * 配置文件缺少参数
     */
    public static final int ERROR_BAD_PARAMES = 603;

    /**
     * 缺少yxgame_config.xml配置文件
     */
    public static final int ERROR_NO_CONFIG_FILE = 601;

    /**
     * xygame_config.xml文件配置错误
     */
    public static final int ERROR_BAD_CONFIG = 602;

    /**
     * 客户端本地错误
     */
    public static final int ERROR_CLIENT = 604;

}
