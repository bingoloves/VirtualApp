package com.bingoloves.plugin_core.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by bingo on 2020/9/23.
 * 可设置是否滑动的ViewPager
 */

public class CustomViewPager extends ViewPager {

    private boolean isCanScroll = true;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isCanScroll) {
            //允许滑动则应该调用父类的方法
            return super.onTouchEvent(ev);
        } else {
            //禁止滑动则不做任何操作，直接返回true即可
            return true;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (isCanScroll){
            return super.onInterceptTouchEvent(arg0);
        } else {
            return false;
        }
    }

    /**
     * 设置是否允许滑动，true是可以滑动，false是禁止滑动
     * @param isCanScroll
     */
    public void setIsCanScroll(boolean isCanScroll) {
        this.isCanScroll = isCanScroll;
    }
}