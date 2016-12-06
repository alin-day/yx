package com.yxyige.sdk.bean;

import android.view.View;

public class ViewLayoutBean {

    private String mTitle;
    private View mLayout;

    public ViewLayoutBean(String title, View layout) {
        this.mLayout = layout;
        this.mTitle = title;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public View getmLayout() {
        return mLayout;
    }

    public void setmLayout(View mLayout) {
        this.mLayout = mLayout;
    }

}