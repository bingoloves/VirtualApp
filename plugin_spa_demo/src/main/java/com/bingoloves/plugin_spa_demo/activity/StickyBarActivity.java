package com.bingoloves.plugin_spa_demo.activity;

import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.bingoloves.plugin_core.widget.CustomToolbar;
import com.bingoloves.plugin_core.widget.CustomViewPager;
import com.bingoloves.plugin_spa_demo.R;
import com.bingoloves.plugin_spa_demo.fragment.FunctionsFragment;
import com.bingoloves.plugin_spa_demo.fragment.HomeFragment;
import com.bingoloves.plugin_spa_demo.fragment.MineFragment;
import com.bingoloves.plugin_spa_demo.fragment.TabFragment;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.gyf.immersionbar.ImmersionBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.cqs.im.base.BaseActivity;

/**
 * Created by bingo on 2020/12/7.
 *
 * @Author: bingo
 * @Email: 657952166@qq.com
 * @Description: 类作用描述
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/12/7
 */

public class StickyBarActivity extends BaseActivity{
    @BindView(R.id.toolbar)
    CustomToolbar customToolbar;
    @BindView(R.id.tabLayout)
    SlidingTabLayout slidingTabLayout;
    @BindView(R.id.viewPager)
    CustomViewPager mViewPager;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_sticky_bar;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        ImmersionBar.with(this).titleBar(customToolbar).statusBarDarkFont(true).init();
        customToolbar.back(v -> finish());
        ArrayList<Fragment> mFragments = new ArrayList<>();
        mFragments.add(new TabFragment());
        mFragments.add(new TabFragment());
        mViewPager.setIsCanScroll(false);
        slidingTabLayout.setViewPager(mViewPager, new String[]{"功能","个人中心"}, this, mFragments);
        //slidingTabLayout.setCurrentTab(0);
        slidingTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {

            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }
}
