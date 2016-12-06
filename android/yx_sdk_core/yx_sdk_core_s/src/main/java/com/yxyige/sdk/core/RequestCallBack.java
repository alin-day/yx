package com.yxyige.sdk.core;

public interface RequestCallBack {

    /**
     * 网络请求回调接口
     *
     * @param content
     */
    public void onRequestSuccess(String content);

    public void onRequestError(String errorMsg);

}
