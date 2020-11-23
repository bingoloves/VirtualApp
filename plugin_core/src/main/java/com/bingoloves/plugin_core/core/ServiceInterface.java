package com.bingoloves.plugin_core.core;

import android.app.Service;
import android.content.Intent;

/**
 * Created by bingo on 2020/11/23.
 *
 * @Author: bingo
 * @Email: 657952166@qq.com
 * @Description: Service规则定义
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/11/23
 */
public interface ServiceInterface {

    void insertAppContext(Service hostService);

    void onCreate();

    int onStartCommand(Intent intent, int flags, int startId);

    void onDestroy();
}
