package com.bingoloves.plugin_spa_demo.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bingoloves.plugin_core.adapter.recyclerview.CommonAdapter;
import com.bingoloves.plugin_core.adapter.recyclerview.base.ViewHolder;
import com.bingoloves.plugin_core.adapter.recyclerview.wrapper.AdapterWrapper;
import com.bingoloves.plugin_core.utils.DensityUtils;
import com.bingoloves.plugin_spa_demo.R;
import com.bingoloves.plugin_spa_demo.bean.MenuItem;

import java.util.ArrayList;
import java.util.List;

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

public class MineFragment extends BaseFragment {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @OnClick(R.id.rl_person_info)
    public void clickEvent(){
        MenuItem mineItem = list.get(1);
        mineItem.name = "关于我们2";
        commonAdapter.notifyItemChanged(1);
    }
    private CommonAdapter<MenuItem> commonAdapter;
    private AdapterWrapper adapterWrapper;
    private List<MenuItem> list = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initView() {
        initMineList();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //recyclerView.addItemDecoration(new CustomItemDecoration(getContext(), CustomItemDecoration.ItemDecoration.VERTICAL,DensityUtils.dp2px(getContext(),15.5f),0));
        commonAdapter = new CommonAdapter<MenuItem>(getContext(),R.layout.layout_mine_item,list) {
            @Override
            protected void convert(ViewHolder holder, MenuItem mineItem, int position) {
                holder.setImageResource(R.id.iv_mine_icon,mineItem.menuIcon);
                holder.setText(R.id.tv_name,mineItem.name);
                holder.setVisible(R.id.line,mineItem.hasLine);
                holder.itemView.setOnClickListener(mineItem.clickListener);
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
                layoutParams.topMargin = position == 1? DensityUtils.dp2px(getContext(),12):0;
            }
        };
        adapterWrapper = new AdapterWrapper(commonAdapter);
        recyclerView.setAdapter(adapterWrapper);
    }
    private void initMineList(){
        list.add(new MenuItem(R.mipmap.ic_apple_black,"意见反馈","", false,null));
        list.add(new MenuItem(R.mipmap.ic_apply_person,"关于我们","", true,null));
        list.add(new MenuItem(R.mipmap.ic_phone_blue,"退出登录","", false,null));
    }
}
