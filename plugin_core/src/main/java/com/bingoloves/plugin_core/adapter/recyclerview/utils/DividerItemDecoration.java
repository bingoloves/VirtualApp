package com.bingoloves.plugin_core.adapter.recyclerview.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * This class is from the v7 samples of the Android SDK. It's not by me!
 * <p/>
 * See the license above for details.
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    private Paint mPaint;
    private Drawable mDivider;
    private int mDividerHeight;
    private int mOrientation;
    /**
     *  系统默认的分割线
     * */
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    /**
     *  设置默认的分割线
     *  @param orientation 指代分割线的方向
     * */
    public DividerItemDecoration(Context context, int orientation) {
        this.mDividerHeight = 2;
        if(orientation != 1 && orientation != 0) {
            throw new IllegalArgumentException("请输入正确的参数！");
        } else {
            this.mOrientation = orientation;
            TypedArray a = context.obtainStyledAttributes(ATTRS);
            this.mDivider = a.getDrawable(0);
            a.recycle();
        }
    }

    /**
     *  设置图片资源为分割线
     * */
    public DividerItemDecoration(Context context, int orientation, int drawableId) {
        this(context, orientation);
        this.mDivider = ContextCompat.getDrawable(context, drawableId);
        this.mDividerHeight = this.mDivider.getIntrinsicHeight();
    }

    /**
     *  设置线条颜色和高度给分割线
     * */
    public DividerItemDecoration(Context context, int orientation, int dividerHeight, int dividerColor) {
        this(context, orientation);
        this.mDividerHeight = dividerHeight;
        this.mPaint = new Paint(1);
        this.mPaint.setColor(dividerColor);
        this.mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(0, 0, 0, this.mDividerHeight);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if(this.mOrientation == 1) {
            this.drawVertical(c, parent);
        } else {
            this.drawHorizontal(c, parent);
        }
    }

    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        canvas.save();
        final  int left;
        final int right;
        // 判断RecyclerView是否要裁剪padding值
        if (parent.getClipToPadding()){
            // 需要裁剪那么就进行裁剪
            left = parent.getPaddingLeft();
            right = parent.getWidth()-parent.getPaddingRight();
            // 裁剪rv可视区域,  限制视图在该区域内是可见的，很重要的，这里
            canvas.clipRect(left,parent.getPaddingTop(),right,parent.getHeight()-parent.getPaddingBottom());
        }else {
            //  不裁剪则宽贼为rv的宽
            left = 0;
            right = parent.getWidth();
        }

        int childSize = parent.getChildCount();
        for(int i = 0; i < childSize; ++i) {
            View child = parent.getChildAt(i);
            // 获得item的信息包
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)child.getLayoutParams();
            // 分割线顶部 = item的底部 + item到底部的距离 + 动画偏移
            int top = child.getBottom() + layoutParams.bottomMargin + Math.round(ViewCompat.getTranslationY(child));
            // 分割线底部 =  分割线底部 + 高度
            int bottom = top + this.mDividerHeight;
            if(this.mDivider != null) {
                this.mDivider.setBounds(left, top, right, bottom);
                this.mDivider.draw(canvas);
            }

            if(this.mPaint != null) {
                canvas.drawRect((float)left, (float)top, (float)right, (float)bottom, this.mPaint);
            }
        }
        canvas.restore();
    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        canvas.save();
        final int top;
        final int bottom;
        if (parent.getClipToPadding()){
            top = parent.getPaddingTop();
            bottom = parent.getHeight() - parent.getPaddingBottom();
            canvas.clipRect(parent.getPaddingLeft(),top,parent.getWidth()-parent.getPaddingRight(),bottom);
        }else {
            top  = 0;
            bottom = parent.getHeight();
        }
        int childSize = parent.getChildCount();
        for(int i = 0; i < childSize; ++i) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)child.getLayoutParams();
            int left = child.getRight() + layoutParams.rightMargin + Math.round(ViewCompat.getTranslationX(child));
            int right = left + this.mDividerHeight;
            if(this.mDivider != null) {
                this.mDivider.setBounds(left, top, right, bottom);
                this.mDivider.draw(canvas);
            }

            if(this.mPaint != null) {
                canvas.drawRect((float)left, (float)top, (float)right, (float)bottom, this.mPaint);
            }
        }
        canvas.restore();
    }
}