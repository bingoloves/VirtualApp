package com.bingoloves.plugin_core.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import com.bingoloves.plugin_core.core.ActivityInterface;
import com.bingoloves.plugin_core.skin.entity.DynamicAttr;
import com.bingoloves.plugin_core.skin.listener.IDynamicNewView;
import com.bingoloves.plugin_core.skin.listener.ISkinUpdate;
import com.bingoloves.plugin_core.skin.loader.SkinInflaterFactory;
import com.bingoloves.plugin_core.skin.loader.SkinManager;
import java.util.List;

import static com.bingoloves.plugin_core.proxy.ProxyActivity.EXT_CLASS_NAME;

@SuppressLint("Registered")
public class BaseActivity extends Activity implements ActivityInterface, ISkinUpdate, IDynamicNewView {

    public Activity mHostActivity;
    public Activity selfActivity;
    /**
     * Whether response to skin changing after create
     */
    private boolean isResponseOnSkinChanging = true;

    private SkinInflaterFactory mSkinInflaterFactory;
    @Override
    public void insertAppContext(Activity hostActivity) {
        mHostActivity = hostActivity;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (mHostActivity == null){
            super.onCreate(savedInstanceState);
        }
        selfActivity = this;
        mSkinInflaterFactory = new SkinInflaterFactory();
        getLayoutInflater().setFactory(mSkinInflaterFactory);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onStart() {
        super.onStart();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onResume() {
        super.onResume();
        SkinManager.getInstance().attach(this);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onPause() {
        super.onPause();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onStop() {
        super.onStop();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onDestroy() {
        super.onDestroy();
        SkinManager.getInstance().detach(this);
        mSkinInflaterFactory.clean();
    }

    @Override
    public void setContentView(int layoutResID) {
        if (mHostActivity != null){
            mHostActivity.setContentView(layoutResID);
        } else {
            super.setContentView(layoutResID);
        }
    }

    public Activity getSelfActivity(){
        if (mHostActivity != null){
            return mHostActivity;
        } else {
            return selfActivity;
        }
    }

    @Override
    public <T extends View> T findViewById(int id) {
        if (mHostActivity != null){
            return mHostActivity.findViewById(id);
        } else {
            return super.findViewById(id);
        }
    }

    @Override
    public void startActivity(Intent intent) {
        if (mHostActivity != null){
            Intent newIntent = new Intent();
            newIntent.putExtra(EXT_CLASS_NAME, intent.getComponent().getClassName());
            mHostActivity.startActivity(newIntent);
        } else {
            super.startActivity(intent);
        }
    }

    @Override
    public ComponentName startService(Intent intent) {
        if (mHostActivity != null){
            Intent newIntent = new Intent();
            newIntent.putExtra(EXT_CLASS_NAME, intent.getComponent().getClassName());
            return mHostActivity.startService(newIntent);
        } else {
            return super.startService(intent);
        }
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        if (mHostActivity != null){
            return mHostActivity.registerReceiver(receiver, filter);
        } else {
            return super.registerReceiver(receiver, filter);
        }
    }
    @Override
    public void sendBroadcast(Intent intent) {
        if (mHostActivity != null){
            mHostActivity.sendBroadcast(intent);
        } else {
            super.sendBroadcast(intent);
        }
    }
    /**
     * dynamic add a skin view
     *
     * @param view
     * @param attrName
     * @param attrValueResId
     */
    protected void dynamicAddSkinEnableView(View view, String attrName, int attrValueResId){
        mSkinInflaterFactory.dynamicAddSkinEnableView(this, view, attrName, attrValueResId);
    }

    protected void dynamicAddSkinEnableView(View view, List<DynamicAttr> pDAttrs){
        mSkinInflaterFactory.dynamicAddSkinEnableView(this, view, pDAttrs);
    }

    final protected void enableResponseOnSkinChanging(boolean enable){
        isResponseOnSkinChanging = enable;
    }

    @Override
    public void onThemeUpdate() {
        if(!isResponseOnSkinChanging){
            return;
        }
        mSkinInflaterFactory.applySkin();
    }

    @Override
    public void dynamicAddView(View view, List<DynamicAttr> pDAttrs) {
        mSkinInflaterFactory.dynamicAddSkinEnableView(this, view, pDAttrs);
    }
}
