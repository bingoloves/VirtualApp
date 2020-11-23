package com.bingoloves.plugin_login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bingoloves.plugin_core.base.BaseActivity;
import com.bingoloves.plugin_core.utils.log.LogUtils;

public class LoginActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getSelfActivity(), TestActivity.class));
            }
        });
        findViewById(R.id.btn_receiver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(new Intent("static_receiver"));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.e("123");
    }
}
