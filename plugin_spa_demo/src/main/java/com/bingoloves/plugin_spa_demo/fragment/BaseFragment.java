package com.bingoloves.plugin_spa_demo.fragment;

import com.bingoloves.plugin_spa.base.SupportFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by bingo on 2020/11/24.
 *
 * @Author: bingo
 * @Email: 657952166@qq.com
 * @Description: 类作用描述
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/11/24
 */
public abstract class BaseFragment extends SupportFragment{
    private Unbinder unbinder;
    @Override
    protected void initView() {
        unbinder= ButterKnife.bind(this, mContentView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null){
            unbinder.unbind();
        }
    }
}
