package com.yxyige.msdk.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

public class ViewUtils {


    public static View inflate(Activity activity, int layout) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(layout, null);
        return view;
    }

    public static View inflate(Context context, int layout) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View view = inflater.inflate(layout, null);
        return view;
    }


    public static void showToast(Context context, String words) {

        Toast.makeText(context, words, Toast.LENGTH_SHORT).show();
    }


}
