package com.bingoloves.plugin_spa_demo;

import android.app.Application;
import com.bingoloves.plugin_core.http.MMKVHelper;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import cn.bmob.newim.BmobIM;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;
import cn.cqs.im.DemoMessageHandler;

/**
 * Created by bingo on 2020/11/25.
 *
 * @Author: bingo
 * @Email: 657952166@qq.com
 * @Description: 类作用描述
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/11/25
 */

public class App extends Application{

    public static boolean isLogin = false;

    @Override
    public void onCreate() {
        super.onCreate();
        iniCache();
        initBmob();
    }

    /**
     * 初始化缓存数据
     */
    private void iniCache() {
        MMKVHelper.init(this);
        isLogin = MMKVHelper.decodeBoolean(Constants.IS_LOGIN);
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
