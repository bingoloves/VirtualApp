package com.bingoloves.plugin_spa_demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bingoloves.plugin_spa.ISupportFragment;
import com.bingoloves.plugin_spa.anim.DefaultHorizontalAnimator;
import com.bingoloves.plugin_spa.anim.FragmentAnimator;
import com.bingoloves.plugin_spa.base.SupportActivity;
import com.bingoloves.plugin_spa.base.SupportFragment;
import com.bingoloves.plugin_spa_demo.fragment.DetailFragment;
import com.bingoloves.plugin_spa_demo.fragment.MainFragment;
import com.gyf.immersionbar.ImmersionBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 仿微信交互方式
 */
public class MainActivity extends SupportActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Unbinder unbinder;
    // 再点一次退出程序时间设置
    private static final long WAIT_TIME = 2000L;
    private long TOUCH_TIME = 0;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    private TextView mTvName;   // NavigationView上的名字
    private ImageView mImgNav;  // NavigationView上的头像

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        if (findFragment(MainFragment.class) == null) {
            loadRootFragment(R.id.fl_container, MainFragment.newInstance());
        }
        initView();
    }

    private void initView() {
        ImmersionBar.with(this).statusBarDarkFont(true).init();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        mDrawer.setDrawerListener(toggle);
        toggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setCheckedItem(R.id.nav_home);

        LinearLayout navHeader = (LinearLayout) mNavigationView.getHeaderView(0);
        mTvName = navHeader.findViewById(R.id.tv_name);
        mImgNav = navHeader.findViewById(R.id.img_nav);
        navHeader.setOnClickListener(v -> {
            mDrawer.closeDrawer(GravityCompat.START);
            mDrawer.postDelayed(() -> {
                //goLogin();
                start(new DetailFragment());
            }, 250);
        });
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置横向(和安卓4.x动画相同)
        //return new DefaultHorizontalAnimator();
        // 设置默认Fragment动画  默认竖向(和安卓5.0以上的动画相同)
        return super.onCreateFragmentAnimator();
    }
    @Override
    public void onBackPressedSupport() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            ISupportFragment topFragment = getTopFragment();
            //主页的Fragment
//            if (topFragment instanceof BaseMainFragment) {
//                mNavigationView.setCheckedItem(R.id.nav_home);
//            }
            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                pop();
            } else {
                if (System.currentTimeMillis() - TOUCH_TIME < WAIT_TIME) {
                    finish();
                } else {
                    TOUCH_TIME = System.currentTimeMillis();
                    Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        mDrawer.closeDrawer(GravityCompat.START);
        mDrawer.postDelayed(() -> {
            int id = item.getItemId();
            final ISupportFragment topFragment = getTopFragment();
            SupportFragment myHome = (SupportFragment) topFragment;
   /*         if (id == R.id.nav_home) {
                HomeFragment fragment = findFragment(HomeFragment.class);
                Bundle newBundle = new Bundle();
                newBundle.putString("from", "From:" + topFragment.getClass().getSimpleName());
                fragment.putNewBundle(newBundle);
                myHome.start(fragment, SupportFragment.SINGLETASK);
            } else if (id == R.id.nav_discover) {
                DiscoverFragment fragment = findFragment(DiscoverFragment.class);
                if (fragment == null) {
                    myHome.startWithPopTo(DiscoverFragment.newInstance(), HomeFragment.class, false);
                } else {
                    // 如果已经在栈内,则以SingleTask模式start
                    myHome.start(fragment, SupportFragment.SINGLETASK);
                }
            } else if (id == R.id.nav_shop) {
                ShopFragment fragment = findFragment(ShopFragment.class);
                if (fragment == null) {
                    myHome.startWithPopTo(ShopFragment.newInstance(), HomeFragment.class, false);
                } else {
                    // 如果已经在栈内,则以SingleTask模式start,也可以用popTo
//                        start(fragment, SupportFragment.SINGLETASK);
                    myHome.popTo(ShopFragment.class, false);
                }
            } else if (id == R.id.nav_login) {
                goLogin();
            } else if (id == R.id.nav_swipe_back) {
                startActivity(new Intent(MainActivity.this, SwipeBackSampleActivity.class));
            }*/
        }, 300);
        return true;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
