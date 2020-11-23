package com.bingoloves.plugin_core.core;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by bingo on 2020/11/23.
 *
 * @Author: bingo
 * @Email: 657952166@qq.com
 * @Description: Activity规则定义
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/11/23
 */
public interface ActivityInterface {

    void insertAppContext(Activity hostActivity);

    void onCreate(Bundle savedInstanceState);

    void onStart();

    void onResume();

    void onPause();

    void onStop();

    void onDestroy();
}
