package com.bingoloves.plugin_spa_demo.fragment;

import android.view.View;

import com.bingoloves.plugin_core.utils.Utils;
import com.bingoloves.plugin_core.widget.CustomToolbar;
import com.bingoloves.plugin_spa_demo.R;
import com.gyf.immersionbar.ImmersionBar;
import butterknife.BindView;
import butterknife.OnClick;
import cn.cqs.im.base.BaseFragment;

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
    public void clickEvent(View view){
        Utils.snack(view, Utils.getLogDebug());

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView() {

    }
}
