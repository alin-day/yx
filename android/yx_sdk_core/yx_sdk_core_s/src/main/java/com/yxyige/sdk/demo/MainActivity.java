package com.yxyige.sdk.demo;


import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.yxyige.sdk.core.YXSDK;
import com.yxyige.sdk.core.YXSDKListener;

public class MainActivity extends Activity {

    private String appkey = "CPaRKFSmZgG9MA_rDVubdloiByj0te";
    private YXSDK myYXSDK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myYXSDK = myYXSDK.getInstance();
        myYXSDK.init(this, appkey, new YXSDKListener() {

            @Override
            public void onSuccess(Bundle bundle) {

                Toast.makeText(MainActivity.this, "init onSuccess", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailture(int code, String msg) {

                Toast.makeText(MainActivity.this, "init onFailture:" + msg, Toast.LENGTH_LONG).show();

            }
        });

        Button login_regisiter = new Button(this);
        login_regisiter.setText("Login/Regisiter");
        login_regisiter.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        login_regisiter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        myYXSDK.login(MainActivity.this, new YXSDKListener() {

                            @Override
                            public void onSuccess(Bundle bundle) {
                                Toast.makeText(MainActivity.this,
                                        "登录成功： \n userid: " + bundle.getString("userid") + "\n username: "
                                                + bundle.getString("username") + "\n token: "
                                                + bundle.getString("token")
                                        , Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onFailture(int code, String msg) {
                                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                            }
                        });

                    }

                });

            }
        });


        Button change = new Button(this);
        change.setText("Change Account");
        change.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        change.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                myYXSDK.changeAccount(MainActivity.this, new YXSDKListener() {

                    @Override
                    public void onSuccess(Bundle bundle) {

                        Toast.makeText(
                                MainActivity.this,
                                "主动切换帐号成功：\n userid: " + bundle.getString("userid") + "\n username: "
                                        + bundle.getString("username") + "\n token: "
                                        + bundle.getString("token")

                                , Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onFailture(int code, String msg) {

                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        Button logout = new Button(this);
        logout.setText("Logout");
        logout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        logout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        myYXSDK.logout(MainActivity.this, new YXSDKListener() {

                            @Override
                            public void onSuccess(Bundle bundle) {
                                Log.e("myYXSDK", "logout success");
                                MainActivity.this.finish();
                            }

                            @Override
                            public void onFailture(int code, String msg) {
                                Log.e("myYXSDK", "logout failtrue:" + msg);
                            }
                        });


                    }

                }).start();

            }
        });

        Button pay = new Button(this);
        pay.setText("Pay");
        pay.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        pay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().post(new Runnable() {

                    @Override
                    public void run() {

                        myYXSDK.pay(MainActivity.this, "A" + System.currentTimeMillis(), "一堆金币", "金币", "S001", "金戈铁马", "CP扩展字段",
                                "RID0001", "nimo", 10, 1, 10, "B" + System.currentTimeMillis(), new YXSDKListener() {
                                    @Override
                                    public void onSuccess(Bundle bundle) {
                                        //完成充值，充值页上会直接给出相应提示
                                        System.out.println("收到支付onSuccess");
                                        Toast.makeText(getApplicationContext(), "充值完成,充值结果以服务器为准", Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onFailture(int code, String msg) {
                                        System.out.println("收到支付onFailed,code:" + code + " msg:" + msg);
                                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                });
            }
        });


        LinearLayout frame = new LinearLayout(this);
        frame.setOrientation(LinearLayout.VERTICAL);
        frame.addView(login_regisiter);
        frame.addView(change);
        frame.addView(logout);
        frame.addView(pay);
        frame.setBackgroundColor(Color.WHITE);
        setContentView(frame);


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            myYXSDK.logout(MainActivity.this, new YXSDKListener() {

                @Override
                public void onSuccess(Bundle bundle) {
                    Log.e("myYXSDK", "logout success");
                    MainActivity.this.finish();
                }

                @Override
                public void onFailture(int code, String msg) {
                    Log.e("myYXSDK", "logout failtrue:" + msg);
                }
            });

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}