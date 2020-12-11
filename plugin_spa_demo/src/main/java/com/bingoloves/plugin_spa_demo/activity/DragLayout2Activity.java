package com.bingoloves.plugin_spa_demo.activity;

import android.view.View;

import com.bingoloves.plugin_core.widget.CustomToolbar;
import com.bingoloves.plugin_spa_demo.R;
import com.gyf.immersionbar.ImmersionBar;

import butterknife.BindView;
import cn.cqs.im.base.BaseActivity;

/**
 * Created by bingo on 2020/12/9.
 *
 * @Author: bingo
 * @Email: 657952166@qq.com
 * @Description: 类作用描述
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/12/9
 */

public class DragLayout2Activity extends BaseActivity {
    @BindView(R.id.toolbar)
    CustomToolbar toolbar;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_drag_layout;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        ImmersionBar.with(this).titleBar(toolbar).statusBarDarkFont(true).init();
        toolbar.back(v -> finish());
    }
}
