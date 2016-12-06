package com.yxyige.sdk.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.yxyige.sdk.bean.ALAccountInfo;
import com.yxyige.sdk.bean.UserInfo;
import com.yxyige.sdk.core.Callback;
import com.yxyige.sdk.core.IError;
import com.yxyige.sdk.core.RequestCallBack;
import com.yxyige.sdk.core.RequestManager;
import com.yxyige.sdk.core.YXSDKListener;
import com.yxyige.sdk.utils.AccountTools;
import com.yxyige.sdk.utils.Util;
import com.yxyige.sdk.utils.ViewController;
import com.yxyige.sdk.utils.ZipString;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Gemini on 16/12/4.
 */
public class AutoLoginDialog extends Dialog {
    private Context context;
    private TextView mSwitchAcc, mAutoAcc;
    private ALAccountInfo mAccountInfo;
    private Callback mSwitchAccountCallback;
    private YXSDKListener mLoginCallback;
    private RequestManager requestManager;
    private boolean canceled = false;

    public AutoLoginDialog(Context context, ALAccountInfo info, Callback switchAccountCallback, YXSDKListener loginCallback) {
        super(context);
        this.context = context;
        mAccountInfo = info;
        mSwitchAccountCallback = switchAccountCallback;
        mLoginCallback = loginCallback;
        requestManager = new RequestManager(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        getContext().setTheme(Util.getIdByName("Mdialog", "style", context.getPackageName(), context));
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(Util.getIdByName("yx_dialog_auto_login", "layout", context.getPackageName(), context), null);
        mSwitchAcc = (TextView) view.findViewById(Util.getIdByName("tv_switch_account", "id", context.getPackageName(), context));
        mAutoAcc = (TextView) view.findViewById(Util.getIdByName("tv_auto_login_account", "id", context.getPackageName(), context));
        mSwitchAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canceled = true;
                if (mSwitchAccountCallback != null) {
                    mSwitchAccountCallback.callback();
                }
                dismiss();
            }
        });
        mAutoAcc.setText(mAccountInfo == null?"" : mAccountInfo.account);
        setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        autoLogin();
    }

    private void autoLogin() {
        if (mAccountInfo == null) {
            //// TODO: 16/12/5 to login view
            dismiss();
        }

        //登录
        String logName = mAccountInfo.account;
        final String logPw = mAccountInfo.password;

        if ("".equals(logName) || "".equals(logPw)) {
            ViewController.showToast(context, "请输入正确的帐号和密码");
            return;
        }

        requestManager.loginRequest(logName, logPw, new RequestCallBack() {

            @Override
            public void onRequestSuccess(String content) {

                handleResponseData(content, "登录异常，请重试", new ResponseCallback() {

                    @Override
                    public void success(String content) {

                        if (!canceled) {
                            handleLoginSuccess(content, logPw);
                        }
                    }

                    @Override
                    public void error(int state, String errMsg, String exceptionTipsMsg) {

                        if (!canceled) {
                            handleLoginError(errMsg);

                            mLoginCallback.onFailture(IError.ERROR_GET_DATA_FAILD, errMsg);
                        }
                    }
                });
            }

            @Override
            public void onRequestError(String errorMsg) {
                ViewController.showToast(context, errorMsg);

            }
        }, true);
    }

    protected void handleResponseData(String content, String exceptionTips, ResponseCallback callback) {

        try {

            JSONObject jsonObj = new JSONObject(content);
            int state = jsonObj.getInt("state");
            if (state == 1) {

                callback.success(content);
            } else {

                String errorMsg = jsonObj.getString("msg");

                callback.error(state, errorMsg, exceptionTips);
            }
        } catch (Exception e) {
            e.printStackTrace();

            ViewController.showToast(context, exceptionTips);
            mLoginCallback.onFailture(IError.ERROR_GET_DATA_FAILD, exceptionTips + "0xe");
        }

    }

    /*
   * 处理登录/注册成功逻辑，回调
   */
    private void handleLoginSuccess(String content, String pwd) {

        JSONObject data;

        try {
            data = new JSONObject(content).getJSONObject("data");

            Util.setUserid(context, data.getString("uid"));
            Util.setUsername(context, data.getString("uname"));
            Util.setToken(context, data.getString("token"));
            Util.setPassword(context, ZipString.json2ZipString(pwd));

            Util.setDisname(context, data.getString("disname"));
            Util.setSex(context, data.getString("sex"));
            Util.setNick(context, data.getString("nick"));
            Util.setBrith(context, data.getString("birth"));
            Util.setPhone(context, data.getString("phone"));
            Util.setLoginNurl(context, data.getString("nurl"));

            UserInfo user = new UserInfo();
            user.setUname(data.getString("uname"));
            user.setUid(data.getString("uid"));
            user.setPassword(ZipString.json2ZipString(pwd));
            user.setToken(data.getString("token"));
            user.setDisname(data.getString("disname"));
            user.setSex(data.getString("sex"));
            user.setNick(data.getString("nick"));
            user.setBirth(data.getString("birth"));
            user.setPhone(data.getString("phone"));
            user.setNurl(data.getString("nurl"));

            AccountTools.setAccountToFile(context, user);

            if (mLoginCallback != null) {
                Bundle callbackBundle = new Bundle();
                callbackBundle.putString("userid", Util.getUserid(context));
                callbackBundle.putString("username", Util.getUsername(context));
                callbackBundle.putString("token", Util.getToken(context));

                mLoginCallback.onSuccess(callbackBundle);
                dismiss();

            } else {
                dismiss();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    /*
     * 处理登录/注册失败逻辑，回调
     */
    private void handleLoginError(String errorMsg) {
        ViewController.showToast(context, errorMsg);
    }

    interface ResponseCallback {

        void success(String content);

        void error(int state, String errMsg, String exceptionTipsMsg);
    }

}
