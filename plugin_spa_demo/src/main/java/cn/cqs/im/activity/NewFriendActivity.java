package cn.cqs.im.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bingoloves.plugin_core.adapter.recyclerview.CommonAdapter;
import com.bingoloves.plugin_core.adapter.recyclerview.base.ViewHolder;
import com.bingoloves.plugin_core.utils.log.LogUtils;
import com.bingoloves.plugin_core.widget.CustomToolbar;
import com.bingoloves.plugin_spa_demo.R;
import cn.cqs.im.bean.AgreeAddFriendMessage;
import com.bingoloves.plugin_spa_demo.bean.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gyf.immersionbar.ImmersionBar;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.core.ConnectionStatus;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.cqs.im.Config;
import cn.cqs.im.base.BaseActivity;
import cn.cqs.im.db.NewFriend;
import cn.cqs.im.db.NewFriendManager;
import cn.cqs.im.model.UserModel;

/**新朋友
 * @author :smile
 * @project:NewFriendActivity
 * @date :2016-01-25-18:23
 */
public class NewFriendActivity extends BaseActivity {
    @BindView(R.id.ll_root)
    LinearLayout ll_root;
    @BindView(R.id.toolbar)
    CustomToolbar customToolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;
    CommonAdapter adapter;

    List<NewFriend> allNewFriend = new ArrayList<>();
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_conversation;
    }

    @Override
    protected void initData() {
        //批量更新未读未认证的消息为已读状态
        NewFriendManager.getInstance(this).updateBatchStatus();
    }

    @Override
    protected void initView() {
        ImmersionBar.with(this).statusBarDarkFont(true).titleBar(customToolbar).init();
        customToolbar.setCenterTitle("新朋友");
        customToolbar.back(v -> finish());
        ImmersionBar.with(this).statusBarDarkFont(true).titleBar(customToolbar).init();
        customToolbar.setRightTitle("搜索", v -> {
            startActivity(new Intent(mActivity,SearchUserActivity.class));
        });
        adapter = new CommonAdapter<NewFriend>(this, R.layout.item_new_friend, allNewFriend) {
            @Override
            protected void convert(ViewHolder holder, NewFriend add, int position) {
                ImageView imageView = holder.getView(R.id.iv_recent_avatar);
                Glide.with(mActivity).asBitmap().load(add.getAvatar()).apply(new RequestOptions().error(R.mipmap.head)).into(imageView);
                holder.setText(R.id.tv_recent_name, add == null ? "未知" : add.getName());
                holder.setText(R.id.tv_recent_msg, add == null ? "未知" : add.getMsg());
                Integer status = add.getStatus();
                //当状态是未添加或者是已读未添加
                if (status == null || status == Config.STATUS_VERIFY_NONE || status == Config.STATUS_VERIFY_READED) {
                    holder.setText(R.id.btn_aggree, "接受");
                    holder.setEnabled(R.id.btn_aggree, true);
                    holder.setOnClickListener(R.id.btn_aggree, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            agreeAdd(add, new SaveListener<Object>() {
                                @Override
                                public void done(Object o, BmobException e) {
                                    if (e == null) {
                                        holder.setText(R.id.btn_aggree, "已添加");
                                        holder.setEnabled(R.id.btn_aggree, false);
                                    } else {
                                        holder.setEnabled(R.id.btn_aggree, true);
                                        LogUtils.e("添加好友失败:" + e.getMessage());
                                        toast("添加好友失败:" + e.getMessage());
                                    }
                                }
                            });
                        }
                    });
                } else {
                    holder.setText(R.id.btn_aggree, "已添加");
                    holder.setEnabled(R.id.btn_aggree, false);
                }
                //长按删除
                holder.itemView.setOnLongClickListener(v -> {
                    NewFriendManager.getInstance(NewFriendActivity.this).deleteNewFriend(add);
                    adapter.remove(position);
                    return true;
                });
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        setListener();
    }

    private void setListener(){
        ll_root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ll_root.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                refreshLayout.finishRefresh(true);
                query();
            }
        });
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setOnRefreshListener(refreshLayout -> query());
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshLayout.finishRefresh();
        query();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
      查询本地会话
     */
    public void query(){
        allNewFriend = NewFriendManager.getInstance(this).getAllNewFriend();
        adapter.update(allNewFriend);
        refreshLayout.finishRefresh(true);
    }
    /**
     * TODO 好友管理：9.10、添加到好友表中再发送同意添加好友的消息
     *
     * @param add
     * @param listener
     */
    private void agreeAdd(final NewFriend add, final SaveListener<Object> listener) {
        User user = new User();
        user.setObjectId(add.getUid());
        UserModel.getInstance()
                .agreeAddFriend(user, new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            sendAgreeAddFriendMessage(add, listener);
                        } else {
                            LogUtils.e(e.getMessage());
                            listener.done(null, e);
                        }
                    }
                });
    }

    /**
     * 发送同意添加好友的消息
     */
    //TODO 好友管理：9.8、发送同意添加好友
    private void sendAgreeAddFriendMessage(final NewFriend add, final SaveListener<Object> listener) {
        if (BmobIM.getInstance().getCurrentStatus().getCode() != ConnectionStatus.CONNECTED.getCode()) {
            toast("尚未连接IM服务器");
            return;
        }
        BmobIMUserInfo info = new BmobIMUserInfo(add.getUid(), add.getName(), add.getAvatar());
        //TODO 会话：4.1、创建一个暂态会话入口，发送同意好友请求
        BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, true, null);
        //TODO 消息：5.1、根据会话入口获取消息管理，发送同意好友请求
        BmobIMConversation messageManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);
        //而AgreeAddFriendMessage的isTransient设置为false，表明我希望在对方的会话数据库中保存该类型的消息
        AgreeAddFriendMessage msg = new AgreeAddFriendMessage();
        final User currentUser = BmobUser.getCurrentUser(User.class);
        msg.setContent("我通过了你的好友验证请求，我们可以开始 聊天了!");//这句话是直接存储到对方的消息表中的
        Map<String, Object> map = new HashMap<>();
        map.put("msg", currentUser.getUsername() + "同意添加你为好友");//显示在通知栏上面的内容
        map.put("uid", add.getUid());//发送者的uid-方便请求添加的发送方找到该条添加好友的请求
        map.put("time", add.getTime());//添加好友的请求时间
        msg.setExtraMap(map);
        messageManager.sendMessage(msg, new MessageSendListener() {
            @Override
            public void done(BmobIMMessage msg, BmobException e) {
                if (e == null) {//发送成功
                    NewFriendManager.getInstance(mActivity).updateNewFriend(add, Config.STATUS_VERIFIED);
                    listener.done(msg, e);
                } else {//发送失败
                    LogUtils.e(e.getMessage());
                    listener.done(msg, e);
                }
            }
        });
    }
}
