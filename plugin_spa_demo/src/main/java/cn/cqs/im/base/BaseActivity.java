package cn.cqs.im.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import com.bingoloves.plugin_core.base.PluginActivity;
import com.bingoloves.plugin_core.utils.Injector;
import com.gyf.immersionbar.ImmersionBar;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.cqs.im.R;

/**
 * Created by bingo on 2020/11/25.
 *
 * @Author: bingo
 * @Email: 657952166@qq.com
 * @Description: 类作用描述
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/11/25
 */

public abstract class BaseActivity extends PluginActivity{
    protected Activity mActivity;
    private Unbinder unBinder;
    /**
     * 取消页面跳转动画
     * @return
     */
    public boolean cancelNavigateAnimation(){
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (!cancelNavigateAnimation())overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(getLayoutId());
        //绑定控件
        unBinder = ButterKnife.bind(this);
        //注入工具
        Injector.inject(this);
        //初始化沉浸式
        initImmersionBar();
        //初始化数据
        initData();
        //view与数据绑定
        initView();
    }


    /**
     * 子类设置布局Id
     *
     * @return the layout id
     */
    protected abstract int getLayoutId();

    protected abstract void initData();

    protected abstract void initView();

    /**
     * 初始化沉浸式
     * Init immersion bar.
     */
    protected void initImmersionBar() {
        ImmersionBar.with(this).init();
    }
    /**
     * 页面跳转
     * @param cls
     */
    protected void navigateTo(Class<?> cls){
        navigateTo(new Intent(),cls);
    }
    protected void navigateTo(Intent intent,Class<?> cls){
        intent.setClass(mActivity,cls);
        startActivity(intent);
    }
    /**
     * toast
     * @param msg
     */
    protected void toast(String msg){
        Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finish() {
        super.finish();
        if (!cancelNavigateAnimation()){
            new Handler().postDelayed(() -> overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right),400);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unBinder != null){
            unBinder.unbind();
        }
    }
}
