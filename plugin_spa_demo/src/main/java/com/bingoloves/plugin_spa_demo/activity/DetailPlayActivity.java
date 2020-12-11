package com.bingoloves.plugin_spa_demo.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.widget.RelativeLayout;

import com.bingoloves.plugin_spa_demo.R;
import com.bingoloves.plugin_spa_demo.bean.VideoBean;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.player.IPlayer;
import butterknife.BindView;
import cn.cqs.im.base.BaseActivity;
import cn.cqs.video.base.ISPayer;
import cn.cqs.video.play.DataInter;
import cn.cqs.video.play.ListPlayer;
import cn.cqs.video.play.OnHandleListener;
import cn.cqs.video.utils.OrientationSensor;

public class DetailPlayActivity extends BaseActivity implements OnHandleListener {

    private static final String KEY_GO_PLAY = "go_on_play";
    private static final String KEY_ITEM_DATA = "item_data";
    @BindView(R.id.layoutContainer)
    RelativeLayout mPlayerContainer;

    private boolean isLandScape;

    private OrientationSensor mOrientationSensor;

    public static void launch(Context context, boolean goOnPlay, VideoBean item){
        Intent intent = new Intent(context, DetailPlayActivity.class);
        intent.putExtra(KEY_GO_PLAY, goOnPlay);
        intent.putExtra(KEY_ITEM_DATA, item);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_detail;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
//                , WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        mPlayerContainer = findViewById(R.id.layoutContainer);
        boolean goOnPlay = getIntent().getBooleanExtra(KEY_GO_PLAY, false);
        VideoBean item = (VideoBean) getIntent().getSerializableExtra(KEY_ITEM_DATA);
        ListPlayer.get().setReceiverConfigState(this, ISPayer.RECEIVER_GROUP_CONFIG_DETAIL_PORTRAIT_STATE);
        ListPlayer.get().attachContainer(mPlayerContainer);
        ListPlayer.get().setOnHandleListener(this);
        ListPlayer.get().updateGroupValue(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, true);
        if(!goOnPlay){
            ListPlayer.get().play(new DataSource(item.getPath()));
        }
        mOrientationSensor = new OrientationSensor(this, onOrientationListener);
    }

    private OrientationSensor.OnOrientationListener onOrientationListener =
            new OrientationSensor.OnOrientationListener() {
        @Override
        public void onLandScape(int orientation) {
            if(ListPlayer.get().isInPlaybackState()){
                setRequestedOrientation(orientation);
            }
        }
        @Override
        public void onPortrait(int orientation) {
            if(ListPlayer.get().isInPlaybackState()){
                setRequestedOrientation(orientation);
            }
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        isLandScape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    public void finish() {
        ListPlayer.get().stop();
        mOrientationSensor.disable();
        super.finish();
    }

    private void toggleScreen(){
        setRequestedOrientation(isLandScape?
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void onBackPressed() {
        if(isLandScape){
            toggleScreen();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        int state = ListPlayer.get().getState();
        if(state == IPlayer.STATE_PLAYBACK_COMPLETE)
            return;
        if(ListPlayer.get().isInPlaybackState()){
            ListPlayer.get().resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        int state = ListPlayer.get().getState();
        if(state == IPlayer.STATE_PLAYBACK_COMPLETE)
            return;
        if(ListPlayer.get().isInPlaybackState())
            ListPlayer.get().pause();
    }

    @Override
    public void onStart() {
        super.onStart();
        ListPlayer.get().attachActivity(this);
        mOrientationSensor.enable();
    }

    @Override
    public void onStop() {
        super.onStop();
        mOrientationSensor.disable();
    }

    @Override
    public void onBack() {
        onBackPressed();
    }

    @Override
    public void onToggleScreen() {
        toggleScreen();
    }
}
