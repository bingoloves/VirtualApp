package com.bingoloves.plugin_spa_demo.dialog;

import android.content.res.Configuration;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.bingoloves.plugin_core.utils.log.LogUtils;
import com.bingoloves.plugin_core.widget.drag.DragLayout;
import com.bingoloves.plugin_spa_demo.R;
import com.gyf.immersionbar.ImmersionBar;

import butterknife.BindView;

/**
 * 顶部DialogFragment
 */
public class TopDialogFragment extends BaseDialogFragment {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    protected DragTransformer mDragTransformer = null;
    @Override
    public void onStart() {
        super.onStart();
        mWindow.setGravity(Gravity.TOP);
        mWindow.setWindowAnimations(R.style.TopAnimation);
        mWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, mWidthAndHeight[1] / 2);
        DragLayout dragLayout = mRootView.findViewById(R.id.dragLayout);
        dragLayout.setDragStyle(DragLayout.DragStyle.Top);
        dragLayout.setOnDragListener(new DragLayout.OnDragListener() {
            @Override
            public void onDragStart() {
                if (mDragTransformer == null) {
                    mDragTransformer = (content, background, f) ->{
                        background.setAlpha(1F - f);
                    };
                }
            }

            @Override
            public void onDragging(float f) {
                if (mDragTransformer != null) {
                    LogUtils.e("f = "+f);
                    mDragTransformer.onDragging(dragLayout,mRootView, f);
                }
            }

            @Override
            public void onDragEnd() {
                // 动画执行结束后不能直接removeView，要在下一个dispatchDraw周期移除
                // 否则会崩溃，因为viewGroup的childCount没有来得及-1，获取到的view为空
                dragLayout.setVisibility(View.INVISIBLE);
                dragLayout.post(() -> dismiss());
            }
        });
    }
    public interface DragTransformer {
        void onDragging(@NonNull View content,
                        @NonNull View background,
                        @FloatRange(from = 0F, to = 1F) float f);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.dialog_drag;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        ImmersionBar.with(this)
                .titleBar(toolbar)
                .statusBarDarkFont(true)
                .navigationBarWithKitkatEnable(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                .init();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, mWidthAndHeight[1] / 2);
        ImmersionBar.with(this)
                .statusBarDarkFont(false)
                .navigationBarWithKitkatEnable(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
                .init();
    }
}
