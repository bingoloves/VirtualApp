package com.bingoloves.plugin_spa_demo.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import com.bingoloves.plugin_spa_demo.App;
import com.bingoloves.plugin_spa_demo.R;
import com.bingoloves.plugin_spa_demo.utils.PermissionsUtils;
import com.gyf.immersionbar.ImmersionBar;

import butterknife.BindView;
import butterknife.OnClick;
import cn.cqs.im.base.BaseActivity;

public class SplashActivity extends BaseActivity {

    @BindView(R.id.iv_splash)
    ImageView splashIv;
    @BindView(R.id.tv_close)
    TextView closeTv;
    @OnClick(R.id.tv_close)
    public void clickEvent(){
        startActivity(new Intent(mActivity,LoginActivity.class));
        finish();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initData() {

    }
    private String[] needPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA
    };
    @Override
    protected void initView() {
        PermissionsUtils.request(this, needPermissions, accept -> {
            if (!accept){
                PermissionsUtils.gotoPermissionSetting(mActivity);
            } else {
                new Handler().postDelayed(() -> {
                    if (App.isLogin){
                        startActivity(new Intent(mActivity,MainActivity.class));
                    } else {
                        startActivity(new Intent(mActivity,LoginActivity.class));
                    }finish();
                },1500);
            }
        });
    }
}
