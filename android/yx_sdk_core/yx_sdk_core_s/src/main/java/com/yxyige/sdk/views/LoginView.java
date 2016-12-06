package com.yxyige.sdk.views;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yxyige.sdk.adapter.AccountAdapter;
import com.yxyige.sdk.bean.UserInfo;
import com.yxyige.sdk.bean.ViewLayoutBean;
import com.yxyige.sdk.core.IConfig;
import com.yxyige.sdk.core.IError;
import com.yxyige.sdk.core.RequestCallBack;
import com.yxyige.sdk.core.RequestManager;
import com.yxyige.sdk.core.YXSDKListener;
import com.yxyige.sdk.utils.AccountTools;
import com.yxyige.sdk.utils.AppUtils;
import com.yxyige.sdk.utils.Util;
import com.yxyige.sdk.utils.ViewController;
import com.yxyige.sdk.utils.ZipString;
import com.yxyige.sdk.widget.AbstractView;
import com.yxyige.sdk.widget.MarqueeTextView;
import com.yxyige.sdk.widget.ProgressDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class LoginView extends AbstractView {

    private static final long TIMER_THRESHOLD = 60 * 1000;
    public static EditText loginName;
    public static EditText loginPW;
    //声明
    int retryCount = 0;
    boolean isRetryQuery = true;
    private LoginDialog dialog;
    private ProgressDialog msgRegProgressDialog;
    //注册登录标题栏
    private TextView regLoginTitleTips;
    private Button backBtn;
    //登录相关
    private View accountFlagTextView;
    private View loginLayout;
    private Button toReg;
    private Button toLogin;
    private ImageView fg_select_account;
    private ImageView fg_pwd_login_eye;
    private YXSDKListener selectCallback;
    private PopupWindow mPopupWindow;
    private boolean isShowPwd = false;
    private TextView forgetPwdText;
    //注册
    private View regLayout;
    private EditText regName;
    private EditText regPW;
    private Button regBtn;
    private TextView loginHasAccountTextView;
    private TextView regByPhoneTextView;
    private TextView regClauseText;//注册提示
    private CheckBox regClause;//注册提示

    //手机密码找回
    private View resetPwdLayout;
    private Button resetPwdGetVertifyCodeBtn;
    private EditText resetPwdPhoneNumEdit, resetPwdPhoneVertifyEdit;
    private Button resetPwdSubmitBtn;

    //手机号注册模块
    private View regByPhoneNumLayout;
    private Button getVerifyCodeBtn;
    private EditText phoneNumEdit, phoneVerifyEdit;
    private Button regByPhoneNumBtn;
    private TextView regByEmailTextView2;
    private TextView regByPhoneHasAccountTextView2;
    private TextView regPhoneNumClauseText;
    private CheckBox regPhoneNumClause;

    //底部跑马灯
    private MarqueeTextView downInfo;
    private RequestManager requestManager;
    private AccountAdapter accountAdapter;
    private List<UserInfo> accountList;
    private HashMap<ViewLayout, ViewLayoutBean> viewLayoutMap;
    private TimeCount timer;

    private boolean showReg = false;

    public LoginView(Activity activity, LoginDialog dialog, YXSDKListener listener, boolean showRegisterView) {
        super(activity);
        this.dialog = dialog;
        this.listener = listener;
        this.showReg = showRegisterView;
        if (showReg) {
            showViewLayout(ViewLayout.REG_BY_EMAIL);
        } else {
            //默认显示登录界面
            showViewLayout(ViewLayout.LOGIN);
        }
    }

    @Override
    protected View getParent() {
        return inflate(Util.getIdByName("yx_kefu_login_view", "layout", getActivity().getPackageName(), getActivity()));//Util.getIdByName("view_account", "layout", getActivity().getPackageName(), getActivity()));
    }

    @Override
    protected void onFinishInflate() {

        requestManager = new RequestManager(getActivity());

        initView();
        initData();

    }

    private void findViews() {
        //标题栏
        regLoginTitleTips = (TextView) findViewById(Util.getIdByName("tips_login_reg_view", "id", getActivity().getPackageName(), getActivity()));
        backBtn = (Button) findViewById(Util.getIdByName("tips_login_back_btn", "id", getActivity().getPackageName(), getActivity()));
        //登录
        loginLayout = findViewById(Util.getIdByName("loginLayout", "id", getActivity().getPackageName(), getActivity()));
        accountFlagTextView = findViewById(Util.getIdByName("text_flag_account", "id", getActivity().getPackageName(), getActivity()));
        loginName = (EditText) findViewById(Util.getIdByName("loginName", "id", getActivity().getPackageName(), getActivity()));
        loginPW = (EditText) findViewById(Util.getIdByName("loginPW", "id", getActivity().getPackageName(), getActivity()));
        toReg = (Button) findViewById(Util.getIdByName("toReg", "id", getActivity().getPackageName(), getActivity()));
        toLogin = (Button) findViewById(Util.getIdByName("toLogin", "id", getActivity().getPackageName(), getActivity()));
        fg_select_account = (ImageView) findViewById(Util.getIdByName("fg_select_account", "id", getActivity().getPackageName(), getActivity()));
        fg_pwd_login_eye = (ImageView) findViewById(Util.getIdByName("fg_pwd_login_eye", "id", getActivity().getPackageName(), getActivity()));
        downInfo = (MarqueeTextView) Util.getViewByIdName(this, "downInfo");
        //登录界面上的忘记密码按钮
        forgetPwdText = (TextView) findViewById(Util.getIdByName("forget_pwd", "id", getActivity().getPackageName(), getActivity()));

        //注册
        regLayout = findViewById(Util.getIdByName("regLayout", "id", getActivity().getPackageName(), getActivity()));
        regName = (EditText) findViewById(Util.getIdByName("regName", "id", getActivity().getPackageName(), getActivity()));
        regPW = (EditText) findViewById(Util.getIdByName("regPW", "id", getActivity().getPackageName(), getActivity()));
        regBtn = (Button) findViewById(Util.getIdByName("regBtn", "id", getActivity().getPackageName(), getActivity()));
        loginHasAccountTextView = (TextView) findViewById(Util.getIdByName("yx_reg_text_has_account", "id", getActivity().getPackageName(), getActivity()));
        regByPhoneTextView = (TextView) findViewById(Util.getIdByName("yx_reg_text_by_phone", "id", getActivity().getPackageName(), getActivity()));
        regClauseText = (TextView) findViewById(Util.getIdByName("yx_reg_clause_text", "id", getActivity().getPackageName(), getActivity()));
        regClause = (CheckBox) findViewById(Util.getIdByName("yx_reg_clause", "id", getActivity().getPackageName(), getActivity()));

        //手机密码找回
        resetPwdLayout = findViewById(Util.getIdByName("resetPhonePwd", "id", getActivity().getPackageName(), getActivity()));
        resetPwdPhoneNumEdit = (EditText) findViewById(Util.getIdByName("yx_reset_phone_pwd_edit", "id", getActivity().getPackageName(), getActivity()));
        resetPwdGetVertifyCodeBtn = (Button) findViewById(Util.getIdByName("yx_reset_phone_pwd_getVerifyCode_btn", "id", getActivity().getPackageName(), getActivity()));
        resetPwdPhoneVertifyEdit = (EditText) findViewById(Util.getIdByName("yx_reset_phone_pwd_edit_verifyCode", "id", getActivity().getPackageName(), getActivity()));
        resetPwdSubmitBtn = (Button) findViewById(Util.getIdByName("yx_reset_phone_pwd_submit_btn", "id", getActivity().getPackageName(), getActivity()));

        //手机号码注册
        regByPhoneNumLayout = findViewById(Util.getIdByName("regByPhoneNumLayout", "id", getActivity().getPackageName(), getActivity()));
        getVerifyCodeBtn = (Button) findViewById(Util.getIdByName("yx_btn_getVerifyCode", "id", getActivity().getPackageName(), getActivity()));
        phoneNumEdit = (EditText) findViewById(Util.getIdByName("yx_account_edit_phoneNum", "id", getActivity().getPackageName(), getActivity()));
        phoneVerifyEdit = (EditText) findViewById(Util.getIdByName("yx_account_edit_verifyCode", "id", getActivity().getPackageName(), getActivity()));
        regByPhoneNumBtn = (Button) findViewById(Util.getIdByName("yx_account_btn_regByPhoneNum", "id", getActivity().getPackageName(), getActivity()));
        regByEmailTextView2 = (TextView) findViewById(Util.getIdByName("yx_reg_text_by_email2", "id", getActivity().getPackageName(), getActivity()));
        regByPhoneHasAccountTextView2 = (TextView) findViewById(Util.getIdByName("yx_reg_by_phone_text_has_account2", "id", getActivity().getPackageName(), getActivity()));
        regPhoneNumClauseText = (TextView) findViewById(Util.getIdByName("yx_reg_by_phonenum_clause_text", "id", getActivity().getPackageName(), getActivity()));
        regPhoneNumClause = (CheckBox) findViewById(Util.getIdByName("yx_reg_by_phonenum_clause", "id", getActivity().getPackageName(), getActivity()));

        viewLayoutMap = new HashMap<LoginView.ViewLayout, ViewLayoutBean>();
        viewLayoutMap.put(ViewLayout.LOGIN, new ViewLayoutBean("优象手游", loginLayout));
        viewLayoutMap.put(ViewLayout.REG_BY_EMAIL, new ViewLayoutBean("普通注册", regLayout));
        viewLayoutMap.put(ViewLayout.REG_BY_PHONE_NUM, new ViewLayoutBean("手机注册", regByPhoneNumLayout));
        viewLayoutMap.put(ViewLayout.FORGET_PWD, new ViewLayoutBean("密码找回", resetPwdLayout));

    }

    private void handleViewsSpecial() {

        //1.一键注册和立即登录改变背景色
        int yx_kefu_reg_simple_id = Util.getIdByName("yx_kefu_reg_simple", "drawable", getActivity().getPackageName(), getActivity());
        int yx_kefu_submit_simple_id = Util.getIdByName("yx_kefu_submit_simple", "drawable", getActivity().getPackageName(), getActivity());
        int yx_bg_reg_by_phone_simple_id = Util.getIdByName("yx_bg_reg_by_phone_simple", "drawable", getActivity().getPackageName(), getActivity());
//        toReg.setBackgroundResource(yx_kefu_reg_simple_id);
//        toLogin.setBackgroundResource(yx_kefu_submit_simple_id);
        //2.标题栏换颜色
        int s_special_text_id = Util.getIdByName("s_special_text", "color", getActivity().getPackageName(), getActivity());
//        regLoginTitleTips.setTextColor(s_special_text_id);
        //3.注册界面按钮也要更换，三个界面
//        regBtn.setBackgroundResource(yx_kefu_reg_simple_id);
//        regByPhoneNumBtn.setBackgroundResource(yx_kefu_reg_simple_id);
        //4.替换用户登录界面的文字
//        viewLayoutMap.remove(ViewLayout.LOGIN);
//        viewLayoutMap.put(ViewLayout.LOGIN, new ViewLayoutBean("用户登录", loginLayout));
    }

    private void initView() {

        //加载界面
        findViews();
        handleViewsSpecial();


        loginHasAccountTextView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        regByPhoneTextView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        regByPhoneHasAccountTextView2.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        regByEmailTextView2.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        //处理《服务条款》样式-加下划线
        SpannableString spannString = new SpannableString(regClauseText.getText());
        //加下划线
        spannString.setSpan(new UnderlineSpan(), 0, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置为黑色
        ForegroundColorSpan span = new ForegroundColorSpan(Color.BLACK);
        spannString.setSpan(span, 0, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        regPhoneNumClauseText.setText(spannString);
        regClauseText.setText(spannString);

        //处理《忘记密码》样式-加下划线
        SpannableString spannString2 = new SpannableString(forgetPwdText.getText());
        //加下划线
        spannString2.setSpan(new UnderlineSpan(), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        forgetPwdText.setText(spannString2);

        //控制跑马灯显示
        if (!IConfig.showHorseLamp) {
            downInfo.setVisibility(View.GONE);
        } else {
            downInfo.setVisibility(View.VISIBLE);
        }

        downInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!TextUtils.isEmpty(IConfig.horse_race_lamp_url)) {
                        //不为空时候才跳转
                        handleDownInfo(IConfig.horse_race_lamp_url);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        //选择帐号
        fg_select_account.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (isQuickClick()) {
                    return;
                }

                if (mPopupWindow != null) {
                    if (!mPopupWindow.isShowing()) {
                        fg_select_account.invalidate();
                        mPopupWindow.showAsDropDown(accountFlagTextView);
                    } else {
                        mPopupWindow.dismiss();

                    }
                } else {

                    initPopWindows();

                    if (!mPopupWindow.isShowing()) {
                        fg_select_account.invalidate();
                        mPopupWindow.showAsDropDown(accountFlagTextView);
                    } else {
                        mPopupWindow.dismiss();
                    }
                }

            }
        });

        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到登录界面
                showViewLayout(ViewLayout.LOGIN);
            }
        });


        /**
         *   ----------------- 登录界面 ------------------
         */

        //处理登录界面密码眼事件
        fg_pwd_login_eye.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                String pwd = loginPW.getText().toString();
                if (isShowPwd) {

                    loginPW.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    fg_pwd_login_eye.setImageResource(Util.getIdByName("yx_user_pwd_eye_open", "drawable", getActivity()));

                } else {

                    loginPW.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    fg_pwd_login_eye.setImageResource(Util.getIdByName("yx_user_pwd_eye_close", "drawable", getActivity()));
                }

                isShowPwd = !isShowPwd;
                loginPW.setText(pwd);
            }
        });


        //调转到找回密码界面
        forgetPwdText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                showViewLayout(ViewLayout.FORGET_PWD);

            }
        });


        //到注册界面
        toReg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                showViewLayout(ViewLayout.REG_BY_PHONE_NUM);
            }
        });


        //登录
        toLogin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                //登录
                String logName = loginName.getText().toString();
                final String logPw = loginPW.getText().toString();

                if ("".equals(logName) || "".equals(logPw)) {
                    ViewController.showToast(getActivity(), "请输入正确的帐号和密码");
                    return;
                }

                toLogin.setEnabled(false);    //防止多次点击

                requestManager.loginRequest(logName, logPw, new RequestCallBack() {

                    @Override
                    public void onRequestSuccess(String content) {

                        toLogin.setEnabled(true);

                        handleResponseData(content, "登录异常，请重试", new ResponseCallback() {

                            @Override
                            public void success(String content) {

                                handleLoginSuccess(content, logPw);
                            }

                            @Override
                            public void error(int state, String errMsg, String exceptionTipsMsg) {

                                handleLoginError(errMsg);

                                listener.onFailture(IError.ERROR_GET_DATA_FAILD, errMsg);
                            }
                        });
                    }

                    @Override
                    public void onRequestError(String errorMsg) {
                        toLogin.setEnabled(true);
                        ViewController.showToast(getActivity(), errorMsg);

                    }
                }, true);

            }
        });


        /**
         *   ----------------- 邮箱/用户名注册界面 ------------------
         */


        //邮箱注册view-已有帐号文字事件
        loginHasAccountTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                showViewLayout(ViewLayout.LOGIN);
            }
        });

        //邮箱注册view-手机注册文字事件
        regByPhoneTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                showViewLayout(ViewLayout.REG_BY_PHONE_NUM);
            }
        });


        //邮箱注册事件
        regBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //注册确认
                String reg_Name = regName.getText().toString();
                final String regPw = regPW.getText().toString();
                if ("".equals(reg_Name) || "".equals(regPw)) {
                    ViewController.showToast(getActivity(), "帐号或密码不能为空");
                    return;
                }

                if (reg_Name.length() < 4 || reg_Name.length() > 20) {
                    ViewController.showToast(getActivity(), "帐号为4至20个字母或数字");
                    return;
                }

                if (regPw.length() < 6) {
                    ViewController.showToast(getActivity(), "请输入至少6位数的密码");
                    return;
                }

                //20151210新增用户服务协议
                if (!regClause.isChecked()) {
                    ViewController.showToast(getActivity(), "请阅读注册协议并勾选同意《用户注册服务协议》");
                    return;
                }
                //注册
                requestManager.registerRequest(reg_Name, regPw, new RequestCallBack() {

                    @Override
                    public void onRequestSuccess(String content) {

                        handleResponseData(content, "注册异常，请重试", new ResponseCallback() {

                            @Override
                            public void success(String content) {

                                handleLoginSuccess(content, regPw);
                            }

                            @Override
                            public void error(int state, String errMsg, String exceptionTipsMsg) {

                                handleLoginError(errMsg);
                            }
                        });
                    }

                    @Override
                    public void onRequestError(String errorMsg) {
                        ViewController.showToast(getActivity(), errorMsg);
                    }
                }, true);

            }
        });


        /**
         * ----------------- 手机短信一键注册界面 ------------------
         */

        regByPhoneHasAccountTextView2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                showViewLayout(ViewLayout.LOGIN);
            }
        });

        regByEmailTextView2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                showViewLayout(ViewLayout.REG_BY_EMAIL);

            }
        });

        regPhoneNumClauseText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                AppUtils.toSdkUrl(getActivity(), Util.getUserAgreeUrl(getActivity()));
            }
        });
        regClauseText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                AppUtils.toSdkUrl(getActivity(), Util.getUserAgreeUrl(getActivity()));
            }
        });

        /**
         *  ----------------- 手机帐号密码找回 ------------------
         */
        checkTimer(); //初始化倒计时
        resetPwdGetVertifyCodeBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                    /*
                 * 60s内只能点击一次，
                 */

                String phoneNum = resetPwdPhoneNumEdit.getText().toString();

                if (phoneNum == null || "".equals(phoneNum)) {
                    ViewController.showToast(getActivity(), "请输入手机号");
                } else if (!Util.isMobileNO(phoneNum)) {
                    ViewController.showToast(getActivity(), "请填写正确的手机号");
                } else {
                    Util.hideSystemKeyBoard(getActivity(), view);

                    requestManager.getResetVerifyCodeRequest(phoneNum, new RequestCallBack() {

                        @Override
                        public void onRequestSuccess(String content) {
                            if (isQuickClick()) {
                                return;
                            }
                            handleResponseData(content, "短信发送失败", new ResponseCallback() {

                                @Override
                                public void success(String content) {
                                    ViewController.showToast(getActivity(), "请求已发送，请注意查收短信");
                                    Util.setVerifyCodeLastTime(getActivity(), System.currentTimeMillis());
                                    checkTimer();//更新倒计时
                                }

                                @Override
                                public void error(int state, String errMsg, String exceptionTipsMsg) {
                                    ViewController.showToast(getActivity(), errMsg);
                                    if (timer != null) {
                                        timer.cancel();
                                    }
                                    getVerifyCodeBtn.setText("获取验证码");
                                    getVerifyCodeBtn.setEnabled(true);
                                    getVerifyCodeBtn.setClickable(true);
                                }
                            });
                        }

                        @Override
                        public void onRequestError(String errorMsg) {
                            ViewController.showToast(getActivity(), errorMsg);
                            if (timer != null) {
                                timer.cancel();
                            }
                            getVerifyCodeBtn.setEnabled(true);
                            getVerifyCodeBtn.setClickable(true);
                        }
                    }, true);
                }
            }
        });

        resetPwdSubmitBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isQuickClick()) {
                    return;
                }
                final String phoneNum = resetPwdPhoneNumEdit.getText().toString();
                String verifyCode = resetPwdPhoneVertifyEdit.getText().toString();
                if (phoneNum == null || "".equals(phoneNum) || !Util.isMobileNO(phoneNum)) {
                    ViewController.showToast(getActivity(), "请输入正确手机号");
                } else if (verifyCode == null || "".equals(verifyCode)) {
                    ViewController.showToast(getActivity(), "请输入验证码");
                } else {
                    requestManager.phoneNumResetRequest(phoneNum, verifyCode, new RequestCallBack() {

                        @Override
                        public void onRequestSuccess(String content) {
                            handleResponseData(content, "注册异常，请重试", new ResponseCallback() {
                                @Override
                                public void success(String content) {
                                    ViewController.showToast(getActivity(), "新密码已随短信下发，请注意查收！");
                                }

                                @Override
                                public void error(int state, String errMsg, String exceptionTipsMsg) {
                                    handleLoginError(errMsg);
                                    phoneVerifyEdit.setText("");
                                }
                            });
                        }

                        @Override
                        public void onRequestError(String errorMsg) {
                            ViewController.showToast(getActivity(), errorMsg);
                        }
                    }, true);
                }
            }
        });

        /**
         *  ----------------- 手机号注册界面 ------------------
         */
        checkTimer(); //初始化倒计时
        getVerifyCodeBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                /*
                 * 60s内只能点击一次，
                 */

                String phoneNum = phoneNumEdit.getText().toString();

                if (phoneNum == null || "".equals(phoneNum)) {
                    ViewController.showToast(getActivity(), "请输入手机号");
                } else if (!Util.isMobileNO(phoneNum)) {
                    ViewController.showToast(getActivity(), "请填写正确的手机号");
                } else {
                    Util.hideSystemKeyBoard(getActivity(), v);

                    requestManager.getVerifyCodeRequest(phoneNum, new RequestCallBack() {

                        @Override
                        public void onRequestSuccess(String content) {
                            if (isQuickClick()) {
                                return;
                            }
                            handleResponseData(content, "短信发送失败", new ResponseCallback() {

                                @Override
                                public void success(String content) {
                                    ViewController.showToast(getActivity(), "请求已发送，请注意查收短信");
                                    Util.setVerifyCodeLastTime(getActivity(), System.currentTimeMillis());
                                    checkTimer();//更新倒计时
                                }

                                @Override
                                public void error(int state, String errMsg, String exceptionTipsMsg) {
                                    ViewController.showToast(getActivity(), errMsg);
                                    if (timer != null) {
                                        timer.cancel();
                                    }
                                    getVerifyCodeBtn.setText("获取验证码");
                                    getVerifyCodeBtn.setEnabled(true);
                                    getVerifyCodeBtn.setClickable(true);
                                }
                            });
                        }

                        @Override
                        public void onRequestError(String errorMsg) {
                            ViewController.showToast(getActivity(), errorMsg);
                            if (timer != null) {
                                timer.cancel();
                            }
                            getVerifyCodeBtn.setEnabled(true);
                            getVerifyCodeBtn.setClickable(true);
                        }
                    }, true);
                }
            }
        });


        //手机号码注册
        regByPhoneNumBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isQuickClick()) {
                    return;
                }
                final String phoneNum = phoneNumEdit.getText().toString();
                String verifyCode = phoneVerifyEdit.getText().toString();
                if (phoneNum == null || "".equals(phoneNum) || !Util.isMobileNO(phoneNum)) {
                    ViewController.showToast(getActivity(), "请输入正确手机号");
                } else if (verifyCode == null || "".equals(verifyCode)) {
                    ViewController.showToast(getActivity(), "请输入验证码");
                } else if (!regPhoneNumClause.isChecked()) {
                    ViewController.showToast(getActivity(), "请阅读注册协议并勾选同意《用户注册服务协议》");
                } else {
                    requestManager.phoneNumRegRequest(phoneNum, verifyCode, new RequestCallBack() {

                        @Override
                        public void onRequestSuccess(String content) {
                            handleResponseData(content, "注册异常，请重试", new ResponseCallback() {
                                @Override
                                public void success(String content) {
                                    try {
                                        String pwd = new JSONObject(content).getJSONObject("data").getString("upwd");
                                        handleLoginSuccess(content, pwd);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void error(int state, String errMsg, String exceptionTipsMsg) {
                                    handleLoginError(errMsg);
                                    phoneVerifyEdit.setText("");
                                }
                            });
                        }

                        @Override
                        public void onRequestError(String errorMsg) {
                            ViewController.showToast(getActivity(), errorMsg);
                        }
                    }, true);
                }
            }
        });
    }

    /*
     * 处理登录/注册成功逻辑，回调
     */
    private void handleLoginSuccess(String content, String pwd) {

        JSONObject data;

        try {
            data = new JSONObject(content).getJSONObject("data");

            Util.setUserid(getActivity(), data.getString("uid"));
            Util.setUsername(getActivity(), data.getString("uname"));
            Util.setToken(getActivity(), data.getString("token"));
            Util.setPassword(getActivity(), ZipString.json2ZipString(pwd));

            Util.setDisname(getActivity(), data.getString("disname"));
            Util.setSex(getActivity(), data.getString("sex"));
            Util.setNick(getActivity(), data.getString("nick"));
            Util.setBrith(getActivity(), data.getString("birth"));
            Util.setPhone(getActivity(), data.getString("phone"));
            Util.setLoginNurl(getActivity(), data.getString("nurl"));

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

            AccountTools.setAccountToFile(getActivity(), user);

            if (listener != null) {
                Bundle callbackBundle = new Bundle();
                callbackBundle.putString("userid", Util.getUserid(getActivity()));
                callbackBundle.putString("username", Util.getUsername(getActivity()));
                callbackBundle.putString("token", Util.getToken(getActivity()));

                listener.onSuccess(callbackBundle);
                dialog.dismiss();

            } else {
                dialog.dismiss();
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
        ViewController.showToast(getActivity(), errorMsg);
    }

    private void checkTimer() {

        long storeLastRequestTime = Util.getVerifyCodeLastTime(getActivity());
        long currentTime = System.currentTimeMillis();
        long spaceTime = currentTime - storeLastRequestTime;

        if (spaceTime < TIMER_THRESHOLD) { //小于60s，则不可继续点击

            getVerifyCodeBtn.setClickable(false);
            getVerifyCodeBtn.setEnabled(false);

            timer = new TimeCount((TIMER_THRESHOLD - spaceTime), 1000);//构造CountDownTimer对象
            timer.start();

        } else {

            if (timer != null) {
                timer.cancel();
            }
            getVerifyCodeBtn.setEnabled(true);
            getVerifyCodeBtn.setClickable(true);
        }
    }

    private void initData() {

        downInfo.setText(IConfig.horse_race_lamp_content);

        initAccountData();

    }

    private void initAccountData() {

        final String name = Util.getUsername(getActivity());
        String password = Util.getPassword(getActivity());
        final String pw = ZipString.zipString2Json(password);
        if (name.equals("") || "".equals(pw)) {
            // SP没有帐号
            /**
             * 读取本地帐号
             */
            accountList = AccountTools.getAccountFromFile(getActivity());
            if (accountList != null && accountList.size() > 0) {   // 无记录新帐号
                loginName.setText(accountList.get(accountList.size() - 1).getUname()); // 取最后一个登录用户的帐号显示
                loginPW.setText(ZipString.zipString2Json(accountList.get(accountList.size() - 1).getPassword())); // 取最后一个登录帐号的密码显示
            }
        } else { // 已有帐号
            loginName.setText(name);
            loginPW.setText(pw);
            loginName.setSelection(name.length());
        }
        downInfo.requestFocus();
    }

    private void handleDownInfo(final String pname) {
        //跳转到外部
        AppUtils.toSdkUrl(getActivity(), IConfig.horse_race_lamp_url);
    }

    private void showViewLayout(ViewLayout e) {

        for (ViewLayout viewLayout : viewLayoutMap.keySet()) {

            String title = viewLayoutMap.get(viewLayout).getmTitle();
            View layout = viewLayoutMap.get(viewLayout).getmLayout();

            if (viewLayout == e) {

                layout.setVisibility(View.VISIBLE);

                if (regLoginTitleTips != null) {

                    regLoginTitleTips.setText(title);
                }
            } else {

                layout.setVisibility(View.GONE);
            }

        }

        if (ViewLayout.REG_BY_EMAIL == e) {
            autoCreateAccountAndPwd();
        }

        if (resetPwdLayout.getVisibility() == View.VISIBLE) {
            //显示登录标题旁边的返回按钮
            backBtn.setVisibility(View.VISIBLE);
        } else {
            backBtn.setVisibility(View.GONE);
        }
    }

    private void autoCreateAccountAndPwd() {
        regName.setText(AccountTools.randomAccount());
        regPW.setText(AccountTools.randomPwd());
    }

    //初始化帐号选择弹出框
    private void initPopWindows() {

        accountList = AccountTools.getAccountFromFile(getActivity());
        ListView listView = new ListView(getActivity());
        listView.setCacheColorHint(0xffffffff);
        listView.setFocusable(false);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mPopupWindow != null) {
                    mPopupWindow.dismiss();
                }
                loginName.setText(accountList.get(position).getUname());
                loginPW.setText(ZipString.zipString2Json(accountList.get(position).getPassword()));
            }

        });
        listView.setDivider(getActivity().getResources().getDrawable(Util.getIdByName("yx_divider", "drawable", getActivity().getPackageName(), getActivity())));
        mPopupWindow = new PopupWindow(listView, loginLayout.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(getActivity().getResources().getDrawable(Util.getIdByName("yx_bg_account_drop_select", "drawable", getActivity().getPackageName(), getActivity())));

        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                if (fg_select_account != null) {
                    fg_select_account.setImageResource(Util.getIdByName("yx_user_set_down_bg_down", "drawable", getActivity().getPackageName(), getActivity()));
                }
            }
        });

        selectCallback = new YXSDKListener() {

            @Override
            public void onSuccess(Bundle bundle) {

                loginName.setText(bundle.getString("uname"));
                loginPW.setText(ZipString.zipString2Json(bundle.getString("upwd")));
            }

            @Override
            public void onFailture(int code, String msg) {
                loginName.setText("");
                loginPW.setText("");
            }
        };

        accountAdapter = new AccountAdapter(getActivity(), mPopupWindow, getActivity().getLayoutInflater(), accountList, selectCallback);

        listView.setAdapter(accountAdapter);

        accountAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean back() {
        return super.back();
    }


    //处理登录框中view的显示隐藏模块
    private enum ViewLayout {

        LOGIN,
        REG_BY_EMAIL,
        REG_BY_MSG,
        REG_BY_PHONE_NUM,
        FORGET_PWD
    }

    /* 定义一个倒计时的内部类 */
    private class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发

            if (getVerifyCodeBtn != null) {
                getVerifyCodeBtn.setText("获取验证码");
                getVerifyCodeBtn.setEnabled(true);
                getVerifyCodeBtn.setClickable(true);
            }

        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            getVerifyCodeBtn.setEnabled(false);
            getVerifyCodeBtn.setClickable(false);
            getVerifyCodeBtn.setText(millisUntilFinished / 1000 + "秒");
        }
    }


}
