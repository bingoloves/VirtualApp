package cn.cqs.im.holder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bingoloves.plugin_spa_demo.R;
import java.text.SimpleDateFormat;
import butterknife.BindView;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMSendStatus;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.exception.BmobException;
import cn.cqs.im.adapter.OnRecyclerViewListener;
import cn.cqs.im.model.UserModel;

/**
 * 发送的文本类型
 */
public class SendTextHolder extends BaseViewHolder implements View.OnClickListener,View.OnLongClickListener {

  @BindView(R.id.iv_avatar)
  protected ImageView iv_avatar;

  @BindView(R.id.iv_fail_resend)
  protected ImageView iv_fail_resend;

  @BindView(R.id.tv_time)
  protected TextView tv_time;

  @BindView(R.id.tv_message)
  protected TextView tv_message;
  @BindView(R.id.tv_send_status)
  protected TextView tv_send_status;

  @BindView(R.id.progress_load)
  protected ProgressBar progress_load;

  BmobIMConversation c;

  public SendTextHolder(Context context, ViewGroup root,BmobIMConversation c,OnRecyclerViewListener listener) {
    super(context, root, R.layout.item_chat_sent_message, listener);
    this.c =c;
  }

  @Override
  public void bindData(Object o) {
    final BmobIMMessage message = (BmobIMMessage)o;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    final BmobIMUserInfo info = message.getBmobIMUserInfo();
    String avatar = info.getAvatar();
    if (TextUtils.isEmpty(avatar)&&info.getUserId().equals(UserModel.getInstance().getCurrentUser().getObjectId())){
        avatar = UserModel.getInstance().getCurrentUser().getAvatar();
    }
    loadImage(avatar,iv_avatar);
    String time = dateFormat.format(message.getCreateTime());
    String content = message.getContent();
    tv_message.setText(content);
    tv_time.setText(time);

    int status =message.getSendStatus();
    if (status == BmobIMSendStatus.SEND_FAILED.getStatus()) {
      iv_fail_resend.setVisibility(View.VISIBLE);
      progress_load.setVisibility(View.GONE);
    } else if (status== BmobIMSendStatus.SENDING.getStatus()) {
      iv_fail_resend.setVisibility(View.GONE);
      progress_load.setVisibility(View.VISIBLE);
    } else {
      iv_fail_resend.setVisibility(View.GONE);
      progress_load.setVisibility(View.GONE);
    }

    tv_message.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        toast("点击"+message.getContent());
        if(onRecyclerViewListener!=null){
          onRecyclerViewListener.onItemClick(getAdapterPosition());
        }
      }
    });

    tv_message.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        if (onRecyclerViewListener != null) {
          onRecyclerViewListener.onItemLongClick(getAdapterPosition());
        }
        return true;
      }
    });

    iv_avatar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        toast("点击" + info.getName() + "的头像");
      }
    });

    //重发
    iv_fail_resend.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        c.resendMessage(message, new MessageSendListener() {
          @Override
          public void onStart(BmobIMMessage msg) {
            progress_load.setVisibility(View.VISIBLE);
            iv_fail_resend.setVisibility(View.GONE);
            tv_send_status.setVisibility(View.INVISIBLE);
          }

          @Override
          public void done(BmobIMMessage msg, BmobException e) {
            if(e==null){
              tv_send_status.setVisibility(View.VISIBLE);
              tv_send_status.setText("已发送");
              iv_fail_resend.setVisibility(View.GONE);
              progress_load.setVisibility(View.GONE);
            }else{
              iv_fail_resend.setVisibility(View.VISIBLE);
              progress_load.setVisibility(View.GONE);
              tv_send_status.setVisibility(View.INVISIBLE);
            }
          }
        });
      }
    });
  }

  public void showTime(boolean isShow) {
    tv_time.setVisibility(isShow ? View.VISIBLE : View.GONE);
  }
}
