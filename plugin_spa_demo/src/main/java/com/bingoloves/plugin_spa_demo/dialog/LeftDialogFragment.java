package com.bingoloves.plugin_spa_demo.dialog;

import android.content.res.Configuration;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.ViewGroup;

import com.bingoloves.plugin_spa_demo.R;
import com.bingoloves.plugin_spa_demo.base.BaseDialogFragment;
import com.gyf.immersionbar.ImmersionBar;

import butterknife.BindView;

/**
 * 左边DialogFragment
 */
public class LeftDialogFragment extends BaseDialogFragment {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    public void onStart() {
        super.onStart();
        mWindow.setGravity(Gravity.TOP | Gravity.START);
        mWindow.setWindowAnimations(R.style.LeftAnimation);
        mWindow.setLayout(mWidthAndHeight[0] / 2, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        ImmersionBar.with(this).titleBar(toolbar)
                .keyboardEnable(true)
                .statusBarDarkFont(true)
                .navigationBarWithKitkatEnable(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                .init();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mWindow.setLayout(mWidthAndHeight[0] / 2, ViewGroup.LayoutParams.MATCH_PARENT);
        ImmersionBar.with(this)
                .statusBarDarkFont(true)
                .navigationBarWithKitkatEnable(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
                .init();
    }
}