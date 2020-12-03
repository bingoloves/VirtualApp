package com.bingoloves.plugin_spa_demo.fragment;

import android.view.View;

import com.bingoloves.plugin_core.utils.log.LogUtils;
import com.bingoloves.plugin_core.widget.CustomToolbar;
import com.bingoloves.plugin_spa_demo.R;
import com.bingoloves.plugin_spa_demo.activity.WebActivity;
import com.bingoloves.plugin_spa_demo.dialog.AlertDialogUtils;
import com.bingoloves.plugin_spa_demo.dialog.BottomDialogFragment;
import com.bingoloves.plugin_spa_demo.dialog.LeftDialogFragment;
import com.bingoloves.plugin_spa_demo.dialog.RightDialogFragment;
import com.bingoloves.plugin_spa_demo.dialog.TopDialogFragment;
import com.gyf.immersionbar.ImmersionBar;
import butterknife.BindView;
import butterknife.OnClick;
import cn.cqs.aop.annotation.SingleClick;
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

public class FunctionsFragment extends BaseFragment {
    @BindView(R.id.toolbar)
    CustomToolbar customToolbar;
    @SingleClick
    @OnClick({R.id.btn_top,R.id.btn_bottom,R.id.btn_left,R.id.btn_right,R.id.btn_center,R.id.btn_web})
    public void clickEvent(View view){
        switch (view.getId()){
            case R.id.btn_top:
                TopDialogFragment topDialogFragment = new TopDialogFragment();
                topDialogFragment.show(getChildFragmentManager(), TopDialogFragment.class.getSimpleName());
                break;
            case R.id.btn_bottom:
                BottomDialogFragment bottomDialogFragment = new BottomDialogFragment();
                bottomDialogFragment.show(getChildFragmentManager(), BottomDialogFragment.class.getSimpleName());
                break;
            case R.id.btn_left:
                LeftDialogFragment leftDialogFragment = new LeftDialogFragment();
                leftDialogFragment.show(getChildFragmentManager(), LeftDialogFragment.class.getSimpleName());
                break;
            case R.id.btn_right:
                RightDialogFragment rightDialogFragment = new RightDialogFragment();
                rightDialogFragment.show(getChildFragmentManager(), RightDialogFragment.class.getSimpleName());
                break;
            case R.id.btn_center:
                AlertDialogUtils.show(getActivity(),null);
                break;
            case R.id.btn_web:
                //navigateTo(WebActivity.class);
                LogUtils.e("哈哈");
                break;
            default:
                break;
        }
    }
    @Override
    protected void initView() {
        customToolbar.setCenterTitle("分类");
        customToolbar.showBaseLine();
        ImmersionBar.with(this).statusBarDarkFont(true).titleBar(customToolbar).init();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_functions;
    }
}
