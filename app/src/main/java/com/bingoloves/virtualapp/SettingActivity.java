package com.bingoloves.virtualapp;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toast;

import com.bingoloves.plugin_core.adapter.listview.CommonAdapter;
import com.bingoloves.plugin_core.adapter.listview.ViewHolder;
import com.bingoloves.plugin_core.base.PluginActivity;
import com.bingoloves.plugin_core.skin.entity.SkinInfo;
import com.bingoloves.plugin_core.skin.listener.ILoaderListener;
import com.bingoloves.plugin_core.skin.loader.SkinManager;
import com.bingoloves.plugin_core.utils.Utils;
import com.bingoloves.plugin_core.utils.log.LogUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SettingActivity extends PluginActivity {
	private GridView gridView;
	private CommonAdapter<SkinInfo> adapter;
	private List<SkinInfo> skinInfoList = new ArrayList<>();
	private boolean isOfficalSelected;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		initView();
		initDefaultSkin();
		initSkinView();
	}

	/**
	 * 初始化默认的皮肤
	 */
	private void initDefaultSkin() {
		new Thread(() -> {
            try {
                AssetManager assets = getAssets();
                String[] list = assets.list("");
                for (String path : list) {
                    if (path.endsWith(".skin")){
						Utils.copyFileFromAssets(SettingActivity.this, path);
                    }
                }
				skinInfoList = getAssetsList();
				adapter.update(skinInfoList);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
	}

	private void initSkinView() {
		gridView = findViewById(R.id.grid);
		adapter = new CommonAdapter<SkinInfo>(this,R.layout.layout_skin_item,skinInfoList) {
			@Override
			protected void convert(ViewHolder viewHolder, SkinInfo skinInfo, int position) {
				viewHolder.setText(R.id.skin_name,skinInfo.getName());
				viewHolder.setImageDrawable(R.id.skin_icon,skinInfo.getIcon());
			}
		};
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener((parent, view, position, id) -> {
			SkinInfo skinInfo = skinInfoList.get(position);
			File skin = skinInfo.getFile();
			SkinManager.getInstance().load(skin.getAbsolutePath(),
					new ILoaderListener() {
						@Override
						public void onStart() {
							LogUtils.e("start loadSkin");
						}

						@Override
						public void onSuccess() {
							LogUtils.e("loadSkinSuccess");
							Toast.makeText(getApplicationContext(), "切换成功", Toast.LENGTH_SHORT).show();
							isOfficalSelected = false;

						}

						@Override
						public void onFailed() {
							LogUtils.e("loadSkinFail");
							Toast.makeText(getApplicationContext(), "切换失败", Toast.LENGTH_SHORT).show();
						}
					});
		});
	}

	private void initView() {
		findViewById(R.id.tv_skin_default).setOnClickListener(v -> onSkinResetClick());
		isOfficalSelected = !SkinManager.getInstance().isExternalSkin();
	}

	/**
	 * 获取Assets下的默认皮肤
	 * @return
	 */
	private List<SkinInfo> getAssetsList(){
		List<SkinInfo> skinInfoList = new ArrayList<>();
		File[] skinList = SkinManager.getInstance().getSkinList();
		if (skinList != null){
			for (File file : skinList) {
				String name = file.getName();
				SkinInfo skinInfo = SkinManager.getInstance().getSkinInfo(name);
				if (skinInfo != null){
					skinInfoList.add(skinInfo);
				}
			}
		}
		return skinInfoList;
	}
	protected void onSkinResetClick() {
		SkinManager.getInstance().restoreDefaultTheme();
		Toast.makeText(getApplicationContext(), "切换成功", Toast.LENGTH_SHORT).show();
	}
}
