package com.yxyige.sdk.utils;

/**
 * Created by Gemini on 16/12/4.
 */
public class StringUtils {

    public static String swapCase(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }

        char[] buffer = str.toCharArray();

        for (int i = 0; i < buffer.length; i++) {
            char ch = buffer[i];
            if (Character.isUpperCase(ch)) {
                buffer[i] = Character.toLowerCase(ch);
            } else if (Character.isTitleCase(ch)) {
                buffer[i] = Character.toLowerCase(ch);
            } else if (Character.isLowerCase(ch)) {
                buffer[i] = Character.toUpperCase(ch);
            }
        }
        return new String(buffer);
    }

    public static String swapUpperCase(String str) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }

        char[] buffer = str.toCharArray();

        for (int i = 0; i < buffer.length; i++) {
            char ch = buffer[i];
            if (Character.isUpperCase(ch)) {
                buffer[i] = Character.toLowerCase(ch);
            }
        }
        return new String(buffer);

    }

    private static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

}
