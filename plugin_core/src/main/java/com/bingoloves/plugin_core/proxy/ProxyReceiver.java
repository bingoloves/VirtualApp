package com.bingoloves.plugin_core.proxy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bingoloves.plugin_core.core.BroadcastReceiverInterface;
import com.bingoloves.plugin_core.utils.PluginManager;

public class ProxyReceiver extends BroadcastReceiver {

    private String mReceiverName;

    public ProxyReceiver(String receiverName) {
        mReceiverName = receiverName;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Class clazz = null;
        try {
            clazz = PluginManager.getInstance().getClassLoader().loadClass(mReceiverName);
            BroadcastReceiverInterface receiverInterface = (BroadcastReceiverInterface) clazz.newInstance();
            receiverInterface.onReceive(context, intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
