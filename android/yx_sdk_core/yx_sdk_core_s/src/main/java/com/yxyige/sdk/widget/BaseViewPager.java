package com.yxyige.sdk.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class BaseViewPager extends ViewPager {

    private boolean enabled = true;

    public BaseViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // Tell our parent to stop intercepting our events!
        if (this.enabled) {
            getParent().requestDisallowInterceptTouchEvent(true);
            return super.onInterceptTouchEvent(ev);
        }

        return false;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.enabled) {
            return super.onTouchEvent(event);
        }
        return false;
    }

    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}