package com.yxyige.m;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.yxyige.msdk.YXMCore;
import com.yxyige.msdk.api.YXAppConfig;
import com.yxyige.msdk.api.YXMResultListener;
import com.yxyige.sdk.m.R;
import com.yxyige.sdk.utils.AccountTools;
import com.yxyige.sdk.utils.RandomStringUtil;

import java.util.HashMap;

public class YXMainActivity extends Activity implements View.OnClickListener {

    private String appkey = "CPaRKFSmZgG9MA_rDVubdloiByj0te";
    private YXAppConfig config;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yx_demo_main);

        findViewById(R.id.loginBtn).setOnClickListener(this);
        findViewById(R.id.changeAccountBtn).setOnClickListener(this);
        findViewById(R.id.payBtn).setOnClickListener(this);
        findViewById(R.id.payBtn2).setOnClickListener(this);
        findViewById(R.id.showExit).setOnClickListener(this);
        findViewById(R.id.creatRoleBtn).setOnClickListener(this);
        findViewById(R.id.submitDataBtn).setOnClickListener(this);
        findViewById(R.id.upgradeDataBtn).setOnClickListener(this);
        findViewById(R.id.getappconfigBtn).setOnClickListener(this);


        /**
         * 1.越早调用越好，放在调用游戏UI界面之前调用
         * 2.初始化接口只需调用一次
         * 3.测试方法：优象闪屏在游戏的第一页。
         *
         * @param context 上下文
         * @param appkey 优象提供的基础通信秘钥
         * @param listener 初始化监听回调
         */
        YXMCore.getInstance().init(YXMainActivity.this, appkey, new YXMResultListener() {

            @Override
            public void onSuccess(Bundle bundle) {
                Toast.makeText(YXMainActivity.this, "初始化完成", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailture(int code, String msg) {
                Toast.makeText(YXMainActivity.this, "初始化失败", Toast.LENGTH_SHORT).show();
            }
        });

        /**
         * 切换帐号监听
         * 测试方法：悬浮窗-点击【切换帐号】-可以弹出登录界面，登录成功后，会回到onSuccess的回调中。
         *
         * @param listener 切换帐号监听回调
         **/
        YXMCore.getInstance().setSwitchAccountListener(new YXMResultListener() {
            @Override
            public void onSuccess(Bundle bundle) {
                Toast.makeText(YXMainActivity.this,
                        "悬浮窗切换帐号成功:" +
                                "\n token:" + bundle.getString("token") +
                                "\n gid:" + bundle.getString("gid") +
                                "\n pid:" + bundle.getString("pid"),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailture(int code, String msg) {
                Toast.makeText(YXMainActivity.this,
                        "悬浮窗切换帐号失败:" +
                                "\n msg=" + msg
                        , Toast.LENGTH_LONG).show();
            }
        });
    }

    //-----------------------  周期方法声明开始  ------------------------------

    @Override
    protected void onStart() {
        super.onStart();
        YXMCore.getInstance().onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        YXMCore.getInstance().onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        YXMCore.getInstance().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        YXMCore.getInstance().onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        YXMCore.getInstance().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        YXMCore.getInstance().onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        YXMCore.getInstance().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        YXMCore.getInstance().onNewIntent(intent);
    }


    //-----------------------  周期方法声明结束  ------------------------------


    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.loginBtn) {
            /**
             * 登录接口，需要在UI线程中调用
             * @param context 上下文
             * @param loginListener 登录回调
             */
            YXMCore.getInstance().login(YXMainActivity.this, new YXMResultListener() {
                @Override
                public void onSuccess(Bundle bundle) {
                    //CP拿到token后，需要通过服务器进行token验证，拿到对应的uid和uname
                    Toast.makeText(YXMainActivity.this,
                            "登录成功:" +
                                    "\n token:" + bundle.getString("token") +
                                    "\n gid:" + bundle.getString("gid") +
                                    "\n pid:" + bundle.getString("pid"),
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailture(int code, String msg) {
                    Toast.makeText(YXMainActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            });
        } else if (v.getId() == R.id.changeAccountBtn) {
            /**
             * 游戏内切换帐号接口，需要在UI线程中调用
             * CP也可以通过回到游戏自己的登录界面重新发起登录，达到切换帐号的目的。
             * @param context 上下文
             * @param loginListener 登录回调
             */
            YXMCore.getInstance().changeAccount(YXMainActivity.this, new YXMResultListener() {
                @Override
                public void onSuccess(Bundle bundle) {
                    //CP拿到token后，需要通过服务器进行token验证，拿到对应的uid和uname
                    Toast.makeText(YXMainActivity.this,
                            "主动切换帐号成功:" +
                                    "\n token:" + bundle.getString("token") +
                                    "\n gid:" + bundle.getString("gid") +
                                    "\n pid:" + bundle.getString("pid"),
                            Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailture(int code, String msg) {
                    Toast.makeText(YXMainActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            });
        } else if (v.getId() == R.id.payBtn) {
            /**
             * 支付接口(选接)参数说明
             * !!! 注意必传参数,不能为空，推荐所有参数都传值 !!!
             *
             * @param context 上下文 (*必传)
             * @param doid CP订单ID (*必传)
             * @param dpt CP商品名(*必传)
             * @param dcn CP货币名称 (*必传)
             * @param dsid CP游戏服ID (*必传)
             * @param dsname CP游戏服名称(*必传)
             * @param dext CP扩展回调参数 (*必传)
             * @param drid CP角色ID(*必传)
             * @param drname CP角色名(*必传)
             * @param drlevel CP角色等级(*必传)
             * @param dmoney CP金额(定额) (*必传)
             * @param dradio CP兑换比率(1元兑换率默认1:10)(*必传)
             * @param payListener 充值回调 (*必传)
             */
            YXMCore.getInstance().pay(YXMainActivity.this,
                    "A" + System.currentTimeMillis(),
                    "很多金币", "金币",
                    "S001", "铁马金戈",
                    "CP扩展字段", "RID0001", "路人甲", 1,
                    1, 10, new YXMResultListener() {
                        @Override
                        public void onSuccess(Bundle bundle) {
                            Toast.makeText(YXMainActivity.this, "成功发起充值请求(充值结果以服务端为准)", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailture(int code, String msg) {
                            Toast.makeText(YXMainActivity.this, msg, Toast.LENGTH_LONG).show();
                        }
                    });
        } else if (v.getId() == R.id.payBtn2) {
            YXMCore.getInstance().pay(YXMainActivity.this, "A" + System.currentTimeMillis(), "很多金币", "金币", "S001", "金戈铁马", "CP扩展字段",
                    "RID0001", "路人甲", 1, 0, 10, new YXMResultListener() {

                        @Override
                        public void onSuccess(Bundle bundle) {
                            Toast.makeText(YXMainActivity.this, "成功发起充值请求(充值结果以服务端为准)", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailture(int code, String msg) {
                            Toast.makeText(YXMainActivity.this, msg, Toast.LENGTH_LONG).show();
                        }
                    });
        } else if (v.getId() == R.id.showExit) {
            /**
             * 退出游戏弹窗参数说明:
             * @param context 上下文
             * @param logoutListener 登出游戏回调接口
             */
            YXMCore.getInstance().showExitDailog(YXMainActivity.this, new YXMResultListener() {
                @Override
                public void onSuccess(Bundle bundle) {
                    Toast.makeText(YXMainActivity.this, "登出完成，请处理游戏逻辑(例如清理资源、退出游戏等)",
                            Toast.LENGTH_LONG).show();
                    //在此做真正退出游戏逻辑，此处是模拟退出
                    System.exit(0);
                }

                @Override
                public void onFailture(int code, String msg) {
                    Toast.makeText(YXMainActivity.this, "取消登出，则不做退出处理，继续游戏",
                            Toast.LENGTH_LONG).show();
                }
            });
        } else if (v.getId() == R.id.creatRoleBtn) {
            /**
             * 提交角色信息参数说明
             * @param serverId          当前玩家登录的区服ID
             * @param serverName        当前玩家登录的区服名称
             * @param roleId            当前玩家的角色ID
             * @param roleName          当前玩家的角色名称
             * @param roleLevel         当前玩家的角色等级
             * @param balance           用户余额（RMB购买的游戏币）
             * @param partyName         帮派、公会等，没有则填空字符串
             * @param vipLevel          Vip等级,没有vip系统的传0
             * @param roleCTime         角色创建时间，单位秒，必须真实，需要CP保存在服务器，每次从服务器进行获取。
             * @param roleLevelMTime         角色升级的时间，单位：秒。获取不到请传-1
             */
            HashMap<String, String> infos1 = new HashMap<String, String>();
            infos1.put(YXMCore.INFO_SERVERID, "yourServerId");
            infos1.put(YXMCore.INFO_SERVERNAME, "yourServerName");
            infos1.put(YXMCore.INFO_ROLEID, "yourRoleId");
            infos1.put(YXMCore.INFO_ROLENAME, "yourRoleName");
            infos1.put(YXMCore.INFO_ROLELEVEL, "yourRoleLevel");
            infos1.put(YXMCore.INFO_BALANCE, "yourBalance");
            infos1.put(YXMCore.INFO_PARTYNAME, "yourPartyName");
            infos1.put(YXMCore.INFO_VIPLEVEL, "yourVipLevel");
            infos1.put(YXMCore.INFO_ROLE_TIME_CREATE, "" + 1458542706);//从服务器获取的真实创建角色时间
            infos1.put(YXMCore.INFO_ROLE_TIME_LEVEL, "-1");//第一次创建，没有升级时间，传-1
            YXMCore.getInstance().creatRoleInfo(infos1);
            Toast.makeText(getApplicationContext(), infos1.toString(), Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.submitDataBtn) {
            HashMap<String, String> infos2 = new HashMap<String, String>();
            infos2.put(YXMCore.INFO_SERVERID, "yourServerId");//服务器id
            infos2.put(YXMCore.INFO_SERVERNAME, "yourServerName");//服务器名称
            infos2.put(YXMCore.INFO_ROLEID, "yourRoleId");//角色id
            infos2.put(YXMCore.INFO_ROLENAME, "yourRoleName");//角色名称
            infos2.put(YXMCore.INFO_ROLELEVEL, "yourRoleLevel");//角色等级
            infos2.put(YXMCore.INFO_BALANCE, "yourBalance");//角色余额
            infos2.put(YXMCore.INFO_PARTYNAME, "yourPartyName");//工会名称
            infos2.put(YXMCore.INFO_VIPLEVEL, "yourVipLevel");//vip等级
            YXMCore.getInstance().submitRoleInfo(infos2);
            Toast.makeText(getApplicationContext(), infos2.toString(), Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.upgradeDataBtn) {
            HashMap<String, String> info3 = new HashMap<String, String>();
            info3.put(YXMCore.INFO_SERVERID, "yourServerId");
            info3.put(YXMCore.INFO_SERVERNAME, "yourServerName");
            info3.put(YXMCore.INFO_ROLEID, "yourRoleId");
            info3.put(YXMCore.INFO_ROLENAME, "yourRoleName");
            info3.put(YXMCore.INFO_ROLELEVEL, "yourRoleLevel");
            info3.put(YXMCore.INFO_BALANCE, "yourBalance");
            info3.put(YXMCore.INFO_PARTYNAME, "yourPartyName");
            info3.put(YXMCore.INFO_VIPLEVEL, "yourVipLevel");
            info3.put(YXMCore.INFO_ROLE_TIME_CREATE, "" + 1458542706);//从服务器获取的真实创建角色时间
            info3.put(YXMCore.INFO_ROLE_TIME_LEVEL, "" + 145345667);//当前角色升级时间
            YXMCore.getInstance().upgradeRoleInfo(info3);
            Toast.makeText(getApplicationContext(), info3.toString(), Toast.LENGTH_SHORT).show();
        } else if (v.getId() == R.id.getappconfigBtn) {
            /**
             * 获取游戏配置信息(其实是解析assets/优象wan_config.xml配置文件)
             * @result gid 游戏id(appid)
             * @result pid 联运商id(pid)
             * @result refer 渠道号(refer)
             */
            config = YXMCore.getInstance().getAppConfig();
            String gid = config.getGameid();
            String pid = config.getPtid();
            String refer = config.getRefid();
            Toast.makeText(YXMainActivity.this, "gid:" + gid + " \npid:" + pid + "\nrefer:" + refer,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            YXMCore.getInstance().showExitDailog(YXMainActivity.this, new YXMResultListener() {
                @Override
                public void onSuccess(Bundle bundle) {
                    Toast.makeText(YXMainActivity.this, "登出完成，请处理游戏逻辑(例如清理资源、退出游戏等)", Toast.LENGTH_LONG).show();
                    //在此做真正退出游戏逻辑，此处是模拟退出
                    System.exit(0);
                }

                @Override
                public void onFailture(int code, String msg) {
                    Toast.makeText(YXMainActivity.this, "取消登出，则不做退出处理，继续游戏", Toast.LENGTH_LONG).show();
                }
            });

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
