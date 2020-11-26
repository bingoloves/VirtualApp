package com.bingoloves.plugin_spa_demo.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.bingoloves.plugin_spa_demo.BuildConfig;
import com.tbruyelle.rxpermissions2.RxPermissions;

/**
 * Created by bingo on 2020/9/18.
 * 动态权限申请
 */

public class PermissionsUtils {
    /**
     * 请求动态权限
     * @param activity
     * @param permissions  多项权限有一个拒绝，都会返回accept = false
     * @param onPermissionResultListener
     */
    public static void request(Activity activity,String[] permissions,OnPermissionResultListener onPermissionResultListener){
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.request(permissions)
                     .subscribe(accept->{
                         if (onPermissionResultListener != null){
                             onPermissionResultListener.onResult(accept);
                         }
                     });
    }

    /**
     * 请求单项权限
     * @param activity
     * @param permission
     * @param onPermissionResultListener
     */
    public static void request(Activity activity,String permission, OnPermissionResultListener onPermissionResultListener){
        RxPermissions rxPermissions = new RxPermissions(activity);
        rxPermissions.request(permission)
                     .subscribe(accept -> {
                         if (onPermissionResultListener != null){
                             onPermissionResultListener.onResult(accept);
                         }
                     });
    }
    /**
     * 检查某个权限是否被申请
     *
     * @param activity
     */
    public static boolean checkPermissions(Activity activity,String permission) {
        RxPermissions permissions = new RxPermissions(activity);
        permissions.setLogging(true);
        return permissions.isGranted(permission);
    }

    /**
     * 打开手机设置页面
     * @param context
     */
    public static void gotoPermissionSetting(Context context) {
        String brand = Build.BRAND;//手机厂商
        if (TextUtils.equals(brand.toLowerCase(), "redmi") || TextUtils.equals(brand.toLowerCase(), "xiaomi")) {
            gotoMiuiPermission(context);//小米
        } else if (TextUtils.equals(brand.toLowerCase(), "meizu")) {
            gotoMeizuPermission(context);
        } else if (TextUtils.equals(brand.toLowerCase(), "huawei") || TextUtils.equals(brand.toLowerCase(), "honor")) {
            gotoHuaweiPermission(context);
        } else {
            context.startActivity(getAppDetailSettingIntent(context));
        }
    }


    /**
     * 跳转到miui的权限管理页面
     */
    private static void gotoMiuiPermission(Context context) {
        try { // MIUI 8
            Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
            localIntent.putExtra("extra_pkgname", context.getPackageName());
            context.startActivity(localIntent);
        } catch (Exception e) {
            try { // MIUI 5/6/7
                Intent localIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
                localIntent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                localIntent.putExtra("extra_pkgname", context.getPackageName());
                context.startActivity(localIntent);
            } catch (Exception e1) { // 否则跳转到应用详情
                context.startActivity(getAppDetailSettingIntent(context));
            }
        }
    }

    /**
     * 跳转到魅族的权限管理系统
     */
    private static void gotoMeizuPermission(Context context) {
        try {
            Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            context.startActivity(getAppDetailSettingIntent(context));
        }
    }

    /**
     * 华为的权限管理页面
     */
    private static void gotoHuaweiPermission(Context context) {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
            intent.setComponent(comp);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            context.startActivity(getAppDetailSettingIntent(context));
        }
    }

    /**
     * 获取应用详情页面intent（如果找不到要跳转的界面，也可以先把用户引导到系统设置页面）
     */
    private static Intent getAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        return localIntent;
    }

    public interface OnPermissionResultListener{
        void onResult(boolean accept);
    }
}
