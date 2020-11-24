package com.bingoloves.plugin_spa_demo.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.bingoloves.plugin_core.adapter.recyclerview.CommonAdapter;
import com.bingoloves.plugin_core.adapter.recyclerview.base.ViewHolder;
import com.bingoloves.plugin_core.widget.CustomToolbar;
import com.bingoloves.plugin_spa_demo.R;

import java.util.ArrayList;
import java.util.List;

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
public class DetailFragment extends BaseFragment{

    public static DetailFragment newInstance(String title) {
        DetailFragment fragment = new DetailFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @Override
    protected int getContentView() {
        return R.layout.fragment_detail;
    }

    @Override
    protected void initView() {
        super.initView();
        collapsingToolbar.setTitle("Title");
        //mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add("商品"+i);
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new CommonAdapter<String>(getContext(),R.layout.layout_list_item,list) {
            @Override
            protected void convert(ViewHolder holder, String item, int position) {
                holder.setText(R.id.tv_item_name,item);
            }
        });
    }
}
