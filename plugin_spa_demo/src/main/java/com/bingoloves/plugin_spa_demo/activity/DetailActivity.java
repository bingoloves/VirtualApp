package com.bingoloves.plugin_spa_demo.activity;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import com.bingoloves.plugin_core.adapter.recyclerview.CommonAdapter;
import com.bingoloves.plugin_core.adapter.recyclerview.base.ViewHolder;
import com.bingoloves.plugin_core.utils.log.LogUtils;
import com.bingoloves.plugin_spa_demo.R;
import com.gyf.immersionbar.ImmersionBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.cqs.im.base.BaseActivity;

/**
 * Created by bingo on 2020/11/24.
 *
 * @Author: bingo
 * @Email: 657952166@qq.com
 * @Description: 类作用描述
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/11/24
 */
public class DetailActivity extends BaseActivity {
    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_detail;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        ImmersionBar.with(this).titleBar(mToolbar).init();
    }

    @Override
    protected void initView() {
        collapsingToolbar.setTitle("Title");
        //mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add("商品"+i);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CommonAdapter<String>(this,R.layout.layout_list_item,list) {
            @Override
            protected void convert(ViewHolder holder, String item, int position) {
                holder.setText(R.id.tv_menu_name,item);
            }
        });

        //verticalOffset是当前appbarLayout的高度与最开始appbarlayout高度的差，向上滑动的话是负数
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            //通过日志得出活动启动是两次，由于之前有setExpanded所以三次
            LogUtils.e("verticalOffset = " + verticalOffset);
            LogUtils.e("appBarLayout = " + appBarLayout.getHeight());
        });
    }
}
