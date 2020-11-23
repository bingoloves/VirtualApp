package com.bingoloves.plugin_core.skin.listener;

import android.view.View;
import com.bingoloves.plugin_core.skin.entity.DynamicAttr;
import java.util.List;

public interface IDynamicNewView {
	void dynamicAddView(View view, List<DynamicAttr> pDAttrs);
}
