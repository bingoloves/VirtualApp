package com.bingoloves.plugin_core.skin.listener;

import android.content.Context;

public interface ISkinChecker {
	
	/**
	 * Check whether the skin is exits or legal
	 * @param context
	 * @param path
	 * @return 
	 */
	boolean isSkinPackageLegality(Context context, String path);
}
