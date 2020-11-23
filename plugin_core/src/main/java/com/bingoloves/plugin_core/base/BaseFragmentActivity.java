package com.bingoloves.plugin_core.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;

import com.bingoloves.plugin_core.skin.entity.DynamicAttr;
import com.bingoloves.plugin_core.skin.listener.IDynamicNewView;
import com.bingoloves.plugin_core.skin.listener.ISkinUpdate;
import com.bingoloves.plugin_core.skin.loader.SkinInflaterFactory;
import com.bingoloves.plugin_core.skin.loader.SkinManager;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Base Fragment Activity for development
 * 
 * <p>NOTICE:<br> 
 * You should extends from this if you want to do skin change
 * 
 * @author fengjun
 */
public class BaseFragmentActivity extends FragmentActivity implements ISkinUpdate, IDynamicNewView {
	
	/**
	 * Whether response to skin changing after create
	 */
	private boolean isResponseOnSkinChanging = true;
	
	private SkinInflaterFactory mSkinInflaterFactory;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
        try {
            Field field = LayoutInflater.class.getDeclaredField("mFactorySet");
            field.setAccessible(true);
            field.setBoolean(getLayoutInflater(), false);
            
    		mSkinInflaterFactory = new SkinInflaterFactory();
    		getLayoutInflater().setFactory(mSkinInflaterFactory);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } 
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		SkinManager.getInstance().attach(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		SkinManager.getInstance().detach(this);
	}
	
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
		if(!isResponseOnSkinChanging) return;
		mSkinInflaterFactory.applySkin();
	}
	
	@Override
	public void dynamicAddView(View view, List<DynamicAttr> pDAttrs) {
		mSkinInflaterFactory.dynamicAddSkinEnableView(this, view, pDAttrs);
	}
}
