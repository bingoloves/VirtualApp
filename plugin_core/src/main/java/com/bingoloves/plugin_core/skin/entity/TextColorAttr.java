package com.bingoloves.plugin_core.skin.entity;

import android.view.View;
import android.widget.TextView;

import com.bingoloves.plugin_core.skin.loader.SkinManager;

public class TextColorAttr extends SkinAttr {

	@Override
	public void apply(View view) {
		if(view instanceof TextView){
			TextView tv = (TextView)view;
			if(RES_TYPE_NAME_COLOR.equals(attrValueTypeName)){
//				LogUtils.e("TextColorAttr");
				tv.setTextColor(SkinManager.getInstance().convertToColorStateList(attrValueRefId));
			}
		}
	}
}
