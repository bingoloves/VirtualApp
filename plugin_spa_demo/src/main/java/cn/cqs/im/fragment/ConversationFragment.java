package cn.cqs.im.fragment;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.bingoloves.plugin_core.adapter.recyclerview.CommonAdapter;
import com.bingoloves.plugin_core.adapter.recyclerview.base.ViewHolder;
import com.bingoloves.plugin_core.utils.log.LogUtils;
import com.bingoloves.plugin_core.widget.CustomToolbar;
import com.bingoloves.plugin_spa_demo.R;
import cn.cqs.im.bean.Conversation;
import cn.cqs.im.bean.NewFriendConversation;
import cn.cqs.im.bean.PrivateConversation;
import cn.cqs.im.utils.TimeUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.gyf.immersionbar.ImmersionBar;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import butterknife.BindView;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.event.OfflineMessageEvent;
import cn.cqs.im.activity.ChatActivity;
import cn.cqs.im.activity.SearchUserActivity;
import cn.cqs.im.base.BaseFragment;
import cn.cqs.im.db.NewFriend;
import cn.cqs.im.db.NewFriendManager;
import cn.cqs.im.event.RefreshEvent;

/**会话界面
 * @author :smile
 * @project:ConversationFragment
 * @date :2016-01-25-18:23
 */
public class ConversationFragment extends BaseFragment {
    @BindView(R.id.ll_root)
    LinearLayout rootView;
    @BindView(R.id.toolbar)
    CustomToolbar customToolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;
    CommonAdapter<Conversation> adapter;
    List<Conversation> conversationList = new ArrayList<>();
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_conversation;
    }

    @Override
    protected void initView() {
        ImmersionBar.with(this).titleBar(customToolbar).init();
        EventBus.getDefault().register(this);
        customToolbar.setCenterTitle("会话");
        customToolbar.showBaseLine();
        customToolbar.setRightTitle("搜索", v -> startActivity(new Intent(getContext(),SearchUserActivity.class)));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new CommonAdapter<Conversation>(getActivity(),R.layout.item_conversation,conversationList) {
            @Override
            protected void convert(ViewHolder holder, Conversation conversation, int position) {
                ImageView imageView = holder.getView(R.id.iv_recent_avatar);
                holder.setText(R.id.tv_recent_msg,conversation.getLastMessageContent());
                holder.setText(R.id.tv_recent_time, TimeUtil.getChatTime(false,conversation.getLastMessageTime()));
                //会话图标
                Object obj = conversation.getAvatar();
                Glide.with(getContext()).asBitmap().load(obj).apply(new RequestOptions().error(R.mipmap.head)).into(imageView);
                //会话标题
                holder.setText(R.id.tv_recent_name, conversation.getcName());
                //查询指定未读消息数
                long unread = conversation.getUnReadCount();
                holder.setVisible(R.id.tv_recent_unread, unread>0);
                holder.setText(R.id.tv_recent_unread, String.valueOf(unread));
                holder.itemView.setOnClickListener(v -> {
                    toast("哈哈哈");
                    if (conversation instanceof PrivateConversation){
                        PrivateConversation privateConversation = (PrivateConversation) conversation;
                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        intent.putExtra("c", privateConversation.conversation);
                        startActivity(intent);
                    }
                });
                holder.itemView.setOnLongClickListener(v -> {
                    conversation.onLongClick(getContext());
                    return true;
                });
            }
        };
        recyclerView.setAdapter(adapter);
        refreshLayout.setEnableLoadMore(false);
        setListener();
    }



    private void setListener() {
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                rootView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                refreshLayout.finishRefresh();
                query();
            }
        });
        refreshLayout.setOnRefreshListener(refreshLayout -> query());
    }

    /**
      查询本地会话
     */
    public void query(){
        adapter.update(getConversations());
        refreshLayout.finishRefresh();
    }

    /**
     * 获取会话列表的数据：增加新朋友会话
     * @return
     */
    private List<Conversation> getConversations(){
        //添加会话
        List<Conversation> conversationList = new ArrayList<>();
        conversationList.clear();
        //TODO 会话：4.2、查询全部会话
        List<BmobIMConversation> list =BmobIM.getInstance().loadAllConversation();
        if(list!=null && list.size()>0){
            for (BmobIMConversation item:list){
                switch (item.getConversationType()){
                    case 1://私聊
                        conversationList.add(new PrivateConversation(item));
                        break;
                    default:
                        break;
                }
            }
        }
        //添加新朋友会话-获取好友请求表中最新一条记录
        List<NewFriend> friends = NewFriendManager.getInstance(getActivity()).getAllNewFriend();
        if(friends!=null && friends.size()>0){
            conversationList.add(new NewFriendConversation(friends.get(0)));
        }
        //重新排序
        Collections.sort(conversationList);
        return conversationList;
    }

    /**注册自定义消息接收事件
     * @param event
     */
    @Subscribe
    public void onEventMainThread(RefreshEvent event){
        LogUtils.e("---会话页接收到自定义消息---");
        //因为新增`新朋友`这种会话类型
        adapter.update(getConversations());
    }

    /**注册离线消息接收事件
     * @param event
     */
    @Subscribe
    public void onEventMainThread(OfflineMessageEvent event){
        //重新刷新列表
        adapter.update(getConversations());
    }

    /**注册消息接收事件
     * @param event
     * 1、与用户相关的由开发者自己维护，SDK内部只存储用户信息
     * 2、开发者获取到信息后，可调用SDK内部提供的方法更新会话
     */
    @Subscribe
    public void onEventMainThread(MessageEvent event){
        //重新获取本地消息并刷新列表
        adapter.update(getConversations());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
