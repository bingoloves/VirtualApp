package com.bingoloves.plugin_spa_demo.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bingoloves.plugin_core.adapter.recyclerview.CommonAdapter;
import com.bingoloves.plugin_spa_demo.R;
import com.bingoloves.plugin_spa_demo.adapter.VideoListAdapter;
import com.bingoloves.plugin_spa_demo.bean.VideoBean;
import com.gyf.immersionbar.ImmersionBar;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.player.IPlayer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.cqs.im.base.BaseActivity;
import cn.cqs.video.base.ISPayer;
import cn.cqs.video.play.DataInter;
import cn.cqs.video.play.ListPlayer;
import cn.cqs.video.play.OnHandleListener;
import cn.cqs.video.utils.OrientationSensor;

/**
 * Created by bingo on 2020/12/8.
 *
 * @Author: bingo
 * @Email: 657952166@qq.com
 * @Description: 类作用描述
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/12/8
 */

public class VideoListActivity extends BaseActivity implements VideoListAdapter.OnListListener {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_bar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.rv_video)
    RecyclerView videoRv;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.mIv)
    ImageView mIv;
    @BindView(R.id.playButton)
    View playButton;

    @BindView(R.id.listPlayContainer)
    FrameLayout mPlayerContainer;

    private CommonAdapter adapter;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_list;
    }

    @Override
    protected void initData() {}

    private boolean isLandScape;
    private boolean toDetail;
    private OrientationSensor mOrientationSensor;
    private VideoListAdapter mAdapter;
    @Override
    protected void initView() {
        ImmersionBar.with(this).statusBarDarkFont(true).titleBar(toolbar).init();
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        fab.setOnClickListener(view -> Snackbar.make(view, "我是Snackbar", Snackbar.LENGTH_LONG).show());
        toolbar.setNavigationOnClickListener(v -> finish());
        state = CollapsingToolbarLayoutState.EXPANDED;
        mAppBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (verticalOffset == 0) {
                if (state != CollapsingToolbarLayoutState.EXPANDED) {
                    state = CollapsingToolbarLayoutState.EXPANDED;//修改状态标记为展开
                    collapsingToolbarLayout.setTitle("EXPANDED");//设置title为EXPANDED
                }
            } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                if (state != CollapsingToolbarLayoutState.COLLAPSED) {
                    collapsingToolbarLayout.setTitle("");//设置title不显示
                    playButton.setVisibility(View.VISIBLE);//隐藏播放按钮
                    state = CollapsingToolbarLayoutState.COLLAPSED;//修改状态标记为折叠
                }
            } else {
                if (state != CollapsingToolbarLayoutState.INTERNEDIATE) {
                    if(state == CollapsingToolbarLayoutState.COLLAPSED){
                        playButton.setVisibility(View.GONE);//由折叠变为中间状态时隐藏播放按钮
                    }
                    collapsingToolbarLayout.setTitle("INTERNEDIATE");//设置title为INTERNEDIATE
                    state = CollapsingToolbarLayoutState.INTERNEDIATE;//修改状态标记为中间
                }
            }
        });

        mOrientationSensor = new OrientationSensor(this,onOrientationListener);
        mOrientationSensor.enable();


        videoRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new VideoListAdapter(this, videoRv,getVideoList());
        mAdapter.setOnListListener(this);
        videoRv.setAdapter(mAdapter);

        mOrientationSensor = new OrientationSensor(this,onOrientationListener);
        mOrientationSensor.enable();
    }
    private OrientationSensor.OnOrientationListener onOrientationListener = new OrientationSensor.OnOrientationListener() {
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
        if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE){
            mPlayerContainer.setBackgroundColor(Color.BLACK);
            ListPlayer.get().attachContainer(mPlayerContainer, false);
            ListPlayer.get().setReceiverConfigState(this, ISPayer.RECEIVER_GROUP_CONFIG_FULL_SCREEN_STATE);
        }else{
            mPlayerContainer.setBackgroundColor(Color.TRANSPARENT);
            videoRv.post(new Runnable() {
                @Override
                public void run() {
                    VideoListAdapter.VideoItemHolder currentHolder = mAdapter.getCurrentHolder();
                    if(currentHolder!=null){
                        ListPlayer.get().attachContainer(currentHolder.layoutContainer, false);
                        ListPlayer.get().setReceiverConfigState(mActivity, ISPayer.RECEIVER_GROUP_CONFIG_LIST_STATE);
                    }
                }
            });
        }
        ListPlayer.get().updateGroupValue(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, isLandScape);
        ListPlayer.get().updateGroupValue(DataInter.Key.KEY_IS_LANDSCAPE, isLandScape);
    }

    private void toggleScreen(){
        setRequestedOrientation(isLandScape?
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    public void onResume() {
        super.onResume();
        ListPlayer.get().updateGroupValue(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, isLandScape);
        ListPlayer.get().setOnHandleListener(new OnHandleListener() {
            @Override
            public void onBack() {
                onBackPressed();
            }
            @Override
            public void onToggleScreen() {
                toggleScreen();
            }
        });
        if(!toDetail && ListPlayer.get().isInPlaybackState()){
            ListPlayer.get().resume();
        }
        toDetail = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        int state = ListPlayer.get().getState();
        if(state == IPlayer.STATE_PLAYBACK_COMPLETE)
            return;
        if(!toDetail){
            ListPlayer.get().pause();
        }
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
    public void onBackPressed() {
        if(isLandScape){
            toggleScreen();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onTitleClick(VideoListAdapter.VideoItemHolder holder, VideoBean item, int position) {
        toDetail = true;
        DetailPlayActivity.launch(this, mAdapter.getPlayPosition() == position, item);
        mAdapter.reset();
    }

    @Override
    public void playItem(VideoListAdapter.VideoItemHolder holder, VideoBean item, int position) {
        ListPlayer.get().setReceiverConfigState(this, ISPayer.RECEIVER_GROUP_CONFIG_LIST_STATE);
        ListPlayer.get().attachContainer(holder.layoutContainer);
        ListPlayer.get().play(new DataSource(item.getPath()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mOrientationSensor.disable();
        ListPlayer.get().destroy();
    }

    /**
     * 展开状态的标记
     */
    private CollapsingToolbarLayoutState state;
    private enum CollapsingToolbarLayoutState {
        EXPANDED,
        COLLAPSED,
        INTERNEDIATE
    }

    public List<VideoBean> getVideoList() {
        List<VideoBean> videoList = new ArrayList<>();
        videoList.add(new VideoBean(
                "你欠缺的也许并不是能力",
                "http://open-image.nosdn.127.net/image/snapshot_movie/2016/11/b/a/c36e048e284c459686133e66a79e2eba.jpg",
                "https://mov.bn.netease.com/open-movie/nos/mp4/2016/06/22/SBP8G92E3_hd.mp4"));
        videoList.add(new VideoBean(
                "rtmp类型播放",
                "http://open-image.nosdn.127.net/image/snapshot_movie/2016/11/b/a/c36e048e284c459686133e66a79e2eba.jpg",
                "rtmp://58.200.131.2:1935/livetv/hunantv"));

        videoList.add(new VideoBean(
                "坚持与放弃",
                "http://open-image.nosdn.127.net/image/snapshot_movie/2016/11/0/4/e4c8836bfe154d76a808da38d0733304.jpg",
                "https://mov.bn.netease.com/open-movie/nos/mp4/2015/08/27/SB13F5AGJ_sd.mp4"));

        videoList.add(new VideoBean(
                "不想从被子里出来",
                "http://open-image.nosdn.127.net/57baaaeaad4e4fda8bdaceafdb9d45c2.jpg",
                "https://mov.bn.netease.com/open-movie/nos/mp4/2018/01/12/SD70VQJ74_sd.mp4"));

        videoList.add(new VideoBean(
                "不耐烦的中国人?",
                "http://open-image.nosdn.127.net/image/snapshot_movie/2016/11/e/9/ac655948c705413b8a63a7aaefd4cde9.jpg",
                "https://mov.bn.netease.com/open-movie/nos/mp4/2017/05/31/SCKR8V6E9_hd.mp4"));

        videoList.add(new VideoBean(
                "神奇的珊瑚",
                "http://open-image.nosdn.127.net/image/snapshot_movie/2016/11/e/4/75bc6c5227314e63bbfd5d9f0c5c28e4.jpg",
                "https://mov.bn.netease.com/open-movie/nos/mp4/2016/01/11/SBC46Q9DV_hd.mp4"));

        videoList.add(new VideoBean(
                "怎样经营你的人脉",
                "http://open-image.nosdn.127.net/image/snapshot_movie/2018/3/b/c/9d451a2da3cf42b0a049ba3e249222bc.jpg",
                "https://mov.bn.netease.com/open-movie/nos/mp4/2018/04/19/SDEQS1GO6_hd.mp4"));

        videoList.add(new VideoBean(
                "怎么才能不畏将来",
                "http://open-image.nosdn.127.net/image/snapshot_movie/2018/1/c/8/1aec3637270f465faae52713a7c191c8.jpg",
                "https://mov.bn.netease.com/open-movie/nos/mp4/2018/01/25/SD82Q0AQE_hd.mp4"));

        videoList.add(new VideoBean(
                "音乐和艺术如何改变世界",
                "http://open-image.nosdn.127.net/image/snapshot_movie/2017/12/2/8/f30dd5f2f09c405c98e7eb6c06c89928.jpg",
                "https://mov.bn.netease.com/open-movie/nos/mp4/2017/12/04/SD3SUEFFQ_hd.mp4"));

        return videoList;
    }
}
