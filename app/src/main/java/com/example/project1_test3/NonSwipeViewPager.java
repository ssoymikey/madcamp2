package com.example.project1_test3;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.core.view.MotionEventCompat;
import androidx.viewpager.widget.ViewPager;

public class NonSwipeViewPager extends ViewPager {
    private boolean enable = true;

    public NonSwipeViewPager(Context context) {
        super(context);
    }

    public NonSwipeViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (enable) return super.onInterceptTouchEvent(ev);
        else{
            if (ev.getAction() == MotionEvent.ACTION_MOVE);
            else if(super.onInterceptTouchEvent(ev)) super.onTouchEvent(ev);
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (enable) return super.onTouchEvent(ev);
        else return ev.getAction() != MotionEvent.ACTION_MOVE && super.onTouchEvent(ev);
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}