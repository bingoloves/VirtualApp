package com.bingoloves.plugin_spa_demo.bean;

import android.view.View;

/**
 * Created by bingo on 2020/11/26.
 *
 * @Author: bingo
 * @Email: 657952166@qq.com
 * @Description: 可配置的菜单项
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/11/26
 */

public class MenuItem {
    public String name;
    public int menuIcon;
    public View.OnClickListener clickListener;

    public MenuItem(int menuIcon,String name,View.OnClickListener clickListener) {
        this.name = name;
        this.menuIcon = menuIcon;
        this.clickListener = clickListener;
    }
}
