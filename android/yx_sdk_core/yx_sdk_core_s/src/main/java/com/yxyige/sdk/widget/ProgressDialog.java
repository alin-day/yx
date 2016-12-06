package com.yxyige.sdk.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yxyige.sdk.utils.Util;

public class ProgressDialog extends Dialog {
    TextView textView;
    private CharSequence mMessage;
    private Context context;

    public ProgressDialog(Context context) {
        this(context, Util.getIdByName("progressDialog", "style", context.getPackageName(), context));
        this.context = context;
    }

    ProgressDialog(Context context, int theme) {
        super(context, theme);
        this.context = context;
    }

    ProgressDialog(Context context, boolean cancelable,
                   OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }

    public static ProgressDialog show(Context context, CharSequence msg) {
        return show(context, msg, false);
    }

    public static ProgressDialog show(Context context, int id) {
        return show(context, context.getResources().getString(id), false);
    }

    public static ProgressDialog show(Context context, CharSequence msg, boolean cancelable) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage(msg);
        dialog.setCancelable(cancelable);
        dialog.show();
        return dialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setAttributes(p);
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;
//        if (Util.getIsSpecialSDK(context)) {
        //简版的dialog
        view = inflater.inflate(Util.getIdByName("yx_progress_dialog_simple", "layout", context.getPackageName(), context), null);
        textView = (TextView) view.findViewById(Util.getIdByName("msg", "id", context.getPackageName(), context));
//		}else {
//			//37原版Dialog
//			 view = inflater.inflate(Util.getIdByName("yx_progress_dialog", "layout", context.getPackageName(),context), null);
//			textView = (TextView) view.findViewById(Util.getIdByName("msg", "id", context.getPackageName(),context));
//		}

        setContentView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    public void setMessage(CharSequence msg) {
        mMessage = msg;

        if (!TextUtils.isEmpty(mMessage) && textView != null) {
            textView.setText(mMessage);
        }

    }

}
