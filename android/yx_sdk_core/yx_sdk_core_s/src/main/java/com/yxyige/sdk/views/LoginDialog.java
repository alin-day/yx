package com.yxyige.sdk.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.yxyige.sdk.core.SDKManager;
import com.yxyige.sdk.core.YXSDKListener;
import com.yxyige.sdk.utils.DisplayUtil;
import com.yxyige.sdk.utils.Util;
import com.yxyige.sdk.widget.ViewStrategy;

public class LoginDialog extends Dialog {

    private Context context;
    private YXSDKListener listener;

    private FrameLayout content;//按钮下方内容展示区
    private View parentLayout;            //最低层控件，点击后显示登录界面
    private View contentLayout;            //登录界面

    private ViewStrategy mStrategy;        //View控件管理器
    private SDKManager sqManager;
    private Handler dismissHandler = new Handler() {

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            LoginDialog.this.dismiss();
            if (listener != null) {
                listener.onFailture(SDKManager.CANCEL_LOGIN, "取消登录");
            }

        }
    };
    private boolean showReg = false;

    public LoginDialog(Context context, YXSDKListener listener, SDKManager sqManager) {
        super(context);
        this.context = context;
        this.listener = listener;
        this.sqManager = sqManager;
    }

    public LoginDialog(Context context, YXSDKListener listener, SDKManager sqManager, boolean showRegisterView) {
        super(context);
        this.context = context;
        this.listener = listener;
        this.sqManager = sqManager;
        this.showReg = showRegisterView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getContext().setTheme(Util.getIdByName("Mdialog", "style", context.getPackageName(), context));
        setContentView(Util.getIdByName("yx_kefu_account", "layout", context.getPackageName(), context));

        initParent();
        init();

        //1.压缩布局高度290dip
        int height = DisplayUtil.dip2px(context, 290);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, height);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        contentLayout.setLayoutParams(layoutParams);

        //2.更换背景图
        int simple_bg_id = Util.getIdByName("yx_bg_kefu_dialog_simple", "drawable", context.getPackageName(), context);
        content.setBackgroundResource(simple_bg_id);
    }

    private void initParent() {

        parentLayout = findViewById(Util.getIdByName("parentLayout", "id", context.getPackageName(), context));

        contentLayout = findViewById(Util.getIdByName("contentLayout", "id", context.getPackageName(), context));


        final Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {

                super.handleMessage(msg);
                if (contentLayout.getVisibility() == View.GONE) {    //如果登录界面不可见，则显示
                    contentLayout.setVisibility(View.VISIBLE);
                    parentLayout.setOnTouchListener(null);        //去掉监听
                }
            }
        };

        parentLayout.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (contentLayout.getVisibility() == View.GONE) {    //如果登录界面不可见，则显示
                    handler.sendEmptyMessage(0);
                }
                return false;
            }
        });

        Message msg = new Message();
        msg.what = 0;
        handler.sendMessage(msg);

    }

    /**
     * 初始化管理器及界面
     */
    private void init() {

        //内容展示区
        content = (FrameLayout) findViewById(Util.getIdByName("content", "id", context.getPackageName(), context));
        //管理展示区
        mStrategy = new ViewStrategy(context, content);
        mStrategy.addTab(0);
        mStrategy.switchToTab(0, new AccountView((Activity) context, LoginDialog.this, listener, sqManager, showReg), false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            dismissHandler.sendEmptyMessageDelayed(0, 500);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
