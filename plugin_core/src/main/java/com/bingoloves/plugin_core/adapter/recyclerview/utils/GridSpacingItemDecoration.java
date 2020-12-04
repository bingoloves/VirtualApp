package com.bingoloves.plugin_core.adapter.recyclerview.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2020/9/15 0015.
 */

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int width;
    private int height;
    private boolean includeEdge;

    public GridSpacingItemDecoration(int spanCount, int width, int height, boolean includeEdge) {
        this.spanCount = spanCount;
        this.width = width;
        this.height = height;
        this.includeEdge = includeEdge;
    }

    /**
     * 这种是等宽高
     * @param spanCount
     * @param wh
     * @param includeEdge
     */
    public GridSpacingItemDecoration(int spanCount, int wh, boolean includeEdge) {
        this.spanCount = spanCount;
        this.width = wh;
        this.height = wh;
        this.includeEdge = includeEdge;
    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position
        int column = position % spanCount; // item column
        if (includeEdge) {
            outRect.left = width - column * width / spanCount; // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * width / spanCount; // (column + 1) * ((1f / spanCount) * spacing)
            if (position < spanCount) { // top edge
                outRect.top = height;
            }
            outRect.bottom = height; // item bottom
        } else {
            outRect.left = column * width / spanCount; // column * ((1f / spanCount) * spacing)
            outRect.right = width - (column + 1) * width / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            if (position >= spanCount) {
                outRect.top = height; // item top
            }
        }
    }
}