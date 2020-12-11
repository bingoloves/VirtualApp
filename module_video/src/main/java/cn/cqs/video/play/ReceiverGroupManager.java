package cn.cqs.video.play;

import android.content.Context;
import com.kk.taurus.playerbase.receiver.GroupValue;
import com.kk.taurus.playerbase.receiver.ReceiverGroup;

import cn.cqs.video.cover.CompleteCover;
import cn.cqs.video.cover.ControllerCover;
import cn.cqs.video.cover.ErrorCover;
import cn.cqs.video.cover.GestureCover;
import cn.cqs.video.cover.LoadingCover;

import static cn.cqs.video.play.DataInter.ReceiverKey.KEY_COMPLETE_COVER;
import static cn.cqs.video.play.DataInter.ReceiverKey.KEY_CONTROLLER_COVER;
import static cn.cqs.video.play.DataInter.ReceiverKey.KEY_ERROR_COVER;
import static cn.cqs.video.play.DataInter.ReceiverKey.KEY_GESTURE_COVER;
import static cn.cqs.video.play.DataInter.ReceiverKey.KEY_LOADING_COVER;

/**
 * Created by Taurus on 2018/4/18.
 */

public class ReceiverGroupManager {

    private static ReceiverGroupManager i;

    private ReceiverGroupManager(){
    }

    public static ReceiverGroupManager get(){
        if(null==i){
            synchronized (ReceiverGroupManager.class){
                if(null==i){
                    i = new ReceiverGroupManager();
                }
            }
        }
        return i;
    }

    public ReceiverGroup getLittleReceiverGroup(Context context){
        return getLiteReceiverGroup(context, null);
    }

    public ReceiverGroup getLittleReceiverGroup(Context context, GroupValue groupValue){
        ReceiverGroup receiverGroup = new ReceiverGroup(groupValue);
        receiverGroup.addReceiver(KEY_LOADING_COVER, new LoadingCover(context));
        receiverGroup.addReceiver(KEY_COMPLETE_COVER, new CompleteCover(context));
        receiverGroup.addReceiver(KEY_ERROR_COVER, new ErrorCover(context));
        return receiverGroup;
    }

    public ReceiverGroup getLiteReceiverGroup(Context context){
        return getLiteReceiverGroup(context, null);
    }

    public ReceiverGroup getLiteReceiverGroup(Context context, GroupValue groupValue){
        ReceiverGroup receiverGroup = new ReceiverGroup(groupValue);
        receiverGroup.addReceiver(KEY_LOADING_COVER, new LoadingCover(context));
        receiverGroup.addReceiver(KEY_CONTROLLER_COVER, new ControllerCover(context));
        receiverGroup.addReceiver(KEY_COMPLETE_COVER, new CompleteCover(context));
        receiverGroup.addReceiver(KEY_ERROR_COVER, new ErrorCover(context));
        return receiverGroup;
    }

    public ReceiverGroup getReceiverGroup(Context context){
        return getReceiverGroup(context, null);
    }

    public ReceiverGroup getReceiverGroup(Context context, GroupValue groupValue){
        ReceiverGroup receiverGroup = new ReceiverGroup(groupValue);
        receiverGroup.addReceiver(KEY_LOADING_COVER, new LoadingCover(context));
        receiverGroup.addReceiver(KEY_CONTROLLER_COVER, new ControllerCover(context));
        receiverGroup.addReceiver(KEY_GESTURE_COVER, new GestureCover(context));
        receiverGroup.addReceiver(KEY_COMPLETE_COVER, new CompleteCover(context));
        receiverGroup.addReceiver(KEY_ERROR_COVER, new ErrorCover(context));
        return receiverGroup;
    }

}
