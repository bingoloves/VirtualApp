package com.bingoloves.plugin_spa_demo.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bingoloves.plugin_core.adapter.recyclerview.CommonAdapter;
import com.bingoloves.plugin_core.adapter.recyclerview.base.ViewHolder;
import com.bingoloves.plugin_core.http.MMKVHelper;
import com.bingoloves.plugin_core.utils.Utils;
import com.bingoloves.plugin_core.widget.CustomViewPager;
import com.bingoloves.plugin_spa_demo.App;
import com.bingoloves.plugin_spa_demo.Constants;
import com.bingoloves.plugin_spa_demo.R;
import com.bingoloves.plugin_spa_demo.adapter.BaseViewPagerAdapter;
import com.bingoloves.plugin_spa_demo.bean.MenuItem;
import com.bingoloves.plugin_spa_demo.bean.TabEntity;
import com.bingoloves.plugin_spa_demo.bean.User;
import com.bingoloves.plugin_spa_demo.dao.UserDao;
import com.bingoloves.plugin_spa_demo.fragment.FunctionsFragment;
import com.bingoloves.plugin_spa_demo.fragment.MineFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.gyf.immersionbar.ImmersionBar;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.cqs.im.activity.NewFriendActivity;
import cn.cqs.im.base.BaseActivity;
import cn.cqs.im.fragment.ConversationFragment;
import cn.cqs.im.model.UserModel;

/**
 * 仿微信交互方式
 */
public class MainActivity extends BaseActivity implements DrawerLayout.DrawerListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @BindView(R.id.viewPager)
    CustomViewPager mViewPager;
    @BindView(R.id.bottomTab)
    CommonTabLayout bottomTab;
    //左侧抽屉View
    @BindView(R.id.navigation_view)
    LinearLayout mDrawerView;
    @BindView(R.id.iv_avatar)
    ImageView avatarIv;
    @BindView(R.id.tv_name)
    TextView userNameTv;
    @BindView(R.id.tv_mail)
    TextView emailTv;
    @BindView(R.id.drawer_list_view)
    RecyclerView drawerRecyclerView;
    @BindView(R.id.tv_version)
    TextView versionTv;


    private String[] mTitles = {"首页", "功能", "我的"};
    private int[] mIconUnselectIds = {R.mipmap.ic_tabbar_home, R.mipmap.ic_tabbar_functions, R.mipmap.ic_tabbar_mine};
    private int[] mIconSelectIds = {R.mipmap.ic_tabbar_home_select, R.mipmap.ic_tabbar_functions_select, R.mipmap.ic_tabbar_mine_select};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private ArrayList<Fragment> mFragments = new ArrayList<>();

    @Override
    public boolean cancelNavigateAnimation() {
        return true;
    }

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
            mFragments.add(new ConversationFragment());
            mFragments.add(new FunctionsFragment());
            mFragments.add(new MineFragment());
        }
    }

    @Override
    protected void initView() {
        initDrawerView();
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
        connectIM();
    }

    /**
     * 左侧抽屉数据
     */
    private void initDrawerView() {
        ViewGroup.LayoutParams layoutParams = mDrawerView.getLayoutParams();
        layoutParams.width = Utils.getWidthAndHeight(getWindow())[0]*2/3;
        //当前登录用户信息
        User currentUser = UserDao.getCurrentUser();
        if (currentUser != null){
            userNameTv.setText(currentUser.getUsername());
            RequestOptions requestOptions = new RequestOptions().optionalCircleCrop().error(R.mipmap.head);
            Glide.with(this).asBitmap().apply(requestOptions).load(currentUser.getAvatar()).into(avatarIv);
        }
        drawerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        drawerRecyclerView.setAdapter(new CommonAdapter<MenuItem>(this,R.layout.layout_list_item,getMenuList()) {
            @Override
            protected void convert(ViewHolder holder, MenuItem menu, int position) {
                holder.setText(R.id.tv_menu_name,menu.name);
                holder.setImageResource(R.id.iv_menu_icon,menu.menuIcon);
                holder.itemView.setOnClickListener(menu.clickListener);
            }
        });
    }

    /**
     * 加载默认的侧边栏菜单
     * @return
     */
    private List<MenuItem> getMenuList() {
        List<MenuItem> result = new ArrayList<>();
        result.add(new MenuItem(R.mipmap.ic_apply_person, "个人中心", v -> { }));
        result.add(new MenuItem(R.mipmap.ic_im, "聊天", v -> { }));
        result.add(new MenuItem(R.mipmap.ic_add, "新朋友", v -> {startActivity(new Intent(mActivity, NewFriendActivity.class));mDrawer.closeDrawers();}));
        result.add(new MenuItem(R.mipmap.ic_music, "音乐", v -> { }));
        result.add(new MenuItem(R.mipmap.ic_appstore, "组件", v -> { startActivity(new Intent(mActivity,CameraActivity.class));mDrawer.closeDrawers();}));
        result.add(new MenuItem(R.mipmap.ic_quit, "退出登录", v -> {
            UserModel.getInstance().logout();
            App.isLogin = false;
            MMKVHelper.removeKey(Constants.IS_LOGIN);
            startActivity(new Intent(mActivity,LoginActivity.class)); finish();
        }));
        return result;
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

    /**
     * 连接IM 服务
     */
    private void connectIM(){
        User user = BmobUser.getCurrentUser(User.class);
        if (!TextUtils.isEmpty(user.getObjectId())) {
            BmobIM.connect(user.getObjectId(), new ConnectListener() {
                @Override
                public void done(String uid, BmobException e) {
                    if (e == null) {
                        toast("连接成功");
                    } else {
                        toast("连接失败:"+e.getMessage());
                    }
                }
            });
        }
    }

}
