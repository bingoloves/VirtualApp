package com.bingoloves.plugin_spa_demo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bingoloves.plugin_core.widget.CustomViewPager;
import com.bingoloves.plugin_spa.base.SupportFragment;
import com.bingoloves.plugin_spa_demo.R;
import com.bingoloves.plugin_spa_demo.adapter.BaseViewPagerAdapter;
import com.bingoloves.plugin_spa_demo.bean.TabEntity;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MainFragment extends BaseFragment {

    @BindView(R.id.viewPager)
    CustomViewPager mViewPager;
    @BindView(R.id.bottomTab)
    CommonTabLayout bottomTab;

    public static MainFragment newInstance() {
        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }
    private String[] mTitles = {"首页", "功能", "我的"};
    private int[] mIconUnselectIds = {R.mipmap.ic_tabbar_home, R.mipmap.ic_tabbar_functions, R.mipmap.ic_tabbar_mine};
    private int[] mIconSelectIds = {R.mipmap.ic_tabbar_home_select, R.mipmap.ic_tabbar_functions_select, R.mipmap.ic_tabbar_mine_select};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private ArrayList<Fragment> mFragments = new ArrayList<>();

    @Override
    protected int getContentView() {
        return R.layout.fragment_main;
    }

    @Override
    protected void initView() {
        super.initView();
        initViewPager();
    }

    private void initViewPager() {
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
        //mViewPager.setIsCanScroll(false);
        mViewPager.setAdapter(new BaseViewPagerAdapter(getChildFragmentManager(),mFragments,mTitles));
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
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }
}
