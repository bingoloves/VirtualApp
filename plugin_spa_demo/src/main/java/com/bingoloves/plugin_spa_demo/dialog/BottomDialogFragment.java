package com.bingoloves.plugin_spa_demo.dialog;

import android.content.res.Configuration;
import android.view.Gravity;
import android.view.ViewGroup;
import com.bingoloves.plugin_spa_demo.R;

/**
 * 底部DialogFragment
 */
public class BottomDialogFragment extends BaseDialogFragment {

    @Override
    public void onStart() {
        super.onStart();
        mWindow.setGravity(Gravity.BOTTOM);
        mWindow.setWindowAnimations(R.style.BottomAnimation);
        mWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, mWidthAndHeight[1] / 2);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog;
    }



    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, mWidthAndHeight[1] / 2);
    }
}
