package com.yxyige.sdk.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.unionpay.UPPayAssistEx;
import com.yxyige.sdk.core.IError;
import com.yxyige.sdk.core.SDKManager;
import com.yxyige.sdk.utils.ViewController;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class GamePayActivity extends Activity {

    private Bundle payBundle;
    private String tn;
    private int iconID = android.R.drawable.ic_dialog_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        payBundle = this.getIntent().getExtras();
        final int PLUGIN_NOT_INSTALLED = -1;
        final int PLUGIN_NEED_UPGRADE = 2;
        tn = payBundle.getString("tn");
        try {
            String mMode = "00";
            /*************************************************
             * 步骤2：通过银联工具类启动支付插件
             ************************************************/
            // mMode参数解释：
            // 0 - 启动银联正式环境
            // 1 - 连接银联测试环境
            // UPPayAssistEx.startPayByJAR((Activity)context,PayActivity.class,
            // null, null, tn, mMode);

            int ret = UPPayAssistEx.startPay(GamePayActivity.this, null,
                    null, tn, mMode);


            if (ret == PLUGIN_NEED_UPGRADE || ret == PLUGIN_NOT_INSTALLED) {
                // 需要重新安装控件
                //boolean isInstallSuccess = UPPayAssistEx.installUPPayPlugin(PayCallBackActivity.this);

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        GamePayActivity.this);
                builder.setTitle("提示");
                builder.setMessage("完成购买需要安装银联支付控件，是否安装？");

                builder.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String str = "UPPayPluginEx.apk";
                                boolean install = installAssetsApk(
                                        GamePayActivity.this, str);
                                if (!install)
                                    Toast.makeText(GamePayActivity.this,
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
                dialog.setOnDismissListener(new OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {

                        GamePayActivity.this.finish();
                    }
                });
                dialog.show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(GamePayActivity.this, "支付失败，请稍后再试或选择其他支付方式",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 从assets里面复制文件出来
     *
     * @param context  上下文
     * @param fileName assets下的文件
     */
    private boolean installAssetsApk(Context context, String fileName) {

        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            String type = "application/vnd.android.package-archive";

            //从assets读取文件流
            InputStream is = context.getAssets().open(fileName);
            //将该文件流写入到本应用程序的私有数据区this.getFilesDir().getPath();
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE + Context.MODE_WORLD_READABLE);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.flush();
            is.close();
            fos.close();

            File f = new File(context.getFilesDir().getPath() + "/" + fileName);
            intent.setDataAndType(Uri.fromFile(f), type);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        /*************************************************
         * 步骤3：处理银联手机支付控件返回的支付结果
         ************************************************/
        if (data == null) {
            return;
        }

        String msg = "支付失败！请重试";
        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
         */
        Bundle bundle = data.getExtras();
        if (bundle != null) {

            String str = bundle.getString("pay_result");

            Log.i("PayManager", str);

            if ("success".equalsIgnoreCase(str)) {
                msg = "支付成功！";
                GamePayActivity.this.finish();

                if (SDKManager.payListener != null) {
                    SDKManager.payListener.onSuccess(new Bundle());
                } else {

                    ViewController.showToast(GamePayActivity.this, msg);
                }
            } else if ("fail".equalsIgnoreCase(str)) {
                msg = "支付失败！";
                GamePayActivity.this.finish();
                if (SDKManager.payListener != null) {
                    SDKManager.payListener.onFailture(IError.NET_ERROR, msg);
                } else {

                    ViewController.showToast(GamePayActivity.this, msg);
                }
            } else if ("cancel".equalsIgnoreCase(str)) {
                msg = "取消了支付";
                GamePayActivity.this.finish();
                if (SDKManager.payListener != null) {
                    SDKManager.payListener.onFailture(IError.CANCEL_PAY, msg);
                } else {

                    ViewController.showToast(GamePayActivity.this, msg);
                }
            }
        }

    }

    /**
     * @return void 返回类型
     * @Title: showInfoDialog(这里用一句话描述这个方法的作用)
     * @param: context
     * @param: strTitle
     * @param: message
     * @param: icon
     * @param: onClickListener
     */
    public void showInfoDialog(Context context, String strTitle, String message, int icon, DialogInterface.OnClickListener onClickOKListener,
                               DialogInterface.OnClickListener onClickCancleListener, boolean cancle) {

        AlertDialog.Builder localBuilder = new AlertDialog.Builder(context);
        if (icon != 0) {
            localBuilder.setIcon(icon);
        }
        localBuilder.setTitle(strTitle);
        localBuilder.setMessage(message);
        localBuilder.setPositiveButton("YES", onClickOKListener);
        if (onClickCancleListener != null) {
            localBuilder.setNegativeButton("NO", onClickCancleListener);
        }
        localBuilder.setCancelable(cancle);
        AlertDialog dialog = localBuilder.create();
        dialog.show();

    }

}
