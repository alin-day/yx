package com.yxyige.sdk.views;

import android.app.Activity;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ProgressBar;

import com.yxyige.sdk.core.SDKManager;
import com.yxyige.sdk.core.YXSDKListener;
import com.yxyige.sdk.utils.Util;
import com.yxyige.sdk.widget.AbstractView;
import com.yxyige.sdk.widget.BaseViewPager;

import java.util.ArrayList;

/**
 * 登录界面
 */
public class AccountView extends AbstractView {

    public static boolean hasGetUserInfo = false;
    private ProgressBar waiting;
    private LoginDialog dialog;
    private YXSDKListener listener;
    private LoginView loginView;
    private BaseViewPager viewPager;
    private ArrayList<View> guideViewList;
    private CardPageAdapter pageAdapter;
    private View left;
    private View right;
    private SDKManager sqManager;
    private boolean showReg = false;

    public AccountView(Activity activity, LoginDialog dialog, YXSDKListener listener, SDKManager manager, boolean showRegisterView) {
        super(activity);
        this.dialog = dialog;
        this.listener = listener;
        this.sqManager = manager;
        this.showReg = showRegisterView;
        initView();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    @Override
    protected View getParent() {
        return inflate(Util.getIdByName("yx_view_account", "layout", getActivity().getPackageName(), getActivity()));
    }

    private void initView() {
        hasGetUserInfo = false;
        View commonLayout = findViewById(Util.getIdByName("commonLayout", "id", getActivity().getPackageName(), getActivity()));
        if (commonLayout != null) {
            commonLayout.setVisibility(View.VISIBLE);
        }

        waiting = (ProgressBar) findViewById(Util.getIdByName("waiting", "id", getActivity().getPackageName(), getActivity()));
        waiting.setVisibility(View.GONE);
        left = findViewById(Util.getIdByName("left", "id", getActivity().getPackageName(), getActivity()));
        right = findViewById(Util.getIdByName("right", "id", getActivity().getPackageName(), getActivity()));
        loginView = new LoginView(getActivity(), dialog, listener, showReg);//Config.getProfileView(CardActivity.this);
        guideViewList = new ArrayList<View>();

        pageAdapter = new CardPageAdapter(guideViewList);
        viewPager = (BaseViewPager) findViewById(Util.getIdByName("dateViews", "id", getActivity().getPackageName(), getActivity()));
        viewPager.setAdapter(pageAdapter);
        guideViewList.add(0, loginView.getContentView());
        pageAdapter.notifyDataSetChanged();
    }

    private class CardPageAdapter extends PagerAdapter {

        private ArrayList<View> guideViewList;

        public CardPageAdapter(ArrayList<View> guideViewList) {
            this.guideViewList = guideViewList;
        }

        @Override
        public int getCount() {
            return guideViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public int getItemPosition(Object object) {

            return super.getItemPosition(object);
        }

        @Override
        public void destroyItem(View collection, int position, Object o) {

            ((ViewPager) collection).removeView(guideViewList.get(position));
        }

        @Override
        public Object instantiateItem(View view, int position) {

            View v = guideViewList.get(position);
            ((ViewPager) view).addView(v);
            return v;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {


        }

        @Override
        public Parcelable saveState() {

            return null;
        }

        @Override
        public void startUpdate(View arg0) {


        }

        @Override
        public void finishUpdate(View arg0) {

        }
    }
}
