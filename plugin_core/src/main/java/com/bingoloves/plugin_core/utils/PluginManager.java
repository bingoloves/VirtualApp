package com.bingoloves.plugin_core.utils;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.bingoloves.plugin_core.proxy.ProxyActivity;
import com.bingoloves.plugin_core.utils.log.LogUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dalvik.system.DexClassLoader;

public class PluginManager {
    private volatile static PluginManager sPluginManager;
    private Context mContext;
    /**
     * 插件缓存
     */
    private Map<String,File> pluginCache;
    private DexClassLoader mPluginClassLoader;
    private Resources mPluginResources;

//    public static PluginManager getInstance(Context context) {
//        if (sPluginManager == null) {
//            synchronized (PluginManager.class) {
//                if (sPluginManager == null) {
//                    sPluginManager = new PluginManager(context);
//                }
//            }
//        }
//        return sPluginManager;
//    }
    public static PluginManager getInstance() {
        if (sPluginManager == null) {
            synchronized (PluginManager.class) {
                if (sPluginManager == null) {
                    sPluginManager = new PluginManager(Utils.getApp());
                }
            }
        }
        return sPluginManager;
    }
    public PluginManager(Context context) {
        mContext = context;
        pluginCache = new HashMap<>();
    }

    public void loadPlugin(String pluginName) {
        if (!pluginCache.containsKey(pluginName)){
            Utils.copyFileFromAssets(mContext, pluginName);
        }
        File extractFile = mContext.getFileStreamPath(pluginName);
        pluginCache.put(pluginName,extractFile);
        String dexPath = extractFile.getPath();
        File fileRelease = mContext.getDir("dex", Context.MODE_PRIVATE);
        mPluginClassLoader = new DexClassLoader(dexPath, fileRelease.getAbsolutePath(), null, mContext.getClassLoader());
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            Method method = AssetManager.class.getMethod("addAssetPath", String.class);
            method.invoke(assetManager, dexPath);
            mPluginResources = new Resources(assetManager, mContext.getResources().getDisplayMetrics(),
                    mContext.getResources().getConfiguration());
        } catch (Exception e) {
            Toast.makeText(mContext, pluginName+"插件加载失败", Toast.LENGTH_SHORT).show();
        }
    }

    public DexClassLoader getClassLoader() {
        return mPluginClassLoader;
    }

    public Resources getResources() {
        return mPluginResources;
    }

    @SuppressLint("PrivateApi")
    public void parserApkAction(String pluginName) {
        try {
            Class packageParserClass = Class.forName("android.content.pm.PackageParser");
            Object packageParser = packageParserClass.newInstance();
            Method method = packageParserClass.getMethod("parsePackage", File.class, int.class);
            File extractFile = mContext.getFileStreamPath(pluginName);
            Object packageObject = method.invoke(packageParser, extractFile, PackageManager.GET_RECEIVERS);
            Field receiversFields = packageObject.getClass().getDeclaredField("receivers");
            ArrayList arrayList = (ArrayList) receiversFields.get(packageObject);

            Class packageUserStateClass = Class.forName("android.content.pm.PackageUserState");
            Class userHandleClass = Class.forName("android.os.UserHandle");
            int userId = (int) userHandleClass.getMethod("getCallingUserId").invoke(null);

            for (Object activity : arrayList) {
                Class component = Class.forName("android.content.pm.PackageParser$Component");
                Field intents = component.getDeclaredField("intents");
                // 1.获取 Intent-Filter
                ArrayList<IntentFilter> intentFilterList = (ArrayList<IntentFilter>) intents.get(activity);
                // 2.需要获取到广播的全类名，通过 ActivityInfo 获取
                // ActivityInfo generateActivityInfo(Activity a, int flags, PackageUserState state, int userId)
                Method generateActivityInfoMethod = packageParserClass.getMethod("generateActivityInfo", activity.getClass()
                        , int.class, packageUserStateClass, int.class);
                ActivityInfo activityInfo = (ActivityInfo) generateActivityInfoMethod.invoke(null, activity, 0,
                        packageUserStateClass.newInstance(), userId);
                Class broadcastReceiverClass = getClassLoader().loadClass(activityInfo.name);
                BroadcastReceiver broadcastReceiver = (BroadcastReceiver) broadcastReceiverClass.newInstance();
                for (IntentFilter intentFilter : intentFilterList) {
                    mContext.registerReceiver(broadcastReceiver, intentFilter);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动插件App
     * @param pluginName
     */
    public void startPluginApp(String pluginName) {
        String dexPath = mContext.getFileStreamPath(pluginName).getPath();
        PackageInfo packageInfo = mContext.getPackageManager().getPackageArchiveInfo(dexPath, PackageManager.GET_ACTIVITIES);
        ActivityInfo activityInfo = packageInfo.activities[0];
        Intent intent = new Intent(mContext, ProxyActivity.class);
        intent.putExtra(ProxyActivity.EXT_CLASS_NAME, activityInfo.name);
        mContext.startActivity(intent);
    }
    /**
     * 启动插件App 中某个Activity
     * @param pluginName 插件名
     * @param activityName  完整类名
     */
    public void startPluginActivity(String pluginName,String activityName) {
        if (TextUtils.isEmpty(activityName)) return;
        String dexPath = mContext.getFileStreamPath(pluginName).getPath();
        PackageInfo packageInfo = mContext.getPackageManager().getPackageArchiveInfo(dexPath, PackageManager.GET_ACTIVITIES);
        boolean isExist = true;
        for (ActivityInfo info : packageInfo.activities) {
            LogUtils.e( "ActivityInfo: " + info.name);
            if (info.name.equals(activityName)){
                isExist = true;
                break;
            } else {
                isExist = false;
            }
        }
        LogUtils.e("isExist = "+isExist);
        Intent intent = new Intent(mContext, ProxyActivity.class);
        intent.putExtra(ProxyActivity.EXT_CLASS_NAME, activityName);
        mContext.startActivity(intent);
    }
    /**
     * 启动某个Activity未被注册的Activity
     * @param activityName  完整类名
     */
    public void startActivityNoRegisterManifast(String activityName) {
        if (TextUtils.isEmpty(activityName)) return;
        Intent intent = new Intent(mContext, ProxyActivity.class);
        intent.putExtra(ProxyActivity.EXT_CLASS_NAME, activityName);
        mContext.startActivity(intent);
    }
}
