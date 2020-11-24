package com.bingoloves.plugin_spa_demo.fragment;

import com.bingoloves.plugin_core.widget.CustomToolbar;
import com.bingoloves.plugin_spa.base.SupportFragment;
import com.bingoloves.plugin_spa_demo.R;

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

public class MineFragment extends BaseFragment{
    @BindView(R.id.toolbar)
    CustomToolbar customToolbar;

    @Override
    protected int getContentView() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initView() {
        super.initView();
        customToolbar.setCenterTitle("我的");
        customToolbar.showBaseLine();
    }
}
