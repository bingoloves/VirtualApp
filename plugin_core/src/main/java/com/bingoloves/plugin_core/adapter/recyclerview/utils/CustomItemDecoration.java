package com.bingoloves.plugin_core.adapter.recyclerview.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bingoloves.plugin_core.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CustomItemDecoration extends RecyclerView.ItemDecoration {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            ItemDecoration.HORIZONTAL,
            ItemDecoration.VERTICAL
    })
    public @interface ItemDecoration{
        int HORIZONTAL= LinearLayoutManager.HORIZONTAL;
        int VERTICAL = LinearLayoutManager.VERTICAL;
    }

    private int rightInset;
    private Drawable mDivider;
    private int mOrientation;
    /**
     * 分割线缩进值
     */
    private int leftInset;
    private Paint paint;


    public CustomItemDecoration(Context context, @ItemDecoration int orientation) {
        this(context, orientation, R.drawable.split_line_linear);
    }
    public CustomItemDecoration(Context context, @ItemDecoration int orientation, int leftInset, int rightInset) {
        this(context, orientation, R.drawable.split_line_linear,leftInset,rightInset);
    }
    public CustomItemDecoration(Context context, @ItemDecoration int orientation, @DrawableRes int drawable) {
        this(context, orientation, drawable, 0);
    }

//    public CustomItemDecoration(Context context, @ItemDecoration int orientation, @DrawableRes int drawable, int inset) {
//        this(context, orientation, drawable, inset, inset);
//    }

    /**
     * @param context
     * @param orientation layout的方向
     * @param drawable    引入的drawable的ID
     * @param leftInset   左边分割线缩进值
     * @param rightInset  右边分割线缩进值
     */
    public CustomItemDecoration(Context context, @ItemDecoration int orientation, @DrawableRes int drawable, int leftInset, int rightInset) {
        mDivider = context.getResources().getDrawable(drawable);
        this.leftInset = leftInset;
        this.rightInset = rightInset;
        paint = new Paint();
        paint.setColor(context.getResources().getColor(android.R.color.white));
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        setOrientation(orientation);
    }

    public void setOrientation(int orientation) {
        if (orientation != ItemDecoration.VERTICAL && orientation != ItemDecoration.HORIZONTAL) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent) {
        if (mOrientation == ItemDecoration.VERTICAL) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    private void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        //最后一个item不画分割线
        for (int i = 0; i < childCount - 1; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            if (leftInset > 0) {
                c.drawRect(left, top, right, bottom, paint);
                if (right > 0) {
                    mDivider.setBounds(left + leftInset, top, right - rightInset, bottom);
                } else {
                    mDivider.setBounds(left + leftInset, top, right, bottom);
                }
            } else {
                if (right > 0) {
                    mDivider.setBounds(left, top, right - rightInset, bottom);
                } else {
                    mDivider.setBounds(left, top, right, bottom);
                }
            }
            mDivider.draw(c);
        }
    }

    private void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    //由于Divider也有宽高，每一个Item需要向下或者向右偏移
    @Override
    public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
        if (mOrientation == ItemDecoration.VERTICAL) {
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
        } else {
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
        }
    }
}