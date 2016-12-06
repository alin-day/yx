package com.yxyige.msdk.api;

public interface MRequestCallBack {
    /**
     * 网络请求回调接口
     *
     * @param content
     */
    void onRequestSuccess(String content);

    void onRequestError(String errorMsg);

}
