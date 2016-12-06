package com.yxyige.sdk.core;

import android.os.Bundle;

public interface YXSDKListener {
    /**
     * 完成
     *
     * @param bundle
     */
    public void onSuccess(Bundle bundle);

    public void onFailture(int code, String msg);
}
