package com.bingoloves.plugin_core.skin.entity;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import com.bingoloves.plugin_core.skin.loader.SkinManager;

public class BackgroundAttr extends SkinAttr {

	@Override
	public void apply(View view) {
		
		if(RES_TYPE_NAME_COLOR.equals(attrValueTypeName)){
			view.setBackgroundColor(SkinManager.getInstance().getColor(attrValueRefId));
			Log.i("attr", "_________________________________________________________");
			Log.i("attr", "apply as color");
		}else if(RES_TYPE_NAME_DRAWABLE.equals(attrValueTypeName)){
			Drawable bg = SkinManager.getInstance().getDrawable(attrValueRefId);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				view.setBackground(bg);
			}
			Log.i("attr", "_________________________________________________________");
			Log.i("attr", "apply as drawable");
			Log.i("attr", "bg.toString()  " + bg.toString());
			Log.i("attr", this.attrValueRefName + " 是否可变换状态? : " + bg.isStateful());
		}
	}
}
