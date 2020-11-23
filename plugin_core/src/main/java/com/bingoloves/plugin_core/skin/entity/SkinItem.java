package com.bingoloves.plugin_core.skin.entity;

import java.util.ArrayList;
import java.util.List;
import android.content.res.Resources;
import android.view.View;

import com.bingoloves.plugin_core.skin.util.ListUtils;

public class SkinItem {
	
	public View view;
	
	public List<SkinAttr> attrs;
	
	public SkinItem(){
		attrs = new ArrayList<SkinAttr>();
	}
	
	public void apply(){
		if(ListUtils.isEmpty(attrs)){
			return;
		}
		for(SkinAttr at : attrs){
			at.apply(view);
		}
	}
	
	public void clean(){
		if(ListUtils.isEmpty(attrs)){
			return;
		}
		for(SkinAttr at : attrs){
			at = null;
		}
	}

	@Override
	public String toString() {
		return "SkinItem [view=" + view.getClass().getSimpleName() + ", attrs=" + attrs + "]";
	}
}
