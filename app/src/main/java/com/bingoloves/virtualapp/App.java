package com.bingoloves.virtualapp;

import android.app.Application;

import com.bingoloves.plugin_core.skin.loader.SkinManager;
import com.bingoloves.plugin_core.utils.Utils;

/**
 * Created by bingo on 2020/11/23.
 *
 * @Author: bingo
 * @Email: 657952166@qq.com
 * @Description: 类作用描述
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/11/23
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        initSkinLoader();
    }
    /**
     * Must call init first
     */
    private void initSkinLoader() {
        SkinManager.getInstance().init(this);
        SkinManager.getInstance().load();
    }
}
