package com.bingoloves.plugin_core.proxy;

import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.bingoloves.plugin_core.core.ServiceInterface;
import com.bingoloves.plugin_core.utils.PluginManager;

import static com.bingoloves.plugin_core.proxy.ProxyActivity.EXT_CLASS_NAME;

public class ProxyService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 获取到真正要启动的插件 Service，然后执行 onStartCommand 方法
        String className = intent.getStringExtra(EXT_CLASS_NAME);
        try {
            Class clazz = getClassLoader().loadClass(className);
            ServiceInterface serviceInterface = (ServiceInterface) clazz.newInstance();
            serviceInterface.insertAppContext(this);
            serviceInterface.onStartCommand(intent, flags, startId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Resources getResources() {
        return PluginManager.getInstance().getResources();
    }

    @Override
    public ClassLoader getClassLoader() {
        return PluginManager.getInstance().getClassLoader();
    }
}
