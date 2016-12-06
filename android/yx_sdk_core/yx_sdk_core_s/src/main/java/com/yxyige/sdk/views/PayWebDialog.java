package com.yxyige.sdk.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.switfpass.pay.MainApplication;
import com.switfpass.pay.activity.PayPlugin;
import com.switfpass.pay.bean.RequestMsg;
import com.unionpay.UPPayAssistEx;
import com.yxyige.sdk.alipay.Base64;
import com.yxyige.sdk.alipay.PayResult;
import com.yxyige.sdk.core.IError;
import com.yxyige.sdk.core.PayCallBackActivity;
import com.yxyige.sdk.core.PayClient;
import com.yxyige.sdk.core.RequestCallBack;
import com.yxyige.sdk.core.RequestManager;
import com.yxyige.sdk.core.YXSDKListener;
import com.yxyige.sdk.core.YXWebChromeClient;
import com.yxyige.sdk.utils.LogUtil;
import com.yxyige.sdk.utils.Util;
import com.yxyige.sdk.utils.ViewController;
import com.yxyige.sdk.views.webview.WebViewManager;
import com.yxyige.sdk.widget.ProgressDialog;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;


public class PayWebDialog extends Dialog {

    public static final int CODE_PAY_SUCCESS = 1;    //支付成功
    public static final int CODE_PAY_FAILD = 0;      //支付失败
    public static final int CODE_PAY_CANCLE = 2;     //支付取消
    public static final int CODE_PAY_JUMP = 3;       //支付跳转(调用插件)
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_CHECK_FLAG = 2;

    private Context context;
    private ImageButton toGame;
    private TextView toService;
    private WebView webView;
    private PayClient mWebClient = null;
    private YXSDKListener payListener;
    private ProgressDialog progressDialog;
    private ProgressDialog waitDialog;
    private String url;
    //20151201增加微信支付
    private boolean isWxPayClicked;
    private String uuid;//支付订单号
    private YXWebChromeClient mWebChromeClient = null;
    /**
     * for payment
     **/
    private ProgressDialog mProgressDialog;
    private ImageView paywebClose;
    private HashMap<String, Object> payWebMap = new HashMap<String, Object>();
    private Handler dismissPayhandler = new Handler() {
        @Override
        public void handleMessage(Message messaage) {
            super.handleMessage(messaage);

            try {
                if (webView != null) {
                    webView.stopLoading();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


            if (PayWebDialog.this.isShowing()) {

                PayWebDialog.this.dismiss();
            }

            int type = messaage.what;

            System.out.println("收到closePay type：" + type);


            switch (type) {
                case PayWebDialog.CODE_PAY_SUCCESS:

                    if (payListener != null) {

                        payListener.onSuccess(new Bundle());
                    } else {

                        ViewController.showToast(context, "支付成功");
                    }


                    break;
                case PayWebDialog.CODE_PAY_FAILD:


                    if (payListener != null) {

                        payListener.onFailture(IError.ERROR_GET_DATA_FAILD, "支付失败");
                    } else {

                        ViewController.showToast(context, "支付失败");
                    }

                    break;
                case PayWebDialog.CODE_PAY_CANCLE:

                    if (payListener != null) {

                        payListener.onFailture(IError.CANCEL_PAY, "取消支付");
                    } else {

                        ViewController.showToast(context, "取消支付");
                    }

                    break;
                case PayWebDialog.CODE_PAY_JUMP:

                    System.out.println("支付跳转");

                    break;

                default:
                    break;
            }


        }
    };
    // 这里接收支付结果，支付宝手机端同步通知
    private Handler alipayHandler = new Handler() {
        public void handleMessage(Message msg) {
            try {

                dismissPay(CODE_PAY_JUMP);

                switch (msg.what) {
                    case SDK_PAY_FLAG: {
                        //
                        closeProgress();

                        // 处理交易结果
                        try {
                            PayResult resultObj = new PayResult((String) msg.obj);
                            String tradeStatus = resultObj.getResultStatus();

                            // 验签成功。验签成功后再判断交易状态码
                            if (tradeStatus.equals("9000")) {// 判断交易状态码，只有9000表示交易成功
                                {
                                    if (payListener != null) {
                                        payListener.onSuccess(new Bundle());
                                    }
                                }

                            } else if (tradeStatus.equals("6001")) {
                                if (payListener != null) {
                                    payListener.onFailture(IError.CANCEL_PAY, "取消了支付");
                                }

                            } else if (tradeStatus.equals("8000")) {
                                if (payListener != null) {
                                    payListener.onFailture(IError.ERROR_GET_DATA_FAILD,
                                            "支付结果确认中,请耐心等待");
                                }

                            } else {
                                if (payListener != null) {
                                    payListener.onFailture(IError.ERROR_GET_DATA_FAILD, "支付失败");
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            ViewController.showToast(context, "支付失败");
                        }
                    }
                    break;
                }

                super.handleMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private PayWaitCallback payWaitCallback = new PayWaitCallback() {

        @Override
        public void loading(int progress) {
            updateWaitDialog("加载中..." + progress + "%");
        }

        @Override
        public void loadStart(String url) {
            try {
                if (progressDialog != null) {
                    progressDialog.show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void loadFinish() {

            try {
//                show();
//                hideWaitDialog();
                progressDialog.dismiss();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void loadError(String errorMsg) {

            if (payListener != null) {
                payListener.onFailture(IError.NET_ERROR, errorMsg);
            }

            try {
                hideWaitDialog();
                progressDialog.dismiss();
                dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                ViewController.showToast(context, errorMsg);
            }
        }
    };


    public PayWebDialog(Context context, String url) {
        super(context);
        this.context = context;
        this.url = url;
    }

    public PayWebDialog(Context context, int theme, String url, YXSDKListener payListener) {

        super(context, theme);
        this.context = context;
        this.url = url;
        this.payListener = payListener;

        progressDialog = new ProgressDialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setMessage("正在加载中...");

    }

    public static boolean checkApkExist(Context context, Intent intent) {
        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, 0);
        if (list.size() > 0) {
            return true;
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(Util.getIdByName("yx_paydialog", "layout", context.getPackageName(), context), null);
        paywebClose = (ImageView) view.findViewById(Util.getIdByName("togame", "id", context.getPackageName(), context));
        paywebClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWxPayClicked) {
                    //20151201新增；如果点击过微信，则优先查询微信订单详情，反之则进行关闭支付页面（注意查询后，状态要设为false）
                    checkWeixinPay();
                } else {
                    dismissPay(CODE_PAY_CANCLE);

                    if (webView != null) {
                        webView.stopLoading();
                    }
                }
            }
        });

        getContext().setTheme(android.R.style.Theme_NoTitleBar_Fullscreen);
        webView = (WebView) view.findViewById(Util.getIdByName("webView", "id",
                context.getPackageName(), context));

        webView.setBackgroundColor(Color.WHITE);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setVisibility(View.VISIBLE);

        mWebChromeClient = new YXWebChromeClient(payWaitCallback);
        mWebClient = new PayClient(context, payWaitCallback);

        webView.setWebChromeClient(mWebChromeClient);// new WebChromeClient());
        webView.setWebViewClient(mWebClient);

        /**
         * 防止输入框点击后页面放大
         */
        webView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    try {
                        Field defaultScale = WebView.class.getDeclaredField("mDefaultScale");
                        defaultScale.setAccessible(true);
                        // WebViewSettingUtil.getInitScaleValue(VideoNavigationActivity.this,
                        // false )/100.0f 是我的程序的一个方法，可以用float 的scale替代
                        defaultScale.setFloat(webView, 1);
                    } catch (Exception e) {
                        //
                    }
                }
            }
        });

        WebSettings ws = webView.getSettings();
        ws.setAllowFileAccess(true);
        ws.setBuiltInZoomControls(false);
        ws.setRenderPriority(RenderPriority.HIGH);
        ws.setSavePassword(true);
        ws.setDomStorageEnabled(true); // 设置支持localStorage特性
        ws.setLightTouchEnabled(true);
        //H5缓存
        ws.setAppCacheMaxSize(1024 * 1024 * 5);
        String appCachePath = context.getApplicationContext().getCacheDir().getAbsolutePath();
        ws.setAppCachePath(appCachePath);
        ws.setAppCacheEnabled(true);
        //网页缓存
        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);// 不使用缓存
        ws.setAllowFileAccess(true);
        // ws.setEnableSmoothTransition(true);
        ws.setJavaScriptEnabled(true);


        webView.clearCache(false);
        webView.loadUrl(url);

        webView.addJavascriptInterface(new JsObj(context), "fee");
        // webView.requestFocus();

        setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * 微信支付状态查询
     */
    protected void checkWeixinPay() {

        LogUtil.d("yx --> payDialog --> checkWeixinPay ---> do");

        RequestManager rm = new RequestManager(context);
        rm.checkPayStatus(context, "wxpay", uuid, new RequestCallBack() {

            @Override
            public void onRequestSuccess(String content) {
                isWxPayClicked = false;
                //查询成功
                try {

                    JSONObject jsonObj = new JSONObject(content);
                    int state = jsonObj.getInt("state");

                    if (state == 1) {
                        //支付成功
                        dismissPay(CODE_PAY_SUCCESS);
                    } else {
                        //支付失败
                        dismissPay(CODE_PAY_FAILD);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    dismissPay(CODE_PAY_FAILD);
                }
            }

            @Override
            public void onRequestError(String errorMsg) {
                isWxPayClicked = false;
                payListener.onFailture(205, "查询失败");
                dismissPay(CODE_PAY_JUMP);
            }
        }, false);

    }

    public void dismissPay(int code) {
        dismissPayhandler.sendEmptyMessage(code);
    }

    // 关闭进度框
    void closeProgress() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * get the sign type we use. 获取签名方式
     *
     * @return
     */
    String getSignType() {
        String getSignType = "sign_type=" + "\"" + "RSA" + "\"";
        return getSignType;
    }

	/*支付宝支付方法结束*/

    /**
     * get the char set we use. 获取字符集
     *
     * @return
     */
    String getCharset() {
        String charset = "charset=" + "\"" + "utf-8" + "\"";
        return charset;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            //其他情况
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                //web来处理，js方法:back()
                webView.loadUrl("javascript:back()");//JS代码要是带参数
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void waitShow() {

        hide();

    }

    private void hideWaitDialog() {

        if (waitDialog != null && waitDialog.isShowing()) {
            waitDialog.dismiss();
        }
    }

    private void updateWaitDialog(String msg) {

        if (waitDialog != null && waitDialog.isShowing()) {
            waitDialog.setMessage(msg);
        }
    }

    public interface PayWaitCallback {

        public void loadStart(String url);

        public void loadFinish();

        public void loading(int progress);

        public void loadError(String errorMsg);
    }

    public class JsObj {

        private Context con;

        public JsObj(Context con) {
            this.con = con;
        }

        public JsObj(Context con, boolean fromMMPay) {
            this.con = con;
        }

        @JavascriptInterface
        public void checkWX() {
            //检查微信支付
            checkWeixinPay();
        }

        @JavascriptInterface
        public void closePay() {

            closePay("" + CODE_PAY_FAILD);
        }

        /**
         * 关闭支付
         * 特别注意，js调用android参数应该为string型，如果为int则默认所有传值都为0
         *
         * @param flag 关闭类型
         */
        @JavascriptInterface
        public void closePay(String flag) {

//          System.out.println("收到closePay String flag："+flag);

            int resCode = CODE_PAY_JUMP;
            try {

                resCode = Integer.parseInt(flag);

            } catch (Exception e) {
                e.printStackTrace();
                resCode = CODE_PAY_FAILD;
            }

            dismissPay(resCode);

        }

        /**
         * 银联支付
         *
         * @param tn 订单串
         */
        @JavascriptInterface
        public void enUpomp(String tn) {

            LogUtil.e("enUpomp:" + tn);


            if (tn == null || "".equals(tn)) {
                Toast.makeText(context, "银联单号错误，请稍后再试或选择其他支付方式", Toast.LENGTH_SHORT).show();
                return;
            }


            boolean isInstallUnipay = Util.checkAppInstalled(context, "com.unionpay.uppay"); //检测是否安装有银联插件
            System.out.println("SQ isInstallUnipay:" + isInstallUnipay);

            if (!isInstallUnipay) {


                // 需要重新安装控件
                //boolean isInstallSuccess = UPPayAssistEx.installUPPayPlugin(PayCallBackActivity.this);

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        context);
                builder.setTitle("提示");
                builder.setMessage("完成购买需要安装银联支付控件，是否安装？");

                builder.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                // 需要重新安装控件
                                boolean isInstallSuccess = UPPayAssistEx.installUPPayPlugin(context);
                                System.out.println("SQ unipay install result:" + isInstallSuccess);

                                if (!isInstallSuccess)
                                    Toast.makeText(context,
                                            "安装失败，请稍后再试或选择其他支付方式",
                                            Toast.LENGTH_SHORT).show();
                            }
                        });

                builder.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();


            } else {

                dismissPay(CODE_PAY_JUMP);

                Bundle payBundle = new Bundle();
                payBundle.putString("tn", tn);
                Intent payIntent = new Intent(context, PayCallBackActivity.class);
                payIntent.putExtras(payBundle);
                context.startActivity(payIntent);

            }
        }

        /**
         * 支付宝支付
         *
         * @param orderinfo 游戏订单号
         */
        @JavascriptInterface
        public void enAli(String orderinfo) {

            LogUtil.e("enAli orderinfo:" + orderinfo);

            final String obj = new String(Base64.decode(orderinfo));

            LogUtil.e("enAli encode:" + obj);

            if (obj == null || "".equals(obj)) {
                Toast.makeText(context, "暂不支持该支付类型，请选择其他支付方式.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (obj != null) { // 有订单信息

                // 调用pay方法进行支付
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        String payInfo = obj;
                        // 构造PayTask 对象
                        PayTask alipay = new PayTask((Activity) context);
                        // 调用支付接口
                        String result = alipay.pay(payInfo);
                        LogUtil.e("alipay result：" + result);
                        Message msg = new Message();
                        msg.what = SDK_PAY_FLAG;
                        msg.obj = result;
                        alipayHandler.sendMessage(msg);

                    }
                }).start();

            }
        }


        /**
         * 微信支付
         *
         * @param wxJson 参数命令行，json格式
         */
        @JavascriptInterface
        public void enWX(String wxJson) {
            System.out.println("调用微信支付:" + wxJson);

            try {
                JSONObject json = new JSONObject(wxJson);
                //有数据
                final String trade = json.getString("trade");
                uuid = json.getString("uuid");

                if (trade == null || "".equals(trade)) {
                    Toast.makeText(context, "暂不支持该支付类型，请选择其他支付方式.", Toast.LENGTH_SHORT).show();
                    return;
                }
                //wap端跳转微信
                isWxPayClicked = true;
                final String prepay_url = trade + "&type=android";

                ((Activity) context).runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        new WebViewManager(context).showWebView(prepay_url);
                    }
                });
            } catch (Exception e) {
                if (payListener != null) {
                    payListener.onFailture(205, "微信支付数据解析失败");
                }
            }
        }

        @JavascriptInterface
        public void enWXWft(String wxJson) {
            System.out.println("威富通的微信支付:" + wxJson);
            Double money = null;
            String token_id = null;
            String payOrderNo = null;
            try {
                JSONObject json = new JSONObject(wxJson);
                //有数据
                money = Double.parseDouble(json.getString("money"));
                token_id = json.getString("token_id");
                payOrderNo = json.getString("payOrderNo");

                if (token_id == null || "".equals(token_id) || payOrderNo == null || "".equals(payOrderNo)) {
                    Toast.makeText(context, "暂不支持该支付类型，请选择其他支付方式.", Toast.LENGTH_SHORT).show();
                    return;
                }

                RequestMsg msg = new RequestMsg();
                msg.setMoney(money);
                msg.setTokenId(token_id);
                msg.setOutTradeNo(payOrderNo);
                System.out.println("威富通 支付 参数+" + msg.toString());
                // 微信wap支付
                msg.setTradeType(MainApplication.PAY_WX_WAP);
                PayPlugin.unifiedH5Pay((Activity) con, msg);

            } catch (Exception e) {
                if (payListener != null) {
                    payListener.onFailture(205, "支付数据解析失败");
                }
            }
        }
    }
}
