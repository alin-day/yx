package com.yxyige.msdk.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.LinearLayout;

import com.yxyige.msdk.api.MultiSDKUtils;
import com.yxyige.sdk.utils.Util;


public class YXSplashDialog extends Dialog {

    private Context mcontext;
    private int splashTime = 3;//默认3秒
    private SplahListener splashCallback;
    Handler splashHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //隐藏闪屏
            dismiss();
            //如果闪屏结束有需要做的事情
            if (splashCallback != null) {
                splashCallback.afterSplash();
            }
        }

    };

    public YXSplashDialog(Context context, int time) {
        super(context, Util.getIdByName("Mdialog", "style", context.getPackageName(), context));
        this.mcontext = context;
        this.splashTime = time;
    }

    public YXSplashDialog(Context context) {
        super(context, Util.getIdByName("Mdialog", "style", context.getPackageName(), context));
        this.mcontext = context;
    }

    public void setSplashTime(int time) {
        this.splashTime = time;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //创建一个父View
        LinearLayout fatherView = new LinearLayout(mcontext);
        fatherView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));

        setContentView(fatherView);

        //根据屏幕显示闪屏
        if (MultiSDKUtils.isScreenOriatationPortrait(mcontext)) {
            fatherView.setBackgroundResource(Util.getIdByName("yx_splash_port", "drawable", mcontext.getPackageName(), mcontext));
        } else {
            fatherView.setBackgroundResource(Util.getIdByName("yx_splash_land", "drawable", mcontext.getPackageName(), mcontext));
        }
        //定时
        splashHandler.sendEmptyMessageDelayed(0, splashTime * 1000);

    }

    public void setSplashListener(SplahListener listener) {
        this.splashCallback = listener;
    }

    // --------------------------回调函数------------------------------------
    public interface SplahListener {
        void afterSplash();
    }

}
