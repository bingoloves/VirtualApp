package com.bingoloves.plugin_spa_demo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bingoloves.plugin_spa_demo.R;
import com.bingoloves.plugin_spa_demo.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class SplashActivity extends BaseActivity {

    @BindView(R.id.iv_splash)
    ImageView splashIv;
    @BindView(R.id.tv_close)
    TextView closeTv;
    @OnClick(R.id.tv_close)
    public void clickEvent(){
        finish();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        new Handler().postDelayed(() -> {
            startActivity(new Intent(mActivity,MainActivity.class));
            finish();
        },2000);
    }
}
