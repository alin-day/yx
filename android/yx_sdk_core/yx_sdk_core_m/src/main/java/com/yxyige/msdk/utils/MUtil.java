package com.yxyige.msdk.utils;

import android.content.Context;

import com.yxyige.msdk.BaseYXMCore;
import com.yxyige.msdk.api.MultiSDKUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author feng'shanshan
 */
public class MUtil {

    public static String mapToStr(TreeMap<String, String> map) {

        if (map == null) {
            return "";
        }
        StringBuffer stb = new StringBuffer();
        Set set = map.entrySet();
        for (Iterator iter = set.iterator(); iter.hasNext(); ) {
            Map.Entry entry = (Map.Entry) iter.next();

            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            stb.append(value);
        }

        return stb.toString();

    }

    /**
     * Unicode转汉字
     *
     * @param str
     * @return
     */
    public static String encodingtoStr(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");
        }
        return str;
    }


    public static String enZip(String key, String content) {
        try {
            return EncodeUtil.encode(key, content);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String unZip(String key, String content) {
        try {
            return EncodeUtil.decode(key, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String sqEnZip(Context context, String content) {
        String key = MultiSDKUtils.getPID(context) + MultiSDKUtils.getGID(context) + ZipString.zipString2Json(MultiSDKUtils.getKey(context));
        BaseYXMCore.sendLog("zip key=" + key);
        return enZip(key, content);
    }

    public static String sqUnZip(Context context, String content) {
        String key = MultiSDKUtils.getPID(context) + MultiSDKUtils.getGID(context) + ZipString.zipString2Json(MultiSDKUtils.getKey(context));
        BaseYXMCore.sendLog("zip key=" + key);
        return unZip(key, content);
    }

}
