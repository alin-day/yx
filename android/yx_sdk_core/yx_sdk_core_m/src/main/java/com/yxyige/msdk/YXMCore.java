package com.yxyige.msdk;

import android.content.Context;

import com.yxyige.msdk.api.InitBean;
import com.yxyige.msdk.api.YXMResultListener;
import com.yxyige.msdk.api.YXMSdkInterface;
import com.yxyige.msdk.api.sdk.YXGame;

public class YXMCore extends BaseYXMCore {


    private YXMCore() {
    }

    /**
     * 获取YXMCore单例
     *
     * @return
     */
    public static YXMCore getInstance() {

        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new YXMCore();
                }
            }
        }
        return instance;
    }


    /**
     * 设置平台示例
     */
    @Override
    public YXMSdkInterface getPlatform(Context context, InitBean bean, YXMResultListener initListener) {
        return new YXGame(context, bean, initListener);
    }

}
