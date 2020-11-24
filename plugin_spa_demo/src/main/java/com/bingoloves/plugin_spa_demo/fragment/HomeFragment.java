package com.bingoloves.plugin_spa_demo.fragment;

import android.widget.Toast;

import com.bingoloves.plugin_core.widget.CustomToolbar;
import com.bingoloves.plugin_spa.base.SupportFragment;
import com.bingoloves.plugin_spa_demo.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

public class HomeFragment extends BaseFragment{

    @BindView(R.id.toolbar)
    CustomToolbar customToolbar;

    @OnClick(R.id.btn_move_to_detail)
    public void clickEvent(){
        Toast.makeText(_mActivity, "haha", Toast.LENGTH_SHORT).show();
        _mActivity.start(DetailFragment.newInstance("详情"));
    }
    @Override
    protected int getContentView() {
        return R.layout.fragment_home;
    }
    @Override
    protected void initView() {
        super.initView();
        customToolbar.setCenterTitle("首页");
        customToolbar.showBaseLine();
    }

}
