package com.bingoloves.plugin_spa_demo.fragment;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bingoloves.plugin_core.widget.CustomToolbar;
import com.bingoloves.plugin_spa_demo.R;
import com.bingoloves.plugin_spa_demo.base.BaseFragment;
import com.gyf.immersionbar.ImmersionBar;

import butterknife.BindView;

/**
 * Created by bingo on 2020/11/24.
 *
 * @Author: bingo
 * @Email: 657952166@qq.com
 * @Description: 类作用描述
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/11/24
 */

public class MineFragment extends BaseFragment {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_bar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.text)
    TextView textView;
    @BindView(R.id.mIv)
    ImageView mIv;
    @BindView(R.id.playButton)
    RelativeLayout playButton;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initView() {
        ImmersionBar.with(this).statusBarDarkFont(true).titleBar(toolbar).init();
        if(getActivity() instanceof AppCompatActivity){
            AppCompatActivity activity = (AppCompatActivity)getActivity();
            activity.setSupportActionBar(toolbar);
            ActionBar actionBar = activity.getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
        textView.setText("关于Snackbar在4.4和emui3.1上高度显示不准确的问题是由于沉浸式使用了系统的" +
                "WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS或者WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION" +
                "属性造成的，目前尚不知有什么解决办法");
        fab.setOnClickListener(view -> Snackbar.make(view, "我是Snackbar", Snackbar.LENGTH_LONG).show());
        //toolbar返回按钮监听
        toolbar.setNavigationOnClickListener(v -> getActivity().finish());

        state = CollapsingToolbarLayoutState.EXPANDED;
        mAppBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (verticalOffset == 0) {
                if (state != CollapsingToolbarLayoutState.EXPANDED) {
                    state = CollapsingToolbarLayoutState.EXPANDED;//修改状态标记为展开
                    collapsingToolbarLayout.setTitle("EXPANDED");//设置title为EXPANDED
                }
            } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                if (state != CollapsingToolbarLayoutState.COLLAPSED) {
                    collapsingToolbarLayout.setTitle("");//设置title不显示
                    playButton.setVisibility(View.VISIBLE);//隐藏播放按钮
                    state = CollapsingToolbarLayoutState.COLLAPSED;//修改状态标记为折叠
                }
            } else {
                if (state != CollapsingToolbarLayoutState.INTERNEDIATE) {
                    if(state == CollapsingToolbarLayoutState.COLLAPSED){
                        playButton.setVisibility(View.GONE);//由折叠变为中间状态时隐藏播放按钮
                    }
                    collapsingToolbarLayout.setTitle("INTERNEDIATE");//设置title为INTERNEDIATE
                    state = CollapsingToolbarLayoutState.INTERNEDIATE;//修改状态标记为中间
                }
            }
        });
    }

    /**
     * 展开状态的标记
     */
    private CollapsingToolbarLayoutState state;
    private enum CollapsingToolbarLayoutState {
        EXPANDED,
        COLLAPSED,
        INTERNEDIATE
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }
}
