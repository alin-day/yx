package com.yxyige.msdk.views;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yxyige.sdk.utils.Util;

public class YXConfirmDialog extends Dialog {

    private Context context;

    private Button cancel, confirm;//开始和停止下载，隐藏下载按钮
    private TextView msg_tv;//确认框的显示信息

    private ConfirmListener listener;//按键监听

    private String text;//确定框的内容

    public YXConfirmDialog(Context context) {
        super(context);
    }

    /**
     * @param context
     * @param theme
     * @param msg     确定框内容
     */
    public YXConfirmDialog(Context context, int theme, String msg) {
        super(context, theme);
        this.context = context;
        this.text = msg;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(Util.getIdByName("yx_confirm_dialog", "layout", context.getPackageName(), context));
        //顶栏按钮
        msg_tv = (TextView) findViewById(Util.getIdByName("message", "id", context.getPackageName(), context));
        cancel = (Button) findViewById(Util.getIdByName("cancel", "id", context.getPackageName(), context));
        confirm = (Button) findViewById(Util.getIdByName("confirm", "id", context.getPackageName(), context));

        //赋值
        msg_tv.setText(text);
        //按钮确认
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCancel();
                }
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onConfirm();
                }
            }
        });

        //设置对话框属性
        setCanceledOnTouchOutside(false);

    }

    public void setConfirmListenr(ConfirmListener listenr) {
        if (listenr != null) {
            this.listener = listenr;
        }
    }

    /**
     * 确认框按钮监听器
     *
     * @author smalli
     */
    public interface ConfirmListener {
        void onCancel();

        void onConfirm();
    }

}
