package com.bingoloves.plugin_spa_demo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;

import com.bingoloves.plugin_core.base.BaseApplication;
import com.bingoloves.plugin_core.http.MMKVHelper;
import com.bingoloves.plugin_core.utils.Utils;
import com.kk.taurus.playerbase.config.PlayerConfig;
import com.kk.taurus.playerbase.config.PlayerLibrary;
import com.kk.taurus.playerbase.entity.DecoderPlan;
import com.kk.taurus.playerbase.record.PlayRecordManager;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.DefaultRefreshHeaderCreator;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedHashMap;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;
import cn.cqs.im.DemoMessageHandler;
import cn.cqs.video.ijk.IjkPlayer;

/**
 * Created by bingo on 2020/11/25.
 *
 * @Author: bingo
 * @Email: 657952166@qq.com
 * @Description: 类作用描述
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/11/25
 */

public class App extends BaseApplication{

    @Override
    public void onCreate() {
        super.onCreate();
        initBmob();
        iniVideoPlayer();
        initBugly();
    }
    /**
     * 可以自定义崩溃数据
     * <p>注意：
     * 1、CrashReport.putUserData(context, key, value);
     *    最多可以有9对自定义的key-value（超过则添加失败）；
     *    key限长50字节，value限长200字节，过长截断；
     *    key必须匹配正则：[a-zA-Z[0-9]]+。
     * 2、CrashReport.setUserId();//用于精确定位某个用户
     *
     * </p
     */
    private void initBugly() {
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this);
        strategy.setCrashHandleCallback(new CrashReport.CrashHandleCallback() {
            public Map<String, String> onCrashHandleStart(int crashType, String errorType, String errorMessage, String errorStack) {
                LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
                //这里的上报数据 会在bugly后台 崩溃分析>列表>详情>跟踪数据>附件信息中得extraMessage.txt文件中
                //map.put("userName", "Hello World");
                return map;
            }

            @Override
            public byte[] onCrashHandleStart2GetExtraDatas(int crashType, String errorType, String errorMessage, String errorStack) {
                try {
                    return "Extra data.".getBytes("UTF-8");
                } catch (Exception e) {
                    return null;
                }
            }

        });
        CrashReport.initCrashReport(getApplicationContext(), BuildConfig.BUGLY_ID, true,strategy);
    }

    public static final int PLAN_ID_IJK = 1;
    public static final int PLAN_ID_EXO = 2;
    private void iniVideoPlayer() {
        PlayerConfig.addDecoderPlan(new DecoderPlan(PLAN_ID_IJK, IjkPlayer.class.getName(), "IjkPlayer"));
//        PlayerConfig.addDecoderPlan(new DecoderPlan(PLAN_ID_EXO, ExoMediaPlayer.class.getName(), "ExoPlayer"));
        PlayerConfig.setDefaultPlanId(PLAN_ID_IJK);
        //use default NetworkEventProducer.
        PlayerConfig.setUseDefaultNetworkEventProducer(true);
        PlayerConfig.playRecord(true);
        PlayRecordManager.setRecordConfig(new PlayRecordManager.RecordConfig.Builder().setMaxRecordCount(100).build());
        PlayerLibrary.init(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * 初始化缓存数据
     */
    public static boolean isLogin() {
        boolean isLogin = MMKVHelper.decodeBoolean(Constants.IS_LOGIN);
        return isLogin;
    }

    public void initBmob(){
        /**
         * SDK初始化方式一
         */
        //Bmob.initialize(application, BuildConfig.BMOB_APP_ID);
        /**
         * SDK初始化方式二
         * 设置BmobConfig，允许设置请求超时时间、文件分片上传时每片的大小、文件的过期时间
         */
        BmobConfig config = new BmobConfig.Builder(this)
                //设置APPID
                .setApplicationId(BuildConfig.BMOB_APP_ID)
                //请求超时时间（单位为秒）：默认15s
                .setConnectTimeout(30)
                //文件分片上传时每片的大小（单位字节），默认512*1024
                .setUploadBlockSize(1024 * 1024)
                //文件的过期时间(单位为秒)：默认1800s
                .setFileExpiration(5500)
                .build();
        Bmob.initialize(config);

        if (getApplicationInfo().packageName.equals(getMyProcessName())){
            BmobIM.init(this);
            BmobIM.registerDefaultMessageHandler(new DemoMessageHandler(this));
        }
    }
    /**
     * 获取当前运行的进程名
     * @return
     */
    public static String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * SmartRefresh 刷新库全局配置头部
     * static 代码段可以防止内存泄露
     */
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> new ClassicsHeader(context).setArrowResource(R.drawable.ic_arrow_down_default).setEnableLastTime(false));
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) ->  new ClassicsFooter(context).setDrawableSize(20));
    }
}
