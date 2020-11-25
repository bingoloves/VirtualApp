package com.bingoloves.plugin_spa_demo.activity;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bingoloves.plugin_core.utils.Utils;
import com.bingoloves.plugin_core.widget.CustomViewPager;
import com.bingoloves.plugin_spa_demo.R;
import com.bingoloves.plugin_spa_demo.adapter.BaseViewPagerAdapter;
import com.bingoloves.plugin_spa_demo.base.BaseActivity;
import com.bingoloves.plugin_spa_demo.bean.TabEntity;
import com.bingoloves.plugin_spa_demo.fragment.FunctionsFragment;
import com.bingoloves.plugin_spa_demo.fragment.HomeFragment;
import com.bingoloves.plugin_spa_demo.fragment.MineFragment;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.gyf.immersionbar.ImmersionBar;

import java.util.ArrayList;
import butterknife.BindView;

/**
 * 仿微信交互方式
 */
public class MainActivity extends BaseActivity implements DrawerLayout.DrawerListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    //左侧抽屉View
    @BindView(R.id.navigation_view)
    LinearLayout mDrawerView;
    @BindView(R.id.viewPager)
    CustomViewPager mViewPager;
    @BindView(R.id.bottomTab)
    CommonTabLayout bottomTab;

    private String[] mTitles = {"首页", "功能", "我的"};
    private int[] mIconUnselectIds = {R.mipmap.ic_tabbar_home, R.mipmap.ic_tabbar_functions, R.mipmap.ic_tabbar_mine};
    private int[] mIconSelectIds = {R.mipmap.ic_tabbar_home_select, R.mipmap.ic_tabbar_functions_select, R.mipmap.ic_tabbar_mine_select};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private ArrayList<Fragment> mFragments = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        if (mTabEntities != null){
            for (int i = 0; i < mTitles.length; i++) {
                mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
            }
        }
        if (mFragments != null){
            mFragments.add(new HomeFragment());
            mFragments.add(new FunctionsFragment());
            mFragments.add(new MineFragment());
        }
    }

    @Override
    protected void initView() {
        ViewGroup.LayoutParams layoutParams = mDrawerView.getLayoutParams();
        layoutParams.width = Utils.getWidthAndHeight(getWindow())[0]*2/3;
        //mViewPager.setIsCanScroll(false);
        mViewPager.setAdapter(new BaseViewPagerAdapter(getSupportFragmentManager(),mFragments,mTitles));
        bottomTab.setTabData(mTabEntities);
        bottomTab.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mViewPager.setCurrentItem(position,false);
            }

            @Override
            public void onTabReselect(int position) {
            }
        });
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bottomTab.setCurrentTab(position);
                ImmersionBar.with(mActivity).statusBarDarkFont(position != 2).init();
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mViewPager.setCurrentItem(0);
    }

    @Override
    public void onDrawerSlide(@NonNull View view, float v) {

    }

    @Override
    public void onDrawerOpened(@NonNull View view) {

    }

    @Override
    public void onDrawerClosed(@NonNull View view) {

    }

    @Override
    public void onDrawerStateChanged(int i) {

    }
}
