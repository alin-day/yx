package com.yxyige.msdk.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigPictureStyle;
import android.support.v4.app.NotificationCompat.BigTextStyle;

import com.yxyige.msdk.YXMCore;
import com.yxyige.msdk.api.MRequestCallBack;
import com.yxyige.msdk.api.MRequestManager;
import com.yxyige.msdk.api.MultiSDKUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class YXPushReceiver extends BroadcastReceiver {

    public static final int NOTIFICATION_TEXT = 1;
    public static final int NOTIFICATION_PICTURE = 2;
    public static final int NOTIFICATION_LINK = 3;
    public static final int NOTIFICATION_APP = 4;
    int yxGame_icon_url;
    private Context context;
    private NotificationManager manager;

    public static void setPushTime(Context context) {
        SharedPreferences sp = context.getSharedPreferences("pref_time", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putLong("time", System.currentTimeMillis());
        editor.commit();
    }

    public static long getPushTime(Context context) {
        SharedPreferences sp = context.getSharedPreferences("pref_time", Context.MODE_PRIVATE);
        return sp.getLong("time", 0);
    }

    public static boolean canrun(Context context) {

        if (System.currentTimeMillis() - getPushTime(context) > MultiSDKUtils.getPushDelay(context)) {
            setPushTime(context);
            return true;
        }
        return !MultiSDKUtils.getPushIsDelay(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        String action = intent.getAction();

        yxGame_icon_url = MultiSDKUtils.getIdByName("yx_transparent", "drawable", context.getPackageName(), context);

        if (action.equals(Intent.ACTION_USER_PRESENT)) {

            System.out.println("SQ action:Intent.ACTION_USER_PRESENT push");

            push(context, 1);

        } else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            State wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

            State mobileState = null;
            NetworkInfo mobileNi = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (mobileNi != null) {
                mobileState = mobileNi.getState();
            }


            if (wifiState != null && mobileState != null && State.CONNECTED != wifiState
                    && State.CONNECTED != mobileState) {

            } else {
                push(context, 3);
            }
        }
    }

    private void push(final Context context, final int type) {

        System.out.println("SQ rec push request");

//         push
        if (!canrun(context)) {
            YXMCore.sendLog("推送时间未到");
            return;
        }

        System.out.println("SQ start push request");


        MRequestManager requestManager = new MRequestManager(context);
        requestManager.pushRequest(new MRequestCallBack() {

            @Override
            public void onRequestSuccess(String content) {


                try {
                    JSONObject jsonObj = new JSONObject(content);
                    int state = jsonObj.getInt("state");

                    if (state == 0)
                        return;

                    JSONObject data = jsonObj.getJSONObject("data");

                    final int ptype = data.getInt("ptype");
                    String icon = data.getString("icon");
                    final String title = data.getString("title");
                    final String text = data.getString("msg");
                    final String url = data.getString("url");
                    final String dpgn = data.getString("dpgn");
                    final String psize = data.getString("psize");
                    final String pimg = data.getString("pimg");

//                    final Bitmap defaultIcon= BitmapFactory.decodeResource(context.getResources(),sy37_icon_url);
                    final Bitmap defaultIcon = ((BitmapDrawable) MultiSDKUtils.getAppIcon(context)).getBitmap();
                    if (icon != null && !"".equals(icon)) {
                        MultiSDKUtils.downLoadBitmap(icon, new Handler() {

                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);

                                switch (msg.what) {
                                    case 1:
                                        Bitmap iconBitmap = (Bitmap) msg.obj;
                                        showPushNotice(ptype, iconBitmap, title, text, url, dpgn, psize, pimg);
                                        break;

                                    case -1:
                                        showPushNotice(ptype, defaultIcon, title, text, url, dpgn, psize, pimg);
                                        break;

                                    default:
                                        break;
                                }
                            }

                        });
                    } else {
                        showPushNotice(ptype, defaultIcon, title, text, url, dpgn, psize, pimg);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onRequestError(String errorMsg) {

            }
        }, type);

    }

    public void showPushNotice(int type, final Bitmap lagerIcon, final String title, final String text, final String url, final String pName
            , String app_size, String bigPicUrl) {

        switch (type) {
            case 1:
                showPushNotice(lagerIcon, title, text, url, null, app_size, bigPicUrl);
                break;

            case 2:
                showPushNotice(lagerIcon, title, text, url, pName, app_size, bigPicUrl);
                break;


            default:
                break;
        }

    }

    public void showPushNotice(final Bitmap lagerIcon, final String title, final String text, final String url, final String pName
            , String app_size, String bigPicUrl) {

        if (bigPicUrl != null && !"".equals(bigPicUrl)) {

            MultiSDKUtils.downLoadBitmap(bigPicUrl, new Handler() {

                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case 1:
                            Bitmap bigPic = (Bitmap) msg.obj;
                            pushInPicture(title, text, bigPic, lagerIcon, url, pName);
                            break;

                        case -1:
                            pushInText(title, text, lagerIcon, url, pName);
                            break;

                        default:
                            break;
                    }
                }

            });

        } else {
            pushInText(title, text, lagerIcon, url, pName);
        }

    }

    private void pushInPicture(String title, String msg, Bitmap bigPic, Bitmap icon, String url, String pName) {

        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.BigPictureStyle pictureStyle = new BigPictureStyle();
        pictureStyle.setBigContentTitle(title).setSummaryText(msg).bigPicture(bigPic);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setLargeIcon(icon);
        builder.setSmallIcon(yxGame_icon_url);
        builder.setTicker(title);
        builder.setContentTitle(title);
        builder.setContentText(msg);
        builder.setContentInfo("");
        builder.setStyle(pictureStyle);
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_ALL);
        if (pName != null && !"".equals(pName) && MultiSDKUtils.checkPackInstalled(context, pName)) {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(pName);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
        } else {
            if (url != null && !"".equals(url)) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);
            }
        }

        Notification notification = builder.build();
        manager.notify(NOTIFICATION_PICTURE, notification);
    }

    private void pushInText(String title, String msg, Bitmap icon, String url, String pName) {

        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.BigTextStyle textStyle = new BigTextStyle();
        textStyle.setBigContentTitle(title)
//					.setSummaryText("")
                .bigText(msg);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setLargeIcon(icon);
        builder.setSmallIcon(yxGame_icon_url);
        builder.setTicker(title);
        builder.setContentTitle(title);
        builder.setContentText(msg);
        builder.setContentInfo("");
        builder.setStyle(textStyle);
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_ALL);
        if (pName != null && !"".equals(pName) && MultiSDKUtils.checkPackInstalled(context, pName)) {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(pName);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
        } else {
            if (url != null && !"".equals(url)) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);
            }
        }
        Notification notification = builder.build();
        manager.notify(NOTIFICATION_TEXT, notification);
    }


}
