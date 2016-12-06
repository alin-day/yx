package com.yxyige.sdk.utils;

import android.util.Log;

public class LogUtil {

    /*
     * debug set true
     * release need set false
     */
    public static boolean isShowLog = false;


    public static void e(String msg) {

        if (isShowLog) {

            Log.e("yx", msg);
        }
    }

    public static void i(String msg) {

        if (isShowLog) {

            Log.i("yx", msg);
        }

    }

    public static void d(String msg) {
        if (isShowLog) {

            Log.d("yx", msg);
        }
    }

    public static void w(String msg) {

        if (isShowLog) {

            Log.w("yx", msg);
        }
    }

}
