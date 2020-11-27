package cn.cqs.im.bean;

import android.text.TextUtils;
import com.bingoloves.plugin_core.utils.log.LogUtils;
import org.json.JSONObject;
import cn.bmob.newim.bean.BmobIMExtraMessage;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.cqs.im.Config;
import cn.cqs.im.db.NewFriend;

/**
 * 添加好友请求
 *
 * @author :smile
 * @project:AddFriendMessage
 * @date :2016-01-30-17:28
 */
//TODO 自定义消息：7.2、自定义消息类型，用于发送添加好友请求
//TODO 好友管理：9.5、自定义添加好友的消息类型
public class AddFriendMessage extends BmobIMExtraMessage {


    public static final String ADD = "add";

    public AddFriendMessage() {
    }

    /**
     * 将BmobIMMessage转成NewFriend
     *
     * @param msg 消息
     * @return
     */
    public static NewFriend convert(BmobIMMessage msg) {
        NewFriend add = new NewFriend();
        String content = msg.getContent();
        add.setMsg(content);
        add.setTime(msg.getCreateTime());
        add.setStatus(Config.STATUS_VERIFY_NONE);
        try {
            String extra = msg.getExtra();
            if (!TextUtils.isEmpty(extra)) {
                JSONObject json = new JSONObject(extra);
                String name = json.getString("name");
                add.setName(name);
                String avatar = json.getString("avatar");
                add.setAvatar(avatar);
                add.setUid(json.getString("uid"));
            } else {
                LogUtils.i("AddFriendMessage的extra为空");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return add;
    }


    @Override
    public String getMsgType() {
        return ADD;
    }

    @Override
    public boolean isTransient() {
        //设置为true,表明为暂态消息，那么这条消息并不会保存到本地db中，SDK只负责发送出去
        //设置为false,则会保存到指定会话的数据库中
        return true;
    }

}
