package com.yxyige.sdk.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class ViewStrategy {

    public static final long ANIMATION_DURATION = 300;
    private static final long ANIMATION_DELAY = 100;
    private static final int ANIMATION_COM = 5;
    private static final int MSG_REMOVE_OUT = 1, MSG_REMOVE_IN = 2;
    private static final int MSG_ADD_OUT = 3, MSG_ADD_IN = 4;
    private static final int MSG_SWITCH_LEFT_OUT = 0x10;
    private static final int MSG_SWITCH_RIGHT_IN = 0x11;
    private static final int MSG_SWITCH_RIGHT_OUT = 0x12;
    private static final int MSG_SWITCH_LEFT_IN = 0x13;
    /**
     * A constant indicates that this view can has infinite instances in the
     * queue. This is the default count.
     *
     * @see {@link #setMaxCountInQueue(int)}.
     */
    private static final int INFINITE_COUNT = -1;
    /**
     * 动画效果开关
     */
    public static boolean ANIMATION_ENABLED = true;
    private int mCurrentTab = -1;
    private HashMap<Class<? extends AbstractView>, Integer> mCountMap;
    private HashMap<Integer, ArrayList<AbstractView>> mAbstactViewMap;
    /**
     * For forward use.
     */
    private Animation mRightIn, mLeftOut;
    /**
     * For back use.
     */
    private Animation mLeftIn, mRightOut;
    private FrameLayout mViewGroupContent;
    private boolean mSwitchOver = true;
    private Handler mSwitchHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SWITCH_LEFT_OUT:
                    AbstractView avLO = (AbstractView) msg.obj;
                    View outView = avLO.getContentView();
                    ViewGroup parent = (ViewGroup) outView.getParent();
                    if (null != parent) {
                        parent.removeView(outView);
                        avLO.onViewOut();
                    }
                    if (msg.arg1 != 0) {//clear
                        ArrayList<AbstractView> tabList = mAbstactViewMap.get(mCurrentTab);
                        ArrayList<AbstractView> tmpList = new ArrayList<AbstractView>();
                        for (int i = 1, len = tabList.size(); i < len; i++) {
                            tabList.get(i).onDestroy();
                            tmpList.add(tabList.get(i));
                        }
                        tabList.removeAll(tmpList);
                    }
                    if (!hasMessages(MSG_SWITCH_RIGHT_IN)) {
                        sendEmptyMessageDelayed(ANIMATION_COM, ANIMATION_DELAY);
                    }
                    break;
                case MSG_SWITCH_RIGHT_IN:
                    if (!hasMessages(MSG_SWITCH_LEFT_OUT)) {
                        sendEmptyMessageDelayed(ANIMATION_COM, ANIMATION_DELAY);
                    }
                    break;
                case MSG_SWITCH_RIGHT_OUT:
                    AbstractView avRO = (AbstractView) msg.obj;
                    View roView = avRO.getContentView();
                    ViewGroup roParent = (ViewGroup) roView.getParent();
                    if (null != roParent) {
                        roParent.removeView(roView);
                        avRO.onViewOut();
                    }
                    if (msg.arg1 != 0) {//clear
                        ArrayList<AbstractView> tabList = mAbstactViewMap.get(mCurrentTab);
                        ArrayList<AbstractView> tmpList = new ArrayList<AbstractView>();
                        for (int i = 1, len = tabList.size(); i < len; i++) {
                            tabList.get(i).onDestroy();
                            tmpList.add(tabList.get(i));
                        }
                        tabList.removeAll(tmpList);
                    }
                    if (!hasMessages(MSG_SWITCH_LEFT_IN)) {
                        sendEmptyMessageDelayed(ANIMATION_COM, ANIMATION_DELAY);
                    }
                    break;
                case MSG_SWITCH_LEFT_IN:
                    if (!hasMessages(MSG_SWITCH_RIGHT_OUT)) {
                        sendEmptyMessageDelayed(ANIMATION_COM, ANIMATION_DELAY);
                    }
                    break;
                case ANIMATION_COM:
                    mSwitchOver = true;
                    break;
            }
        }

    };
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            final int what = msg.what;
            if (MSG_REMOVE_OUT == what) {
                AbstractView abstractView = (AbstractView) msg.obj;
                View outView = abstractView.getContentView();

                ViewGroup parent = (ViewGroup) outView.getParent();
                if (null != parent) {
                    parent.removeView(outView);
                }
                abstractView.onViewOut();
                abstractView.onDestroy();
                if (!hasMessages(MSG_REMOVE_IN)) {
                    sendEmptyMessageDelayed(ANIMATION_COM, ANIMATION_DELAY);
                }
            } else if (MSG_REMOVE_IN == what) {
                if (!hasMessages(MSG_REMOVE_OUT)) {
                    sendEmptyMessageDelayed(ANIMATION_COM, ANIMATION_DELAY);
                }
            } else if (MSG_ADD_OUT == what) {
                AbstractView abstractView = (AbstractView) msg.obj;
                View outView = abstractView.getContentView();
                ViewGroup parent = (ViewGroup) outView.getParent();
                if (null != parent) {
                    parent.removeView(outView);
                    abstractView.onViewOut();
                }

                if (!hasMessages(MSG_ADD_IN)) {
                    sendEmptyMessageDelayed(ANIMATION_COM, ANIMATION_DELAY);
                }
            } else if (MSG_ADD_IN == what) {
                if (!hasMessages(MSG_ADD_OUT)) {
                    sendEmptyMessageDelayed(ANIMATION_COM, ANIMATION_DELAY);
                }
            } else if (what == ANIMATION_COM) {
                mSwitchOver = true;
            }
        }
    };

    public ViewStrategy(Context context, FrameLayout frameLayout) {
        mViewGroupContent = frameLayout;
        mAbstactViewMap = new HashMap<Integer, ArrayList<AbstractView>>();
        mAbstactViewMap.put(-1, new ArrayList<AbstractView>());
        mCountMap = new HashMap<Class<? extends AbstractView>, Integer>();
    }

    public static long getAnimationDuration() {
        return ANIMATION_ENABLED ? ANIMATION_DURATION : 0;
    }

    public ViewGroup getContentFrame() {
        return mViewGroupContent;
    }

    /**
     * 增加Tab。默认Tab = -1
     *
     * @param tab
     * @author William.cheng
     * @version 创建时间：2011-12-21 下午7:11:14
     */
    public final void addTab(int tab) {
        if (tab == -1) {
            throw new IllegalArgumentException(
                    "-1 is the default tab index, choose another.");
        }
        final HashMap<Integer, ArrayList<AbstractView>> map = mAbstactViewMap;
        if (map.containsKey(tab)) {
            throw new IllegalArgumentException("tab " + tab
                    + " already exists.");
        }
        map.put(tab, new ArrayList<AbstractView>());
    }

    /**
     * 取Tab数量
     *
     * @param tab
     * @return
     * @author William.cheng
     * @version 创建时间：2011-12-21 下午7:11:37
     */
    public final int getTabViewCount(int tab) {
        return mAbstactViewMap.get(tab).size();
    }

    public int getTabViewCount() {
        return getTabViewCount(mCurrentTab);
    }

    /**
     * 取tab中viewClass类的第一个实例
     *
     * @param tab
     * @param viewClass
     * @return List中的第一个实例
     * @author William.cheng
     * @version 创建时间：2011-12-21 下午7:12:44
     */
    public final AbstractView getView(int tab,
                                      Class<? extends AbstractView> viewClass) {
        List<AbstractView> list = mAbstactViewMap.get(tab);
        for (AbstractView abstractView : list) {
            if (abstractView.getClass() == viewClass) {
                return abstractView;
            }
        }
        return null;
    }

    public final AbstractView getView(Class<? extends AbstractView> viewClass) {
        return getView(mCurrentTab, viewClass);
    }

    /**
     * 取tab中viewClass类的所有实例
     *
     * @param viewClass
     * @return
     * @author William.cheng
     * @version 创建时间：2011-12-21 下午7:14:57
     */
    public final ArrayList<AbstractView> getAllViews(
            Class<? extends AbstractView> viewClass) {
        synchronized (mAbstactViewMap) {
            ArrayList<AbstractView> list = new ArrayList<AbstractView>();
            HashMap<Integer, ArrayList<AbstractView>> map = mAbstactViewMap;
            Set<Integer> set = map.keySet();
            for (Integer key : set) {
                ArrayList<AbstractView> viewList = map.get(key);
                int size = viewList.size();
                for (int i = 0; i < size; i++) {
                    AbstractView abstractView = viewList.get(i);
                    if (abstractView.getClass() == viewClass) {
                        list.add(abstractView);
                    }
                }
            }
            return list;
        }
    }

    /**
     * 切换Tab.如果view为null，切换到当前Tab，否则增加view到Tab中，并切换到Tab
     *
     * @param targetTab    目标Tab
     * @param view         目标view
     * @param hasAnimation 是否要动画效果。在动画效果开关{@link #ANIMATION_ENABLED}开启时才生效
     * @return
     * @author William.cheng
     * @version 创建时间：2011-12-21 上午9:26:10
     */
    public final boolean switchToTab(final int targetTab, final AbstractView view, boolean hasAnimation) {
        if (!isAnimationOver()) {
            return false;
        }
        final int currTab = mCurrentTab;
        if (currTab == targetTab) {
            return false;
        }
        final HashMap<Integer, ArrayList<AbstractView>> map = mAbstactViewMap;
        if (!map.containsKey(targetTab)) {
            throw new IllegalArgumentException("tab " + targetTab + " does not exist.");
        }
        ArrayList<AbstractView> targetViews = map.get(targetTab);
        final int size = targetViews.size();
        final AbstractView targetView;
        AbstractView tempView = size > 0 ? targetViews.get(size - 1) : null;
        if (null != view) {
            if (tempView != null) {
                throw new NullPointerException("tab " + targetTab + " is not empty");
            }
            targetView = view;
            targetViews.add(targetView);
        } else {
            targetView = tempView;
        }
        if (null == targetView) {
            throw new NullPointerException("view is null.");
        }
        ArrayList<AbstractView> currTabViews = map.get(currTab);
        final int currSize = currTabViews.size();
        final AbstractView currView = currSize > 0 ? currTabViews
                .get(currSize - 1) : null;

        mCurrentTab = targetTab;
        if (currView == targetView) {
            return true;
        }
        if (!ANIMATION_ENABLED || !hasAnimation) {
            getContentFrame().removeAllViews();
            if (currView != null) currView.onViewOut();
            getContentFrame().addView(targetView.getContentView());
            targetView.onViewIn();
            return true;
        }

        switchAnimation(targetTab, currTab, targetView, currView, 0);
        return true;
    }

    /**
     * 切换Tab
     *
     * @param targetTab 目标Tab
     * @return
     * @author William.cheng
     * @version 创建时间：2011-12-21 下午12:18:43
     * @see #switchToTab(int, AbstractView)
     */
    public final boolean switchToTab(final int targetTab) {
        return switchToTab(targetTab, null, true);
    }

    /**
     * 清除tab的views
     *
     * @param tab
     * @author William.cheng
     * @version 创建时间：2011-12-21 下午4:25:35
     */
    public void clearTab(int tab) {
        ArrayList<AbstractView> m = mAbstactViewMap.get(tab);
        if (null != m) {
            m.clear();
        }
    }

    /**
     * 动画是否停止
     */
    public final boolean isAnimationOver() {
        return mSwitchOver;
    }

    /**
     * 取当前Tab
     *
     * @return
     * @author William.cheng
     * @version 创建时间：2011-12-21 下午7:16:49
     */
    public final int getCurrentTab() {
        return mCurrentTab;
    }

    /**
     * 取当前View
     *
     * @return
     * @author William.cheng
     * @version 创建时间：2011-12-21 下午4:25:15
     */
    public final AbstractView getCurrentView() {
        int tab = mCurrentTab;
        ArrayList<AbstractView> list = mAbstactViewMap.get(tab);
        if (null == list) {
            return null;
        }
        int size = list.size();
        if (size <= 0) {
            return null;
        }
        return list.get(size - 1);
    }

    private Animation checkRightInAnimation() {
        Animation rightIn = mRightIn;
        if (null == rightIn) {
            rightIn = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
                    1.0f, Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f,
                    Animation.ABSOLUTE, 0.0f);
            rightIn.setDuration(ANIMATION_DURATION);
            mRightIn = rightIn;
        }
        return rightIn;
    }

    private Animation checkLeftOutAnimation() {
        Animation leftOut = mLeftOut;
        if (null == leftOut) {
            leftOut = new TranslateAnimation(Animation.ABSOLUTE, 0.0f,
                    Animation.RELATIVE_TO_PARENT, -1.0f, Animation.ABSOLUTE,
                    0.0f, Animation.ABSOLUTE, 0.0f);
            leftOut.setDuration(ANIMATION_DURATION);
            mLeftOut = leftOut;
        }
        return leftOut;
    }

    private Animation checkLeftInAnimation() {
        Animation leftIn = mLeftIn;
        if (null == leftIn) {
            leftIn = new TranslateAnimation(Animation.RELATIVE_TO_PARENT,
                    -1.0f, Animation.ABSOLUTE, 0.0f, Animation.ABSOLUTE, 0.0f,
                    Animation.ABSOLUTE, 0.0f);
            leftIn.setDuration(ANIMATION_DURATION);
            mLeftIn = leftIn;
        }
        return leftIn;
    }

    private Animation checkRightOutAnimation() {
        Animation rightOut = mRightOut;
        if (null == rightOut) {
            rightOut = new TranslateAnimation(Animation.ABSOLUTE, 0.0f,
                    Animation.RELATIVE_TO_PARENT, 1.0f, Animation.ABSOLUTE,
                    0.0f, Animation.ABSOLUTE, 0.0f);
            rightOut.setDuration(ANIMATION_DURATION);
            mRightOut = rightOut;
        }
        return rightOut;
    }

    /**
     * 将targetView放入targetTab中
     *
     * @param targetTab
     * @param targetView
     * @return 成功返回true，否则返回false
     * @author William.cheng
     * @version 创建时间：2011-12-20 下午4:32:22
     * @see {@link #backToPreviousView()}
     */
    public final boolean bringViewIn(int targetTab, AbstractView targetView, boolean hasAnimation) {
        if (!isAnimationOver()) {
            return false;
        }
        if (!mAbstactViewMap.containsKey(targetTab)) {
            throw new IllegalArgumentException("tab " + targetTab + " does not exist.");
        }

        int currentTab = mCurrentTab;
        AbstractView currentView = null;
        final ArrayList<AbstractView> viewList = mAbstactViewMap.get(currentTab);
        int size = viewList.size();
        if (size > 0) {
            currentView = viewList.get(size - 1);
        }
        final ArrayList<AbstractView> targetViews = mAbstactViewMap.get(targetTab);
        int targetSize = targetViews.size();

        final AbstractView outView = currentView;
        final AbstractView inView = targetView;
        if (currentView == targetView) {
            return false;
        }

        final Class<? extends AbstractView> klass = inView.getClass();
        final int count = getMaxCountInQueue(klass);
        if (INFINITE_COUNT == count) {
            targetViews.add(inView);
        } else {
            int sum = 0;
            for (int i = 0; i < targetSize; i++) {
                AbstractView av = targetViews.get(i);
                if (av.getClass() == klass) {
                    sum++;
                }
            }
            if (sum <= count - 1) {
                targetViews.add(inView);
            } else {
                // Find the last AbstractView who's object instance
                // count is limited.
                int i = targetSize - 1;
                for (; i >= 0; i--) {
                    AbstractView av = targetViews.get(i);
                    if (av.getClass() == klass) {
                        break;
                    }
                }
                // Well, it's found! remove it, and add the new one.
                targetViews.remove(i);
                targetViews.add(inView);
            }
        }

        mCurrentTab = targetTab;
        if (!ANIMATION_ENABLED || !hasAnimation) {
            getContentFrame().removeAllViews();
            if (outView != null) outView.onViewOut();
            getContentFrame().addView(inView.getContentView());
            inView.onViewIn();
            return true;
        }

        mSwitchOver = false;
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (null != outView) {
                    Animation leftOut = checkLeftOutAnimation();
                    long duration = leftOut.getDuration();
                    outView.getContentView().startAnimation(
                            checkLeftOutAnimation());
                    Message outMessage = Message.obtain();
                    outMessage.what = MSG_ADD_OUT;
                    outMessage.obj = outView;

                    mHandler.sendMessageDelayed(outMessage, duration);
                }
                if (null != inView) {
                    if (null == inView.getContentView().getParent()) {
                        getContentFrame().addView(inView.getContentView());
                        inView.onViewIn();
                    }
                    Animation animation = checkRightInAnimation();
                    long duration = animation.getDuration();

                    Message inMessage = Message.obtain();
                    inMessage.what = MSG_ADD_IN;
                    inMessage.obj = inView;
                    inView.getContentView().startAnimation(
                            checkRightInAnimation());
                    mHandler.sendMessageDelayed(inMessage, duration);
                }
            }
        });
        return true;
    }

    /**
     * 将targetView放入当前Tab中
     *
     * @param targetView
     * @return 成功返回true，否则返回false
     * @author William.cheng
     * @version 创建时间：2011-12-20 下午4:32:22
     * @see {@link #backToPreviousView()}
     */
    public final boolean bringViewIn(AbstractView targetView) {
        return bringViewIn(mCurrentTab, targetView, true);
    }

    public final boolean bringViewIn(AbstractView targetView, boolean hasAnimation) {
        return bringViewIn(mCurrentTab, targetView, hasAnimation);
    }

    public final boolean backToPreviousView() {
        return backToPreviousView(true);
    }

    /**
     * 返回当前Tab的前一个View
     *
     * @return 当前View是最后一个View时返回false，否则返回true
     * @author William.cheng
     * @version 创建时间：2011-12-20 下午4:17:42
     * @see {@link #bringViewIn(AbstractView)}
     */
    public final boolean backToPreviousView(boolean hasAnimation) {
        if (!isAnimationOver()) {
            return true;
        }
        int tab = mCurrentTab;
        final ArrayList<AbstractView> viewList = mAbstactViewMap.get(tab);
        int size = viewList.size();
        if (size <= 1) {
            return false;
        }

        final AbstractView outView = viewList.get(size - 1);
        final AbstractView inView = viewList.get(size - 2);

        viewList.remove(size - 1);
        if (!ANIMATION_ENABLED || !hasAnimation) {
            getContentFrame().removeAllViews();
            outView.onViewOut();
            outView.onDestroy();
            getContentFrame().addView(inView.getContentView());
            inView.onViewIn();
            return true;
        }

        mSwitchOver = false;
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (null != outView) {
                    Animation leftOut = checkLeftOutAnimation();
                    long duration = leftOut.getDuration();
                    outView.getContentView().startAnimation(checkRightOutAnimation());
                    Message outMessage = Message.obtain();
                    outMessage.what = MSG_REMOVE_OUT;
                    outMessage.obj = outView;

                    mHandler.sendMessageDelayed(outMessage, duration);
                }
                if (null != inView) {
                    if (null == inView.getContentView().getParent()) {
                        getContentFrame().addView(inView.getContentView());
                        inView.onViewIn();
                    }
                    Animation animation = checkRightInAnimation();
                    long duration = animation.getDuration();

                    Message inMessage = Message.obtain();
                    inMessage.what = MSG_REMOVE_IN;
                    inMessage.obj = inView;
                    inView.getContentView().startAnimation(
                            checkLeftInAnimation());
                    mHandler.sendMessageDelayed(inMessage, duration);
                }
            }
        });

        return true;
    }

    /**
     * 显示当前tab中最底层的view，并将其他view清除
     *
     * @param tab
     * @author William.cheng
     * @version 创建时间：2011-12-21 下午4:49:33
     */
    public final void bringBottomToFront() {
        bringBottomToFront(mCurrentTab, true);
    }

    /**
     * 显示tab中最底层的view，并将其他view清除
     *
     * @param targetTab
     * @author William.cheng
     * @version 创建时间：2011-12-21 下午4:49:33
     */
    public final boolean bringBottomToFront(int targetTab, boolean hasAnimation) {
        if (!isAnimationOver()) {
            return false;
        }
        HashMap<Integer, ArrayList<AbstractView>> map = mAbstactViewMap;
        if (!map.containsKey(targetTab)) {
            return false;
        }
        ArrayList<AbstractView> tabList = map.get(targetTab);
        int size = tabList.size();
        if (size < 1 || (size == 1 && targetTab == mCurrentTab)) {
            return false;
        }
        int currTab = mCurrentTab;
        final AbstractView targetView = tabList.get(0);
        ArrayList<AbstractView> currTabViews = map.get(currTab);
        final int currSize = currTabViews.size();
        final AbstractView currView = currSize > 0 ? currTabViews
                .get(currSize - 1) : null;

        mCurrentTab = targetTab;
        if (targetView == currView) {
            return true;
        }
        if (!ANIMATION_ENABLED || !hasAnimation) {
            ViewGroup group = getContentFrame();
            group.removeAllViews();
            currView.onViewOut();
            ArrayList<AbstractView> tmpList = new ArrayList<AbstractView>();
            for (int i = 1, len = tabList.size(); i < len; i++) {
                tabList.get(i).onDestroy();
                tmpList.add(tabList.get(i));
            }
            tabList.removeAll(tmpList);
            group.addView(targetView.getContentView());
            targetView.onViewIn();
            return true;
        }

        switchAnimation(targetTab, currTab, targetView, currView, 1);
        return true;
    }

    private void switchAnimation(final int targetTab, final int currTab, final AbstractView targetView, final AbstractView currView, final int clear) {
        mSwitchOver = false;
        if (targetTab > currTab) {
            mSwitchHandler.post(new Runnable() {

                @Override
                public void run() {
                    if (null != currView) {
                        Animation outAnimation = checkLeftOutAnimation();
                        long duration = outAnimation.getDuration();
                        currView.getContentView().startAnimation(outAnimation);
                        Message msg = Message.obtain();
                        msg.what = MSG_SWITCH_LEFT_OUT;
                        msg.obj = currView;
                        msg.arg1 = clear;
                        mSwitchHandler.sendMessageDelayed(msg, duration);
                    }
                    final View tView = targetView.getContentView();
                    if (null == tView.getParent()) {
                        getContentFrame().addView(tView);
                        targetView.onViewIn();
                    }

                    Animation inAnimation = checkRightInAnimation();
                    long duration = inAnimation.getDuration();
                    tView.startAnimation(inAnimation);
                    Message msg = Message.obtain();
                    msg.what = MSG_SWITCH_RIGHT_IN;
                    msg.arg1 = targetTab;
                    msg.obj = targetView;
                    mSwitchHandler.sendMessageDelayed(msg, duration);
                }
            });
        } else {
            mSwitchHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (null != currView) {
                        Animation outAnimation = checkRightOutAnimation();
                        long duration = outAnimation.getDuration();
                        currView.getContentView().startAnimation(outAnimation);
                        Message msg = Message.obtain();
                        msg.what = MSG_SWITCH_RIGHT_OUT;
                        msg.obj = currView;
                        msg.arg1 = clear;
                        mSwitchHandler.sendMessageDelayed(msg, duration);
                    }

                    final View tView = targetView.getContentView();
                    if (null == tView.getParent()) {
                        getContentFrame().addView(tView);
                        targetView.onViewIn();
                    }
                    Animation inAnimation = checkLeftInAnimation();
                    long duration = inAnimation.getDuration();
                    tView.startAnimation(inAnimation);
                    Message msg = Message.obtain();
                    msg.what = MSG_SWITCH_LEFT_IN;
                    msg.arg1 = targetTab;
                    msg.obj = targetView;
                    mSwitchHandler.sendMessageDelayed(msg, duration);
                }
            });
        }
    }

    /**
     * 设置klass类在默认Tab中实例的个数。默认为无限个
     *
     * @param klass
     * @param max   最大实例个数
     * @author William.cheng
     * @version 创建时间：2011-12-21 下午3:18:32
     */
    public final void setMaxCountInQueue(Class<? extends AbstractView> klass,
                                         int max) {
        if (max <= 0) {
            throw new IllegalArgumentException("max instance count " + max
                    + " is invalid");
        }
        mCountMap.put(klass, max);
    }

    /**
     * 获取klass类的最大个数
     *
     * @param klass
     * @return -1为无限制
     * @author William.cheng
     * @version 创建时间：2011-12-21 下午4:21:25
     */
    public final int getMaxCountInQueue(Class<? extends AbstractView> klass) {
        final HashMap<Class<? extends AbstractView>, Integer> map = mCountMap;
        if (!map.containsKey(klass)) {
            return INFINITE_COUNT;
        }
        return mCountMap.get(klass);
    }

    /**
     * 清除所有View
     *
     * @author William.cheng
     * @version 创建时间：2011-12-21 下午4:22:12
     */
    public void reset() {
        synchronized (mAbstactViewMap) {
            HashMap<Integer, ArrayList<AbstractView>> map = mAbstactViewMap;
            mCountMap.clear();
            Set<Integer> set = map.keySet();
            for (Integer key : set) {
                ArrayList<AbstractView> viewList = map.get(key);
                viewList.clear();
            }
            getContentFrame().removeAllViews();
            mCurrentTab = -1;
        }
    }
}
