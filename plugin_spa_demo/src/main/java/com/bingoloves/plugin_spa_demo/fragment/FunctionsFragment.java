package com.bingoloves.plugin_spa_demo.fragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;

import com.bingoloves.plugin_core.adapter.recyclerview.CommonAdapter;
import com.bingoloves.plugin_core.adapter.recyclerview.base.ViewHolder;
import com.bingoloves.plugin_core.adapter.recyclerview.utils.GridSpacingItemDecoration;
import com.bingoloves.plugin_core.utils.DensityUtils;
import com.bingoloves.plugin_core.utils.log.LogUtils;
import com.bingoloves.plugin_core.widget.CustomToolbar;
import com.bingoloves.plugin_core.widget.particle.ParticleView;
import com.bingoloves.plugin_spa_demo.R;
import com.bingoloves.plugin_spa_demo.activity.StickyBarActivity;
import com.bingoloves.plugin_spa_demo.activity.WebActivity;
import com.bingoloves.plugin_spa_demo.bean.MenuItem;
import com.bingoloves.plugin_spa_demo.dialog.AlertDialogUtils;
import com.bingoloves.plugin_spa_demo.dialog.BottomDialogFragment;
import com.bingoloves.plugin_spa_demo.dialog.LeftDialogFragment;
import com.bingoloves.plugin_spa_demo.dialog.RightDialogFragment;
import com.bingoloves.plugin_spa_demo.dialog.TopDialogFragment;
import com.gyf.immersionbar.ImmersionBar;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private List<MenuItem> list = new ArrayList<>();
    //颜色缓存
    private Map<Integer,Integer> manageBackgroundMap = new HashMap<>();
    private  Map<Integer,Integer> manageTextColorMap = new HashMap<>();
    int num = 0;
    @Override
    protected void initView() {
        customToolbar.setCenterTitle("分类");
        customToolbar.showBaseLine();
        ImmersionBar.with(this).statusBarDarkFont(true).titleBar(customToolbar).init();
        initWidgets();
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),4));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(4, DensityUtils.dp2px(getContext(),12),DensityUtils.dp2px(getContext(),15),false));
        recyclerView.setAdapter(new CommonAdapter<MenuItem>(getContext(),R.layout.layout_functions_item,list) {
            @Override
            protected void convert(ViewHolder holder, MenuItem o, int position) {
                int random = (int)(Math.random()* manageBackgroundMap.size());
                holder.setText(R.id.tv_name,o.name);
                holder.setBackgroundColorRes(R.id.root,manageBackgroundMap.get(random));
                holder.setTextColorRes(R.id.tv_name,manageTextColorMap.get(random));
                holder.itemView.setOnClickListener(o.clickListener);
            }
        });
    }

    private void initWidgets() {
        //背景颜色缓存
        manageBackgroundMap.put(0,R.color.color_E1F5F1);
        manageBackgroundMap.put(1,R.color.color_E7F2FF);
        manageBackgroundMap.put(2,R.color.color_E6F0FE);
        manageBackgroundMap.put(3,R.color.color_FCECE3);
        //字体颜色缓存
        manageTextColorMap.put(0,R.color.color_00A783);
        manageTextColorMap.put(1,R.color.color_308EFD);
        manageTextColorMap.put(2,R.color.color_355FE7);
        manageTextColorMap.put(3,R.color.color_FF671D);

        list.add(new MenuItem("Top", v -> {
            TopDialogFragment topDialogFragment = new TopDialogFragment();
            topDialogFragment.show(getChildFragmentManager(), TopDialogFragment.class.getSimpleName());
        }));
        list.add(new MenuItem("Bottom",v -> {
            BottomDialogFragment bottomDialogFragment = new BottomDialogFragment();
            bottomDialogFragment.show(getChildFragmentManager(), BottomDialogFragment.class.getSimpleName());
        }));
        list.add(new MenuItem("Left",v -> {
            LeftDialogFragment leftDialogFragment = new LeftDialogFragment();
            leftDialogFragment.show(getChildFragmentManager(), LeftDialogFragment.class.getSimpleName());
        }));
        list.add(new MenuItem("Right",v -> {
            RightDialogFragment rightDialogFragment = new RightDialogFragment();
            rightDialogFragment.show(getChildFragmentManager(), RightDialogFragment.class.getSimpleName());
        }));
        list.add(new MenuItem("Center",v -> AlertDialogUtils.show(getActivity(),null)));
        list.add(new MenuItem("WebView",v -> navigateTo(WebActivity.class)));
        list.add(new MenuItem("防重复点击", new View.OnClickListener() {
            @SingleClick
            @Override
            public void onClick(View v) {
                num++;
                toast("有效点击数："+ num);
            }
        }));
        list.add(new MenuItem("广告",v -> AlertDialogUtils.showAd(getActivity(),null)));
        list.add(new MenuItem("二次吸顶",v ->navigateTo(StickyBarActivity.class)));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_functions;
    }
}
