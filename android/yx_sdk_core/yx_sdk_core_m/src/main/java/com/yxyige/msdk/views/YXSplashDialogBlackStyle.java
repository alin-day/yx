package com.yxyige.msdk.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;


public class YXSplashDialogBlackStyle extends Dialog {

    private Context mcontext;
    private int bgColor = android.R.color.black;

    public YXSplashDialogBlackStyle(Context context, int time) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.mcontext = context;
        this.bgColor = time;
    }

    public YXSplashDialogBlackStyle(Context context) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.mcontext = context;
    }

    public void setBgColor(int color) {
        this.bgColor = color;
    }


    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //创建一个父View
        LinearLayout fatherView = new LinearLayout(mcontext);
        fatherView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
        setContentView(fatherView);
    }
    
}
