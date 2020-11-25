package com.bingoloves.plugin_spa_demo.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bingoloves.plugin_core.utils.Utils;
import com.bingoloves.plugin_spa_demo.R;
import com.gyf.immersionbar.ImmersionBar;

/**
 * Created by bingo on 2020/11/25.
 *
 * @Author: bingo
 * @Email: 657952166@qq.com
 * @Description: 类作用描述
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/11/25
 */

public class AlertDialogUtils {

    public static AlertDialog show(Activity activity, DialogInterface.OnDismissListener onDismissListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        AlertDialog mAlertDialog = builder.create();
        if (onDismissListener != null){
            mAlertDialog.setOnDismissListener(onDismissListener);
        }
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.layout_alert_dialog, null);
        mAlertDialog.setView(dialogView);
        mAlertDialog.show();
        Window mDialogWindow = mAlertDialog.getWindow();
        if (mDialogWindow != null) {//解决无法弹出输入法的问题
            mDialogWindow.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                    WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        }
        //计算屏幕宽高
        //Integer[] widthAndHeight = Utils.getWidthAndHeight(activity.getWindow());
        //mDialogWindow.setGravity(Gravity.CENTER);
        //mDialogWindow.setWindowAnimations(R.style.RightAnimation);
        //mDialogWindow.setLayout(widthAndHeight[0]*2/3,widthAndHeight[1]/4);
        //mDialogWindow.setLayout(widthAndHeight[0]*2/3,ViewGroup.LayoutParams.WRAP_CONTENT);
        ImmersionBar.with(activity, mAlertDialog)
                .keyboardEnable(true)
                .init();
        return mAlertDialog;
    }
}
