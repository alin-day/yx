package com.yxyige.sdk.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.yxyige.sdk.core.IError;
import com.yxyige.sdk.core.YXSDKListener;
import com.yxyige.sdk.utils.ViewController;

import org.json.JSONObject;

/**
 * This is an abstract view, It's extends by MoreView.
 */
public abstract class AbstractView {
    public static final int match_parent = ViewGroup.LayoutParams.MATCH_PARENT,
            WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;
    public static final int STATE_RESPONSE_SUCCESS = 1;
    public static final int STATE_RESPONSE_FAUILED = 0;
    protected static final Handler mHandler = new Handler();
    protected View mParent;
    protected long clickTime = 0;
    protected YXSDKListener listener;
    private Activity mActivity;
    private LayoutInflater mInflater;

    public AbstractView(){}

    public AbstractView(Activity activity) {
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
        final View parent = getParent();
        if (null == parent) {
            throw new NullPointerException("getParent() must return a non-null View.");
        }
        parent.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mParent = parent;
        onFinishInflate();
    }

    protected View getParent() {
        return new RelativeLayout(getActivity());
    }

    public final View getContentView() {
        return mParent;
    }

    // 不在界面显示的时候调用
    public void onViewOut() {

    }

    // 在界面显示的时候调用
    public void onViewIn() {
    }

    // 在界面获取焦点的时候调用
    protected void onResume() {
    }

    // 在界面获取焦点的时候调用
    protected void onDestroy() {
    }

    // 在界面初始化完成后调用
    protected void onFinishInflate() {
    }

    // 由其他activity返回的时候调用
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    // 在收到广播后调用
    public void onReceive(Context context, Intent intent) {
    }

    public boolean back() {
        return false;
    }

    public View findViewById(int id) {
        return mParent.findViewById(id);
    }

    public View inflate(int resource) {
        return mInflater.inflate(resource, null);
    }

    public Activity getActivity() {
        return mActivity;
    }

    protected synchronized boolean isQuickClick() {
        long current = System.currentTimeMillis();
        if (current - clickTime < 400) {
            clickTime = current;
            return true;
        }
        clickTime = current;
        return false;
    }

    protected void handleResponseData(String content, String exceptionTips, ResponseCallback callback) {

        try {

            JSONObject jsonObj = new JSONObject(content);
            int state = jsonObj.getInt("state");
            if (state == STATE_RESPONSE_SUCCESS) {

                callback.success(content);
            } else {

                String errorMsg = jsonObj.getString("msg");

                callback.error(state, errorMsg, exceptionTips);
            }
        } catch (Exception e) {
            e.printStackTrace();

            ViewController.showToast(getActivity(), exceptionTips);
            listener.onFailture(IError.ERROR_GET_DATA_FAILD, exceptionTips + "0xe");
        }

    }

    protected interface ResponseCallback {

        public void success(String content);

        public void error(int state, String errMsg, String exceptionTipsMsg);
    }

}
