package com.bingoloves.plugin_spa_demo.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import com.bingoloves.plugin_core.http.MMKVHelper;
import com.bingoloves.plugin_core.utils.Utils;
import com.bingoloves.plugin_core.utils.log.LogUtils;
import com.bingoloves.plugin_core.widget.CustomToolbar;
import com.bingoloves.plugin_spa_demo.Constants;
import com.bingoloves.plugin_spa_demo.R;
import cn.cqs.im.bean.User;

import com.gyf.immersionbar.ImmersionBar;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.cqs.im.base.BaseActivity;
import cn.cqs.im.model.UserModel;

/**
 * Created by bingo on 2020/11/26.
 *
 * @Author: bingo
 * @Email: 657952166@qq.com
 * @Description: 类作用描述
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/11/26
 */

public class LoginActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    CustomToolbar toolbar;
    @BindView(R.id.et_name)
    EditText nameEt;
    @BindView(R.id.et_password)
    EditText passwordEt;
    @BindView(R.id.iv_eyes)
    ImageView eyesIv;

    @OnClick({R.id.btn_login,R.id.btn_register,R.id.iv_eyes})
    public void clickEvent(View view){
        switch (view.getId()){
            case R.id.btn_login:
                login(view);
                break;
            case R.id.btn_register:
                register(view);
                break;
            case R.id.iv_eyes:
                setPasswordVisible(passwordEt);
                showPwd = !showPwd;
                eyesIv.setImageResource(showPwd?R.mipmap.login_icon_show:R.mipmap.login_icon_hide);
                break;
            default:
                break;
        }
    }

    /**
     * 是否显示密码
     */
    private boolean showPwd = false;

    private void register(View view) {
        String userName = nameEt.getText().toString().trim();
        String password = passwordEt.getText().toString().trim();
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)){
            Utils.snack(view,"请输入完整信息");
            return;
        }
        UserModel.getInstance().register(userName, password, new LogInListener<User>() {

            @Override
            public void done(User user, BmobException e) {
                if (e == null){
                    toast("注册成功");
                } else {
                    toast("注册失败："+ e.getMessage());
                }
            }
        });
    }

    private void login(View view) {
        String userName = nameEt.getText().toString().trim();
        String password = passwordEt.getText().toString().trim();
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)){
            Utils.snack(view,"请输入完整信息");
            return;
        }
        UserModel.getInstance().login(userName, password, new LogInListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null){
                    toast("登录成功");
                    LogUtils.e(user.toString());
                    MMKVHelper.encode(Constants.IS_LOGIN,true);
                    startActivity(new Intent(mActivity,MainActivity.class));
                    finish();
                } else {
                    toast("登录失败："+ e.getMessage());
                }
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        ImmersionBar.with(this).titleBar(toolbar).statusBarDarkFont(true).init();
        //上次登录的账号密码直接填充
        String userNameCache = MMKVHelper.decodeString(Constants.LOGIN_USER_NAME);
        String passwordCache = MMKVHelper.decodeString(Constants.LOGIN_USER_PASSWORD);
        if (!TextUtils.isEmpty(userNameCache)){
            nameEt.setText(userNameCache);
        }
        if (!TextUtils.isEmpty(passwordCache)){
            passwordEt.setText(passwordCache);
        }
    }

    /**
     * 切换密码的明文显示
     */
    private void setPasswordVisible(EditText editText) {
        if (EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD == editText.getInputType()) {
            editText.setInputType(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
            editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
            editText.setInputType(EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        editText.setSelection(editText.getText().toString().length());
    }
}
