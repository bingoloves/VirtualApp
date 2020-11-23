package com.bingoloves.virtualapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bingoloves.plugin_core.proxy.ProxyActivity;
import com.bingoloves.plugin_core.utils.PluginManager;
import com.bingoloves.plugin_core.utils.log.LogUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        PluginManager.getInstance().loadPlugin("plugin_login-debug.apk");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_add_plugin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PluginManager.getInstance().startPluginApp("plugin_login-debug.apk");
            }
        });
        // 注册静态广播
        //findViewById(R.id.btn_register).setOnClickListener(v -> PluginManager.getInstance().parserApkAction("plugin_login-debug.apk"));
    }
}
