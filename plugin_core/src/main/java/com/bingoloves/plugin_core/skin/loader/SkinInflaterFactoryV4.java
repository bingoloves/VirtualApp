package com.bingoloves.plugin_core.skin.loader;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.support.v4.view.LayoutInflaterFactory;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.View;

import com.bingoloves.plugin_core.skin.config.SkinConfig;
import com.bingoloves.plugin_core.skin.entity.AttrFactory;
import com.bingoloves.plugin_core.skin.entity.DynamicAttr;
import com.bingoloves.plugin_core.skin.entity.SkinAttr;
import com.bingoloves.plugin_core.skin.entity.SkinItem;
import com.bingoloves.plugin_core.skin.util.ListUtils;
import com.bingoloves.plugin_core.utils.log.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Supply {@link SkinInflaterFactoryV4} to be called when inflating from a LayoutInflater.
 * 
 * <p>Use this to collect the {skin:enable="true|false"} views availabled in our XML layout files.
 * 
 * @author fengjun
 */
public class SkinInflaterFactoryV4 implements LayoutInflaterFactory {
	
	private static final boolean DEBUG = true;
	
	/**
	 * Store the view item that need skin changing in the activity
	 */
	private List<SkinItem> mSkinItems = new ArrayList<SkinItem>();
	@Override
	public View onCreateView(View view, String name, Context context, AttributeSet attrs) {
		boolean isSkinEnable = attrs.getAttributeBooleanValue(SkinConfig.NAMESPACE, SkinConfig.ATTR_SKIN_ENABLE, false);
		if (!isSkinEnable){
			return null;
		}

		View skinView = createView(context, name, attrs);
		if (skinView == null){
			return null;
		}
		parseSkinAttr(context, attrs, skinView);
		return skinView;
	}

	
	/**
     * Invoke low-level function for instantiating a view by name. This attempts to
     * instantiate a view class of the given <var>name</var> found in this
     * LayoutInflater's ClassLoader.
     * 
     * @param context 
     * @param name The full name of the class to be instantiated.
     * @param attrs The XML attributes supplied for this instance.
     * 
     * @return View The newly instantiated view, or null.
     */
	private View createView(Context context, String name, AttributeSet attrs) {
		View view = null;
		try {
			if (-1 == name.indexOf('.')){
				if ("View".equals(name)) {
					view = LayoutInflater.from(context).createView(name, "android.view.", attrs);
				} 
				if (view == null) {
					view = LayoutInflater.from(context).createView(name, "android.widget.", attrs);
				} 
				if (view == null) {
					view = LayoutInflater.from(context).createView(name, "android.webkit.", attrs);
				} 
			}else {
	            view = LayoutInflater.from(context).createView(name, null, attrs);
	        }
//			LogUtils.i("about to create " + name);
		} catch (Exception e) {
			LogUtils.e("error while create 【" + name + "】 : " + e.getMessage());
			view = null;
		}
		return view;
	}

	/**
	 * Collect skin able tag such as background , textColor and so on
	 * 
	 * @param context
	 * @param attrs
	 * @param view
	 */
	private void parseSkinAttr(Context context, AttributeSet attrs, View view) {
		List<SkinAttr> viewAttrs = new ArrayList<SkinAttr>();
		
		for (int i = 0; i < attrs.getAttributeCount(); i++){
			String attrName = attrs.getAttributeName(i);
			String attrValue = attrs.getAttributeValue(i);
			
			if(!AttrFactory.isSupportedAttr(attrName)){
				continue;
			}
			
		    if(attrValue.startsWith("@")){
				try {
					int id = Integer.parseInt(attrValue.substring(1));
					String entryName = context.getResources().getResourceEntryName(id);
					String typeName = context.getResources().getResourceTypeName(id);
					SkinAttr mSkinAttr = AttrFactory.get(attrName, id, entryName, typeName);
					if (mSkinAttr != null) {
						viewAttrs.add(mSkinAttr);
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (NotFoundException e) {
					e.printStackTrace();
				}
		    }
		}
		
		if(!ListUtils.isEmpty(viewAttrs)){
			SkinItem skinItem = new SkinItem();
			skinItem.view = view;
			skinItem.attrs = viewAttrs;

			mSkinItems.add(skinItem);
			
			if(SkinManager.getInstance().isExternalSkin()){
				skinItem.apply();
			}
		}
	}
	
	public void applySkin(){
		if(ListUtils.isEmpty(mSkinItems)){
			return;
		}
		
		for(SkinItem si : mSkinItems){
			if(si.view == null){
				continue;
			}
			si.apply();
		}
	}
	
	public void dynamicAddSkinEnableView(Context context, View view, List<DynamicAttr> pDAttrs){
		List<SkinAttr> viewAttrs = new ArrayList<SkinAttr>();
		SkinItem skinItem = new SkinItem();
		skinItem.view = view;
		
		for(DynamicAttr dAttr : pDAttrs){
			int id = dAttr.refResId;
			String entryName = context.getResources().getResourceEntryName(id);
			String typeName = context.getResources().getResourceTypeName(id);
			SkinAttr mSkinAttr = AttrFactory.get(dAttr.attrName, id, entryName, typeName);
			viewAttrs.add(mSkinAttr);
		}
		
		skinItem.attrs = viewAttrs;
		addSkinView(skinItem);
	}
	
	public void dynamicAddSkinEnableView(Context context, View view, String attrName, int attrValueResId){	
		int id = attrValueResId;
		String entryName = context.getResources().getResourceEntryName(id);
		String typeName = context.getResources().getResourceTypeName(id);
		SkinAttr mSkinAttr = AttrFactory.get(attrName, id, entryName, typeName);
		SkinItem skinItem = new SkinItem();
		skinItem.view = view;
		List<SkinAttr> viewAttrs = new ArrayList<SkinAttr>();
		viewAttrs.add(mSkinAttr);
		skinItem.attrs = viewAttrs;
		addSkinView(skinItem);
	}
	
	public void addSkinView(SkinItem item){
		mSkinItems.add(item);
	}
	
	public void clean(){
		if(ListUtils.isEmpty(mSkinItems)){
			return;
		}
		
		for(SkinItem si : mSkinItems){
			if(si.view == null){
				continue;
			}
			si.clean();
		}
	}
}
