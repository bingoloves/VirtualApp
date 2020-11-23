package com.bingoloves.plugin_core.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

public class DeviceUtil {

    /**
     * 获取安卓设备信息
     * @return
     */
    public static String getDeviceInfo(){
        PackageManager pm = Utils.getApp().getPackageManager();
        StringBuilder sb = new StringBuilder();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(Utils.getApp().getPackageName(), PackageManager.GET_ACTIVITIES);
            sb.append("App VersionName:").append(packageInfo.versionName)
                    .append("\n")
                    .append("VersionCode:").append(packageInfo.versionCode)
                    .append("\n")
                    .append("OS Version:").append(Build.VERSION.RELEASE).append("_").append(Build.VERSION.SDK_INT)
                    .append("\n")
                    .append("Vendor:").append(Build.MANUFACTURER)
                    .append("\n")
                    .append("Model:").append(Build.MODEL)
                    .append("\n")
                    .append("CPU ABI:").append(Build.CPU_ABI);
            return sb.toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取应用名称
     * @return
     */
    public static String getAppName() {
        try {
            PackageManager packageManager = Utils.getApp().getPackageManager();
            return String.valueOf(packageManager.getApplicationLabel(Utils.getApp().getApplicationInfo()));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String getAppName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            return String.valueOf(packageManager.getApplicationLabel(context.getApplicationInfo()));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}
