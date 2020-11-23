package com.bingoloves.plugin_core.skin.entity;

import android.graphics.drawable.Drawable;

import java.io.File;

/**
 * Created by bingo on 2020/11/23.
 *
 * @Author: bingo
 * @Email: 657952166@qq.com
 * @Description: 皮肤文件的信息
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/11/23
 */

public class SkinInfo {
    private String name;
    private String packageName;
    private String versionName;
    private int versionCode;
    private Drawable icon;
    private String skinName;
    private File file;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getSkinName() {
        return skinName;
    }

    public void setSkinName(String skinName) {
        this.skinName = skinName;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "SkinInfo{" +
                "name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                ", versionName='" + versionName + '\'' +
                ", versionCode=" + versionCode +
                ", icon=" + icon +
                ", skinName='" + skinName + '\'' +
                ", file=" + file +
                '}';
    }
}
