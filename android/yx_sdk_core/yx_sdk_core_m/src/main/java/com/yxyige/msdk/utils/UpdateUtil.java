package com.yxyige.msdk.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yxyige.sdk.core.RequestManager;
import com.yxyige.sdk.utils.DownloaderUtil;
import com.yxyige.sdk.utils.Util;

public class UpdateUtil {

    private Activity context;
    private boolean clickDown = false;    //是否点击了下载更新
    private RequestManager requestManager;

    public UpdateUtil(Activity context) {
        this.context = context;
        clickDown = false;
        requestManager = new RequestManager(context);
    }

    /**
     * 获取软件版本号
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 取应用版本名
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 检查更新
     *
     * @param appid
     */
    public void checkUpdate(String appid) {


//	    requestManager.checkUpdateRequest(appid, new RequestCallBack() {
//
//            @Override
//            public void onRequestSuccess(String content) {
//
//                try{
//
//                    /*
//                     *{"state":"1","data":{"id":"1","app_id_main":"B000","app_id":"A001",
//                     *"app_name":"\u6d4b\u8bd5","source_name":"\u6d4b\u8bd5",
//                     *"title":"\u6d4b\u8bd5","package_name":"com.sy37.sdk",
//                     *"version_name":"","version_code":"2","size":"65",
//                     *"upgrade_desc":"","force_upgrade":"0","state":"1",
//                     *"download_url":"http:\/\/szapp.api.m.37.com\/source\/game_upgrade_apk.php?apk=A001"}
//
//                     */
//                    JSONObject jsonObj = new JSONObject(content);
//                    int state = jsonObj.getInt("state");
//                    if(state==1){
//                        JSONObject data = jsonObj.getJSONObject("data");
//
//                        String desc = data.getString("upgrade_desc");
//                        String apkUrl =  data.getString("download_url");
//                        int force = data.getInt("force_upgrade");
//
//
//                        showUpdateDialog(desc,apkUrl,force);
//
//                    }
//                }catch (Exception e){
//
//                }
//            }
//
//            @Override
//            public void onRequestError(String errorMsg) {
//
//                Log.e("sqwan", "checkUpdate:"+errorMsg);
//            }
//        }, false);
//
    }

    /**
     * 显示更新对话框
     *
     * @param content
     * @param apkUrl
     * @param force
     */
    private void showUpdateDialog(String content, final String apkUrl, final int force) {

        final Dialog updateDialog = new Dialog(context, Util.getIdByName("ContentOverlay", "style", context.getPackageName(), context));
//		final Dialog updateDialog = new Dialog(context, R.style.ContentOverlay);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(Util.getIdByName("sy37_update_dialog", "layout", context), null);

        TextView contentView = (TextView) view.findViewById(Util.getIdByName("updateContent", "id", context));
        Button update = (Button) view.findViewById(Util.getIdByName("toUpdate", "id", context));
        Button cancel = (Button) view.findViewById(Util.getIdByName("toCancel", "id", context));

        contentView.setText(content);

        update.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (clickDown) {
                    Toast.makeText(context, "正在更新，请耐心等待...", Toast.LENGTH_LONG).show();
                    return;
                }

                clickDown = true;

                DownloaderUtil util = new DownloaderUtil(context);
                util.download(apkUrl, "apk");

                if (force == 1) {
                    Toast.makeText(context, "开始下载更新，请耐心等待...", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "正在后台更新...", Toast.LENGTH_LONG).show();
                    updateDialog.dismiss();
                }

            }
        });

        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (force == 1)
                    Toast.makeText(context, "此版本为强制更新版本，必须更新后才能继续游戏.", Toast.LENGTH_LONG).show();
                else
                    updateDialog.dismiss();
            }
        });

        updateDialog.setContentView(view);
        updateDialog.setCanceledOnTouchOutside(false);
        updateDialog.setCancelable(false);
        updateDialog.show();

    }
}
