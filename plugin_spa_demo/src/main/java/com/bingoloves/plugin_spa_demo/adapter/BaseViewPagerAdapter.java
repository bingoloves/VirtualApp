package com.bingoloves.plugin_spa_demo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by bingo on 2020/9/23.
 */

public class BaseViewPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> mFragments;
    private String[] mTitles;
    public BaseViewPagerAdapter(FragmentManager fm, List<Fragment> fragments, String[] titles){
        super(fm);
        this.mFragments = fragments;
        this.mTitles = titles;
    }
    public BaseViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }
}