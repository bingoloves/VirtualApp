package com.bingoloves.plugin_spa_demo.fragment;

import android.content.Intent;

import com.bingoloves.plugin_core.widget.CustomToolbar;
import com.bingoloves.plugin_spa_demo.R;
import com.bingoloves.plugin_spa_demo.activity.DetailActivity;
import com.bingoloves.plugin_spa_demo.base.BaseFragment;
import com.gyf.immersionbar.ImmersionBar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by bingo on 2020/11/24.
 *
 * @Author: bingo
 * @Email: 657952166@qq.com
 * @Description: 类作用描述
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/11/24
 */

public class HomeFragment extends BaseFragment {

    @BindView(R.id.toolbar)
    CustomToolbar customToolbar;

    @OnClick(R.id.btn_move_to_detail)
    public void clickEvent(){
        startActivity(new Intent(getContext(), DetailActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView() {
        customToolbar.setCenterTitle("首页");
        customToolbar.showBaseLine();
        ImmersionBar.with(this).statusBarDarkFont(true).titleBar(customToolbar).init();
    }
}
