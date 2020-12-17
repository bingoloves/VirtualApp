package cn.cqs.im.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.bingoloves.plugin_core.adapter.recyclerview.CommonAdapter;
import com.bingoloves.plugin_core.adapter.recyclerview.base.ViewHolder;
import com.bingoloves.plugin_core.utils.log.LogUtils;
import com.bingoloves.plugin_core.widget.CustomToolbar;
import com.bingoloves.plugin_spa_demo.R;
import cn.cqs.im.bean.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gyf.immersionbar.ImmersionBar;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.cqs.im.base.BaseActivity;
import cn.cqs.im.model.BaseModel;
import cn.cqs.im.model.UserModel;

/**
 * 搜索用户
 *
 * @author :smile
 * @project:SearchUserActivity
 * @date :2016-01-25-18:23
 */
public class SearchUserActivity extends BaseActivity {

    @BindView(R.id.et_find_name)
    EditText et_find_name;
    @BindView(R.id.toolbar)
    CustomToolbar customToolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;
    CommonAdapter adapter;
    @BindView(R.id.btn_search)
    Button btn_search;
    private List<User> newUsers = new ArrayList<>();
    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_user;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        ImmersionBar.with(this).statusBarDarkFont(true).titleBar(customToolbar).init();
        customToolbar.setCenterTitle("搜索用户");
        customToolbar.back(v -> finish());
        ImmersionBar.with(this).statusBarDarkFont(true).titleBar(customToolbar).init();
        adapter = new CommonAdapter<User>(this,R.layout.item_search_user,newUsers) {
            @Override
            protected void convert(ViewHolder holder, User user, int position) {
                ImageView imageView = holder.getView(R.id.avatar);
                Glide.with(mActivity).asBitmap().load(user.getAvatar()).apply(new RequestOptions().error(R.mipmap.head)).into(imageView);
                holder.setText(R.id.name, user.getUsername());
                holder.setOnClickListener(R.id.btn_add, v -> {//查看个人详情
                    Intent intent = new Intent(mActivity,UserInfoActivity.class);
                    intent.putExtra("u", user);
                    startActivity(intent);
                });

//                holder.itemView.setOnClickListener(v -> {
//                    Bundle bundle = new Bundle();
//                    bundle.putSerializable("u", user);
//                    Intent intent = new Intent(mActivity,UserInfoActivity.class);
//                    startActivity(intent);
//                });
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(refreshLayout -> query());
    }



    @OnClick(R.id.btn_search)
    public void onSearchClick(View view) {
        refreshLayout.finishRefresh();
        query();
    }

    public void query() {
        String name = et_find_name.getText().toString();
        if (TextUtils.isEmpty(name)) {
            toast("请填写用户名");
            refreshLayout.finishRefresh();
            return;
        }
        UserModel.getInstance().queryUsers(name, BaseModel.DEFAULT_LIMIT,
                new FindListener<User>() {
                    @Override
                    public void done(List<User> list, BmobException e) {
                        if (e == null) {
                            refreshLayout.finishRefresh(true);
                            adapter.update(list);
                        } else {
                            refreshLayout.finishRefresh(false);
                            adapter.clear();
                            LogUtils.e(e.getMessage());
                        }
                    }
                }
        );
    }

}
