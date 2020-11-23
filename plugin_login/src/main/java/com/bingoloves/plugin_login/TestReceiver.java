package com.bingoloves.plugin_login;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.bingoloves.plugin_core.base.BaseBroadcastReceiver;

public class TestReceiver extends BaseBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "TestReceiver onReceiver 回调完成", Toast.LENGTH_SHORT).show();
        super.onReceive(context, intent);
    }
}
