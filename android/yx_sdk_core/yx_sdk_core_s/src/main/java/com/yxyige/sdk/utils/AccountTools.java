package com.yxyige.sdk.utils;

import android.content.Context;
import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yxyige.sdk.bean.ALAccountInfo;
import com.yxyige.sdk.bean.UserInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AccountTools {

    private static final String accountDir = "yx_sdk";            //帐号存储在sd的位置
    private static final String accountFile = "file.youxiang";        //存储帐号的文件

    /**
     * get account's path on sdcard
     *
     * @param context
     * @return
     */
    private static String getDir(Context context) {
        File file = new File(getSDPath(context) + "/" + accountDir);
        if (!file.exists()) {
            file.mkdir();
        }
        return getSDPath(context) + "/" + accountDir + "/";
    }

    /**
     * @param context
     * @return
     */

    private static File getAccountFile(Context context) {
        try {
            File file = new File(getDir(context) + "/" + accountFile);
            if (!file.exists()) {
                file.createNewFile();
            }
            return file;
        } catch (Exception e) {
            System.err.println("无SDCard，获取AF失败");
            e.printStackTrace();
            return new File("");
        }
    }

    /**
     * @param context
     * @return
     */
    public static List<UserInfo> getAccountFromFile(Context context) {
        File file = getAccountFile(context);
        String json = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                json += tempString;
                line++;
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e1) {
                }
            }
        }
        if (json.equals("")) {
            return null;
        } else {
            List<UserInfo> accountList = new ArrayList<UserInfo>();
            try {
                String json_decode = ZipString.zipString2Json(json);
                accountList = new Gson().fromJson(json_decode, new TypeToken<List<UserInfo>>() {
                }.getType()); //解压
            } catch (Exception e) {
                e.printStackTrace();
            }
            return accountList;
        }
    }

    public static void setAccountToFile(Context context, UserInfo uinfo) {
        List<UserInfo> accountList = getAccountFromFile(context);
        File file = getAccountFile(context);
        if (accountList == null) {
            accountList = new ArrayList<UserInfo>();
            accountList.add(uinfo);
        } else {
            boolean hasAccount = false;
            for (int i = 0; i < accountList.size(); i++) {
                if (accountList.get(i).getUname().equals(uinfo.getUname())) {
                    hasAccount = true;
                    accountList.get(i).setPassword(uinfo.getPassword());
                }
            }
            if (!hasAccount) {
                accountList.add(uinfo);
            }
        }

        try {
            Gson gson = new Gson();
            String json = gson.toJson(accountList);
            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(file.getAbsolutePath(), false);
            writer.write(ZipString.json2ZipString(json));    //压缩
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void delAccountFromFile(Context context, String account) {
        List<UserInfo> accountList = getAccountFromFile(context);
        File file = getAccountFile(context);

        if (accountList == null) {    //have no account
            return;
        }

        List<UserInfo> saveList = new ArrayList<UserInfo>();
        for (UserInfo ac : accountList) {
            if (!ac.getUname().equals(account)) {
                saveList.add(ac);
            }
        }

        try {
            Gson gson = new Gson();
            String json = gson.toJson(saveList);

            //打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(file.getAbsolutePath(), false);
            writer.write(ZipString.json2ZipString(json));    //压缩
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取sd卡路径 返回路径带"／"
     *
     * @param context
     * @return
     */
    public static String getSDPath(Context context) {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            File dirFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/");
            try {

                if (!(dirFile.exists() && !(dirFile.isDirectory()))) {
                    dirFile.mkdirs();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return dirFile.getAbsolutePath();
        }
        return null;

    }

    public static void cleanAccountInfoCache(Context context) {

        getAccountFile(context).delete();

    }

    public static ALAccountInfo getAutoLoginAccount(Context context) {
        ALAccountInfo userInfo = null;
        final String name = Util.getUsername(context);
        String password = Util.getPassword(context);
        final String pw = ZipString.zipString2Json(password);
        if (!"".equals(name) && !"".equals(password)) {
            userInfo = new ALAccountInfo(name, pw);
        } else {
            // SP没有帐号
            /**
             * 读取本地帐号
             */
            List<UserInfo> accountList = AccountTools.getAccountFromFile(context);
            if (accountList != null && accountList.size() > 0) {   // 无记录新帐号
                String a = accountList.get(accountList.size() - 1).getUname(); // 取最后一个登录用户的帐号显示
                String p = ZipString.zipString2Json(accountList.get(accountList.size() - 1).getPassword()); // 取最后一个登录帐号的密码显示
                userInfo = new ALAccountInfo(a, p);
            }
        }
        return userInfo;
    }

    /**
     * 生成一个随机的账号，以"yx_"开头，后面包含6-8位数字和小写字母的随机组合
     * @return a random account
     */
    public static String randomAccount() {
        String prefix = "yx_";
        int count = new Random().nextInt(3) + 6;
        String randomS = RandomStringUtil.random(count, ' ', 'Z' + 1, true, true);
        return prefix + StringUtils.swapUpperCase(randomS);
    }

    public static String randomPwd() {
        return RandomStringUtil.random(6, false, true);
    }
}
