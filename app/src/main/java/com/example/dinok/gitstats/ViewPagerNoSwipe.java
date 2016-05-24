package com.example.dinok.gitstats;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by dinok on 5/23/2016.
 * Custom ViewPager with scrolling disabled
 */
public class ViewPagerNoSwipe extends ViewPager {

    private boolean isPagingEnabled = false;

    public ViewPagerNoSwipe(Context context) {
        super(context);
    }

    public ViewPagerNoSwipe(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onInterceptTouchEvent(event);
    }

    public void setPagingEnabled(boolean b) {
        this.isPagingEnabled = b;
    }
}