package com.bingoloves.plugin_core.base;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.bingoloves.plugin_core.core.ServiceInterface;

public class BaseService extends Service implements ServiceInterface {

    public Service mAppService;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void insertAppContext(Service hostService) {
        if (mAppService != null){
            mAppService = hostService;
        }
    }

    @Override
    public void onCreate() {

    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return 0;
    }

    @Override
    public void onDestroy() {

    }
}
